package com.BloodliviyKot.tools.protocol;


import com.BloodliviyKot.tools.PHP_Poster;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**Класс для создания запросов на сервер
 * Нужно реализовать метод ConstructRequest добавляющий данные в JObj
 *   и вызвать метод Post затем GetAnswerFromPost или Send, который вернет ответ
 */
public abstract class Request
{
  //+//Отправляем асинхронный запрос на сервак и получаем ответ
  public final boolean Post(final String Url) throws E_MESSID.MExeption
  {
    if(post_thread != null)
      return false;
    post_thread = new Thread(new Runnable(){
      @Override
      public void run()
      {
        List<NameValuePair> POST_Parm = new ArrayList<NameValuePair>(2);
        POST_Parm.add( new BasicNameValuePair("Request", JObj.toString()) );
        PHP_Poster PP = new PHP_Poster();
        try
        {
          answer = Answer.AnswerFromString( PP.Post(Url, POST_Parm) );
        }
        catch(IOException e)
        {
          e.printStackTrace();
          answer = null;
        }
        catch(E_MESSID.MExeption mExeption)
        {
          mExeption.printStackTrace();
          answer = null;
        }
        post_thread = null;
      }
    });
    answer = null;
    post_thread.start();
    return true;
  }
  //+//Получить ответ от асинхронного запроса
  protected Answer GetAnswerFromPost() throws E_MESSID.MExeption
  {
    if(answer == null && post_thread == null)
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.PROBLEM_WITH_NET);
    try
    {
      if(post_thread != null)
        post_thread.join();
    } catch(InterruptedException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.PROBLEM_WITH_NET);
    }
    if(answer == null)
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.PROBLEM_WITH_NET);
    return answer;
  }
  //+//Отправляем синхронный запрос на сервак и получаем ответ (только не в главном потоке)
  protected Answer Send(String Url) throws E_MESSID.MExeption
  {
    Answer answer = null;
    List<NameValuePair> POST_Parm = new ArrayList<NameValuePair>(2);
    POST_Parm.add( new BasicNameValuePair("Request", JObj.toString()) );
    PHP_Poster PP = new PHP_Poster();
    try
    {
      answer = Answer.AnswerFromString( PP.Post(Url, POST_Parm) );
    }
    catch(IOException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.PROBLEM_WITH_NET);
    }
    return answer;
  }
  //В своей реализации метода ConstructRequest заполняем данными JObj
  protected abstract void ConstructRequest() throws E_MESSID.MExeption;
  protected JSONObject JObj = new JSONObject();
  protected Request(int _ID) throws E_MESSID.MExeption
  {
    ID = _ID;
    //Создаем JSON объект с полем ID сообщения
    try
    {
      JObj.put("MESSAGE", ID);
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
    }
  }
  //Для пользователя класса не интересно
  private final int ID;
  private Thread post_thread;
  private Answer answer;


  //Классы запросов ----------------------------------------------------------------
  public static class RequestTestConnectServer
    extends Request
  {
    public int TestValue;
    public RequestTestConnectServer() throws E_MESSID.MExeption
    {
      super(E_MESSID.TEST_CONNECT_SERVER);
      ConstructRequest();
    }
    @Override
    protected void ConstructRequest() throws E_MESSID.MExeption
    {
      final Random random = new Random();
      try
      {
        JObj.put( "TEST_VALUE", (TestValue = random.nextInt()) );
      } catch(JSONException e)
      {
        e.printStackTrace();
        throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
      }
    }
    @Override
    public Answer.AnswerTestConnectServer GetAnswerFromPost() throws E_MESSID.MExeption
    {
      return (Answer.AnswerTestConnectServer)super.GetAnswerFromPost();
    }
    @Override
    public Answer.AnswerTestConnectServer Send(String Url) throws E_MESSID.MExeption
    {
      return (Answer.AnswerTestConnectServer)super.Send(Url);
    }
  }
  public static class RequestCreateProfile
    extends Request
  {
    String login, password;
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
    public Answer.AnswerCreateProfile GetAnswerFromPost() throws E_MESSID.MExeption
    {
      return (Answer.AnswerCreateProfile)super.GetAnswerFromPost();
    }
    @Override
    public Answer.AnswerCreateProfile Send(String Url) throws E_MESSID.MExeption
    {
      return (Answer.AnswerCreateProfile)super.Send(Url);
    }
  }

}



















