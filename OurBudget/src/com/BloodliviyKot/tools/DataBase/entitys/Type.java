package com.BloodliviyKot.tools.DataBase.entitys;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

public class Type
{
  public static final String table_name = "type";

  public long _id;
  public long _id_user_account; //Если 0 то нету учетной записи
  public String name;
  public String name_lower;
  public Long id_server;
  public long id_unit;
  public int is_delete;

  public Type(Cursor cursor)
  {
    _id              = cursor.getLong  (cursor.getColumnIndex("_id"));
    _id_user_account = cursor.getLong  (cursor.getColumnIndex("_id_user_account"));
    name             = cursor.getString(cursor.getColumnIndex("name"));
    name_lower       = cursor.getString(cursor.getColumnIndex("name_lower"));
    if(!cursor.isNull(cursor.getColumnIndex("id_server")))
      id_server        = cursor.getLong  (cursor.getColumnIndex("id_server"));
    id_unit          = cursor.getLong  (cursor.getColumnIndex("id_unit"));
    is_delete = cursor.getInt(cursor.getColumnIndex("is_delete"));
  }
  public Type(long __id_user_account, String _name, Long _id_server, long _id_unit, int _is_delete)
  {
    _id_user_account = __id_user_account;
    name             = _name;
    name_lower       = name.toLowerCase();
    id_server        = _id_server;
    id_unit          = _id_unit;
    is_delete        = _is_delete;
  }
  public static Type getFromId(long _id, SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    Cursor cursor = db.rawQuery(oh.getQuery(EQ.TYPE_FROM_ID), new String[]{new Long(_id).toString()});
    if(cursor.moveToFirst())
      return new Type(cursor);
    else
      return null;
  }

  public long insertDateBase(SQLiteDatabase db)
  {
    ContentValues values = new ContentValues();
    //values.put("_id", _id);
    values.put("_id_user_account"  , _id_user_account);
    values.put("name"              , name );
    values.put("name_lower"        , name_lower);
    if(id_server != null)
      values.put("id_unit"         , id_server);
    values.put("id_unit"           , id_unit);
    values.put("is_delete"         , is_delete);
    return db.insert(table_name, null, values);
  }


}
