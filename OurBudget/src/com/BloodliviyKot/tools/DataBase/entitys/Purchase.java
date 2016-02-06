package com.BloodliviyKot.tools.DataBase.entitys;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Purchase
{
  public static final String table_name = "purchase";

  public enum STATE_PURCHASE
  {
    PLAN(0),     //Запланирована
    EXECUTE(1);  //Исполнена (оплачена)

    public final int value;
    private STATE_PURCHASE(int _value)
    {
      this.value = _value;
    }
    public static STATE_PURCHASE getSTATE_PURCHASE(int state)
    {
      if(state == STATE_PURCHASE.PLAN.value)
        return STATE_PURCHASE.PLAN;
      else if(state == STATE_PURCHASE.EXECUTE.value)
        return STATE_PURCHASE.EXECUTE;
      else
        throw new Error();
    }
  }

  public long _id;
  public long _id_user_account; //0 - без учетки
  public Long id_server;
  public long date_time; //Кол-во сек с 70го года
  public STATE_PURCHASE state;
  public int is_delete;

  public Purchase(long __id_user_account, Long _id_server, long _date_time, STATE_PURCHASE _state, int _is_delete)
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
    values.put("state"           , state.value     );
    values.put("is_delete"       , is_delete       );
    return db.insert(table_name, null, values      );
  }

}
