package com.BloodliviyKot.tools.Protocol.Requests;


import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerTestLogin;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;

public class RequestTestLogin
  extends Request
{
  private String login;
  private I_HandlerTestLogin iHandler_test_login;
  public interface I_HandlerTestLogin
  {
    void handlerAnswer(AnswerTestLogin answer);
  }
  public RequestTestLogin(String _login) throws E_MESSID.MException
  {
    super(E_MESSID.TEST_LOGIN);
    login = _login;
    ConstructRequest();
  }
  @Override
  protected void ConstructRequest() throws E_MESSID.MException
  {
    try
    {
      JObj.put( "LOGIN", login);
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
  @Override
  public AnswerTestLogin getAnswerFromPost() throws E_MESSID.MException
  {
    return (AnswerTestLogin)super.getAnswerFromPost();
  }
  @Override
  public AnswerTestLogin send() throws E_MESSID.MException
  {
    return (AnswerTestLogin)super.send();
  }
  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(iHandler_test_login != null)
      iHandler_test_login.handlerAnswer((AnswerTestLogin)answer);
  }
  public boolean postHandler(I_HandlerTestLogin i_handler) throws E_MESSID.MException
  {
    iHandler_test_login = i_handler;
    return post();
  }
}
