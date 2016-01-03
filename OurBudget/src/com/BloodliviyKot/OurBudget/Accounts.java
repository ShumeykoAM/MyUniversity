package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


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

    //Cursor обязательно должен содержать _id иначе SimpleCursorAdapter не заработает
    Cursor c = db.rawQuery("SELECT account._id, account.name, account.balance FROM account;", null);
    ListAdapter list_adapter = new account_SimpleCursorAdapter(this, R.layout.accounts_item, c,
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

//Переопределим SimpleCursorAdapter что бы форматировать данные из базы нужным образом
class account_SimpleCursorAdapter
  extends SimpleCursorAdapter
{
  public account_SimpleCursorAdapter(Context _context, int _layout, Cursor _c,
                                     String[] _from, int[] _to)
  {
    super(_context, _layout, _c, _from, _to);
  }
  @Override
  public void bindView(View _view, Context _context, Cursor _cursor)
  {
    String name = _cursor.getString(_cursor.getColumnIndex("name"));
    double balance = Double.parseDouble(_cursor.getString(_cursor.getColumnIndex("balance")));
    Integer balance_currency = (int)balance;
    Integer balance_currency_small = (int)((balance - balance_currency) * 100);
    TextView nameTV = (TextView)_view.findViewById(R.id.accounts_item_name);
    TextView balanceTV = (TextView)_view.findViewById(R.id.accounts_item_balance);
    nameTV.setText(name);
    balanceTV.setText(balance_currency.toString() + _context.getString(R.string.common_currency) +
      balance_currency_small.toString() + _context.getString(R.string.common_currency_small));
  }
}









