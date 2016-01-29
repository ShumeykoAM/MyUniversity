package com.BloodliviyKot.tools.DataBase.entitys;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Type
{
  public static final String table_name = "type";

  public long _id;
  public Long _id_user_account;
  public String name;
  public String name_lower;
  public Long id_server;
  public long id_unit;
  public int is_delete;

  public Type(Cursor cursor)
  {
    _id              = cursor.getLong  (cursor.getColumnIndex("_id"));
    if(!cursor.isNull(cursor.getColumnIndex("_id_user_account")))
      _id_user_account = cursor.getLong  (cursor.getColumnIndex("_id_user_account"));
    name             = cursor.getString(cursor.getColumnIndex("name"));
    name_lower       = cursor.getString(cursor.getColumnIndex("name_lower"));
    if(!cursor.isNull(cursor.getColumnIndex("id_server")))
      id_server        = cursor.getLong  (cursor.getColumnIndex("id_server"));
    id_unit          = cursor.getLong  (cursor.getColumnIndex("id_unit"));
    is_delete = cursor.getInt(cursor.getColumnIndex("is_delete"));
  }
  public Type(Long __id_user_account, String _name, Long _id_server, long _id_unit, int _is_delete)
  {
    _id_user_account = __id_user_account;
    name             = _name;
    name_lower       = name.toLowerCase();
    id_server        = _id_server;
    id_unit          = _id_unit;
    is_delete        = _is_delete;
  }

  public long insertDateBase(SQLiteDatabase db)
  {
    ContentValues values = new ContentValues();
    //values.put("_id", _id);
    if(_id_user_account != null)
      values.put("_id_user_account", _id_user_account);
    values.put("name"              , name );
    values.put("name_lower"        , name_lower);
    if(id_server != null)
      values.put("id_unit"         , id_server);
    values.put("id_unit"           , id_unit);
    values.put("is_delete"         , is_delete);
    return db.insert(table_name, null, values);
  }


}
