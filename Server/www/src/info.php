﻿<?php
if($rc = mysql_connect("localhost","root","root"))
{
  echo "Hello, MySql!";
}
else
{
  echo (mysql_error());
}

phpinfo();
?>
