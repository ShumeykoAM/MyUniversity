package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
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

  @Override
  public void onClick(View v)
  {
    if(v == b_registration)
    {
      try
      {
        RequestCreateProfile rcp = new RequestCreateProfile(et_login.getText().toString(),
                                                            et_password.getText().toString());
        rcp.post();
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
        if(et_login.getText().toString().length() < 3)
          im_login.setImageResource(R.drawable.ic_illegal);
        else
        {
          try
          {
            RequestTestLogin rtl = new RequestTestLogin(et_login.getText().toString());
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
        String password = et_password.getText().toString();
        if(password.length() >= 4)
          im_password.setImageResource(R.drawable.ic_g_tick);
        else
          im_password.setImageResource(R.drawable.ic_illegal);
      }
    }
  }
}
