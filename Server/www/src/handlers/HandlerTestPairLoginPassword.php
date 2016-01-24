<?php

  //Проверка пары логин пароль и если нужно оставляем флаг фхода в массиве $_SESSION
  class HandlerTestPairLoginPassword
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
      //Проверим наличие всех параметров
      $login      = $JOBJ->{'LOGIN'};
      $password   = $JOBJ->{'PASSWORD'};
      $need_enter = $JOBJ->{'NEED_ENTER'} == true;
      $result = isset($login) and isset($password) and isset($need_enter);
      if($result)
        $result = ($login != "" and $password != "");
      if($result)
        $result = ($link != null);
      if($result)
      {
        $login         = $link->real_escape_string($login);
        $hash_password = md5($password);

        //Сгенерируем ответ, проверим пару
        $result = ($link != null);
        if($result)
        {
          $command  = "SELECT user_account._id FROM user_account ".
                      "  WHERE user_account.login='" . $login . "' AND user_account.hash_password='". $hash_password ."';";
          $q_result = $link->query($command);
          if($q_result)
          {
            $result = $q_result->num_rows != 0;
            if($result)
            {
              //Добавим инфу о входе в систему в $_SESSION




            }
          }
          else
          {
            $result = false;
          }
        }
      }
      $arr = array( 'MESSAGE' => E_MESSAGEID\TEST_PAIR_LOGIN_PASSWORD, 'RESULT' => $result );
      $enc = json_encode($arr);
      return $enc;
    }
  }
?>