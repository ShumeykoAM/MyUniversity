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
//Удобно проверять работоспособность конфигурации
if($rc = mysql_connect("localhost","root","root"))
{
  echo "<p>Hello, MySql!";
}
else
{
  echo (mysql_error());
}

phpinfo();
?>
