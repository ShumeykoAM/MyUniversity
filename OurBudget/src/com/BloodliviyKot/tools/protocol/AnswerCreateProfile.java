package com.BloodliviyKot.tools.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerCreateProfile
  extends Answer
{
  public boolean isCreated;
  AnswerCreateProfile(int _ID, JSONObject JOBJ) throws E_MESSID.MExeption
  {
    super(_ID);
    try
    {
      isCreated = JOBJ.getBoolean("RESULT");
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
    }
  }
}