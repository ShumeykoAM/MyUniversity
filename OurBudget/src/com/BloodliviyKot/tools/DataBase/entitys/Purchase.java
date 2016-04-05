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

public class Purchase
  implements I_Entity
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
  public long _id_user_account;
  public Long id_server;
  public long date_time; //Кол-во сек с 70го года
  public STATE_PURCHASE state;
  public boolean is_delete;

  public Purchase(long _id_user_account, Long id_server, long date_time, STATE_PURCHASE state, boolean is_delete)
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
    this.is_delete        = cursor.getInt(cursor.getColumnIndex("is_delete")) == 1;
  }

  public static Purchase getFromIdServer(long _id_server, long _id_user_account, SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    Cursor cursor = db.rawQuery(oh.getQuery(EQ.PURCHASE_FROM_ID_SERVER),
      new String[]{new Long(_id_user_account).toString(), new Long(_id_server).toString()});
    if(cursor.moveToFirst())
      return new Purchase(cursor);
    else
      return null;
  }

  public long insertDateBase(final SQLiteDatabase db, boolean is_sync)
  {
    return insertDateBase(db, new Date().getTime(), is_sync);
  }
  public long insertDateBase(final SQLiteDatabase db, final long timestamp, final boolean is_sync)
  {
    final ContentValues values = new ContentValues();
    //values.put("_id", _id);
    values.put("_id_user_account", _id_user_account);
    if(id_server != null)
      values.put("id_server"     , id_server       );
    values.put("date_time"       , date_time       );
    values.put("state"           , state.value     );
    values.put("is_delete"       , is_delete       );
    final long[] res_id = new long[1];
    SQLTransaction sql_transaction = new SQLTransaction(db, new I_Transaction()
    {
      @Override
      public boolean trnFunc()
      {
        boolean result = (res_id[0] = db.insert(table_name, null, values)) != -1;
        if(result)
        {
          Chronological chronological = new Chronological(_id_user_account, Chronological.TABLE.PURCHASE, res_id[0],
            timestamp, is_sync);
          result = chronological.insertDateBase(db) != -1;
        }
        return result;
      }
    });
    return sql_transaction.runTransaction() ? res_id[0] : -1;
  }
  //Обновляет запись если есть что обновлять
  public boolean update(Purchase new_type, final SQLiteDatabase db, final MySQLiteOpenHelper oh, boolean is_sync)
  {
    return update(new_type, db, oh, true, is_sync);
  }
  //Обновляет запись если есть что обновлять
  public boolean update(Purchase new_purchase, final SQLiteDatabase db, final MySQLiteOpenHelper oh,
                        final boolean need_chronological, final boolean is_sync)
  {
    if(_id != new_purchase._id)
      throw new Error();
    final ContentValues values = new ContentValues();
    if(_id_user_account != new_purchase._id_user_account)
      values.put("_id_user_account", new Long(new_purchase._id_user_account).toString());
    if(id_server != null && new_purchase.id_server == null)
      values.putNull("id_server");
    else if(id_server == null && new_purchase.id_server != null ||
      id_server != null && new_purchase.id_server != null && id_server.compareTo(new_purchase.id_server) != 0)
      values.put("id_server", new Long(new_purchase.id_server).toString());
    if(date_time != new_purchase.date_time)
      values.put("date_time", new Double(new_purchase.date_time).toString());
    if(state != null && new_purchase.state == null)
      values.putNull("state");
    else if(state == null && new_purchase.state != null ||
      state != null && new_purchase.state != null && state != new_purchase.state)
      values.put("state", new Double(new_purchase.state.value).toString());
    if(is_delete != new_purchase.is_delete)
      values.put("is_delete", new Long(new_purchase.is_delete ? 1 : 0).toString());
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
            Chronological chronological = Chronological.getFromIndex1(_id_user_account, Chronological.TABLE.PURCHASE,
              _id, db, oh);
            if(chronological != null)
            {
              chronological.timestamp = new Date().getTime();
              chronological.is_sync = is_sync;
              result = chronological.update(db, oh);
            }
            else
            {
              chronological = new Chronological(_id_user_account, Chronological.TABLE.PURCHASE, _id,
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

  public Purchase clone()
  {
    Purchase result = new Purchase(this._id_user_account, this.id_server, this.date_time,
      this.state, this.is_delete);
    result._id = this._id;
    return result;
  }

  public static Purchase getPurhaseFromId(long _id, SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    Cursor cursor = db.rawQuery(oh.getQuery(EQ.PURCHASE_FROM_ID), new String[]{new Long(_id).toString()});
    if(cursor.moveToFirst())
      return new Purchase(cursor);
    else
      return null;
  }

  @Override
  public Chronological.TABLE get_table()
  {
    return Chronological.TABLE.PURCHASE;
  }

  @Override
  public JSONObject get_JObj() throws JSONException
  {
    long _id;
    JSONObject JObj = new JSONObject();
    JObj.put("id_server", id_server);
    JObj.put("date_time", date_time);
    JObj.put("state", state.ordinal());
    JObj.put("is_delete", is_delete);
    return JObj;
  }

  @Override
  public boolean set_idServerIfUnset(long _id_server, SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    Purchase new_rec = clone();
    if(new_rec.id_server == null || new_rec.id_server != _id_server)
      new_rec.id_server = _id_server;
    return update(new_rec, db, oh, true);
  }
}
