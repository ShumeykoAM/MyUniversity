<!DOCTYPE html>
<html>
 <head>
  <meta charset="utf-8">
  <title>download</title>
 </head>
 <body>
  <!--<p><a href="production/OurBudget.apk">Открыть файл в браузере</a> -->
  <p><a href="production/OurBudget.apk" download>Скачать программу "Наш Бюджет"</a>
</body>
</html>

<?php
  include_once 'Common.php';

  if(!GLOBALS_VAR\it_is_debug_mode)
    $rc = mysqli_connect("localhost", GLOBALS_VAR\NAME_DB, "diplom_394");
  else
    $rc = mysqli_connect("localhost", "root", "root");

  //Удобно проверять работоспособность конфигурации
  if($rc != null)
  {
    echo "<p>Hello, MySql!";
  }
  else
  {
    echo (mysql_error());
  }

  phpinfo();
?>
