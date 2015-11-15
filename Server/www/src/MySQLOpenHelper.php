<?php

  include 'Common.php';

  class MySQLOpenHelper
  {
    private $link;

    //Конструкторы в PHP называются так __construct(параметры)
    public function __construct()
    {
      global $link; //Такое извращение нужно что бы видеть глобальные, по отношению к этому блоку, переменные
      $link = mysqli_connect("localhost", "root", "root");
    }

    //Создание БД версию назовем 1.0.1 ===============================================================================
    public function onCreate_1_0_1()
    {
      global $link; //Такое извращение нужно что бы видеть глобальные, по отношению к этому блоку, переменные

      //Создание БД
      $result = ($link != null);
      if (!$result)
        echo("Ошибка соединения с СУБД.<br/>");
      if ($result)
      {
        //Создаем БД
        $command = file_get_contents("..\\res\\sql\\CreateDataBase.ddl");
        $result  = $link->query($command);
        if ($result)
          echo("БД успешно создана.<br/>");
        else
          echo("Ошибка создания БД.<br/>");
      }
      if ($result)
      {
        //Выбираем базу для использования
        $result = $link->select_db(GLOBALS_VAR\NAME_DB);
        if ($result)
          echo("Используем БД " . GLOBALS_VAR\NAME_DB . ".<br/>");
        else
          echo("Не удалось выбрать БД " . GLOBALS_VAR\NAME_DB . " для использования!<br/>");
      }
      if ($result)
      {
        //Создаем таблицы
        $command = file_get_contents("..\\res\\sql\\CreateTables_1_0_1.ddl");
        $result  = $link->multi_query($command); //Команда выполняет сразу несколько запросов
        if ($result)
          echo("Таблицы для версии 1_0_1 успешно созданы<br/>");
        else
          echo("Не удалось создать таблицы для версии 1_0_1<br/>");
      }
      if (!$result)
        echo($link->error); //Выведем текст ошибки sql
    }

    //Апгрейд базы до версии 1.0.2 ===================================================================================
    public function upgrade_database_to_1_0_2()
    {
      //$result = mysql_select_db($NAME_DB, $link);

    }

    // Получить линк к БД ============================================================================================
    public function getLink()
    {
      global $link;
      return $link;
    }

  }

?>