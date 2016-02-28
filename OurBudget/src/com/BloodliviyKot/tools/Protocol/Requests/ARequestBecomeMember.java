package com.BloodliviyKot.tools.Protocol.Requests;


import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerBecomeMember;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;

public class ARequestBecomeMember
  extends Request
{
  private int group_code;
  private I_HandlerBecomeMember i_handlerCreateProfile;
  public interface I_HandlerBecomeMember
  {
    void handlerAnswer(AnswerBecomeMember answer);
  }

  public ARequestBecomeMember(int group_code) throws E_MESSID.MException
  {
    super(E_MESSID.BECOME_MEMBER);
    this.group_code = group_code;
    ConstructRequest();
  }
  @Override
  protected void ConstructRequest() throws E_MESSID.MException
  {
    try
    {
      JObj.put( "GROUP_CODE", group_code);
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
  @Override
  public AnswerBecomeMember getAnswerFromPost() throws E_MESSID.MException
  {
    return (AnswerBecomeMember)super.getAnswerFromPost();
  }
  @Override
  public AnswerBecomeMember send() throws E_MESSID.MException
  {
    return (AnswerBecomeMember)super.send();
  }
  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(i_handlerCreateProfile != null)
      i_handlerCreateProfile.handlerAnswer((AnswerBecomeMember)answer);
  }
  public boolean postHandler(I_HandlerBecomeMember i_handler) throws E_MESSID.MException
  {
    i_handlerCreateProfile = i_handler;
    return post();
  }
}
