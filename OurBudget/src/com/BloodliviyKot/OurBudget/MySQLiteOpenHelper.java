package com.BloodliviyKot.OurBudget;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import com.BloodliviyKot.tools.SQLReader;

import java.io.File;
import java.util.List;

public class MySQLiteOpenHelper
  extends SQLiteOpenHelper
{
  public static final int VersionDB = 1; //Версия БД
  public static final String DBName = "OurBudget.db";
  private SQLReader sql_reader;

  public MySQLiteOpenHelper(Context context, SQLReader _sql_reader)
  {
    //Здесь создается или открывается БД
    super(context, DBName, null, VersionDB);
    sql_reader = _sql_reader;
  }
  //Создаем таблицы базы
  @Override
  public void onCreate(SQLiteDatabase db)
  {
    //Здесь создаются таблицы базы
    List<String> queris = sql_reader.getQueris(R.raw.v_001_tables);
    for(String query : queris)
      try
      {
        db.execSQL(query);
      }
      catch(SQLException e)
      {
        String err = e.getMessage();
      }
  }

  //Обновляем таблицы базы
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    //Здесь модифицируются таблицы базы
  }

  //Исключительно для отладки
  public static void debugDeleteDB(Context context)
  {
    //Удалим базу для отладки !!!!!!!!!!!!!!!!!!!
    String dbfile_path = Environment.getDataDirectory().toString() +
      File.separator + "data" + File.separator +
      context.getPackageName() + File.separator + "databases";
    File MyDB1 = new File( dbfile_path + File.separator + DBName );
    MyDB1.delete();
    //Удалим базу для отладки ^^^^^^^^^^^^^^^^^^^
  }


}