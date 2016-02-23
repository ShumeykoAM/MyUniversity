<?php
  include 'Common.php';
  include 'entities/UserAccount.php';
  include 'entities/UserGroup.php';

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
        while(true)
        {
          $group_code = mt_rand(10000, 99999); //Сгенерируем случайный код
          //Проверим не используется ли этот код для другой группы
          $command = "SELECT user_group._id, user_group.code_for_co_user, user_group.timestamp_code," . "  user_account._id AS user_id FROM user_group, user_account" . "  WHERE user_account._id_group=user_group._id AND user_group.code_for_co_user = " . $group_code . ";";
          $q_result = $link->query($command);
          if ($q_result)
          {
            if ($q_result->num_rows != 0)
            {
              $result = false;
              //Проверим timestamp_code и user_id


            }
            else
              $result = true;
          }
          else
            $result = true;
          break;
        }
        if($result)
        {
          //Запишем в базу
          $user_account = UserAccount::getUserAccountFromID($link, $user_id);
          $user_group = UserGroup::getUserGroupFromID($link, $user_account->_id_group);

          $user_group_update = $user_group->copy();
          $user_group_update->timestamp_code = time();
          $user_group_update->code_for_co_user = null;
          $user_group->update($link, $user_group_update);

          $timestamp = time(); //Текущее время в секундах с 1970


/*
          UPDATE user_group SET code_for_co_user=43254, timestamp_code=3545534
  WHERE user_group._id=1;


          $command = "INSERT INTO user_group () VALUES ();";
          $result = $link->query($command);
          if($result)
          {
            //UPDATE user_account SET _id_group=2, _id=17 WHERE _id_group=2 AND _id=23;
            $command = "INSERT INTO user_account (_id_group, _id, login, hash_password)".
              "  VALUES (LAST_INSERT_ID(), 1,'".$params["login"]."', '".$params["hash_password"]."');";
            $result = $link->query($command);
*/


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