package com.BloodliviyKot.tools.Protocol.Requests;
import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerTestGoogle;
import com.BloodliviyKot.tools.Protocol.E_MESSID;

public class RequestTestGoogle
  extends Request
{
  private I_HandlerTestGoogle i_handlerTestConnectServer;
  public interface I_HandlerTestGoogle
  {
    void handlerAnswer(AnswerTestGoogle answer);
  }

  public RequestTestGoogle() throws E_MESSID.MExeption
  {
    super(E_MESSID.TEST_GOOGLE);
  }
  @Override
  protected void ConstructRequest() throws E_MESSID.MExeption{}
  @Override
  public AnswerTestGoogle getAnswerFromPost() throws E_MESSID.MExeption
  {
    return (AnswerTestGoogle)super.getAnswerFromPost();
  }
  @Override
  public AnswerTestGoogle send() throws E_MESSID.MExeption
  {
    return (AnswerTestGoogle)super.send();
  }
  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(i_handlerTestConnectServer != null)
      i_handlerTestConnectServer.handlerAnswer((AnswerTestGoogle)answer);
  }
  public boolean postHandler(I_HandlerTestGoogle i_handler) throws E_MESSID.MExeption
  {
    i_handlerTestConnectServer = i_handler;
    return post();
  }
}
