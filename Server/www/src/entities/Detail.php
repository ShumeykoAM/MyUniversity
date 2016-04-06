<?php

include_once "tools/QueryCreator.php";

class Detail
{
  //$ENUM Перечисление наборов параметров для конструктора, вместо перегрузки
  public static $ENUM_CONSTRUCT_JObj  = 1; //$JObj пришедший от клиента
  public static $ENUM_CONSTRUCT_ROW   = 2; //ROW БД
  public static $ENUM_CONSTRUCT_FIELD = 3;

  public static $TABLE_NAME = "Detail"; //Имя таблицы

  public $_id_group;
  public $_id;
  public $_id_purchase;
  public $_id_type;
  public $price;
  public $for_amount_unit;
  public $for_id_unit;
  public $amount;
  public $id_unit;
  public $cost;
  public $is_delete;

  //Универсальный конструктор
  public function __construct($enum_constructor, //одно из значений $ENUM (см. выше)
                              $par1, $par2, $par3, $par4, $par5, $par6, $par7, $par8, $par9, $par10, $par11)
  {
    switch($enum_constructor)
    {
      case Detail::$ENUM_CONSTRUCT_JObj:
        $JObj = $par2;
        $this->_id_group       = $par1;
        $this->_id_purchase    = $JObj->{'_id_purchase'};
        $this->_id_type        = $JObj->{'_id_type'};
        $this->price           = $JObj->{'price'};
        $this->for_amount_unit = $JObj->{'for_amount_unit'};
        $this->for_id_unit     = $JObj->{'for_id_unit'};
        $this->amount          = $JObj->{'amount'};
        $this->id_unit         = $JObj->{'id_unit'};
        $this->cost            = $JObj->{'cost'};
        $this->is_delete       = $JObj->{'is_delete'};
        break;
      case Detail::$ENUM_CONSTRUCT_ROW:
        $row = $par1;
        $this->_id_group       = $row['_id_group_'];
        $this->_id             = $row['_id'];
        $this->_id_purchase    = $row['_id_purchase'];
        $this->_id_type        = $row['_id_type'];
        $this->price           = $row['price'];
        $this->for_amount_unit = $row['for_amount_unit'];
        $this->for_id_unit     = $row['for_id_unit'];
        $this->amount          = $row['amount'];
        $this->id_unit         = $row['id_unit'];
        $this->cost            = $row['cost'];
        $this->is_delete       = $row['is_delete'];
        break;
      case Detail::$ENUM_CONSTRUCT_FIELD:
        $this->_id_group       = $par1;
        $this->_id             = $par2;
        $this->_id_purchase    = $par3;
        $this->_id_type        = $par4;
        $this->price           = $par5;
        $this->for_amount_unit = $par6;
        $this->for_id_unit     = $par7;
        $this->amount          = $par8;
        $this->id_unit         = $par9;
        $this->cost            = $par10;
        $this->is_delete       = $par11;
        break;
      default:
        throw new Exception('Конструктор не определен');
    }
  }

  public function insert($link)
  {
    $values = array();
    $values["_id_group_"]      = $this->_id_group;
    $values["_id"]             = $this->_id;
    $values["_id_purchase"]    = $this->_id_purchase;
    $values["_id_type"]        = $this->_id_type;
    if(isset($this->price))
      $values["price"]         = $this->price;
    $values["for_amount_unit"] = $this->for_amount_unit;
    $values["for_id_unit"]     = $this->for_id_unit;
    $values["amount"]          = $this->amount;
    $values["id_unit"]         = $this->id_unit;
    if(isset($this->cost))
      $values["cost"]          = $this->cost;
    $values["is_delete"]       = $this->is_delete;
    $command = "INSERT INTO detail ".QueryCreator::construct_fields_values($link, $values).";";
    $result = $link->query($command);
    return $result;
  }

  public function copy()
  {
    $result = new Detail(Detail::$ENUM_CONSTRUCT_FIELD, $this->_id_group, $this->_id, $this->_id_purchase,
      $this->_id_type, $this->price, $this->for_amount_unit, $this->for_id_unit, $this->amount, $this->id_unit,
      $this->cost, $this->is_delete);
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
    if($this->_id_purchase != $new_rec->_id_purchase)
      $values["_id_purchase"] = $new_rec->_id_purchase;
    if($this->_id_type != $new_rec->_id_type)
      $values["_id_type"] = $new_rec->_id_type;
    if($this->price != $new_rec->price)
      $values["price"] = $new_rec->price;
    if($this->for_amount_unit != $new_rec->for_amount_unit)
      $values["for_amount_unit"] = $new_rec->for_amount_unit;
    if($this->for_id_unit != $new_rec->for_id_unit)
      $values["for_id_unit"] = $new_rec->for_id_unit;
    if($this->amount != $new_rec->amount)
      $values["amount"] = $new_rec->amount;
    if($this->id_unit != $new_rec->id_unit)
      $values["id_unit"] = $new_rec->id_unit;
    if($this->cost != $new_rec->cost)
      $values["cost"] = $new_rec->cost;
    if($this->is_delete != $new_rec->is_delete)
      $values["is_delete"] = $new_rec->is_delete;
    if(count($values) != 0)
    {
      $query = "UPDATE detail ".QueryCreator::construct_SET($link, $values)." WHERE detail._id_group_ = ".
        $this->_id_group." AND Detail._id = ".$this->_id.";";
      $result = $link->query($query);
    }
    return $result;
  }

  //Возвращает последний _id записи для указанной группы, 0 - если нет записей для данной группы
  public static function getLast_id($_id_group, $link)
  {
    $result = 0;
    $values = array($_id_group); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\DETAIL_LAST_ID, $values );
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

  public static function getDetailFrom_id($_id_group, $_id, $link)
  {
    $result = null;
    $values = array($_id_group, $_id); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\DETAIL_FROM_ID, $values );
    $q_result = $link->query($request);
    if($q_result && $q_result != null && $q_result->num_rows != 0)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = new Detail(Detail::$ENUM_CONSTRUCT_ROW, $row);
    }
    return $result;
  }

  public static function getDetailFromPurchaseAndType($_id_group, $_id_purchase, $_id_type, $link)
  {
    $result = null;
    $values = array($_id_group, $_id_purchase, $_id_type); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\DETAIL_FROM_ID_P_T, $values );
    $q_result = $link->query($request);
    if($q_result && $q_result != null && $q_result->num_rows != 0)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = new Detail(Detail::$ENUM_CONSTRUCT_ROW, $row);
    }
    return $result;
  }

  public function getJObj()
  {
    $arr = array(
      '_id'            =>$this->_id            ,
      '_id_purchase'   =>$this->_id_purchase   ,
      '_id_type'       =>$this->_id_type       ,
      'price'          =>$this->price          ,
      'for_amount_unit'=>$this->for_amount_unit,
      'for_id_unit'    =>$this->for_id_unit    ,
      'amount'         =>$this->amount         ,
      'id_unit'        =>$this->id_unit        ,
      'cost'           =>$this->cost           ,
      'is_delete'      =>$this->is_delete      ,
      );
    $result = json_decode(json_encode($arr));
    return $result;
  }
}

?>