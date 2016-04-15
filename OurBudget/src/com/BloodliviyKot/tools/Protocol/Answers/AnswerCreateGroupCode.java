package com.BloodliviyKot.tools.Protocol.Answers;


import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;
import org.json.JSONObject;

public class AnswerCreateGroupCode
  extends Answer
{
  public int group_code;
  public boolean result_cancel;
  public boolean result_connected;
  protected AnswerCreateGroupCode(int _ID, JSONObject JOBJ) throws E_MESSID.MException
  {
    super(_ID);
    try
    {
      boolean status = false;
      if(JOBJ.has("RESULT_CANCEL"))
      {
        status = true;
        result_cancel = JOBJ.has("RESULT_CANCEL");
      }
      if(JOBJ.has("GROUP_CODE"))
      {
        status = true;
        group_code = JOBJ.getInt("GROUP_CODE");
      }
      if(JOBJ.has("RESULT_CONNECTED"))
      {
        status = true;
        result_connected = JOBJ.getBoolean("RESULT_CONNECTED");
      }
      if(!status)
        throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
}
