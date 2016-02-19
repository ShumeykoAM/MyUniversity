<?php

  include 'Common.php';

  class MySQLOpenHelper
  {
    private $it_is_debug_mode;
    private $link;
    private $link_for_create;

    //Конструкторы в PHP называются так __construct(параметры)
    public function __construct()
    {
      global $link, $link_for_create, $it_is_debug_mode; //Такое извращение нужно что бы видеть глобальные, по отношению к этому блоку, переменные
$it_is_debug_mode = true;
//$it_is_debug_mode = false;
      if(!$it_is_debug_mode)
        $link_for_create = $link = mysqli_connect("localhost", GLOBALS_VAR\NAME_DB, "diplom_394");
      else
        $link_for_create = $link = mysqli_connect("localhost", "root", "root");
      $result = ($link != null);
      if($result) //Выбираем базу для использования если она уже создана
        $result = $link->select_db(GLOBALS_VAR\NAME_DB);
      else
        echo(mysqli_error()."<br/>");
      //Каждый раз нужно указывать кодировку в которой работает база
      $result = $result and $link->query('set names utf8');
      $result = $result and $link->query('set names utf8');
      $result = $result and $link->query('set character set utf8');
      $result = $result and $link->query('set character_set_client=utf8');
      $result = $result and $link->query('set character_set_results=utf8');
      $result = $result and $link->query('set character_set_connection=utf8');
      $result = $result and $link->query('set character_set_database=utf8');
      $result = $result and $link->query('set character_set_server=utf8');
      if(!$result)
      {
        $link = null;
      }

    }

    //Создание БД версию назовем 1.0.1 ===============================================================================
    public function onCreate_1_0_1()
    {
      global $link_for_create, $it_is_debug_mode; //Такое извращение нужно что бы видеть глобальные, по отношению к этому блоку, переменные

      //Создание БД
      $result = ($link_for_create != null);
      if (!$result)
        echo("Ошибка соединения с СУБД.<br/>");
      
      if ($result && $it_is_debug_mode)
      {
      //Создаем БД
        $command = file_get_contents("res\\sql\\CreateDataBase.ddl");
        $result  = $link_for_create->query($command);
        if ($result)
          echo("БД успешно создана.<br/>");
        else
          echo("Ошибка создания БД.<br/>");
      }
      if ($result)
      {
        //Выбираем базу для использования
        $result = $link_for_create->select_db(GLOBALS_VAR\NAME_DB);
        if ($result)
          echo("Используем БД " . GLOBALS_VAR\NAME_DB . ".<br/>");
        else
          echo("Не удалось выбрать БД " . GLOBALS_VAR\NAME_DB . " для использования!<br/>");
      }
      if ($result)
      {
        //Создаем таблицы
        $ddl_create_table = "res/sql/CreateTables_1_0_1.ddl";
        if(file_exists ($ddl_create_table))
          echo("Файл CreateTables_1_0_1.ddl существует<br/>");
        else
          echo("Отсутствует файл CreateTables_1_0_1.ddl<br/>");

        $command = file_get_contents($ddl_create_table);
        $result  = $link_for_create->multi_query($command); //Команда выполняет сразу несколько запросов
        if ($result)
          echo("Таблицы для версии 1_0_1 успешно созданы<br/>");
        else
        {
          echo("Не удалось создать таблицы для версии 1_0_1<br/>");
          echo(mysqli_error()."<br/>");
        }
      }
      if (!$result)
        echo($link_for_create->error); //Выведем текст ошибки sql
    }

    //Апгрейд базы до версии 1.0.2 ===================================================================================
    public function upgrade_database_to_1_0_2()
    {
      global $link; //Такое извращение нужно что бы видеть глобальные, по отношению к этому блоку, переменные


    }

    // Получить линк к БД ============================================================================================
    public function getLink()
    {
      global $link;
      return $link;
    }

  }

?>