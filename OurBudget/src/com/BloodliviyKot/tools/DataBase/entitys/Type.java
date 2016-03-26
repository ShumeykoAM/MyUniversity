package com.BloodliviyKot.tools.DataBase.entitys;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

public class Type
{
  public static final String table_name = "type";

  public Long _id;
  public long _id_user_account; //Если 0 то нету учетной записи
  public String name;
  public String name_lower;
  public Long id_server;
  public long id_unit;
  public boolean is_delete;

  public Type(Cursor cursor)
  {
    _id              = cursor.getLong  (cursor.getColumnIndex("_id"));
    _id_user_account = cursor.getLong  (cursor.getColumnIndex("_id_user_account"));
    name             = cursor.getString(cursor.getColumnIndex("name"));
    name_lower       = cursor.getString(cursor.getColumnIndex("name_lower"));
    if(!cursor.isNull(cursor.getColumnIndex("id_server")))
      id_server        = cursor.getLong  (cursor.getColumnIndex("id_server"));
    id_unit          = cursor.getLong  (cursor.getColumnIndex("id_unit"));
    is_delete        = cursor.getInt(cursor.getColumnIndex("is_delete")) == 1;
  }
  public Type(long __id_user_account, String _name, Long _id_server, long _id_unit, boolean _is_delete)
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

  public Type clone()
  {
    Type result = new Type(_id_user_account, name, id_server, id_unit, is_delete);
    result._id = _id;
    return result;
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
    values.put("is_delete"         , is_delete ? 1 : 0);
    return db.insert(table_name, null, values);
  }

  //Обновляет запись если есть что обновлять
  public boolean update(Type new_type, SQLiteDatabase db)
  {
    if(_id == null || new_type._id == null || !_id.equals(new_type._id))
      throw new Error();
    ContentValues values = new ContentValues();
    if(_id_user_account != new_type._id_user_account)
      values.put("_id_user_account", new Long(new_type._id_user_account).toString());
    if(name != null && new_type.name == null)
    {
      values.putNull("name");
      new_type.name_lower = null;
    }
    else if(name == null && new_type.name != null ||
      name != null && new_type.name != null && !name.equals(new_type.name))
    {
      values.put("name", new_type.name);
      new_type.name_lower = new_type.name.toLowerCase();
    }
    if(name_lower != null && new_type.name_lower == null)
      values.putNull("name_lower");
    else if(name_lower == null && new_type.name_lower != null ||
      name_lower != null && new_type.name_lower != null && !name_lower.equals(new_type.name_lower))
      values.put("name_lower", new_type.name_lower);
    if(id_server != null && new_type.id_server == null)
      values.putNull("id_server");
    else if(id_server == null && new_type.id_server != null ||
      id_server != null && new_type.id_server != null && id_server.compareTo(new_type.id_server) != 0)
      values.put("id_server", new Long(new_type.id_server).toString());
    if(id_unit != new_type.id_unit)
      values.put("id_unit", new Long(new_type.id_unit).toString());
    if(is_delete != new_type.is_delete)
      values.put("is_delete", new Long(new_type.is_delete ? 1 : 0).toString());
    if(values.size() > 0)
      return db.update(table_name, values, "_id=?", new String[]{new Long(_id).toString()}) == 1;
    else
      return false;
  }

}
