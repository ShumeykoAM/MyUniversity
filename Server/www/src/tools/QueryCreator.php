<?php

  include_once 'Common.php';
  class QueryCreator
  {

    //Формирует строку SET (для запросов UPDATE)
    public static function construct_SET($link, $values) //$values это array ключ-поле, значение(null)-значение поля
    {
      $set = "SET";
      $separator = " ";
      while($key_value = each($values)) //http://php.net/manual/ru/function.next.php
      {
        $set .= $separator;
        $set .= $key_value["key"]."=";
        if($key_value["value"] == null)
          $set .= "null";
        else
          $set .= $link->real_escape_string($key_value["value"]);
        $separator = ", ";
      }
      return $set;
    }

    public static function getQuery($link, $eq /*:EQ*/, $values)
    {
      $query_value = null;
      //Что бы постоянно не парсить по новой файл с запросами, хранить массив запросов будем в $_SESSION
      if(GLOBALS_VAR\it_is_debug_mode || !array_key_exists(GLOBALS_VAR\EQ_ARRAY, $_SESSION))
      //Еще не парсили для текущей сессии, распарсим
      {
        $handle = @fopen("res/sql/Queries.sql", "r");
        if($handle)
        {
          $result = array(); $i=0;
          $query = "";
          while( ($sline = fgets($handle, 4096)) !== false)
          {
            $sline = trim($sline);
            if(strlen($sline) == 0)
            {
              if(strlen($query) != 0)
                $result[$i++] = $query;
              $query = "";
            }
            else
            {
              //Удалим все коментарии
              $index = strpos($sline, "--");
              if($index === 0 || $index > 0)
                $sline = substr($sline, 0, $index);
              //Удалим все лишние пробелы
              $sline = trim(preg_replace("/ {2,}/"," ",$sline));
              if(strlen($query) != 0)
                $query .= " ";
              $query .= $sline;
            }
          }
          if($query != "")
            $result[$i++] = $query;
          fclose($handle);
          $_SESSION[GLOBALS_VAR\EQ_ARRAY] = $result;
        }
      }
      //Вернем интересующий нас запрос
      $query_value = $_SESSION[GLOBALS_VAR\EQ_ARRAY][$eq];
      if($query_value != null)
      {
        //Вставим вместо ? значения из $values
        $count = 1;
        foreach($values as $value)
        {
          $index = strpos($query_value, "?");
          if ($index === 0 || $index > 0)
          {
            $value = $link->real_escape_string($value);
            //Заменим первый попавшийся ? на очередное значение из мессива
            $query_value = preg_replace('~' . preg_quote("?") . '~', $value, $query_value, 1);
          }
          else
            break;
        }
      }
      return $query_value;
    }



  }

?>