<?php

  include_once "tools/QueryCreator.php";
  //В PHP к сожалению нет перегрузки
  class UserAccount
  {
    //$ENUM Перечисление наборов параметров для конструктора, вместо перегрузки
    public static $ENUM_CONSTRUCT_ROW = 1; //$row строка из результата запроса SELECT
    public static $ENUM_CONSTRUCT_FIELD = 2; //$code_for_co_user и $timestamp_code
    public static $TABLE_NAME = "user_account"; //Имя таблицы

    public $_id;           //
    public $_id_group;     //
    public $login;         //
    public $hash_password; //

    //Универсальный конструктор
    public function __construct($enum_constructor, //одно из значений $ENUM (см. выше)
                                $par1, $par2, $par3)
    {
      switch($enum_constructor)
      {
        case UserAccount::$ENUM_CONSTRUCT_ROW:
          $row = $par1;
          $this->_id_group     = $row['_id_group'];
          $this->_id           = $row['_id'];
          $this->login         = $row['login'];
          $this->hash_password = $row['hash_password'];
          break;
        case UserAccount::$ENUM_CONSTRUCT_FIELD:
          $this->_id_group     = $par1;
          $this->login         = $par2;
          $this->hash_password = $par3;
          break;
        default:
          throw new Exception('Конструктор не определен');
      }
    }

    //Обновит отличающиеся поля
    public function update($link, $new_user_account /*:UserAccount*/) //:bool
    {
      $result = false;
      if($this->_id == null || $new_user_account->_id == null || $this->_id != $new_user_account->_id )
        throw new Exception("Неизвестный _id");
      $values = array();
      if($this->_id_group != $new_user_account->_id_group)
        $values["_id_group"] = $new_user_account->_id_group;
      if($this->login != $new_user_account->login)
        $values["login"] = $new_user_account->login;
      if($this->hash_password != $new_user_account->hash_password)
        $values["hash_password"] = $new_user_account->hash_password;
      if(count($values) != 0)
      {
        $query = "UPDATE user_account ".QueryCreator::construct_SET($link, $values)." WHERE user_account._id = ".
          $this->_id.";";
        $result = $link->query($query);
      }
      return $result;
    }

    public function copy() //:UserAccount
    {
      $result = new UserAccount(UserGroup::$ENUM_CONSTRUCT_FIELD, $this->_id_group, $this->login, $this->hash_password);
      $result->_id = $this->_id;
      return $result;
    }

    //Получить учетную запись по _id
    public static function getUserAccountFromID($link, $_id)//:UserAccount
    {
      $result = null;
      $values = array($_id); //Параметры для запроса
      $request = QueryCreator::getQuery( $link, EQ\USER_ACCOUNT_FROM_ID, $values );
      $q_result = $link->query($request);
      if($q_result && $q_result->num_rows != 0)
      {
        $q_result->data_seek(0);
        $row = $q_result->fetch_assoc();
        $result = new UserAccount(UserAccount::$ENUM_CONSTRUCT_ROW, $row);
      }
      return $result;
    }


  }
?>