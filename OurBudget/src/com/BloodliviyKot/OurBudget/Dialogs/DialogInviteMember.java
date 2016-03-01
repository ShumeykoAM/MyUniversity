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
import com.BloodliviyKot.OurBudget.AlertConnect;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerCreateGroupCode;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import com.BloodliviyKot.tools.Protocol.Requests.ARequestCreateGroupCode;

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
    final int code[] = new int[1]; code[0] = 0;
    try
    {
      ARequestCreateGroupCode request_create_groupCode = new ARequestCreateGroupCode(
        ARequestCreateGroupCode.OPERATION.GENERATE);
      AnswerCreateGroupCode answer_create_group_code;
      if(request_create_groupCode.post() &&
          (answer_create_group_code = request_create_groupCode.getAnswerFromPost()) != null )
        code[0] = answer_create_group_code.group_code;
      else
        throw new E_MESSID.MException(E_MESSID.MException.ERR.PROBLEM_WITH_SERVER);
    }
    catch(E_MESSID.MException mException)
    {
      v.post(new Runnable(){
        @Override
        public void run()
        {
          AlertConnect alert_connect = new AlertConnect(v.getContext());
          alert_connect.getServerAccess(true);
          dismiss();
        }
      });
    }
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
        ChooseAlert choose_alert = new ChooseAlert(v.getContext(), getString(R.string.dialog_invite_alert_title),
          android.R.drawable.ic_dialog_alert, getString(R.string.dialog_invite_alert_message),
          getString(R.string.alert_button_yes), getString(R.string.alert_button_no));
        choose_alert.show(new ChooseAlert.I_ChooseAlertHandler(){
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
    try
    {
      ARequestCreateGroupCode request_create_groupCode = new ARequestCreateGroupCode(
        ARequestCreateGroupCode.OPERATION.CANCEL);
      request_create_groupCode.post();
      request_create_groupCode.getAnswerFromPost();
    }
    catch(E_MESSID.MException mException)
    {     }
    super.onCancel(dialog);
  }

}
