<?php

  //Запускаем сессию(нужно делать на каждой странице)
  //С помощью нее нам доступен массив $_SESSION на всех страницах
  session_start();

  //Интерфейс для реализации метода который будет работать в транзакции
  interface I_Transaction
  {
    public function trnFunc($params); //:boolean
  }

  //Класс пускальщик транзакции
  class SQLTransaction
  {
    private $link;
    private $trn_func_execute = null; //:I_Transaction

    function __construct($_link, $_trn_func_execute/*:I_Transaction*/)
    {
      global $link, $trn_func_execute; //Такое извращение нужно что бы видеть глобальные переменные
      $link = $_link;
      $trn_func_execute = $_trn_func_execute;
    }
    //Функция запускает транзакцию и в зависимости что ей вернет
    //  реализовавший I_Transaction завершит или прервет транзакцию
    public function runTransaction($params) //:boolean
    {
      global $link, $trn_func_execute;
      $result = ($link != null and $trn_func_execute != null);
      if($result)
      {
        //Запускаем транзакцию
        $command = "START TRANSACTION";
        $result = $link->query($command);
      }
      if($result)
      {
        $result = ($trn_func_execute->trnFunc($params) == true);
      }
      if($result)
      {
        //Завершаем транзакцию
        $command = "COMMIT";
        $result = $link->query($command);
      }
      else
      {
        //Откатываем транзакцию
        $command = "ROLLBACK";
        $link->query($command);
      }
      return $result;
    }
  }
?>