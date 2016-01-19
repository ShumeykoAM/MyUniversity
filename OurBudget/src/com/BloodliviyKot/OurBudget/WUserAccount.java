package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

public class WUserAccount
  extends Activity
  implements View.OnClickListener
{
  private SQLiteDatabase db;
  private EditText et_user_login;
  private EditText et_user_password;
  private Button b_user_enter;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.user_account);

    et_user_login = (EditText)findViewById(R.id.user_account_login);
    et_user_password = (EditText)findViewById(R.id.user_account_password);
    b_user_enter     = (Button)  findViewById(R.id.user_account_button_enter);

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
        Intent intent = new Intent(this, WRegistration.class);
        intent.putExtra(getString(R.string.intent_login   ), et_user_login.getText());
        intent.putExtra(getString(R.string.intent_password), et_user_password.getText());
        startActivityForResult(intent, R.layout.registration); //Запуск активности с onActivityResult
        return true;
      case R.id.m_user_account_invite_co_user:

        return true;
      case R.id.m_user_account_become_co_user:

        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if(requestCode == R.layout.registration)
    {
      int fdfdfd = 4;
      fdfdfd++;
    }
  }

}
