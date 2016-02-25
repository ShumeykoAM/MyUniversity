<?php
  include_once "tools/QueryCreator.php";

  //В PHP к сожалению нет перегрузки
  class UserGroup
  {
    //$ENUM Перечисление наборов параметров для конструктора, вместо перегрузки
    public static $ENUM_CONSTRUCT_ROW   = 1; //$row строка из результата запроса SELECT
    public static $ENUM_CONSTRUCT_FIELD = 2; //$code_for_co_user и $timestamp_code
    public static $TABLE_NAME = "user_group"; //Имя таблицы

    public $_id;              //
    public $code_for_co_user; //
    public $timestamp_code;   //

    //Универсальный конструктор
    public function __construct($enum_constructor,   //одно из значений $ENUM (см. выше)
                                $par1, $par2) //
    {
      switch($enum_constructor)
      {
        case UserGroup::$ENUM_CONSTRUCT_ROW:
          $row = $par1;
          $this->_id              = $row['_id'];
          $this->code_for_co_user = $row['code_for_co_user'];
          $this->timestamp_code   = $row['timestamp_code'];
          break;
        case UserGroup::$ENUM_CONSTRUCT_FIELD:
          $this->code_for_co_user = $par1;
          $this->timestamp_code   = $par2;
          break;
        default:
          throw new Exception('Конструктор не определен');
      }
    }

    //Обновит отличающиеся поля
    public function update($link, $new_user_group /*:UserGroup*/) //:bool
    {
      $result = false;
      if($this->_id == null || $new_user_group->_id == null || $this->_id != $new_user_group->_id )
        throw new Exception("Неизвестный _id");
      $values = array();
      if($this->code_for_co_user != $new_user_group->code_for_co_user)
        $values["code_for_co_user"] = $new_user_group->code_for_co_user;
      if($this->timestamp_code != $new_user_group->timestamp_code)
        $values["timestamp_code"] = $new_user_group->timestamp_code;
      if(count($values) != 0)
      {
        $query = "UPDATE user_group ".QueryCreator::construct_SET($link, $values)." WHERE user_group._id = ".
          $this->_id.";";
        $result = $link->query($query);
      }
      return $result;
    }

    public function copy() //:UserGroup
    {
      $result = new UserGroup(UserGroup::$ENUM_CONSTRUCT_FIELD, $this->code_for_co_user, $this->timestamp_code);
      $result->_id = $this->_id;
      return $result;
    }

    //Получить группу пользователя по её _id
    public static function getUserGroupFromID($link, $_id)//:UserGroup
    {
      global $ENUM_CONSTRUCT_ROW;
      $result = null;
      $values = array($_id); //Параметры для запроса
      $request = QueryCreator::getQuery( $link, EQ\USER_GROUP_FROM_ID, $values );
      $q_result = $link->query($request);
      if($q_result && $q_result->num_rows != 0)
      {
        $q_result->data_seek(0);
        $row = $q_result->fetch_assoc();
        $result = new UserGroup(UserGroup::$ENUM_CONSTRUCT_ROW, $row);
      }
      return $result;
    }
  }
?>