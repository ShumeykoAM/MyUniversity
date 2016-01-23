package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.BloodliviyKot.tools.DataBase.I_Transaction;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.SQLTransaction;
import com.BloodliviyKot.tools.DataBase.entitys.*;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerCreateProfile;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerTestLogin;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import com.BloodliviyKot.tools.Protocol.Requests.RequestCreateProfile;
import com.BloodliviyKot.tools.Protocol.Requests.RequestTestLogin;

public class WRegistration
  extends Activity
  implements View.OnClickListener
{
  private SQLiteDatabase db;
  private EditText et_login;
  private EditText et_password;
  private Button b_registration;
  private ImageView im_login;
  private ImageView im_password;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.registration);

    et_login       = (EditText)findViewById(R.id.registration_edit_login);
    et_password    = (EditText)findViewById(R.id.registration_edit_password);
    b_registration = (Button)  findViewById(R.id.registration_button_registration);
    im_login       = (ImageView)findViewById(R.id.registration_login_pic);
    im_password    = (ImageView)findViewById(R.id.registration_password_pic);

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
  private static final int MIN_LENGHT_PASSWORD = 4;
  private static final int MIN_LENGHT_LOGIN = 3;
  @Override
  public void onClick(View v)
  {
    if(v == b_registration)
    {
      try
      {
        final String login = et_login.getText().toString();
        final String password = et_password.getText().toString();
        boolean result = true;
        if(result && !checkLogin(login))
        {
          Toast err_login = Toast.makeText(getApplicationContext(),
            getString(R.string.user_account_err_another_login), Toast.LENGTH_LONG);
          err_login.show();
        }
        if(result && !checkPassword(password))
        {
          result = false;
          Toast err_password = Toast.makeText(getApplicationContext(),
            getString(R.string.user_account_err_another_password), Toast.LENGTH_LONG);
          err_password.show();
        }
        if(result)
        {
          RequestCreateProfile rcp = new RequestCreateProfile(login, password);
          if(rcp.post())
          {
            AnswerCreateProfile acpf = rcp.getAnswerFromPost();
            if(acpf.isCreated)
            {
              final long[] _id = new long[1];
              SQLTransaction sqlt = new SQLTransaction(db, new I_Transaction(){
                @Override
                public boolean trnFunc()
                {
                  //Делаем все учетки неактивными
                  ContentValues values = new ContentValues();
                  values.put("is_active", "0");
                  int count = db.update(UserAccount.table_name, values, "is_active=1", null);
                  //Добавим данную учетку в БД и сделаем ее активной
                  values.clear();
                  values.put("login", login);
                  values.put("password", password);
                  values.put("is_active", "1");
                  _id[0] = db.insert(UserAccount.table_name, null, values);
                  //Все данные которым не назначена учетная запись привязываем к созданной учетной записи
                  values.clear();
                  values.put("_id_user_account", _id[0]);
                  count = db.update(Purchase.table_name, values, "_id_user_account IS NULL", null);
                  count = db.update(Type.table_name, values, "_id_user_account IS NULL", null);
                  count = db.update(Detail.table_name, values, "_id_user_account IS NULL", null);
                  count = db.update(Chronological.table_name, values, "_id_user_account IS NULL", null);
                  return true;
                }
              });
              if(sqlt.runTransaction())
              {
                Toast user_account_created = Toast.makeText(getApplicationContext(),
                  getString(R.string.user_account_user_account_created), Toast.LENGTH_LONG);
                user_account_created.show();
                //Выходим из окна регистрации
                Intent ires = new Intent();  //Вернем
                ires.putExtra("_id", _id[0]);   //_id добавленной записи
                setResult(RESULT_OK, ires);  //Возвращаемый в родительскую активность результат
                finish();
              }
            }
            else
            {
              Toast err_login = Toast.makeText(getApplicationContext(),
                getString(R.string.user_account_err_another_login), Toast.LENGTH_LONG);
              err_login.show();
              im_login.setImageResource(R.drawable.ic_illegal);
            }
          }
          else
            throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.PROBLEM_WITH_SERVER);
        }
      }
      catch(E_MESSID.MExeption mExeption)
      {
        AlertConnect alert_connect = new AlertConnect(getApplicationContext());
        alert_connect.getServerAccess(true);
      }
    }
  }
  private boolean checkLogin(String login)
  {
    return login.length() >= MIN_LENGHT_LOGIN;
  }
  private boolean checkPassword(String password)
  {
    return password.length() >= MIN_LENGHT_PASSWORD;
  }

  private class TextChangeHandler
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
        String login = et_login.getText().toString();
        if(!checkLogin(login))
          im_login.setImageResource(R.drawable.ic_illegal);
        else
        {
          try
          {
            RequestTestLogin rtl = new RequestTestLogin(login);
            rtl.postHandler(new RequestTestLogin.I_HandlerTestLogin()
            {
              @Override
              public void handlerAnswer(AnswerTestLogin answer)
              {
                if(answer != null)
                {
                  if(answer.login_is_free)
                  {
                    runOnUiThread(new Runnable()
                    {
                      @Override
                      public void run()
                      {
                        im_login.setImageResource(R.drawable.ic_g_tick);
                      }
                    });
                  } else
                  {
                    runOnUiThread(new Runnable()
                    {
                      @Override
                      public void run()
                      {
                        im_login.setImageResource(R.drawable.ic_illegal);
                      }
                    });
                  }
                }
                else
                {
                  runOnUiThread(new Runnable()
                  {
                    @Override
                    public void run()
                    {
                      AlertConnect alert_connect = new AlertConnect(getApplicationContext());
                      alert_connect.getServerAccess(true);
                      im_login.setImageResource(R.drawable.ic_illegal);
                    }
                  });
                }
              }
            });
          } catch(E_MESSID.MExeption mExeption)
          {          }
        }
      }
      else if(view == et_password)
      {
        //Проверяем допустимость пароля
        if(checkPassword(et_password.getText().toString()))
          im_password.setImageResource(R.drawable.ic_g_tick);
        else
          im_password.setImageResource(R.drawable.ic_illegal);
      }
    }
  }
}
