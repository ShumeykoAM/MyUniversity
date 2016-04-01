package com.BloodliviyKot.tools.Protocol.Requests;
  import com.BloodliviyKot.tools.Protocol.Answers.Answer;
  import com.BloodliviyKot.tools.Protocol.Answers.AnswerTimeServer;
  import com.BloodliviyKot.tools.Protocol.E_MESSID;
  import org.json.JSONException;
  import java.util.Random;

//Классы запросов ----------------------------------------------------------------
public class RequestTimeServer
  extends Request
{
  private final Random random = new Random();
  public final int TestValue = random.nextInt();
  private I_HandlerRequestTimeServer i_handler;
  public interface I_HandlerRequestTimeServer
  {
    void handlerAnswer(AnswerTimeServer answer);
  }

  public RequestTimeServer() throws E_MESSID.MException
  {
    super(E_MESSID.GET_SERVER_TIME);
    ConstructRequest();
  }
  @Override
  protected void ConstructRequest() throws E_MESSID.MException
  {
    try
    {
      JObj.put( "TEST_VALUE", (TestValue) );
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
  @Override
  public AnswerTimeServer getAnswerFromPost() throws E_MESSID.MException
  {
    return (AnswerTimeServer)super.getAnswerFromPost();
  }
  @Override
  public AnswerTimeServer send() throws E_MESSID.MException
  {
    return (AnswerTimeServer)super.send();
  }
  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(i_handler != null)
      i_handler.handlerAnswer((AnswerTimeServer)answer);
  }
  public boolean postHandler(I_HandlerRequestTimeServer i_handler) throws E_MESSID.MException
  {
    this.i_handler = i_handler;
    return post();
  }
}