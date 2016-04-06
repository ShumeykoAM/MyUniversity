package com.BloodliviyKot.tools.Protocol.Answers;

import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;
import org.json.JSONObject;

public class AnswerSendEntity
  extends Answer
{
  public enum RESULT
  {
    INSERTED,
    UPDATED,
    NOT_LAST_REV
  }
  public long _id_server;
  public RESULT result;
  public long s_revision;

  protected AnswerSendEntity(int _ID, JSONObject JOBJ) throws E_MESSID.MException
  {
    super(_ID);
    try
    {
      result = RESULT.values()[JOBJ.getInt("RESULT")];
      if(result == RESULT.INSERTED || result == RESULT.UPDATED)
      {
        _id_server = JOBJ.getLong("_id_server");
        s_revision = JOBJ.getLong("REVISION");
      }
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
}
