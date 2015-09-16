<?php
  //Обработчик запросов клиентов

  //Запускаем сессию(нужно делать на каждой странице)
  //С помощью нее нам доступен массив $_SESSION на всех страницах
  session_start();

  include 'E_MESSAGEID.php';
  include 'Answers.php';

  $result = array_key_exists("Request" ,$_POST);
  $JOBJ = null;
  $ID = null;

  if( $result )
    $result = ($JOBJ = json_decode($_POST["Request"])) != null; //Декодируем строку в json
  if( $result )
    $result = ($ID = $JOBJ->{'MESSAGE'}) != null; //Получаем ID запроса
  if( $result )
  {
    $Answer = null;
    switch($ID) //Определим соответствующий обработчик
    {
      case E_MESSAGEID\TEST_CONNECT_SERVER:
        $Answer = new AnswerTestConnectServer();
        break;
      case E_MESSAGEID\CREATE_NEW_PROFILE:
        $Answer = NEW AnswerCreateNewProfile();


    }
    //Если обработчик определен то сгенерируем ответ
    if($Answer != null)
      echo( $Answer->Ansver($JOBJ) );

  }

?>