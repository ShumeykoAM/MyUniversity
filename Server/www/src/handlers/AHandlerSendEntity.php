<?php

include_once 'common.php';
include_once 'entities/Type.php';
include_once 'entities/Chronological.php';

class AHandlerSendEntity
  extends ABaseHandler
  implements I_Transaction
{
  const INSERTED     = 0;
  const UPDATED      = 1;
  const NOT_LAST_REV = 2;

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
    $user_id = $_SESSION[GLOBALS_VAR\ID];
    $user_account = UserAccount::getUserAccountFromID($link, $user_id);
    $JOBJ = $params["JOBJ"];
    $timestamp_cl = $JOBJ->{'TIMESTAMP'};
    $revision     = $JOBJ->{'REVISION'};
    $table        = $JOBJ->{'TABLE'};
    $entity       = $JOBJ->{'ENTITY'};

    $result = true;
    if($revision < Chronological::getLastRevision($user_account->_id_group, $link))
    {
      $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
      $this->enc = json_encode($arr);
      $result = false;
    }
    if($result)
    {
      switch ($table)
      {
        case E_TABLE\TYPE:
          $_id = $entity->{'id_server'};
          if(!isset($_id)) //Если клиенту не известен id_server
          {
            $type = new Type(Type::$ENUM_CONSTRUCT_JObj, $user_account->_id_group, $entity);
            $type->_id = 1 + Type::getLast_id($user_account->_id_group, $link);
            if (($result = $result && $type->insert($link))) //Вставили запись, вернем id на сервере
            {
              //Записываем в хронологию об изменениях
              $chronological = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $user_account->_id_group,
                0, $table, $type->_id, $JOBJ->{'TIMESTAMP'});
              $result = $result && $chronological->insert($link);
              //Ответ о том что вставили запись
              if($result && $chronological->revision == ($revision + 1))
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::INSERTED,
                  '_id_server' => $type->_id);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
            else
            {
              $type = Type::getTypeFromName($user_account->_id_group, $entity->{'name'}, $link);
              if($type != null)
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::INSERTED,
                  '_id_server' => $type->_id);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
          }
          else
          {
            $type = Type::getTypeFrom_id($user_account->_id_group, $_id, $link);
            $new_type = $type->copy();
            $new_type->is_delete = $entity->{'is_delete'};
            $new_type->id_unit = $entity->{'id_unit'};
            $new_type->name = $entity->{'name'};
            if(($result = $result && $type->update($link, $new_type)))
            {
              //Записываем в хронологию об изменениях
              $chronological = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $user_account->_id_group,
                0, $table, $type->_id, $JOBJ->{'TIMESTAMP'});
              $result = $result && $chronological->insert($link);
              //Ответ о том что обновили запись
              if($result && $chronological->revision == ($revision + 1))
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::UPDATED,
                  '_id_server' => $type->_id);
                $this->enc = json_encode($arr);
              }
              else
              {
                $arr = array('MESSAGE' => E_MESSAGEID\SEND_ENTITY, 'RESULT' => AHandlerSendEntity::NOT_LAST_REV);
                $this->enc = json_encode($arr);
                $result = false;
              }
            }
          }
          break;
      }
    }
    return $result;
  }
}
?>