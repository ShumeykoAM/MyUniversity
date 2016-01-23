package com.BloodliviyKot.OurBudget;

import android.content.Context;
import android.widget.Toast;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerTestConnectServer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerTestGoogle;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import com.BloodliviyKot.tools.Protocol.Requests.RequestTestConnectServer;
import com.BloodliviyKot.tools.Protocol.Requests.RequestTestGoogle;

/**
 * Класс проверяет соединение и в случае проблемы
 *    выдает соответствующее сообщение
 * Если проблем нет то и сообщений нет
 */
public class AlertConnect
{
  private Context context;
  public AlertConnect(Context _context)
  {
    context = _context;
  }
  public enum SERVER_ACCES
  {
    ACCES,
    SERVER_PROBLEM,
    GOOGLE_PROBLEM
  }
  public SERVER_ACCES getServerAccess(boolean need_alert)
  {
    //Проверим доступность сервака потом если что в регистрацию перейдем
    SERVER_ACCES server_access = SERVER_ACCES.SERVER_PROBLEM;
    try
    {
      RequestTestConnectServer rtcs = new RequestTestConnectServer();
      if(rtcs.post())
      {
        AnswerTestConnectServer atc = rtcs.getAnswerFromPost();
        if(atc != null && atc.TestValue == rtcs.TestValue)
          server_access = SERVER_ACCES.ACCES;
      }
    }
    catch(E_MESSID.MExeption mExeption)
    {     }
    if(server_access == SERVER_ACCES.SERVER_PROBLEM)
    {
      RequestTestGoogle rtg = null;
      try
      {
        rtg = new RequestTestGoogle();
        rtg.post();
        AnswerTestGoogle atg = rtg.getAnswerFromPost();
        if(atg==null || !atg.google_access)
        {
          server_access = SERVER_ACCES.GOOGLE_PROBLEM;
        }
      } catch(E_MESSID.MExeption mExeption)
      {
        server_access = SERVER_ACCES.GOOGLE_PROBLEM;
      }
    }
    if(need_alert)
    {
      switch(server_access)
      {
        case SERVER_PROBLEM:
          Toast err_server = Toast.makeText(context,
            context.getString(R.string.alert_connect_err_server_connect), Toast.LENGTH_LONG);
          err_server.show();
          break;
        case GOOGLE_PROBLEM:
          Toast err_inet = Toast.makeText(context,
            context.getString(R.string.alert_connect_err_inet), Toast.LENGTH_LONG);
          err_inet.show();
          break;
      }
    }
    return server_access;
  }
}
