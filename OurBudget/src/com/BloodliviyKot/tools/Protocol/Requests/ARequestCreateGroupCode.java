package com.BloodliviyKot.tools.Protocol.Requests;


import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerCreateGroupCode;
import com.BloodliviyKot.tools.Protocol.E_MESSID;

public class ARequestCreateGroupCode
  extends Request
{
  private I_HandlerCreateGroupCode i_handlerCreateProfile;
  public interface I_HandlerCreateGroupCode
  {
    void handlerAnswer(AnswerCreateGroupCode answer);
  }

  public ARequestCreateGroupCode() throws E_MESSID.MException
  {
    super(E_MESSID.CREATE_GROUP_CODE);
  }

  @Override
  protected void ConstructRequest() throws E_MESSID.MException
  {
    //Ни чего передавать не надо
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
