package com.BloodliviyKot.tools.DataBase.entitys;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.I_Transaction;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.SQLTransaction;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Type
  implements I_Entity
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

  public static long getMaxServerID(SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    Cursor cursor = db.rawQuery(oh.getQuery(EQ.MAX_SERVER_ID_TYPE), null);
    if( cursor.moveToFirst() && !cursor.isNull(cursor.getColumnIndex("id_server")) )
      return cursor.getLong(cursor.getColumnIndex("id_server"));
    else
      return 0;
  }

  public static Type getFromIdServer(long _id_server, long _id_user_account, SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    Cursor cursor = db.rawQuery(oh.getQuery(EQ.TYPE_FROM_ID_SERVER),
      new String[]{new Long(_id_user_account).toString(), new Long(_id_server).toString()});
    if(cursor.moveToFirst())
      return new Type(cursor);
    else
      return null;
  }

  public static Type getFromName(String name, long _id_user_account, SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    Cursor cursor = db.rawQuery(oh.getQuery(EQ.TYPE_FROM_NAME),
      new String[]{new Long(_id_user_account).toString(), name.toLowerCase()});
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

  public long insertDateBase(final SQLiteDatabase db, boolean is_sync)
  {
    return insertDateBase(db, new Date().getTime(), is_sync);
  }
  public long insertDateBase(final SQLiteDatabase db, final long timestamp, final boolean is_sync)
  {
    final ContentValues values = new ContentValues();
    //values.put("_id", _id);
    values.put("_id_user_account"  , _id_user_account);
    values.put("name"              , name );
    values.put("name_lower"        , name_lower);
    if(id_server != null)
      values.put("id_server"         , id_server);
    values.put("id_unit"           , id_unit);
    values.put("is_delete"         , is_delete ? 1 : 0);
    final long[] res_id = new long[1];
    SQLTransaction sql_transaction = new SQLTransaction(db, new I_Transaction()
    {
      @Override
      public boolean trnFunc()
      {
        boolean result = (res_id[0] = db.insert(table_name, null, values)) != -1;
        if(result)
        {
          Chronological chronological = new Chronological(_id_user_account, Chronological.TABLE.TYPE, res_id[0],
            timestamp, is_sync);
          result = chronological.insertDateBase(db) != -1;
        }
        return result;
      }
    });
    return sql_transaction.runTransaction() ? res_id[0] : -1;
  }

  //Обновляет запись если есть что обновлять
  public boolean update(Type new_type, final SQLiteDatabase db, final MySQLiteOpenHelper oh, boolean is_sync)
  {
    return update(new_type, db, oh, true, is_sync);
  }
  private boolean update(Type new_type, final SQLiteDatabase db, final MySQLiteOpenHelper oh,
                         final boolean need_chronological, final boolean is_sync)
  {
    if(_id == null || new_type._id == null || !_id.equals(new_type._id))
      throw new Error();
    final ContentValues values = new ContentValues();
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
    if(values.size() > 0 || is_sync)
    {
      SQLTransaction sql_transaction = new SQLTransaction(db, new I_Transaction()
      {
        @Override
        public boolean trnFunc()
        {
          boolean result = true;
          if(values.size() > 0)
            result = db.update(table_name, values, "_id=?", new String[]{new Long(_id).toString()}) == 1;
          if(result && need_chronological)
          {
            Chronological chronological = Chronological.getFromIndex1(_id_user_account, Chronological.TABLE.TYPE,
                                                                      _id, db, oh);
            if(chronological != null)
            {
              chronological.timestamp = new Date().getTime();
              chronological.is_sync = is_sync;
              result = chronological.update(db, oh);
            }
            else
            {
              chronological = new Chronological(_id_user_account, Chronological.TABLE.TYPE, _id,
                new Date().getTime(), is_sync);
              result = chronological.insertDateBase(db) != -1;
            }
          }
          return result;
        }
      });
      return sql_transaction.runTransaction();
    }
    else
      return false;
  }

  @Override
  public Chronological.TABLE get_table()
  {
    return Chronological.TABLE.TYPE;
  }
  @Override
  public JSONObject get_JObj() throws JSONException
  {
    JSONObject JObj = new JSONObject();
    JObj.put("id_server", id_server);
    JObj.put("name", name);
    JObj.put("id_unit", id_unit);
    JObj.put("is_delete", is_delete);
    return JObj;
  }

  @Override
  public boolean set_idServerIfUnset(long _id_server, SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    Type new_type = clone();
    if(new_type.id_server == null || new_type.id_server != _id_server)
      new_type.id_server = _id_server;
    return update(new_type, db, oh, true);
  }

}
