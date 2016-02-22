package com.BloodliviyKot.OurBudget.Dialogs;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.BloodliviyKot.OurBudget.R;

import java.util.Random;

@SuppressLint("ValidFragment")
public class DialogInviteMember
  extends DialogFragment
  implements Runnable
{
  private View v;
  private TextView tv_code;
  private TextView tv_status;

  private String format_status;
  private Thread while_thread;
  private boolean need_while = true;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    v = inflater.inflate(R.layout.invite_member, null);
    tv_code   = (TextView)v.findViewById(R.id.dialog_invite_member_code);
    tv_status = (TextView)v.findViewById(R.id.dialog_invite_member_status);
    generateCodeAndSendOnServer();
    format_status = getString(R.string.dialog_invite_member_status);
    tv_status.setText(String.format(format_status, getResources().getInteger(R.integer.TIMEOUT_WAIT_INVITE)));

    return v;
  }

  private int generateCodeAndSendOnServer()
  {
    while_thread = new Thread(this);
    Random random = new Random();
    final int code[] = new int[1]; code[0] = 0;
    for(int i=0; i < 5; ++i)
      code[0] += (Math.abs(random.nextInt()%9+1))*Math.pow(10,i);
    //Отправим код на сервак



    tv_code.post(new Runnable(){
      @Override
      public void run()
      {
        tv_code.setText(new Integer(code[0]).toString());
      }
    });
    while_thread.start();
    return code[0];
  }

  @Override
  public void run()
  {
    int tick = getResources().getInteger(R.integer.TIMEOUT_WAIT_INVITE);
    while(need_while)
    {
      tick--;
      try
      {
        Thread.sleep(1000);
      } catch(InterruptedException e)
      {
        e.printStackTrace();
      }
      final String status = String.format(format_status, tick);
      tv_status.post(new Runnable()
      {
        @Override
        public void run()
        {
          tv_status.setText(status);
        }
      });
      if(tick == 0)
        break;
    }
    v.post(new Runnable(){
      @Override
      public void run()
      {
        ChooseAlert choose_аlert = new ChooseAlert(v.getContext(), getString(R.string.dialog_invite_alert_title),
          android.R.drawable.ic_dialog_alert, getString(R.string.dialog_invite_alert_message),
          getString(R.string.alert_button_yes), getString(R.string.alert_button_no));
        choose_аlert.show(new ChooseAlert.I_ChooseAlertHandler(){
          @Override
          public void onClick(ChooseAlert.CHOOSE_BUTTON button)
          {
            if(button == ChooseAlert.CHOOSE_BUTTON.BUTTON1)
            {
              generateCodeAndSendOnServer();
            }
            else
            {
              dismiss();
            }
          }
        });
      }
    });
  }

  @Override
  public void onCancel(DialogInterface dialog)
  {
    need_while = false;
    //Сообщим на сервер что прерываем ождание другого пользователя


    /*
    if(i_dialogResult != null)
      i_dialogResult.onResult(RESULT.CANCEL, null);
    */
    super.onCancel(dialog);
  }

}
