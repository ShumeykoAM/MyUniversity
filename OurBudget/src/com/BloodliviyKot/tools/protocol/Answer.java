package com.BloodliviyKot.tools.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Answer
{
  public final int ID;
  //Генерируем ответ на основе JSON-строки
  public static Answer AnswerFromString(String StrJSON) throws E_MESSID.MExeption
  {
    Answer answer = null;
    try
    {
      JSONObject JOBJ = new JSONObject(StrJSON);
      int ID = JOBJ.getInt("MESSAGE");
      switch(ID)
      {
        case E_MESSID.TEST_CONNECT_SERVER:
          answer = new AnswerTestConnectServer(ID, JOBJ);
          break;
        case E_MESSID.CREATE_NEW_PROFILE:
          answer = new AnswerCreateProfile(ID, JOBJ);
          break;


      }
    } catch(JSONException e)
    {
      e.printStackTrace();
      throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
    }
    return answer;
  }
  //Генерируем ответ на основе ID
  public static Answer AnswerFromID(int ID, String http_answer) throws E_MESSID.MExeption
  {
    Answer answer = null;
    switch(ID)
    {
      case E_MESSID.TEST_GOOGLE:
        answer = new AnswerTestGoogle(ID);
        break;
    }
    return answer;
  }

  protected Answer(int _ID)
  {
    ID = _ID;
  }


  //Подклассы ответов -----------------------------------------
  public static class AnswerTestConnectServer
    extends Answer
  {
    public int TestValue;
    AnswerTestConnectServer(int _ID, JSONObject JOBJ) throws E_MESSID.MExeption
    {
      super(_ID);
      try
      {
        TestValue = JOBJ.getInt("TEST_VALUE");
      } catch(JSONException e)
      {
        e.printStackTrace();
        throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
      }
    }
  }
  public static class AnswerCreateProfile
    extends Answer
  {
    public boolean isCreated;
    AnswerCreateProfile(int _ID, JSONObject JOBJ) throws E_MESSID.MExeption
    {
      super(_ID);
      try
      {
        isCreated = JOBJ.getBoolean("RESULT");
      } catch(JSONException e)
      {
        e.printStackTrace();
        throw new E_MESSID.MExeption(E_MESSID.MExeption.ERR.UNKNOWN);
      }
    }
  }
  //Подклассы ответов -----------------------------------------
  public static class AnswerTestGoogle
    extends Answer
  {
    public boolean google_access = true;
    AnswerTestGoogle(int _ID) throws E_MESSID.MExeption
    {
      super(_ID);
    }
  }

}
