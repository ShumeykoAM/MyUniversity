package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.BloodliviyKot.tools.protocol.Answer;
import com.BloodliviyKot.tools.protocol.E_MESSID;
import com.BloodliviyKot.tools.protocol.Request;

public class AuthenticationCreate
  extends Activity
  implements View.OnClickListener
{
  CheckBox remember_password;
  EditText email;
  EditText password;
  EditText password2;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.authentication_create);
    findViewById(R.id.button_create).setOnClickListener(this);

    remember_password = (CheckBox)findViewById(R.id.remember_password);
    email             = (EditText)findViewById(R.id.Email);
    password          = (EditText)findViewById(R.id.Password);
    password2         = (EditText)findViewById(R.id.Password2);
  }

  @Override
  public void onClick(View v)
  {
    String email_text = email.getText().toString();
    String password_text = password.getText().toString();
    String password_text2 = password2.getText().toString();
    String err_mes = null;
    if(email_text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
      err_mes = getString(R.string.authentication_err_mail);
    else if(password_text.length() <
            (int)getResources().getInteger(R.integer.PASSWORD_MIN_LENGHT)
           )
      err_mes = getString(R.string.authentication_err_pass_L6);
    else if(password_text.compareTo(password_text2) != 0)
      err_mes = getString(R.string.authentication_err_pass_diff);
    else if( !TestServerConnect() )
      err_mes = getString(R.string.authentication_err_test_server_connect);
    else if( !RegisterProfileOnServer() )
      err_mes = getString(R.string.authentication_err_test_server_email);
    if(err_mes != null)
    {
      Toast toast = Toast.makeText(getApplicationContext(), err_mes, Toast.LENGTH_SHORT);
      toast.show();
    }
    else
    {
      Toast toast = Toast.makeText(getApplicationContext(),
        getString(R.string.authentication_create_profile_success), Toast.LENGTH_SHORT);
      toast.show();
      finish();
    }
  }
  private boolean TestServerConnect()
  {
    boolean result = false;
    try
    {
      Request.RequestTestConnectServer request = new Request.RequestTestConnectServer();
      request.Post("http://192.168.10.101/request_handler.php");
      Answer.AnswerTestConnectServer answer =
        (Answer.AnswerTestConnectServer)request.GetAnswerFromPost();
      result = request.TestValue == answer.TestValue;
    } catch(E_MESSID.MExeption me)
    {
      me.printStackTrace();
    }
    return result;
  }
  private boolean RegisterProfileOnServer()
  {
    boolean result = false;
    try
    {
      Request.RequestCreateProfile cp = new Request.RequestCreateProfile(
        email.getText().toString(), password.getText().toString());
      cp.Post("http://192.168.10.101/request_handler.php");
      Answer.AnswerCreateProfile answer =
        (Answer.AnswerCreateProfile)cp.GetAnswerFromPost();
      result = answer.isCreated;
    } catch(E_MESSID.MExeption mExeption)
    {
      mExeption.printStackTrace();
    }
    return result;
  }


}
