package com.BloodliviyKot.OurBudget.Dialogs;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.BloodliviyKot.OurBudget.AlertConnect;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerBecomeMember;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import com.BloodliviyKot.tools.Protocol.Requests.ARequestBecomeMember;

@SuppressLint("ValidFragment")
public class DialogBecomeMember
  extends DialogFragment
  implements View.OnClickListener
{
  private View v;
  private EditText tv_code;
  private Button become;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    v = inflater.inflate(R.layout.become_member, null);
    tv_code = (EditText)v.findViewById(R.id.dialog_become_member_code);
    become  = (Button)v.findViewById(R.id.dialog_become_member_become);
    tv_code.post(new Runnable(){
      @Override
      public void run()
      {
        tv_code.setSelection(0, tv_code.getText().length());
        InputMethodManager imm = (InputMethodManager)
          tv_code.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(tv_code, InputMethodManager.SHOW_IMPLICIT);
        tv_code.requestFocus();
      }
    });
    become.setOnClickListener(this);

    return v;
  }

  @Override
  public void onClick(View v)
  {
    //Проверим этот код на серваке
    try
    {
      Object o = tv_code.getText().toString();
      int group_code = Integer.parseInt(tv_code.getText().toString());
      ARequestBecomeMember request_become_member = new ARequestBecomeMember(group_code);
      AnswerBecomeMember answer_become_member;
      if(request_become_member.post() && (answer_become_member = request_become_member.getAnswerFromPost()) != null)
      {
        if(answer_become_member.result) //Присоединились к группе
        {
          //Нужно для всех записей снять id на сервере, что бы он смог пересинхронизироваться
          //Может снять только для типов, а запсис о покупках синхронизировать только новые
          //     надо быть осторожным что бы случайно не затирать записи на серваке с совпадающими id


          Toast.makeText(v.getContext(), R.string.dialog_become_member_м_ок, Toast.LENGTH_LONG).show();
          dismiss();
        }
        else
          Toast.makeText(v.getContext(), R.string.dialog_become_member_м_error, Toast.LENGTH_LONG).show();
      }
    }
    catch(E_MESSID.MException e)
    {
      e.printStackTrace();
      AlertConnect alert_connect = new AlertConnect(v.getContext());
      alert_connect.getServerAccess(true);
    }
  }



}
