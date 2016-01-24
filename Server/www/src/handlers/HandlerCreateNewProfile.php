<?php

  //Ответ на запрос регистрации нового профиля
  class HandlerCreateNewProfile
    implements I_Handler, I_Transaction
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
      //Проверим наличие всех параметров
      $login    = $JOBJ->{'LOGIN'};
      $password = $JOBJ->{'PASSWORD'};
      $result = isset($login) and isset($password);
      if($result)
        $result = ($login != "" and $password != "");
      if($result)
        $result = ($link != null);
      if($result)
      {
        $login = $link->real_escape_string($login);
        $hash_password = md5($password);

        //Сгенерируем ответ, проверим свободен ли логин, если да то зарегистрируем пользователя
        $result = ($link != null);
        if($result)
        {
          //Попытаемся найти такой логин
          $command = "SELECT user_account._id FROM user_account WHERE user_account.login='".$login."';";
          $q_result = $link->query($command);
          if($q_result)
            $result =  ($q_result->num_rows == 0);
          else
            $result = false;
        }
        if($result) //Не нашли такой логин значит можно создать профиль с таким логином
        {
          $params = array( "login" => $login, "hash_password" => $hash_password );
          $transaction = new SQLTransaction($link, $this);
          $result = $transaction->runTransaction($params);
        }
        $arr = array( 'MESSAGE' => E_MESSAGEID\CREATE_NEW_PROFILE, 'RESULT' => $result );
        $enc = json_encode($arr);
        return $enc;
      }
      else
        return "";
    }
    //Реализуем функцию обработки транзакции
    public function trnFunc($params)
    {
      global $link;
      $command = "INSERT INTO user_group () VALUES ();";
      $result = $link->query($command);
      if($result)
      {
        //UPDATE user_account SET _id_group=2, _id=17 WHERE _id_group=2 AND _id=23;
        $command = "INSERT INTO user_account (_id_group, _id, login, hash_password)".
          "  VALUES (LAST_INSERT_ID(), 1,'".$params["login"]."', '".$params["hash_password"]."');";
        $result = $link->query($command);
      }
      return $result;
    }
  }
?>