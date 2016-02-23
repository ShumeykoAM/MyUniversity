package com.BloodliviyKot.tools.Protocol.Requests;
import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerTestConnectServer;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import org.json.JSONException;
import java.util.Random;

//Классы запросов ----------------------------------------------------------------
public class RequestTestConnectServer
  extends Request
{
  private final Random random = new Random();
  public final int TestValue = random.nextInt();
  private I_HandlerTestConnectServer i_handlerTestConnectServer;
  public interface I_HandlerTestConnectServer
  {
    void handlerAnswer(AnswerTestConnectServer answer);
  }

  public RequestTestConnectServer() throws E_MESSID.MException
  {
    super(E_MESSID.TEST_CONNECT_SERVER);
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
  public AnswerTestConnectServer getAnswerFromPost() throws E_MESSID.MException
  {
    return (AnswerTestConnectServer)super.getAnswerFromPost();
  }
  @Override
  public AnswerTestConnectServer send() throws E_MESSID.MException
  {
    return (AnswerTestConnectServer)super.send();
  }
  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(i_handlerTestConnectServer != null)
      i_handlerTestConnectServer.handlerAnswer((AnswerTestConnectServer)answer);
  }
  public boolean postHandler(I_HandlerTestConnectServer i_handler) throws E_MESSID.MException
  {
    i_handlerTestConnectServer = i_handler;
    return post();
  }
}