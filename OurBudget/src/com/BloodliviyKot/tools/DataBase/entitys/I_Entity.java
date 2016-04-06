package com.BloodliviyKot.tools.DataBase.entitys;


import android.database.sqlite.SQLiteDatabase;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import org.json.JSONException;
import org.json.JSONObject;

public interface I_Entity
{
  Chronological.TABLE get_table();
  JSONObject get_JObj(SQLiteDatabase db, MySQLiteOpenHelper oh) throws JSONException;
  boolean set_idServerIfUnset(long _id_server, SQLiteDatabase db, MySQLiteOpenHelper oh);
}
