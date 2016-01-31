package com.BloodliviyKot.tools.DataBase.entitys;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Purchase
{
  public static final String table_name = "purchase";

  public long _id;
  public long _id_user_account; //0 - без учетки
  public Long id_server;
  public long date_time; //Кол-во сек с 70го года
  public int state;
  public int is_delete;

  public Purchase(long __id_user_account, Long _id_server, long _date_time, int _state, int _is_delete)
  {
    _id_user_account = __id_user_account;
    id_server        = _id_server;
    date_time        = _date_time;
    state            = _state;
    is_delete        = _is_delete;

  }
  public long insertDateBase(SQLiteDatabase db)
  {
    ContentValues values = new ContentValues();
    //values.put("_id", _id);
    values.put("_id_user_account", _id_user_account);
    if(id_server != null)
      values.put("id_server"     , id_server       );
    values.put("date_time"       , date_time       );
    values.put("state"           , state           );
    values.put("is_delete"       , is_delete       );
    return db.insert(table_name, null, values      );
  }

}
