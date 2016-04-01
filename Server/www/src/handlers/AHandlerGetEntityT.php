<?php

include_once 'common.php';
include_once 'entities/Type.php';

class AHandlerGetEntityT
  extends ABaseHandler
{
  protected $link;

  public function __construct()
  {
    $DB = new MySQLOpenHelper();
    $this->link = $DB->getLink();
  }

  protected function AHandling($JOBJ)
  {
    $timestamp = $JOBJ->{"TIMESTAMP"};
    $user_id = $_SESSION[GLOBALS_VAR\ID];
    $user_account = UserAccount::getUserAccountFromID($this->link, $user_id);
    $chrono = Chronological::getAfterTimeStamp($user_account->_id_group, $timestamp, $this->link);
    $answer = "";
    if($chrono != null)
    {
      switch ($chrono->table_db)
      {
        case E_TABLE\TYPE:
          $type = Type::getTypeFrom_id($user_account->_id_group, $chrono->_id_record, $this->link);
          $entity_jobj = $type->getJObj();
          $arr = array('MESSAGE' => E_MESSAGEID\GET_ENTITY_T, 'TABLE' => E_TABLE\TYPE, 'ENTITY' => $entity_jobj, 'TIMESTAMP' => $chrono->timestamp);
          $answer = json_encode($arr);
          break;
      }
    }
    return $answer;
  }
}