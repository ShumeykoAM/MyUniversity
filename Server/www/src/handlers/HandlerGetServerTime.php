<?php

  include_once 'Common.php';

  //Возвращаем время сервера без корректировки на временную зону
  class HandlerGetServerTime
    implements I_Handler
  {
    public function Handling($JOBJ)
    {
      $time = round(microtime(true) * 1000);
      $arr = array( 'MESSAGE' => E_MESSAGEID\GET_SERVER_TIME, 'SERVER_TIME' => $time );
      $enc = json_encode($arr);
      return $enc;
    }
  }
?>