<?php

include_once "tools/QueryCreator.php";

class Type
{
  //$ENUM Перечисление наборов параметров для конструктора, вместо перегрузки
  public static $ENUM_CONSTRUCT_JObj  = 1; //$JObj пришедший от клиента
  public static $ENUM_CONSTRUCT_ROW   = 2; //ROW БД
  public static $ENUM_CONSTRUCT_FIELD = 3;

  public static $TABLE_NAME = "user_account"; //Имя таблицы

  public $_id_group;
  public $_id;
  public $name;
  public $id_unit;
  public $is_delete;

  //Универсальный конструктор
  public function __construct($enum_constructor, //одно из значений $ENUM (см. выше)
                              $par1, $par2, $par3, $par4, $par5)
  {
    switch($enum_constructor)
    {
      case Type::$ENUM_CONSTRUCT_JObj:
        $JObj = $par2;
        $this->_id_group = $par1;
        $this->name      = $JObj->{'name'};
        $this->id_unit   = $JObj->{'id_unit'};
        $this->is_delete = $JObj->{'is_delete'};
        break;
      case Type::$ENUM_CONSTRUCT_ROW:
        $row = $par1;
        $this->_id       = $row['_id'];
        $this->name      = $row['name'];
        $this->id_unit   = $row['id_unit'];
        $this->_id_group = $row['_id_group'];
        $this->is_delete = $row['is_delete'];
        break;
      case Type::$ENUM_CONSTRUCT_FIELD:
        $this->_id       = $par1;
        $this->name      = $par2;
        $this->id_unit   = $par3;
        $this->_id_group = $par4;
        $this->is_delete = $par5;
        break;
      default:
        throw new Exception('Конструктор не определен');
    }
  }

  public function insert($link)
  {
    $command = "INSERT INTO type (_id, name, id_unit, _id_group, is_delete) VALUES (".$this->_id.
      ", '".$this->name."', ".$this->id_unit.", ".$this->_id_group.", ".($this->is_delete ? 1 : 0).");";
    $result = $link->query($command);
    return $result;
  }

  public function copy()
  {
    $result = new Type(Type::$ENUM_CONSTRUCT_FIELD, $this->_id, $this->name, $this->id_unit,
      $this->_id_group, $this->is_delete);
    return $result;
  }

  //Обновит отличающиеся поля
  public function update($link, $new_rec /*:UserAccount*/) //:bool
  {
    $result = true;
    if($this->_id == null || $new_rec->_id == null || $this->_id != $new_rec->_id ||
       $this->_id_group == null || $new_rec->_id_group == null || $this->_id_group != $new_rec->_id_group)
      throw new Exception("Неизвестный _id");
    $values = array();
    if($this->name != $new_rec->name)
      $values["name"] = $new_rec->name;
    if($this->id_unit != $new_rec->id_unit)
      $values["id_unit"] = $new_rec->id_unit;
    if($this->is_delete != $new_rec->is_delete)
      $values["is_delete"] = $new_rec->is_delete;
    if(count($values) != 0)
    {
      $query = "UPDATE type ".QueryCreator::construct_SET($link, $values)." WHERE type._id_group = ".
        $this->_id_group." AND type._id = ".$this->_id.";";
      $result = $link->query($query);
    }
    return $result;
  }

  //Возвращает последний _id записи для указанной группы, 0 - если нет записей для данной группы
  public static function getLast_id($_id_group, $link)
  {
    $result = 0;
    $values = array($_id_group); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\TYPE_LAST_ID, $values );
    $q_result = $link->query($request);
    if($q_result)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = $row['last_id'];
      if(!isset($result))
        $result = 0;
    }
    return $result;
  }

  public static function getTypeFromName($_id_group, $name, $link)
  {
    $result = null;
    $values = array($_id_group, $name); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\TYPE_FROM_NAME, $values );
    $q_result = $link->query($request);
    if($q_result && $q_result->num_rows != null && $q_result->num_rows != 0)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = new Type(Type::$ENUM_CONSTRUCT_ROW, $row);
    }
    return $result;
  }

  public static function getTypeFrom_id($_id_group, $_id, $link)
  {
    $result = null;
    $values = array($_id_group, $_id); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\TYPE_FROM_ID, $values );
    $q_result = $link->query($request);
    if($q_result && $q_result != null && $q_result->num_rows != 0)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = new Type(Type::$ENUM_CONSTRUCT_ROW, $row);
    }
    return $result;
  }

  public function getJObj()
  {
    $arr = array('_id'=>$this->_id, 'name'=>$this->name,
      'id_unit'=>$this->id_unit, 'is_delete'=>$this->is_delete);
    $result = json_decode(json_encode($arr));
    return $result;
  }
}

?>