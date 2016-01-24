package com.BloodliviyKot.tools.Protocol.Requests;

import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerTestPairLoginPassword;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;


public class RequestTestPairLoginPassword
  extends Request
{
  private String login, password;
  private boolean needEnter;
  private I_HandlerTestPairLoginPassword i_handlerCreateProfile;

  public interface I_HandlerTestPairLoginPassword
  {
    void handlerAnswer(AnswerTestPairLoginPassword answer);
  }

  public RequestTestPairLoginPassword(String _login, String _password, boolean _needEnter) throws E_MESSID.MExeption
  {
    super(E_MESSID.TEST_PAIR_LOGIN_PASSWORD);
    login = _login;
    password = _password;
    needEnter = _needEnter;
    ConstructRequest();
  }

  @Override
  protected void ConstructRequest() throws E_MESSID.MExeption
  {
    try
    {
      JObj.put("LOGIN", login);
      JObj.put("PASSWORD", password);
      JObj.put("NEED_ENTER", needEnter);
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
    }
  }

  @Override
  public AnswerTestPairLoginPassword getAnswerFromPost() throws E_MESSID.MExeption
  {
    return (AnswerTestPairLoginPassword)super.getAnswerFromPost();
  }

  @Override
  public AnswerTestPairLoginPassword send() throws E_MESSID.MExeption
  {
    return (AnswerTestPairLoginPassword)super.send();
  }

  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(i_handlerCreateProfile != null)
      i_handlerCreateProfile.handlerAnswer((AnswerTestPairLoginPassword)answer);
  }

  public boolean postHandler(I_HandlerTestPairLoginPassword i_handler) throws E_MESSID.MExeption
  {
    i_handlerCreateProfile = i_handler;
    return post();
  }
}