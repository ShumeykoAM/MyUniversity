package com.BloodliviyKot.tools.Protocol.Answers;


import com.BloodliviyKot.tools.DataBase.entitys.Chronological;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;
import org.json.JSONObject;

public class AnswerGetEntity
  extends Answer
{
  public Chronological.TABLE table;
  public long timestamp;
  public JSONObject entity;
  protected AnswerGetEntity(int _ID, JSONObject JOBJ) throws E_MESSID.MException
  {
    super(_ID);
    try
    {
      table = Chronological.TABLE.values()[JOBJ.getInt("TABLE")];
      timestamp = JOBJ.getLong("TIMESTAMP");
      entity = JOBJ.getJSONObject("ENTITY");
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
}
