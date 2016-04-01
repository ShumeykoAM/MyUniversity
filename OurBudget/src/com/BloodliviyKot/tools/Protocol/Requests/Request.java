package com.BloodliviyKot.tools.Protocol.Requests;


import android.database.sqlite.SQLiteDatabase;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;
import com.BloodliviyKot.tools.Protocol.Answers.Answer;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerTestPairLoginPassword;
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
  private static String uri_s[] = new String[8]; //Список адресов с сервером, основной и запасные
  private static String uri = ""; //Рабочий адрес
  private E_MESSID.MException.ERR error = E_MESSID.MException.ERR.OK;
  //+//Отправляем асинхронный запрос на сервак и получаем ответ
  public final boolean post() throws E_MESSID.MException
  {
    if(post_thread != null || uri == "")
      return false;
    post_thread = new Thread(new Runnable(){
      @Override
      public void run()
      {
        int count_try = 1;
        while(count_try >= 0)
        {
          count_try--;
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
            error = E_MESSID.MException.ERR.PROBLEM_WITH_SERVER;
            answer = null;
          }
          catch(E_MESSID.MException _mException)
          {
            if(_mException.getError() == E_MESSID.MException.ERR.NOT_IDENTIFY)
            {
              //Пытаемся приконнектиться и на второй круг
              MySQLiteOpenHelper oh = new MySQLiteOpenHelper();
              SQLiteDatabase db = oh.getWritableDatabase();
              UserAccount user_account = UserAccount.getActiveUserAccount(oh, db);
              if(user_account != null)
              {
                RequestTestPairLoginPassword rtplp = null;
                try
                {
                  rtplp = new RequestTestPairLoginPassword(user_account.login, user_account.password, true);
                  if(rtplp.post())
                  {
                    AnswerTestPairLoginPassword atplp = rtplp.getAnswerFromPost();
                    if(atplp == null)
                      throw new E_MESSID.MException(E_MESSID.MException.ERR.PROBLEM_WITH_SERVER);
                    if(atplp.isCorrect)
                      continue;
                  }
                }
                catch(E_MESSID.MException e)
                {
                  e.printStackTrace();
                }
              }
            }
            error = _mException.getError();
            answer = null;
          }
          postAnswerHandler(answer);
          post_thread = null;
          break;
        }
      }
    });
    answer = null;
    post_thread.start();
    return true;
  }
  //В наследнике нужно реализовать обработчик ответа от post запроса
  protected abstract void postAnswerHandler(Answer answer);
  //+//Получить ответ от асинхронного запроса
  protected Answer getAnswerFromPost() throws E_MESSID.MException
  {
    if(answer == null && post_thread == null)
      throw new E_MESSID.MException(error);
    try
    {
      if(post_thread != null)
        post_thread.join();
    } catch(InterruptedException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
    if(answer == null)
      throw new E_MESSID.MException(error);
    return answer;
  }
  //+//Отправляем синхронный запрос на сервак и получаем ответ (только не в главном потоке)
  protected Answer send() throws E_MESSID.MException
  {
    Answer answer = null;
    if(uri != "")
    {
      int count_try = 1;
      while(count_try >= 0)
      {
        count_try--;
        List<NameValuePair> POST_Parm = new ArrayList<NameValuePair>(2);
        POST_Parm.add(new BasicNameValuePair("Request", JObj.toString()));
        PHP_Poster PP = new PHP_Poster();
        try
        {
          answer = Answer.AnswerFromString(PP.Post(uri, POST_Parm));
        }
        catch(IOException e)
        {
          e.printStackTrace();
          throw new E_MESSID.MException(E_MESSID.MException.ERR.PROBLEM_WITH_SERVER);
        }
        catch(E_MESSID.MException _mException)
        {
          if(_mException.getError() == E_MESSID.MException.ERR.NOT_IDENTIFY)
          {
            //Пытаемся приконнектиться и на второй круг
            MySQLiteOpenHelper oh = new MySQLiteOpenHelper();
            SQLiteDatabase db = oh.getWritableDatabase();
            UserAccount user_account = UserAccount.getActiveUserAccount(oh, db);
            if(user_account != null)
            {
              RequestTestPairLoginPassword rtplp = null;
              try
              {
                rtplp = new RequestTestPairLoginPassword(user_account.login, user_account.password, true);
                if(rtplp.post())
                {
                  AnswerTestPairLoginPassword atplp = rtplp.getAnswerFromPost();
                  if(atplp == null)
                    throw new E_MESSID.MException(E_MESSID.MException.ERR.PROBLEM_WITH_SERVER);
                  if(atplp.isCorrect)
                    continue;
                }
              }
              catch(E_MESSID.MException e)
              {
                e.printStackTrace();
              }
            }
          }
          else
            throw _mException;
        }
        break;
      }
    }
    return answer;
  }
  //В своей реализации метода ConstructRequest заполняем данными JObj
  protected abstract void ConstructRequest() throws E_MESSID.MException;
  static
  {
    uri_s[0] = "http://diplom.konofeev.ru/RequestHandler.php"; //Мой хост, арендованный у Ромы
    uri_s[1] = "http://192.168.10.105/RequestHandler.php"; //Комп
    uri_s[2] = "http://192.168.10.101/RequestHandler.php"; //Ноут
    uri_s[3] = "http://192.168.121.188/RequestHandler.php"; //Рабочий комп
    uri = uri_s[1];
  }
  protected Request(int _ID) throws E_MESSID.MException
  {
    ID = _ID;
    //Создаем JSON объект с полем ID сообщения
    try
    {
      JObj.put("MESSAGE", ID);
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MException(E_MESSID.MException.ERR.UNKNOWN);
    }
  }
}



















