package com.BloodliviyKot.tools.Protocol.Answers;

import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;
import org.json.JSONObject;

public class AnswerTestConnectServer
  extends Answer
{
  public int TestValue;
  AnswerTestConnectServer(int _ID, JSONObject JOBJ) throws E_MESSID.MExeption
  {
    super(_ID);
    try
    {
      TestValue = JOBJ.getInt("TEST_VALUE");
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
    }
  }
}
