<?php
  //Обработчик запросов клиентов

  //Запускаем сессию(нужно делать на каждой странице)
  //С помощью нее нам доступен массив $_SESSION на всех страницах
  session_start();

  include_once 'Common.php';
  include_once 'handlers/Handlers.php';

  include_once 'tools/MySQLOpenHelper.php'; //Инклюдим здесь
  include_once 'tools/SQLTransaction.php';  //  что бы в файлах приинклюденых ниже не инклюдить каждый раз

  include_once 'handlers/HandlerTestConnectServer.php';
  include_once 'handlers/HandlerCreateNewProfile.php';
  include_once 'handlers/HandlerTestLogin.php';
  include_once 'handlers/HandlerTestPairLoginPassword.php';
  include_once 'handlers/co_user/AHandlerCreateGroupCode.php';
  include_once 'handlers/co_user/AHandlerBecomeMember.php';
  include_once 'handlers/AHandlerSendEntity.php';

  class RequestHandler
  {
    public function handling()
    {
      $result = array_key_exists("Request", $_POST);
      $JOBJ   = null;
      $ID     = null;

      if ($result)
        $result = ($JOBJ = json_decode($_POST["Request"])) != null; //Декодируем строку в json
      if ($result)
        $result = ($ID = $JOBJ->{'MESSAGE'}) != null; //Получаем ID запроса
      if ($result)
      {
        $Answer = null;
        switch ($ID) //Определим соответствующий обработчик
        {
          case E_MESSAGEID\TEST_CONNECT_SERVER:
            $Answer = new HandlerTestConnectServer();
            break;
          case E_MESSAGEID\CREATE_NEW_PROFILE:
            $Answer = new HandlerCreateNewProfile();
            break;
          case E_MESSAGEID\TEST_LOGIN:
            $Answer = new HandlerTestLogin();
            break;
          case E_MESSAGEID\TEST_PAIR_LOGIN_PASSWORD:
            $Answer = new HandlerTestPairLoginPassword();
            break;
          case E_MESSAGEID\CREATE_GROUP_CODE:
            $Answer = new AHandlerCreateGroupCode();
            break;
          case E_MESSAGEID\BECOME_MEMBER:
            $Answer = new AHandlerBecomeMember();
            break;
          case E_MESSAGEID\SEND_ENTITY:
            $Answer = new AHandlerSendEntity();
            break;

        }
        //Если обработчик определен то сгенерируем ответ
        if ($Answer != null)
        {
          $result = $Answer->Handling($JOBJ);
          echo($result);
        }

      }
    }


  }

  $handler = new RequestHandler();
  $handler->handling();

?>