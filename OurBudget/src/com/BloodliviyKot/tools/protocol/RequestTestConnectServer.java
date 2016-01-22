package com.BloodliviyKot.tools.protocol;
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
    void handlerAnswer(Answer.AnswerTestConnectServer answer);
  }

  public RequestTestConnectServer() throws E_MESSID.MExeption
  {
    super(E_MESSID.TEST_CONNECT_SERVER);
    ConstructRequest();
  }
  @Override
  protected void ConstructRequest() throws E_MESSID.MExeption
  {
    try
    {
      JObj.put( "TEST_VALUE", (TestValue) );
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
    }
  }
  @Override
  public Answer.AnswerTestConnectServer getAnswerFromPost() throws E_MESSID.MExeption
  {
    return (Answer.AnswerTestConnectServer)super.getAnswerFromPost();
  }
  @Override
  public Answer.AnswerTestConnectServer send() throws E_MESSID.MExeption
  {
    return (Answer.AnswerTestConnectServer)super.send();
  }
  @Override
  protected void postAnswerHandler(Answer answer)
  {
    if(i_handlerTestConnectServer != null)
      i_handlerTestConnectServer.handlerAnswer((Answer.AnswerTestConnectServer)answer);
  }
  public boolean postHandler(I_HandlerTestConnectServer i_handler) throws E_MESSID.MExeption
  {
    i_handlerTestConnectServer = i_handler;
    return post();
  }
}