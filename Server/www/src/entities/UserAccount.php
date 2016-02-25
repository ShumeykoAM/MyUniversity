<?php

  include_once "tools/QueryCreator.php";
  //В PHP к сожалению нет перегрузки
  class UserAccount
  {
    //$ENUM Перечисление наборов параметров для конструктора, вместо перегрузки
    public static $ENUM_CONSTRUCT_ROW = 1; //$row строка из результата запроса SELECT
    public static $TABLE_NAME = "user_account"; //Имя таблицы

    public $_id_group;     //
    public $_id;           //
    public $login;         //
    public $hash_password; //

    //Универсальный конструктор
    public function __construct($enum_constructor, //одно из значений $ENUM (см. выше)
                                $par1) // $row строка из результата запроса SELECT
    {
      switch($enum_constructor)
      {
        case $this->ENUM_CONSTRUCT_ROW:
          $row = $par1;
          $this->_id_group     = $row['_id_group'];
          $this->_id           = $row['_id'];
          $this->login         = $row['login'];
          $this->hash_password = $row['hash_password'];
          break;
        default:
          throw new Exception('Конструктор не определен');
      }
    }

    public static function getUserAccountFromID($link, $_id)//:UserAccount
    {
      global $ENUM_CONSTRUCT_ROW;
      $result = null;
      $values = array($_id); //Параметры для запроса
      $request = QueryCreator::getQuery( $link, EQ\USER_ACCOUNT_FROM_ID, $values );
      $q_result = $link->query($request);
      if($q_result && $q_result->num_rows != 0)
      {
        $q_result->data_seek(0);
        $row = $q_result->fetch_assoc();
        $result = new UserAccount($ENUM_CONSTRUCT_ROW, $row);
      }
      return $result;
    }


  }
?>