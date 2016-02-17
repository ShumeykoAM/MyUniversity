package com.BloodliviyKot.tools.Protocol.Requests;


import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import com.BloodliviyKot.tools.Protocol.PHP_Poster;
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
  private static String uri_s[] = new String[3]; //Список адресов с сервером, основной и запасные
  private static String uri = ""; //Рабочий адрес
  private E_MESSID.MExeption.ERR error = E_MESSID.MExeption.ERR.OK;
  //+//Отправляем асинхронный запрос на сервак и получаем ответ
  public final boolean post() throws E_MESSID.MExeption
  {
    if(post_thread != null || uri == "")
      return false;
    post_thread = new Thread(new Runnable(){
      @Override
      public void run()
      {
        List<NameValuePair> POST_Parm = new ArrayList<NameValuePair>(2);
        POST_Parm.add(new BasicNameValuePair("Request", JObj.toString()));
        PHP_Poster PP = new PHP_Poster();
        try
        {
          if(ID == E_MESSID.TEST_GOOGLE)
            answer = Answer.AnswerFromID(E_MESSID.TEST_GOOGLE, PP.Post(E_MESSID.URL_GOOGLE, POST_Parm));
          else
            answer = Answer.AnswerFromString(PP.Post(uri, POST_Parm));
        }
        catch(IOException e)
        {
         error = E_MESSID.MExeption.ERR.PROBLEM_WITH_SERVER;
          answer = null;
        } catch(E_MESSID.MExeption _mExeption)
        {
          error = _mExeption.getError();
          answer = null;
        }
        postAnswerHandler(answer);
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
      throw new E_MESSID.MExeption(error);
    try
    {
      if(post_thread != null)
        post_thread.join();
    } catch(InterruptedException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
    }
    if(answer == null)
      throw new E_MESSID.MExeption(error);
    return answer;
  }
  //+//Отправляем синхронный запрос на сервак и получаем ответ (только не в главном потоке)
  protected Answer send() throws E_MESSID.MExeption
  {
    Answer answer = null;
    if(uri != "")
    {
      List<NameValuePair> POST_Parm = new ArrayList<NameValuePair>(2);
      POST_Parm.add(new BasicNameValuePair("Request", JObj.toString()));
      PHP_Poster PP = new PHP_Poster();
      try
      {
        answer = Answer.AnswerFromString(PP.Post(uri, POST_Parm));
      } catch(IOException e)
      {
        e.printStackTrace();
        throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.PROBLEM_WITH_SERVER);
      }
    }
    return answer;
  }
  //В своей реализации метода ConstructRequest заполняем данными JObj
  protected abstract void ConstructRequest() throws E_MESSID.MExeption;
  static
  {
    uri_s[0] = "http://192.168.10.108/RequestHandler.php";
    uri_s[1] = "http://192.168.10.101/RequestHandler.php";
    uri_s[2] = "http://diplom.konofeev.ru/RequestHandler.php";
    uri = uri_s[2];
  }
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



















