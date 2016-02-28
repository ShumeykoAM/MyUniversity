<?php

  include_once 'Common.php';


//Нужно наследоваться в том случае если должна быть авторизауция пользователя при обработке запроса
  abstract class ABaseHandler
    implements I_Handler
  {
    public function Handling($JOBJ)
    {
      $result = "";
      if(array_key_exists(GLOBALS_VAR\ID, $_SESSION))
      {
        $result = $this->AHandling($JOBJ);
      }
      else
      {
        $arr = array( 'MESSAGE' => E_MESSAGEID\NOT_IDENTIFY );
        $result = json_encode($arr);
      }
      return $result;
    }
    //Здесь в наследнике нужно реализовать обработку запроса
    abstract protected function AHandling($JOBJ);
  }

?>