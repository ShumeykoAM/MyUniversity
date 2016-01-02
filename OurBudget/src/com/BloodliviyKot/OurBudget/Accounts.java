package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class Accounts
  extends Activity
{
  private SQLiteDatabase db;
  private ListView list_accounts;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.accounts);

    list_accounts = (ListView)findViewById(R.id.accounts_list_accounts);

//Для отладки удалим базу
MySQLiteOpenHelper.debugDeleteDB(getApplicationContext());
    //Создаем помощник управления БД
    db = (new MySQLiteOpenHelper(getApplicationContext())).getWritableDatabase();

    Cursor c = db.rawQuery("SELECT account.name, account.balance FROM account;", null);
    ListAdapter list_adapter = new SimpleCursorAdapter(this, R.layout.accounts_item, c,
      new String[]{"name", "balance"},
      new int[]{R.id.accounts_item_name, R.id.accounts_item_balance});
    list_accounts.setAdapter(list_adapter);

  }


  private boolean fl_hide = false; //Для примера сокрытия ПМ
  //Создаем меню
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    //Создаем меню из ресурса
    getMenuInflater().inflate(R.menu.m_accounts, menu);
    return true;
  }
  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_account_item1:
        return true;
    }
    return super.onOptionsItemSelected(item);
  }




}
