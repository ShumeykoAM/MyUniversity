<?php


class Chronological
{
  //$ENUM Перечисление наборов параметров для конструктора, вместо перегрузки
  public static $ENUM_CONSTRUCT_FIELD = 1; //по полям
  public static $ENUM_CONSTRUCT_ROW   = 2; //ROW БД

  public $_id_group;
  public $revision;
  public $table_db;
  public $_id_record;
  public $timestamp;

  private $old_rec;

  public function __construct($enum_constructor, //одно из значений $ENUM (см. выше)
                              $par1, $par2, $par3, $par4, $par5)
  {
    switch($enum_constructor)
    {
      case Chronological::$ENUM_CONSTRUCT_FIELD:
        $this->_id_group   = $par1;
        $this->revision    = $par2;
        $this->table_db    = $par3;
        $this->_id_record  = $par4;
        $this->timestamp   = $par5;
        break;
      case Chronological::$ENUM_CONSTRUCT_ROW:
        $row = $par1;
        $this->_id_group   = $row['_id_group'];
        $this->revision    = $row['revision'];
        $this->table_db    = $row['table_db'];
        $this->_id_record  = $row['_id_record'];
        $this->timestamp   = $row['timestamp'];
        $this->old_rec = $this->copy();
        break;
      default:
        throw new Exception('Конструктор не определен');
    }
  }

  public static function getFromFirstKey($_id_group, $revision, $link) /*out Chronological*/
  {
    $result = null;
    $values = array($_id_group, $revision); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\CHRONOLOGICAL_FROM_F_KEY, $values );
    $q_result = $link->query($request);
    if($q_result && $q_result != null && $q_result->num_rows != 0)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = new Chronological(Chronological::$ENUM_CONSTRUCT_ROW, $row);
    }
    return $result;
  }

  public static function getLastRevision($_id_group, $link) /*out revision*/
  {
    $result = 0;
    $values = array($_id_group); //Параметры для запроса
    $request = QueryCreator::getQuery( $link, EQ\CHRONO_LAST_REVISION, $values );
    $q_result = $link->query($request);
    if($q_result && $q_result != null && $q_result->num_rows != 0)
    {
      $q_result->data_seek(0);
      $row = $q_result->fetch_assoc();
      $result = $row['last_revision'];
    }
    return $result;
  }

  public function copy()
  {
    $result = new Chronological(Chronological::$ENUM_CONSTRUCT_FIELD, $this->_id_group, $this->revision,
      $this->table_db, $this->_id_record, $this->timestamp);
    return $result;
  }
  public function insert($link)
  {
    $command = "INSERT INTO chronological (_id_group, table_db, _id_record, timestamp, revision) ".
               "SELECT ".
               "'".$this->_id_group."', '".$this->table_db."', '".$this->_id_record."', '".$this->timestamp.
               "', MAX(revision)+1 FROM chronological WHERE _id_group='".$this->_id_group."';";
    $result = $link->query($command);
    if($result)
    {
      $this->revision = Chronological::getLastRevision($this->_id_group, $link);
    }
    if(!$result)//Возможно это самая первая запись вот и не отработал MAX
    {
      $command = "INSERT INTO chronological (_id_group, table_db, _id_record, timestamp, revision) ".
                 "VALUES (".
                 "'".$this->_id_group."', '".$this->table_db."', '".$this->_id_record."', '".$this->timestamp.
                 "', '1');";
      $result = $link->query($command);
      if($result)
        $this->revision = 1;
    }
    return $result;
  }

  public function update($link)
  {
    $result = true;
    if($this->old_rec == null ||
       $this->old_rec->_id_group == null || $this->old_rec->revision == null ||
       $this->_id_group == null          || $this->revision == null          ||
       $this->old_rec->_id_group != $this->_id_group ||
       $this->old_rec->revision  != $this->revision)
      throw new Exception("Неизвестный _id");
    $values = array();
    if($this->table_db != $this->old_rec->table_db)
      $values["table_db"] = $this->table_db;
    if($this->_id_record != $this->old_rec->_id_record)
      $values["_id_record"] = $this->_id_record;
    if($this->timestamp != $this->old_rec->timestamp)
      $values["timestamp"] = $this->timestamp;
    if(count($values) != 0)
    {
      $query = "UPDATE chronological ".QueryCreator::construct_SET($link, $values).
               " WHERE chronological._id_group = ".$this->_id_group.
               " AND chronological.revision = ".$this->revision.";";
      $result = $link->query($query);
    }
    return $result;
  }


}

?>