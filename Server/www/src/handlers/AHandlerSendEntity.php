<?php

include_once 'common.php';
include_once 'entities/Type.php';
include_once 'entities/Chronological.php';

class AHandlerSendEntity
  extends ABaseHandler
  implements I_Transaction
{
  const INSERTED = 0;
  const UPDATED = 1;
  const USE_SERVER_REC = 2;

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
    $result = true;

    $user_id = $_SESSION[GLOBALS_VAR\ID];
    $user_account = UserAccount::getUserAccountFromID($link, $user_id);
    $table  = $JOBJ->{'TABLE'};
    $entity = $JOBJ->{'ENTITY'};
    switch($table)
    {
      case E_TABLE\TYPE:
        $_id = $entity->{'id_server'};
        if(!isset($_id)) //Если такой записи нет
        {
          $type = Type::getTypeFromName($user_account->_id_group, $entity->{'name'}, $link);
          if($type == null)
          {
            //Еще нету такой записи в БД сервера
            $type = new Type(Type::$ENUM_CONSTRUCT_JObj, $user_account->_id_group, $entity);
            $type->_id = 1 + Type::getLast_id($user_account->_id_group, $link);
            if( ($result = $result && $type->insert($link)) ) //Вставили запись, вернем id на сервере
            {
              //Записываем в хронологию об изменениях
              $chronological = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $user_account->_id_group,
                $table, $type->_id, $JOBJ->{'TIMESTAMP'}, 0);
              $result = $result && $chronological->insert($link);
              //Ответ о том что вставили запись
              $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::INSERTED,
                '_id_server' => $type->_id);
              $this->enc = json_encode($arr);
            }
          }
          else
          {
            $_id = $type->_id;
          }
        }
        if(isset($_id))
        {
          $type = Type::getTypeFrom_id($user_account->_id_group, $_id, $link);
          $chronological = Chronological::getFromFirstKey($user_account->_id_group, $table, $_id, $link);
          if($JOBJ->{'TIMESTAMP'} >= $chronological->timestamp)
          {
            $new_type = $type->copy();
            $new_type->is_delete = $entity->{'is_delete'};
            $new_type->id_unit   = $entity->{'id_unit'};
            $new_type->name      = $entity->{'name'};
            if( ($result = $result && $type->update($link, $new_type)) )
            {
              $chronological->timestamp = $JOBJ->{'TIMESTAMP'};
              $result = $result && $chronological->update($link);
            }
            //Ответ о том что обновили запись
            $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::UPDATED,
              '_id_server' => $type->_id);
            $this->enc = json_encode($arr);
          }
          else
          {
            //Ответ о том что нужно использовать запись с сервера, а ее клиент получит при запросе записей с сервака
            $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::USE_SERVER_REC);
            $this->enc = json_encode($arr);
          }
        }
        break;
    }
    if(!$result)
      $this->enc = "";
    return $result;
  }
}
?>