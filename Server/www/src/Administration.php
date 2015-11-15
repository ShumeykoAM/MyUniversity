<?php
  //Администрирование БД

  include 'MySQLOpenHelper.php';

  //
  $DB_Helper = new MySQLOpenHelper();

  //Создаем БД первой версии
  $DB_Helper->onCreate_1_0_1();
  //Апгрейдим БД
  //$DB_Helper->upgrade_database_to_1_0_2();

?>