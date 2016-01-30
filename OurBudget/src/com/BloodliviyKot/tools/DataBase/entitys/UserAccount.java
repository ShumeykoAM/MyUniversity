package com.BloodliviyKot.tools.DataBase.entitys;

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

  public UserAccount(Cursor cursor)
  {
    _id       = cursor.getLong  (cursor.getColumnIndex("_id"));
    login     = cursor.getString(cursor.getColumnIndex("login"));
    password  = cursor.getString(cursor.getColumnIndex("password"));
    is_active = cursor.getInt   (cursor.getColumnIndex("is_active"));
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


}
