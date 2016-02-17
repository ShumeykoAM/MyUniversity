package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.I_Transaction;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.SQLTransaction;
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerTestPairLoginPassword;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import com.BloodliviyKot.tools.Protocol.Requests.RequestTestPairLoginPassword;

public class WUserAccount
  extends Activity
  implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher
{
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;
  private Spinner sp_user_login;
  private EditText et_user_password;
  private Button b_user_enter;
  private EditText et_user_login;
  private Cursor cursor;
  private String current_login;
  private boolean user_account_change = false;

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
    et_user_login.addTextChangedListener(this);

    //Создаем помощник управления БД
    oh = new MySQLiteOpenHelper(getApplicationContext());
    db = oh.getWritableDatabase();
    cursor = db.rawQuery(oh.getQuery(EQ.USER_ACCOUNTS), null);
    SpinnerAdapter spinner_adapter = new SimpleCursorAdapter(this, R.layout.user_account_item, cursor,
      new String[]{"login"}, new int[]{R.id.user_account_item_login} );
    sp_user_login.setAdapter(spinner_adapter);
    sp_user_login.setOnItemSelectedListener(this);
    loginPosition();

  }
  private void loginPosition()
  {
    if(cursor.moveToFirst())
    {
      int pos = 0;
      do
      {
        if(cursor.getInt(cursor.getColumnIndex("is_active")) == 1)
        {
          sp_user_login.setSelection(pos);
          current_login = cursor.getString(cursor.getColumnIndex("login"));
          break;
        }
        pos++;
      }while(cursor.moveToNext());
    }
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
      String login    = et_user_login.getText().toString();
      String password = et_user_password.getText().toString();
      boolean result = false;
      final long[] _id = new long[1];
      //Проверим есть ли такой логин локально
      Cursor l_cursor = db.rawQuery(oh.getQuery(EQ.USER_ACCOUNT_LOGIN), new String[]{login});
      result = l_cursor.moveToFirst();
      if(!result) //Локально такого логина нет, ищем на сервере
      {
        try
        {
          RequestTestPairLoginPassword rtplp = new RequestTestPairLoginPassword(login, password, false);
          if(rtplp.post())
          {
            AnswerTestPairLoginPassword atplp = rtplp.getAnswerFromPost();
            if(atplp == null)
              throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.PROBLEM_WITH_SERVER);
            if(atplp.isCorrect)
            {
              //Добавим в локальную базу эту учетку
              ContentValues values = new ContentValues();
              values.put("login", login);
              values.put("password", password);
              values.put("is_active", "0");
              _id[0] = db.insert(UserAccount.table_name, null, values);
              if(_id[0] != -1)
                result = true;
            }
            else
            {
              //на сервере нет, ругаемся
              Toast incorrect_login_passewor = Toast.makeText(getApplicationContext(),
                getString(R.string.user_account_err_incorrect_login_password), Toast.LENGTH_LONG);
              incorrect_login_passewor.show();
            }
          }
        } catch(E_MESSID.MExeption mExeption)
        {
          result = false;
          //нет связи с серваком
          AlertConnect alert_connect = new AlertConnect(getApplicationContext());
          alert_connect.getServerAccess(true);
        }
      }
      else
      {
        //Локально есть такой логин, на сервак не пойдем искать
        _id[0] = l_cursor.getLong((l_cursor.getColumnIndex("_id")));
      }
      if(result)
      {
        SQLTransaction sqlt = new SQLTransaction(db, new I_Transaction()
        {
          @Override
          public boolean trnFunc()
          {
            //Делаем все учетки неактивными
            ContentValues values = new ContentValues();
            values.put("is_active", "0");
            String where_args[] = new String[]{(new Long(_id[0])).toString()};
            int count = db.update(UserAccount.table_name, values, "is_active=1", null);
            values.clear();
            values.put("is_active", "1");
            count = db.update(UserAccount.table_name, values, "_id=?", where_args);
            return true;
          }
        });
        if(sqlt.runTransaction())
        {
          cursor.requery();
          loginPosition();
          b_user_enter.setClickable(false);
          b_user_enter.setText(getString(R.string.user_account_button_entered));
          user_account_change = true;
        }
      }
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
          intent.putExtra(getString(R.string.intent_password), "");
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
      cursor.requery();
      loginPosition();
      user_account_change = true;
    }
  }
  @Override
  public void finish()
  {
    Intent ires = new Intent();  //Вернем
    setResult(user_account_change ? RESULT_OK : RESULT_CANCELED); //Возвращаемый в родительскую активность результат
    super.finish();
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
  {
    et_user_login.setText(cursor.getString(cursor.getColumnIndex("login")));
    et_user_password.setText(cursor.getString(cursor.getColumnIndex("password")));
  }
  @Override
  public void onNothingSelected(AdapterView<?> parent)
  {  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after)
  {  }
  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count)
  {  }
  @Override
  public void afterTextChanged(Editable s)
  {
    if(current_login != null && s.toString().compareTo(current_login) == 0)
    {
      b_user_enter.setClickable(false);
      b_user_enter.setText(getString(R.string.user_account_button_entered));
    }
    else
    {
      b_user_enter.setClickable(true);
      b_user_enter.setText(getString(R.string.user_account_button_enter));
    }
  }
}
