<?php

include_once 'common.php';
include_once 'entities/Type.php';
include_once 'entities/Purchase.php';
include_once 'entities/Detail.php';

class AHandlerGetEntity
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
    $revision = $JOBJ->{"REVISION"};
    $user_id = $_SESSION[GLOBALS_VAR\ID];
    $user_account = UserAccount::getUserAccountFromID($this->link, $user_id);
    $chrono = Chronological::getFromFirstKey($user_account->_id_group, $revision, $this->link);
    $arr = array('MESSAGE' => E_MESSAGEID\GET_ENTITY, 'EXIST' => false);
    $answer = json_encode($arr);
    if($chrono != null)
    {
      switch ($chrono->table_db)
      {
        case E_TABLE\TYPE:
          $type = Type::getTypeFrom_id($user_account->_id_group, $chrono->_id_record, $this->link);
          $entity_jobj = $type->getJObj();
          $arr = array('MESSAGE' => E_MESSAGEID\GET_ENTITY, 'EXIST' => true, 'TABLE' => $chrono->table_db,
            'ENTITY' => $entity_jobj, 'TIMESTAMP' => $chrono->timestamp);
          $answer = json_encode($arr);
          break;
        case E_TABLE\DETAIL:
          $detail = Detail::getDetailFrom_id($user_account->_id_group, $chrono->_id_record, $this->link);
          $entity_jobj = $detail->getJObj();
          $arr = array('MESSAGE' => E_MESSAGEID\GET_ENTITY, 'EXIST' => true, 'TABLE' => $chrono->table_db,
            'ENTITY' => $entity_jobj, 'TIMESTAMP' => $chrono->timestamp);
          $answer = json_encode($arr);
          break;
        case E_TABLE\PURCHASE:
          $purchase = Purchase::getPurchaseFrom_id($user_account->_id_group, $chrono->_id_record, $this->link);
          $entity_jobj = $purchase->getJObj();
          $arr = array('MESSAGE' => E_MESSAGEID\GET_ENTITY, 'EXIST' => true, 'TABLE' => $chrono->table_db,
            'ENTITY' => $entity_jobj, 'TIMESTAMP' => $chrono->timestamp);
          $answer = json_encode($arr);
          break;
      }
    }
    return $answer;
  }
}