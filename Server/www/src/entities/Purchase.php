<?php

include_once "tools/QueryCreator.php";

class Purchase
{
  //$ENUM Перечисление наборов параметров для конструктора, вместо перегрузки
  public static $ENUM_CONSTRUCT_JObj  = 1; //$JObj пришедший от клиента
  public static $ENUM_CONSTRUCT_ROW   = 2; //ROW БД
  public static $ENUM_CONSTRUCT_FIELD = 3;

  public static $TABLE_NAME = "purchase"; //Имя таблицы

  public $_id_group;
  public $_id;
  public $date_time;
  public $state;
  public $is_delete;

  //Универсальный конструктор
  public function __construct($enum_constructor, //одно из значений $ENUM (см. выше)
                              $par1, $par2, $par3, $par4, $par5)
  {
    switch($enum_constructor)
    {
      case Purchase::$ENUM_CONSTRUCT_JObj:
        $JObj = $par2;
        $this->_id_group = $par1;
        $this->date_time  = $JObj->{'date_time'};
        $this->state     = $JObj->{'state'};
        $this->is_delete = $JObj->{'is_delete'};
        break;
      case Purchase::$ENUM_CONSTRUCT_ROW:
        $row = $par1;
        $this->_id       = $row['_id'];
        $this->date_time  = $row['date_time'];
        $this->state     = $row['state'];
        $this->_id_group = $row['_id_group'];
        $this->is_delete = $row['is_delete'];
        break;
      case Purchase::$ENUM_CONSTRUCT_FIELD:
        $this->_id       = $par1;
        $this->date_time  = $par2;
        $this->state     = $par3;
        $this->_id_group = $par4;
        $this->is_delete = $par5;
        break;
      default:
        throw new Exception('Конструктор не определен');
    }
  }

  public function insert($link)
  {
    $command = "INSERT INTO purchase (_id, date_time, state, _id_group, is_delete) VALUES (".$this->_id.
      ", '".$this->date_time."', ".$this->state.", ".$this->_id_group.", ".($this->is_delete ? 1 : 0).");";
    $result = $link->query($command);
    return $result;
  }

  public function copy()
  {
    $result = new Purchase(Purchase::$ENUM_CONSTRUCT_FIELD, $this->_id, $this->date_time, $this->state,
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
    if($this->date_time != $new_rec->date_time)
      $values["date_time"] = $new_rec->date_time;
    if($this->state != $new_rec->state)
      $values["state"] = $new_rec->state;
    if($this->is_delete != $new_rec->is_delete)
      $values["is_delete"] = $new_rec->is_delete;
    if(count($values) != 0)
    {
      $query = "UPDATE purchase ".QueryCreator::construct_SET($link, $values)." WHERE purchase._id_group = ".
        $this->_id_group." AND purchase._id = ".$this->_id.";";
      $result = $link->query($query);
    }
    return $result;
  }

  //Возвращает последний _id записи для указанной группы, 0 - если нет записей для данной группы
  public static function getLast_id($_id_group, $link)
  {
    $result = 0;
    $values = array($_id_group); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\PURCHASE_LAST_ID, $values );
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

  public static function getPurchaseFrom_id($_id_group, $_id, $link)
  {
    $result = null;
    $values = array($_id_group, $_id); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\PURCHASE_FROM_ID, $values );
    $q_result = $link->query($request);
    if($q_result && $q_result != null && $q_result->num_rows != 0)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = new Purchase(Purchase::$ENUM_CONSTRUCT_ROW, $row);
    }
    return $result;
  }

  public function getJObj()
  {
    $arr = array('_id'=>$this->_id, 'date_time'=>$this->date_time,
      'state'=>$this->state, 'is_delete'=>$this->is_delete);
    $result = json_decode(json_encode($arr));
    return $result;
  }
}

?>