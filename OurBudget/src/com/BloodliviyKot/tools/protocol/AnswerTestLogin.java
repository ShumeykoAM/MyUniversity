package com.BloodliviyKot.tools.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerTestLogin
  extends Answer
{
  public boolean login_is_free;
  AnswerTestLogin(int _ID, JSONObject JOBJ) throws E_MESSID.MExeption
  {
    super(_ID);
    try
    {
      login_is_free = JOBJ.getBoolean("RESULT");
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
    }
  }
}