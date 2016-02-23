package com.BloodliviyKot.tools.Protocol.Answers;


import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;
import org.json.JSONObject;

public class AnswerCreateGroupCode
  extends Answer
{
  public final int group_code;
  protected AnswerCreateGroupCode(int _ID, JSONObject JOBJ) throws E_MESSID.MException
  {
    super(_ID);
    try
    {
      group_code = JOBJ.getInt("GROUP_CODE");
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
}
