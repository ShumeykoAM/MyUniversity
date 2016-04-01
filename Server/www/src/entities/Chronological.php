<?php


class Chronological
{
  //$ENUM Перечисление наборов параметров для конструктора, вместо перегрузки
  public static $ENUM_CONSTRUCT_FIELD = 1; //по полям
  public static $ENUM_CONSTRUCT_ROW   = 2; //ROW БД

  public $_id_group;
  public $table_db;
  public $_id_record;
  public $timestamp;
  public $operation;

  private $old_rec;

  public function __construct($enum_constructor, //одно из значений $ENUM (см. выше)
                              $par1, $par2, $par3, $par4, $par5)
  {
    switch($enum_constructor)
    {
      case Chronological::$ENUM_CONSTRUCT_FIELD:
        $this->_id_group  = $par1;
        $this->table_db   = $par2;
        $this->_id_record = $par3;
        $this->timestamp  = $par4;
        $this->operation  = $par5;
        break;
      case Chronological::$ENUM_CONSTRUCT_ROW:
        $row = $par1;
        $this->_id_group  = $row['_id_group'];
        $this->table_db   = $row['table_db'];
        $this->_id_record = $row['_id_record'];
        $this->timestamp  = $row['timestamp'];
        $this->operation  = $row['operation'];
        $this->old_rec = $this->copy();
        break;
      default:
        throw new Exception('Конструктор не определен');
    }
  }

  public static function getFromFirstKey($_id_group, $table_db, $_id_record, $link) /*out Chronological*/
  {
    $result = null;
    $values = array($_id_group, $table_db, $_id_record); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\CHRONOLOGICAL_FROM_F_KEY, $values );
    $q_result = $link->query($request);
    if($q_result && $q_result->num_rows != 0)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = new Chronological(Chronological::$ENUM_CONSTRUCT_ROW, $row);
    }
    return $result;
  }
  //Первая хронология после указанной временной метки
  public static function getAfterTimeStamp($_id_group, $timestamp, $link) /*out Chronological*/
  {
    $result = null;
    $values = array($_id_group, $timestamp); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\CHRONO_AFTER_TIMESTAMP, $values );
    $q_result = $link->query($request);
    if($q_result && $q_result->num_rows != 0)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = new Chronological(Chronological::$ENUM_CONSTRUCT_ROW, $row);
    }
    return $result;
  }
  //Запись в хронологии со следующим id записи
  public static function getNextIdRecord($_id_group, $table_db, $_id_record, $link) /*out Chronological*/
  {
    $result = null;
    $values = array($_id_group, $table_db, $_id_record); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\CHRONO_NEXT_ID, $values );
    $q_result = $link->query($request);
    if($q_result && $q_result->num_rows != 0)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = new Chronological(Chronological::$ENUM_CONSTRUCT_ROW, $row);
    }
    return $result;
  }
  public function copy()
  {
    $result = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $this->_id_group, $this->table_db,
      $this->_id_record, $this->timestamp, $this->operation);
    return $result;
  }
  public function insert($link)
  {
    $command = "INSERT INTO chronological(_id_group, table_db, _id_record, timestamp, operation) ".
               "VALUES('".$this->_id_group."', '".$this->table_db."', '".$this->_id_record."', '".
                $this->timestamp."', '".$this->operation."');";
    $result = $link->query($command);
    return $result;
  }

  public function update($link)
  {
    $result = true;
    if($this->old_rec == null ||
       $this->old_rec->_id_group == null || $this->old_rec->table_db == null || $this->old_rec->_id_record == null ||
       $this->_id_group == null          || $this->table_db == null          || $this->_id_record == null          ||
       $this->old_rec->_id_group != $this->_id_group ||
       $this->old_rec->table_db  != $this->table_db  ||
       $this->old_rec->_id_record != $this->_id_record)
      throw new Exception("Неизвестный _id");
    $values = array();
    if($this->timestamp != $this->old_rec->timestamp)
      $values["timestamp"] = $this->timestamp;
    if($this->operation != $this->old_rec->operation)
      $values["operation"] = $this->operation;
    if(count($values) != 0)
    {
      $query = "UPDATE chronological ".QueryCreator::construct_SET($link, $values).
               " WHERE chronological._id_group = ".$this->_id_group.
               " AND chronological.table_db = ".$this->table_db.
               " AND chronological._id_record = ".$this->_id_record.";";
      $result = $link->query($query);
    }
    return $result;
  }


}

?>