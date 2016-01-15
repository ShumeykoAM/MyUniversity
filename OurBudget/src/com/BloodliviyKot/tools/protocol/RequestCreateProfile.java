package com.BloodliviyKot.tools.protocol;

import org.json.JSONException;

public class RequestCreateProfile
  extends Request
{
  private String login, password;
  private I_HandlerCreateProfile i_handlerCreateProfile;
  public interface I_HandlerCreateProfile
  {
    void handlerAnswer(Answer.AnswerCreateProfile answer);
  }

  public RequestCreateProfile(String _login, String _password) throws E_MESSID.MExeption
  {
    super(E_MESSID.CREATE_NEW_PROFILE);
    login    = _login;
    password = _password;
    ConstructRequest();
  }
  @Override
  protected void ConstructRequest() throws E_MESSID.MExeption
  {
    try
    {
      JObj.put( "LOGIN", login);
      JObj.put( "PASSWORD", password );
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
    }
  }
  @Override
  public Answer.AnswerCreateProfile getAnswerFromPost() throws E_MESSID.MExeption
  {
    return (Answer.AnswerCreateProfile)super.getAnswerFromPost();
  }
  @Override
  public Answer.AnswerCreateProfile send(String Url) throws E_MESSID.MExeption
  {
    return (Answer.AnswerCreateProfile)super.send(Url);
  }
  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(i_handlerCreateProfile != null)
      i_handlerCreateProfile.handlerAnswer((Answer.AnswerCreateProfile)answer);
  }
  public boolean postHandler(final String Url, I_HandlerCreateProfile i_handler) throws E_MESSID.MExeption
  {
    i_handlerCreateProfile = i_handler;
    return post(Url);
  }
}