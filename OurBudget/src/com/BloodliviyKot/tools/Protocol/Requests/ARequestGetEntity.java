package com.BloodliviyKot.tools.Protocol.Requests;


import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerGetEntity;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;

public class ARequestGetEntity
  extends Request
{
  long timestamp;
  private I_HandlerGetEntity i_handler;

  public interface I_HandlerGetEntity
  {
    void handlerAnswer(AnswerGetEntity answer);
  }
  public ARequestGetEntity(long timestamp) throws E_MESSID.MException
  {
    super(E_MESSID.GET_ENTITY);
    this.timestamp = timestamp;
    ConstructRequest();
  }
  @Override
  protected void ConstructRequest() throws E_MESSID.MException
  {
    try
    {
      JObj.put("TIMESTAMP", timestamp);
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
  @Override
  public AnswerGetEntity getAnswerFromPost() throws E_MESSID.MException
  {
    return (AnswerGetEntity)super.getAnswerFromPost();
  }
  @Override
  public AnswerGetEntity send() throws E_MESSID.MException
  {
    return (AnswerGetEntity)super.send();
  }

  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(i_handler != null)
      i_handler.handlerAnswer((AnswerGetEntity)answer);
  }
  public boolean postHandler(I_HandlerGetEntity i_handler) throws E_MESSID.MException
  {
    this.i_handler = i_handler;
    return post();
  }
}