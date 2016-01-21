<?php

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
?>