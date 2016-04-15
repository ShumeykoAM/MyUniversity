<?php

include_once 'Common.php';
include_once 'entities/UserAccount.php';
include_once 'entities/UserGroup.php';
include_once 'tools/EQ.php';
include_once 'handlers/ABaseHandler.php';


class AHandlerCreateGroupCode
  extends ABaseHandler
{
  private $link;

  public function __construct()
  {
    global $link;
    $DB = new MySQLOpenHelper();
    $link = $DB->getLink();
  }

  //Перечисление операция по запросу
  public static $GENERATE = 0;
  public static $CANCEL   = 1;
  public static $CHECK    = 2;

  //Реализуем обработку запроса
  protected function AHandling($JOBJ)
  {
    global $link; //Такое извращение нужно что бы видеть глобальные переменные
    $user_id = $_SESSION[GLOBALS_VAR\ID];
    $result = true;
    //Извлечем операцию
    $operation = $JOBJ->{'OPERATION'};
    switch ($operation)
    {
      case AHandlerCreateGroupCode::$GENERATE:
        $group_code = mt_rand(10000, 99999); //Сгенерируем случайный код
        //Проверим не используется ли этот код для какой либо группы
        $values = array($group_code); //Параметры для запроса
        $query = QueryCreator::getQuery( $link, EQ\ID_GROUP_FROM_CODE, $values );
        $q_result = $link->query($query);
        if($q_result && $q_result->num_rows != 0)
        {
          //Сиюминутное совпадение это очень большая редкость, так что просто перезапишем да и всё
          $q_result->data_seek(0);
          $row = $q_result->fetch_assoc();
          $user_group = UserGroup::getUserGroupFromID($link, $row['_id']);
          $user_group_update = $user_group->copy();
          $user_group_update->code_for_co_user = null;
          $result = $user_group->update($link, $user_group_update);
        }
        if($result)
        {
          //Запишем в базу
          $user_account = UserAccount::getUserAccountFromID($link, $user_id);
          $user_group = UserGroup::getUserGroupFromID($link, $user_account->_id_group);
          $user_group_update = $user_group->copy();
          $user_group_update->timestamp_code = time();
          $user_group_update->code_for_co_user = $group_code;
          $result = $user_group->update($link, $user_group_update);
        }
        if($result)
        {
          $arr = array('MESSAGE' => E_MESSAGEID\CREATE_GROUP_CODE, 'GROUP_CODE' => $group_code);
          $enc = json_encode($arr);
          return $enc;
        }
        else
          return "";
        break;
      case AHandlerCreateGroupCode::$CANCEL:
        //Запишем в базу
        $user_account = UserAccount::getUserAccountFromID($link, $user_id);
        $user_group = UserGroup::getUserGroupFromID($link, $user_account->_id_group);
        $user_group_update = $user_group->copy();
        $user_group_update->code_for_co_user = null;
        $result = $user_group->update($link, $user_group_update);
        if($result)
        {
          $arr = array('MESSAGE' => E_MESSAGEID\CREATE_GROUP_CODE, 'RESULT_CANCEL' => true);
          $enc = json_encode($arr);
          return $enc;
        }
        else
          return "";
        break;
      case AHandlerCreateGroupCode::$CHECK:
        $user_account = UserAccount::getUserAccountFromID($link, $user_id);
        $user_group = UserGroup::getUserGroupFromID($link, $user_account->_id_group);
        if($user_group->code_for_co_user < 0)
          $arr = array('MESSAGE' => E_MESSAGEID\CREATE_GROUP_CODE, 'RESULT_CONNECTED' => true);
        else
          $arr = array('MESSAGE' => E_MESSAGEID\CREATE_GROUP_CODE, 'RESULT_CONNECTED' => false);
        $enc = json_encode($arr);
        return $enc;
        break;
    }


  }
}