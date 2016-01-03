package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
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
      new String[]{"name", "balance"}, new int[]{R.id.accounts_item_name, R.id.accounts_item_balance});
    list_accounts.setAdapter(list_adapter);
    //c.requery(); //Обновляет Cursor делая повторный запрос. Устарела, но для наших целей подойдет
    //  актуально если в БД изменились данные. Нужно переходить на LoaderManager CursorLoader
    //  позволяющие работать асинхронно.

    registerForContextMenu(list_accounts);
  }

  //Создаем меню
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    //Создаем меню из ресурса
    getMenuInflater().inflate(R.menu.accounts_menu, menu);
    return true;
  }

  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_account_new_account:
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  //Контекстное меню для элемента списка счетов
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
  {
    super.onCreateContextMenu(menu, v, menuInfo);
    if(v == list_accounts)
    {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.accounts_context_list_accounts, menu);

      //Скроем не нужные на данный момент пункты меню
      MenuItem shareMenuItem = menu.findItem(R.id.m_account_context_list_acc_co_owners);
      shareMenuItem.setVisible(false);
    }
  }
  //Обрабатываем нажатие выбор пункта контекстного меню
  @Override
  public boolean onContextItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_account_context_list_acc_attach_to_authentication:
        return true;

    }
    return super.onContextItemSelected(item);
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
    String s_balance = balance_currency.toString() + _context.getString(R.string.common_currency);
    if(balance_currency_small != 0)
      s_balance += balance_currency_small.toString() + _context.getString(R.string.common_currency_small);
    //На экран выведется отформатированный текст с балансом
    balanceTV.setText(s_balance);
  }
}









