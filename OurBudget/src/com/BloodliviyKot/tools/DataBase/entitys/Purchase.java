package com.BloodliviyKot.tools.DataBase.entitys;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

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

  public Purchase(long _id_user_account, Long id_server, long date_time, STATE_PURCHASE state, int is_delete)
  {
    this._id_user_account = _id_user_account;
    this.id_server        = id_server;
    this.date_time        = date_time;
    this.state            = state;
    this.is_delete        = is_delete;
  }
  public Purchase(Cursor cursor)
  {
    this._id              = cursor.getLong(cursor.getColumnIndex("_id"));
    this._id_user_account = cursor.getLong(cursor.getColumnIndex("_id_user_account"));
    if(!cursor.isNull(cursor.getColumnIndex("id_server")))
      this.id_server      = cursor.getLong(cursor.getColumnIndex("id_server"));
    this.date_time        = cursor.getLong(cursor.getColumnIndex("date_time"));
    this.state            = STATE_PURCHASE.getSTATE_PURCHASE(cursor.getInt(cursor.getColumnIndex("state")));
    this.is_delete        = cursor.getInt(cursor.getColumnIndex("is_delete"));

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

  public static Purchase getPurhaseFromId(long _id, SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    Cursor cursor_purchase = db.rawQuery(oh.getQuery(EQ.PURCHASE_FROM_ID), new String[]{new Long(_id).toString()});
    if(cursor_purchase.moveToFirst())
      return new Purchase(cursor_purchase);
    else
      return null;
  }
}
