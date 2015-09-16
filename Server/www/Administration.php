<?php
  //Администрирование БД
  $NAME_DB = "OurBudgetDB";
  $link = mysqli_connect("localhost", "root", "root");

  //Создание БД версию назовем 1.0.1
  function create_database()
  {
    global $link, $NAME_DB; //Такое извращение нужно что бы видеть глобальные переменные

    //Создание БД
    $result = $link != null;
    if(!$result)
      echo("Ошибка соединения с СУБД.<br/>");
    if($result)
    {
      //Создаем БД
      $command = file_get_contents("SQL\\CreateDataBase.sql");
      $result = $link->query($command);
      if($result)
        echo("БД успешно создана.<br/>");
      else
        echo("Ошибка создания БД.<br/>");
    }
    if($result)
    {
      //Выбираем базу для использования
      $result = $link->select_db($NAME_DB);
      if($result)
        echo("Используем БД ".$NAME_DB.".<br/>");
      else
        echo("Не удалось выбрать БД ".$NAME_DB." для использования!<br/>");
    }
    if($result)
    {
      //Создаем таблицы
      $command = file_get_contents("SQL\\CreateTables_1_0_0.sql");
      $result = $link->multi_query($command); //Команда выполняет сразу несколько запросов
      if($result)
        echo("Таблицы для версии 1_0_0 успешно созданы<br/>");
      else
        echo("Не удалось создать таблицы для версии 1_0_0<br/>");
    }
    if(!$result)
      echo($link->error ); //Выведем текст ошибки sql
  }

  //Апгрейд базы до версии 1.0.1
  function upgrade_database_to_1_0_1()
  {
    //$result = mysql_select_db($NAME_DB, $link);

  }

  create_database();

?>