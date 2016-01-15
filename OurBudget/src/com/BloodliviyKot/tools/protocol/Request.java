package com.BloodliviyKot.tools.protocol;


import com.BloodliviyKot.tools.PHP_Poster;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**Класс для создания запросов на сервер
 * Нужно реализовать метод ConstructRequest добавляющий данные в JObj
 *   и вызвать метод post затем getAnswerFromPost или send, который вернет ответ
 */
public abstract class Request
{
  public final int ID;
  //Для пользователя класса не интересно
  private Thread post_thread;
  private Answer answer;
  protected JSONObject JObj = new JSONObject();
  //+//Отправляем асинхронный запрос на сервак и получаем ответ
  public final boolean post(final String Url) throws E_MESSID.MExeption
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
          postAnswerHandler(answer);
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
  //В наследнике нужно реализовать обработчик ответа от post запроса
  protected abstract void postAnswerHandler(Answer answer);
  //+//Получить ответ от асинхронного запроса
  protected Answer getAnswerFromPost() throws E_MESSID.MExeption
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
  protected Answer send(final String Url) throws E_MESSID.MExeption
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
}



















