package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListAdapter;

public class Movements
  extends Activity
{
  private SQLiteDatabase db;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.accounts);

    //Создаем помощник управления БД
    db = (new MySQLiteOpenHelper(getApplicationContext())).getWritableDatabase();
    //Cursor обязательно должен содержать _id иначе SimpleCursorAdapter не заработает
    Cursor c = db.rawQuery("SELECT account._id, account.name, account.balance FROM account;", null);
    ListAdapter list_adapter = new account_SimpleCursorAdapter(this, R.layout.accounts_item, c,
      new String[]{"name", "balance"}, new int[]{R.id.accounts_item_name, R.id.accounts_item_balance});
    //list_accounts.setAdapter(list_adapter);
    //c.requery(); //Обновляет Cursor делая повторный запрос. Устарела, но для наших целей подойдет
    //  актуально если в БД изменились данные. Нужно переходить на LoaderManager CursorLoader
    //  позволяющие работать асинхронно.

    //registerForContextMenu(list_accounts);
  }


}










