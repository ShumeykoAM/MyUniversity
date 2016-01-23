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
import android.widget.*;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

public class WUserAccount
  extends Activity
  implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;
  private Spinner sp_user_login;
  private EditText et_user_password;
  private Button b_user_enter;
  private EditText et_user_login;
  private Cursor cursor;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.user_account);

    sp_user_login    = (Spinner) findViewById(R.id.user_account_login_sp);
    et_user_password = (EditText)findViewById(R.id.user_account_password);
    b_user_enter     = (Button)  findViewById(R.id.user_account_button_enter);
    et_user_login    = (EditText)findViewById(R.id.user_account_login_ed);

    b_user_enter.setOnClickListener(this);

    //Создаем помощник управления БД
    oh = new MySQLiteOpenHelper(getApplicationContext());
    db = oh.getReadableDatabase();
    cursor = db.rawQuery(oh.getQuery(EQ.USER_ACCOUNTS), null);
    SpinnerAdapter spinner_adapter = new SimpleCursorAdapter(this, R.layout.user_account_item, cursor,
      new String[]{"login"}, new int[]{R.id.user_account_item_login} );
    sp_user_login.setAdapter(spinner_adapter);
    if(cursor.moveToFirst())
    {
      do
      {
        if(cursor.getInt(cursor.getColumnIndex("is_active")) == 1)
        {
          et_user_login.setText(cursor.getString(cursor.getColumnIndex("login")));
          b_user_enter.setClickable(false);
          b_user_enter.setText(getString(R.string.user_account_button_entered));
          break;
        }
      }while(cursor.moveToNext());
    }
    sp_user_login.setOnItemSelectedListener(this);
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
        //Проверим доступность сервака потом если что в регистрацию перейдем
        AlertConnect alert_connect = new AlertConnect(getApplicationContext());
        if(alert_connect.getServerAccess(true) == AlertConnect.SERVER_ACCES.ACCES)
        {
          Intent intent = new Intent(this, WRegistration.class);
          intent.putExtra(getString(R.string.intent_login), et_user_login.getText());
          intent.putExtra(getString(R.string.intent_password), et_user_password.getText());
          startActivityForResult(intent, R.layout.registration); //Запуск активности с onActivityResult
        }
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
    if(requestCode == R.layout.registration && resultCode == RESULT_OK)
    {
      Bundle extras = data.getExtras();
      long _id = (Long)extras.get("_id");
      b_user_enter.setClickable(false);
      b_user_enter.setText(getString(R.string.user_account_button_entered));

      int fdfdfd = 4;
      fdfdfd++;
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
  {
    Cursor cursor = db.rawQuery(oh.getQuery(EQ.USER_ACCOUNT), new String[]{(new Long(id)).toString()} );

  }
  @Override
  public void onNothingSelected(AdapterView<?> parent)
  {

  }
}
