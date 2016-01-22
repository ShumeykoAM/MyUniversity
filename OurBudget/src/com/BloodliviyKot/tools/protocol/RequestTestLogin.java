package com.BloodliviyKot.tools.protocol;


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
  public RequestTestLogin(String _login) throws E_MESSID.MExeption
  {
    super(E_MESSID.TEST_LOGIN);
    login = _login;
    ConstructRequest();
  }
  @Override
  protected void ConstructRequest() throws E_MESSID.MExeption
  {
    try
    {
      JObj.put( "LOGIN", login);
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
    }
  }
  @Override
  public AnswerTestLogin getAnswerFromPost() throws E_MESSID.MExeption
  {
    return (AnswerTestLogin)super.getAnswerFromPost();
  }
  @Override
  public AnswerTestLogin send() throws E_MESSID.MExeption
  {
    return (AnswerTestLogin)super.send();
  }
  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(iHandler_test_login != null)
      iHandler_test_login.handlerAnswer((AnswerTestLogin)answer);
  }
  public boolean postHandler(I_HandlerTestLogin i_handler) throws E_MESSID.MExeption
  {
    iHandler_test_login = i_handler;
    return post();
  }
}
