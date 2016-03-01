package com.BloodliviyKot.tools.Protocol.Requests;


import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerCreateGroupCode;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;

public class ARequestCreateGroupCode
  extends Request
{
  public enum OPERATION
  {
    GENERATE,  //Команда сгенерировать код
    CANCEL,    //Команда отмена
    CHECK;     //Команда проверить, не присоединился ли второй пользователь

    public final int value;
    private OPERATION()
    {
      this.value = Index_i.i++;
    }
    private static class Index_i
    {
      public static int i = 0;
    }
  }
  private OPERATION operation;
  private I_HandlerCreateGroupCode i_handlerCreateProfile;
  public interface I_HandlerCreateGroupCode
  {
    void handlerAnswer(AnswerCreateGroupCode answer);
  }

  public ARequestCreateGroupCode(OPERATION operation) throws E_MESSID.MException
  {
    super(E_MESSID.CREATE_GROUP_CODE);
    this.operation = operation;
    ConstructRequest();
  }

  @Override
  protected void ConstructRequest() throws E_MESSID.MException
  {
    try
    {
      JObj.put( "OPERATION", operation.value);
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }

  @Override
  public AnswerCreateGroupCode getAnswerFromPost() throws E_MESSID.MException
  {
    return (AnswerCreateGroupCode)super.getAnswerFromPost();
  }
  @Override
  public AnswerCreateGroupCode send() throws E_MESSID.MException
  {
    return (AnswerCreateGroupCode)super.send();
  }
  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(i_handlerCreateProfile != null)
      i_handlerCreateProfile.handlerAnswer((AnswerCreateGroupCode)answer);
  }
  public boolean postHandler(I_HandlerCreateGroupCode i_handler) throws E_MESSID.MException
  {
    i_handlerCreateProfile = i_handler;
    return post();
  }

}
