<?php

  //Проверка незанятости логина пользователя
  //  если логин свободет то возвращаем true
  class HandlerTestLogin
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
      global $link;
      //Проверим наличие всех параметров
      $login    = $JOBJ->{'LOGIN'};
      $result = isset($login);
      $result = ($login != "");
      if($result and ($link != null))
      {
        $login = $link->real_escape_string($login);
        //Сгенерируем ответ, проверим свободен ли логин, если да то вернем true
        $result = ($link != null);
        if($result)
        {
          //Попытаемся найти такой логин
          $command = "SELECT user_account._id FROM user_account WHERE user_account.login='".$login."';";
          $q_result = $link->query($command);
          if($q_result)
            $result = ($q_result->num_rows == 0);
          else
            $result = false;
        }
      }
      $arr = array( 'MESSAGE' => E_MESSAGEID\TEST_LOGIN, 'RESULT' => $result );
      $enc = json_encode($arr);
      return $enc;
    }
  }
?>