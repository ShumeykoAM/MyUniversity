<?php

  include 'MySQLOpenHelper.php';

  interface I_Handler
  {
    public function Handling($JOBJ);// :String;
  }

  //Ответ на запрос теста соединения с сервером
  class HandlerTestConnectServer
    implements I_Handler
  {
    public function Handling($JOBJ)
    {
      //Проверим наличие всех параметров
      $Test_value = $JOBJ->{'TEST_VALUE'};
      $result = isset($Test_value);
      if($result)
      {
        //Сгенерируем ответ, вернем тестовое значение которое прислал клиент
        $arr = array( 'MESSAGE' => E_MESSAGEID\TEST_CONNECT_SERVER, 'TEST_VALUE' => $Test_value );
        $enc = json_encode($arr);
        return $enc;
      }
      else
        return "";
    }
  }

  //Ответ на запрос регистрации нового профиля
  class HandlerCreateNewProfile
    implements I_Handler
  {
    private $link;

    public function __construct()
    {
      global $link;
      $DB = new MySQLOpenHelper();
      $link = $DB->getLink();
    }

    public function Handling($JOBJ)
    {
      //Проверим наличие всех параметров
      $login    = $JOBJ->{'LOGIN'};
      $password = $JOBJ->{'PASSWORD'};
      $result = isset($login) and isset($password);
      if($result)
      {
        global $link, $NAME_DB; //Такое извращение нужно что бы видеть глобальные переменные

        $login = $link->real_escape_string($login);
        $password = md5($password);

        //Сгенерируем ответ, проверим свободен ли логин, если да то зарегистрируем пользователя
        $result = $link != null;
        if($result)
          $result = $link->select_db($NAME_DB); //Выбираем базу для использования
        if($result)
        {
          //Попытаемся найти такой логин
          $command = "SELECT Profile_ID FROM profile WHERE Profile_Mail='".$login."';";
          $q_result = $link->query($command);
          if($q_result)
            $result =  $q_result->num_rows == 0;
          else
            $result = false;
        }
        if($result) //Не нашли такой логин значит можно создать профиль с таким логином
        {
          $command = "INSERT INTO profile (Profile_Mail, Profile_HASHPassword) ".
                     "VALUES ('".$login."', '".$password."');";
          $result = $link->query($command);
        }
        $arr = array( 'MESSAGE' => E_MESSAGEID\CREATE_NEW_PROFILE, 'RESULT' => $result );
        $enc = json_encode($arr);
        return $enc;
      }
      else
        return "";
    }
  }


?>