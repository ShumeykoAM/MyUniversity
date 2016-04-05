package com.BloodliviyKot.tools.Protocol.Requests;

import com.BloodliviyKot.tools.DataBase.entitys.I_Entity;
import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerSendEntity;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;


public class ARequestSendEntity
  extends Request
{
  private I_Entity i_entity;
  long timestamp;
  long client_rev;
  private I_HandlerSendEntity i_handler;

  public interface I_HandlerSendEntity
  {
    void handlerAnswer(AnswerSendEntity answer);
  }
  public ARequestSendEntity(I_Entity i_entity, long timestamp, long client_rev) throws E_MESSID.MException
  {
    super(E_MESSID.SEND_ENTITY);
    this.i_entity = i_entity;
    this.timestamp = timestamp;
    this.client_rev = client_rev;
    ConstructRequest();
  }
  @Override
  protected void ConstructRequest() throws E_MESSID.MException
  {
    try
    {
      JObj.put("TIMESTAMP", timestamp);
      JObj.put("TABLE", i_entity.get_table().ordinal());
      JObj.put("REVISION", client_rev);
      JObj.put("ENTITY", i_entity.get_JObj());
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
  @Override
  public AnswerSendEntity getAnswerFromPost() throws E_MESSID.MException
  {
    return (AnswerSendEntity)super.getAnswerFromPost();
  }
  @Override
  public AnswerSendEntity send() throws E_MESSID.MException
  {
    return (AnswerSendEntity)super.send();
  }

  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(i_handler != null)
      i_handler.handlerAnswer((AnswerSendEntity)answer);
  }
  public boolean postHandler(I_HandlerSendEntity i_handler) throws E_MESSID.MException
  {
    this.i_handler = i_handler;
    return post();
  }
}