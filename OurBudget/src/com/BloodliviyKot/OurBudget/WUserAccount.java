package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WUserAccount
  extends Activity
  implements View.OnClickListener
{
  private SQLiteDatabase db;
  private TextView tv_user_login;
  private TextView tv_user_password;
  private Button b_user_enter;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.user_account);

    tv_user_login    = (TextView)findViewById(R.id.user_account_login);
    tv_user_password = (TextView)findViewById(R.id.user_account_password);
    b_user_enter     = (Button)  findViewById(R.id.user_account_enter);

    b_user_enter.setOnClickListener(this);

    //Создаем помощник управления БД
    db = (new MySQLiteOpenHelper(getApplicationContext())).getWritableDatabase();
    String query =
      "SELECT purchase._id, purchase.date_time, purchase.state FROM purchase ";
    Cursor cursor = db.rawQuery(query, null);

  }
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    if(keyCode == (KeyEvent.KEYCODE_BACK)) //Нажата кнопка назад
    {
      //return true;
      super.onKeyDown(keyCode, event);
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public void onClick(View v)
  {
    if(v == b_user_enter)
    {

    }
  }

  //Создаем меню
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    //Создаем меню из ресурса
    getMenuInflater().inflate(R.menu.user_account_menu, menu);
    return true;
  }
  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_user_account_registration:

        return true;
      case R.id.m_user_invite_member:

        return true;
      case R.id.m_user_become_member:

        return true;
    }
    return super.onOptionsItemSelected(item);
  }


}
