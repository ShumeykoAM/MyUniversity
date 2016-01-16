package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WRegistration
  extends Activity
  implements View.OnClickListener
{
  private SQLiteDatabase db;
  private EditText et_login;
  private EditText et_password;
  private Button b_registration;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.registration);

    et_login       = (EditText)findViewById(R.id.registration_edit_login);
    et_password    = (EditText)findViewById(R.id.registration_edit_password);
    b_registration = (Button)  findViewById(R.id.registration_button_registration);

    et_login.addTextChangedListener(new TextChangeHandler(et_login));
    et_password.addTextChangedListener(new TextChangeHandler(et_password));
    b_registration.setOnClickListener(this);

    //Читаем параметры переданные из родительской активности
    Bundle extras = getIntent().getExtras();
    et_login.setText(extras.getString(getString(R.string.intent_login)));
    et_password.setText(extras.getString(getString(R.string.intent_password)));

    //Создаем помощник управления БД
    db = (new MySQLiteOpenHelper(getApplicationContext())).getWritableDatabase();

  }

  @Override
  public void onClick(View v)
  {
    if(v == b_registration)
    {

    }
  }

  class TextChangeHandler
    implements TextWatcher
  {
    private EditText view;
    public TextChangeHandler(EditText _view)
    {
      view = _view;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){}
    @Override
    public void afterTextChanged(Editable s)
    {
      if(view == et_login)
      {
        //Проверяем допустимость логина

      }
      else if(view == et_password)
      {
        //Проверяем допустимость логина

      }
    }
  }
}
