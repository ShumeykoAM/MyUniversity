package com.BloodliviyKot.tools.DataBase.entitys;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

public class UserAccount
{
  public static final long NON = 0; //Нет учетной записи, использовать вместо NULL
  public static final String table_name = "user_account";

  public long _id;
  public String login;
  public String password;
  public int is_active;
  public long timestamp; //Удалить потом как нибудь
  public long current_rev;

  public UserAccount(Cursor cursor)
  {
    _id         = cursor.getLong  (cursor.getColumnIndex("_id"));
    login       = cursor.getString(cursor.getColumnIndex("login"));
    password    = cursor.getString(cursor.getColumnIndex("password"));
    is_active   = cursor.getInt   (cursor.getColumnIndex("is_active"));
    timestamp   = cursor.getLong  (cursor.getColumnIndex("timestamp"));
    current_rev = cursor.getLong(cursor.getColumnIndex("current_rev"));
  }
  public UserAccount(String login, String password, int is_active, long timestamp, long current_rev)
  {
    this.login       = login;
    this.password    = password;
    this.is_active   = is_active;
    this.timestamp   = timestamp;
    this.current_rev = current_rev;
  }

  public static UserAccount getActiveUserAccount(MySQLiteOpenHelper oh, SQLiteDatabase db)
  {
    UserAccount result = null;
    Cursor cursor = db.rawQuery(oh.getQuery(EQ.USER_ACCOUNT_ACTIYE), new String[]{"1"});
    if(cursor.getCount() != 0 && cursor.moveToFirst())
      return new UserAccount(cursor);
    return null;
  }
  public static long getIDActiveUserAccount(MySQLiteOpenHelper oh, SQLiteDatabase db)
  {
    UserAccount active_user_account = UserAccount.getActiveUserAccount(oh, db);
    return active_user_account != null ? active_user_account._id : UserAccount.NON;
  }

  public UserAccount clone()
  {
    UserAccount result = new UserAccount(login, password, is_active, timestamp, current_rev);
    result._id = _id;
    return result;
  }

  //Обновляет запись если есть что обновлять
  public boolean update(UserAccount new_rec, final SQLiteDatabase db, final MySQLiteOpenHelper oh)
  {
    if(_id != new_rec._id || new_rec.login == null || new_rec.password == null)
      throw new Error();
    final ContentValues values = new ContentValues();
    if(!login.equals(new_rec.login))
      values.put("login", new_rec.login);
    if(!password.equals(new_rec.password))
      values.put("password", new_rec.password);
    if(is_active != new_rec.is_active)
      values.put("is_active", new_rec.is_active);
    if(timestamp != new_rec.timestamp)
      values.put("timestamp", new_rec.timestamp);
    if(current_rev != new_rec.current_rev)
      values.put("current_rev", new_rec.current_rev);
    if(values.size() > 0)
    {
      this.login       = new_rec.login;
      this.password    = new_rec.password;
      this.is_active   = new_rec.is_active;
      this.timestamp   = new_rec.timestamp;
      this.current_rev = new_rec.current_rev;
      return db.update(table_name, values, "_id=?", new String[]{new Long(_id).toString()}) == 1;
    }
    else
      return true;
  }

}
