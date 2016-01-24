package com.BloodliviyKot.tools.Protocol.Answers;

import com.BloodliviyKot.tools.Protocol.E_MESSID;
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
        case E_MESSID.TEST_LOGIN:
          answer = new AnswerTestLogin(ID, JOBJ);
          break;
        case E_MESSID.TEST_PAIR_LOGIN_PASSWORD:
          answer = new AnswerTestPairLoginPassword(ID, JOBJ);
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

}
