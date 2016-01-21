package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.protocol.E_MESSID;
import com.BloodliviyKot.tools.protocol.RequestCreateProfile;

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

    //b_registration.setClickable(false);
    //b_registration.setVisibility(View.INVISIBLE);

    //Читаем параметры переданные из родительской активности
    Bundle extras = getIntent().getExtras();
    et_login.setText(extras.getCharSequence(getString(R.string.intent_login)));
    et_password.setText(extras.getCharSequence(getString(R.string.intent_password)));

    //Создаем помощник управления БД
    db = (new MySQLiteOpenHelper(getApplicationContext())).getWritableDatabase();

  }

  @Override
  public void onClick(View v)
  {
    if(v == b_registration)
    {
      try
      {
        RequestCreateProfile rcp = new RequestCreateProfile(et_login.getText().toString(),
                                                            et_password.getText().toString());
        rcp.post("http://192.168.10.108/RequestHandler.php");
        rcp.getAnswerFromPost();
      }
      catch(E_MESSID.MExeption mExeption)
      {
        mExeption.printStackTrace();
      }
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

        //b_registration.setClickable(false);
        //b_registration.setVisibility(View.VISIBLE);
        //Либо асерт кидать о недопустимости
      }
      else if(view == et_password)
      {
        //Проверяем допустимость логина

      }
    }
  }
}
