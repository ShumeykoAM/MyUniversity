<?php

include_once 'entities/UserGroup.php';

class AHandlerBecomeMember
  extends ABaseHandler
  implements I_Transaction
{
  protected $link;
  public function __construct()
  {
    global $link;
    $DB = new MySQLOpenHelper();
    $link = $DB->getLink();
  }

  protected function AHandling($JOBJ)
  {
    $time_limit = 54; //Сделаем поменьше чем на клиенте, что бы не попасть в просак
    global $link; //Такое извращение нужно что бы видеть глобальные переменные
    $user_id = $_SESSION[GLOBALS_VAR\ID];
    //Проверим наличие всех параметров
    $group_code = $JOBJ->{'GROUP_CODE'};
    $result = isset($group_code);
    if($result)
    {
      $values = array($group_code); //Параметры для запроса
      $query = QueryCreator::getQuery( $link, EQ\ID_GROUP_FROM_CODE, $values );
      $q_result = $link->query($query);
      if($q_result && $q_result->num_rows != 0) //Нашли группу с данным кодом, проверим $timestamp_code
      {
        $q_result->data_seek(0);
        $row = $q_result->fetch_assoc();
        $user_account = UserAccount::getUserAccountFromID($link, $user_id);
        $my_group = UserGroup::getUserGroupFromID($link, $user_account->_id_group);
        $user_group = UserGroup::getUserGroupFromID($link, $row['_id']);
        if( (time() - $user_group->timestamp_code <= $time_limit) && $my_group->_id != $user_group->_id)
        //Все нормально, будем присоединять
        {
          $params = array( "user_group" => $user_group );
          $transaction = new SQLTransaction($link, $this);
          $result = $transaction->runTransaction($params);
        }
        else
          $result = false;
      }
      else
        $result = false;
    }
    $arr = array('MESSAGE' => E_MESSAGEID\BECOME_MEMBER, 'RESULT' => $result);
    $enc = json_encode($arr);
    return $enc;
  }

  public function trnFunc($params)
  {
    global $link;
    $user_group = $params["user_group"];
    $user_group_update = $user_group->copy();
    $user_group_update->code_for_co_user = -$user_group_update->code_for_co_user;
    $result = $user_group->update($link, $user_group_update);
    if($result)
    {
      $user_id = $_SESSION[GLOBALS_VAR\ID];
      $user_account = UserAccount::getUserAccountFromID($link, $user_id);
      $user_account_copy = $user_account->copy();
      $user_account_copy->_id_group = $user_group->_id;
      $result = $user_account->update($link, $user_account_copy);
    }
    return $result;
  }
}