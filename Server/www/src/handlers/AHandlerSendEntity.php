<?php

include_once 'common.php';
include_once 'entities/Type.php';
include_once 'entities/Chronological.php';

class AHandlerSendEntity
  extends ABaseHandler
  implements I_Transaction
{
  protected $link;
  private $enc = "";
  public function __construct()
  {
    global $link;
    $DB = new MySQLOpenHelper();
    $link = $DB->getLink();
  }

  protected function AHandling($JOBJ)
  {
    global $link; //Такое извращение нужно что бы видеть глобальные переменные
    $transaction = new SQLTransaction($link, $this);
    $params = array("JOBJ"=>$JOBJ);
    $transaction->runTransaction($params);
    return $this->enc;
  }
  public function trnFunc($params)
  {
    global $link;
    $JOBJ = $params["JOBJ"];

    $user_id = $_SESSION[GLOBALS_VAR\ID];
    $user_account = UserAccount::getUserAccountFromID($link, $user_id);
    $table  = $JOBJ->{'TABLE'};
    $entity = $JOBJ->{'ENTITY'};
    switch($table)
    {
      case E_TABLE\TYPE:
        $_id = $entity->{'id_server'};
        if(isset($_id)) //Если есть такая запись то проверяем хронологию
        {
          $chronological = Chronological::getFromFirstKey($user_account->_id_group, $table, $_id, $link);
          $type = Type::getTypeFrom_id($user_account->_id_group, $_id, $link);
          if($chronological != null && $chronological->timestamp < $JOBJ->{'TIMESTAMP'} )
          {
            $new_type = $type->copy();
            $new_type->is_delete = $entity->{'is_delete'};
            $new_type->id_unit   = $entity->{'id_unit'};
            $new_type->name      = $entity->{'name'};
            if($type->update($link, $new_type))
            {
              $chronological->timestamp = $JOBJ->{'TIMESTAMP'};
              $chronological->update($link);
            }
          }
          $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, '_id_server' => $type->_id);
          $this->enc = json_encode($arr);
        }
        else
        {
          //Еще нету такой записи в БД сервера, попытаемся вставить, запись с таким именем мог вставить др. пользователь
          $type = new Type(Type::$ENUM_CONSTRUCT_JObj, $user_account->_id_group, $entity);
          $type->_id = 1 + Type::getLast_id($user_account->_id_group, $link);
          if($type->insert($link)) //Вставили запись, вернем id на сервере
          {
            $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, '_id_server' => $type->_id);
            $this->enc = json_encode($arr);
          }
          else //Имя дублируется, такое имя уже вставил другой пользователь, просто вернем id на сервере
          {
            $type = Type::getTypeFromName($user_account->_id_group, $entity->{'name'}, $link);
            $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, '_id_server' => $type->_id);
            $this->enc = json_encode($arr);
          }
          //Записываем в хронологию об изменениях
          $chronological = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $user_account->_id_group,
            $table, $type->_id, $JOBJ->{'TIMESTAMP'}, 0);
          $chronological->insert($link);
        }
        break;
    }
    return true;
  }
}
?>