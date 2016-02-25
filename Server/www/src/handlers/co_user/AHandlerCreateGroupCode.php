<?php
  include_once 'Common.php';
  include_once 'entities/UserAccount.php';
  include_once 'entities/UserGroup.php';
  include_once 'tools/EQ.php';


  class AHandlerCreateGroupCode
    implements I_Handler
  {
    private $link;

    public function __construct()
    {
      global $link;
      $DB = new MySQLOpenHelper();
      $link = $DB->getLink();
    }
    //Реализуем обработку запроса
    public function Handling($JOBJ)
    {
      global $link; //Такое извращение нужно что бы видеть глобальные переменные
      //Проверим наличие идентификации
      if(array_key_exists(GLOBALS_VAR\ID, $_SESSION))
      {
        $user_id = $_SESSION[GLOBALS_VAR\ID];
        $result = true;
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
      }
      else
      {
        $arr = array( 'MESSAGE' => E_MESSAGEID\NOT_IDENTIFY );
        $enc = json_encode($arr);
        return $enc;
      }
    }
  }

?>