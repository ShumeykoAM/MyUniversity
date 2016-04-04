package com.BloodliviyKot.tools.DataBase.entitys;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

public class Chronological
{
  public static final String table_name = "chronological";

  public enum TABLE
  {
    TYPE,     //Таблица Type виды товаров и услуг
    DETAIL,   //
    PURCHASE  //
  }

  public long _id;
  public long _id_user_account;
  public TABLE table;
  public long _id_rec;
  public long timestamp;
  public boolean is_sync;

  private Chronological old_rec;
  public Chronological(long _id_user_account, TABLE table, long _id_rec, long timestamp, boolean is_sync)
  {
    this._id = 0;
    this._id_user_account = _id_user_account;
    this.table = table;
    this._id_rec = _id_rec;
    this.timestamp = timestamp;
    this.is_sync = is_sync;
    old_rec = null;
  }

  //Ищем в базе по первому индексу
  public static Chronological getFromIndex1(long _id_user_account, TABLE table, long _id_rec,
                                            SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    String wheres[] = new String[]{new Long(_id_user_account).toString(), new Long(table.ordinal()).toString(),
                                   new Long(_id_rec).toString()};
    Cursor cursor = db.rawQuery(oh.getQuery(EQ.CHRONOLOGICAL_INDEX1), wheres);
    if(cursor.moveToFirst())
      return new Chronological(cursor);
    else
      return null;
  }

  public Chronological(Cursor cursor)
  {
    _id              = cursor.getLong  (cursor.getColumnIndex("_id"));
    _id_user_account = cursor.getLong  (cursor.getColumnIndex("_id_user_account"));
    table = TABLE.values()[((int)cursor.getLong(cursor.getColumnIndex("table_db")))];
    _id_rec = cursor.getLong(cursor.getColumnIndex("_id_record"));
    timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"));
    is_sync = cursor.getLong(cursor.getColumnIndex("is_sync")) == 1;
    old_rec = clone();
  }

  public Chronological clone()
  {
    Chronological result = new Chronological(_id_user_account, table, _id_rec, timestamp, is_sync);
    result._id = _id;
    return result;
  }

  public long insertDateBase(final SQLiteDatabase db)
  {
    final ContentValues values = new ContentValues();
    //values.put("_id", _id);
    values.put("_id_user_account", _id_user_account);
    values.put("table_db"        , new Long(table.ordinal()));
    values.put("_id_record"      , _id_rec);
    values.put("timestamp"       , timestamp);
    values.put("is_sync"         , is_sync ? 1 : 0);
    _id = db.insert(table_name, null, values);
    if(_id != -1)
      old_rec = clone();
    return _id;
  }

  //Обновит только если запись была считана из БД или была записана в БД а потом изменена
  public boolean update(final SQLiteDatabase db, final MySQLiteOpenHelper oh)
  {
    if(old_rec == null || old_rec._id != _id)
      throw new Error();
    final ContentValues values = new ContentValues();

    if(_id_user_account != old_rec._id_user_account)
      values.put("_id_user_account", new Long(_id_user_account));
    if(table != old_rec.table)
      values.put("table_db", new Long(table.ordinal()));
    if(_id_rec != old_rec._id_rec)
      values.put("_id_record", _id_rec);
    if(timestamp != old_rec.timestamp)
      values.put("timestamp", timestamp);
    if(is_sync != old_rec.is_sync)
      values.put("is_sync", is_sync ? 1 : 0);
    if(values.size() > 0)
    {
      return db.update(table_name, values, "_id=?", new String[]{new Long(_id).toString()}) == 1;
    }
    else
      return false;
  }

}
