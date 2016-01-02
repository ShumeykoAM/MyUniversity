package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.BloodliviyKot.tools.SQLReader;


public class Accounts
  extends Activity
{
  private SQLiteDatabase db;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.accounts);


//Для отладки удалим базу
MySQLiteOpenHelper.debugDeleteDB(getApplicationContext());
    //Создаем помощник управления БД
    db = (new MySQLiteOpenHelper(getApplicationContext(),
          new SQLReader(getResources()))).getWritableDatabase();
    if(db != null)
    {
    }

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
