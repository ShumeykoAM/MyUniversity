package com.BloodliviyKot.tools.DataBase;


import android.database.sqlite.SQLiteDatabase;

public class SQLTransaction
{
  private SQLiteDatabase db;
  private I_Transaction trn_func_execute;
  public SQLTransaction(SQLiteDatabase _db, I_Transaction _trn_func_execute)
  {
    db = _db;
    trn_func_execute = _trn_func_execute;
  }
  public boolean runTransaction()
  {
    boolean result = trn_func_execute != null && db != null;
    try
    {
      //Запускаем транзакцию
      db.beginTransaction();
      //Запускаем пользовательский обработчик транзакции
      if(result)
        result = trn_func_execute.trnFunc();
      if(result)
      {
        //Завершаем транзакцию
        //Помечаем что транзакцию нужно успешно завершить
        db.setTransactionSuccessful();
      }
      else
      {
        //Откатываем транзакцию
        //Не помечаем что транзакцию нужно успешно завершить
        //db.setTransactionSuccessful();
      }
    }
    finally
    {
      //Завершаем (применяем или откатываем см. выше) транзакцию
      db.endTransaction();
    }
    return result;
  }
}
