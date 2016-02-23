package com.BloodliviyKot.tools.Protocol.Answers;

import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;
import org.json.JSONObject;

public class AnswerTestLogin
  extends Answer
{
  public boolean login_is_free;
  AnswerTestLogin(int _ID, JSONObject JOBJ) throws E_MESSID.MException
  {
    super(_ID);
    try
    {
      login_is_free = JOBJ.getBoolean("RESULT");
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
}