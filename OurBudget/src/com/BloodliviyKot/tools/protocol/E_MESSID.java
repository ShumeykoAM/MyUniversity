package com.BloodliviyKot.tools.protocol;

//Енум не используем что бы точно задавать номера синхронно с php файлами сервера
public interface E_MESSID
{
  //0 - не используем ни когда
  public static final int TEST_CONNECT_SERVER = 1; //Протестировать доступность сервера
  public static final int CREATE_NEW_PROFILE = 2;  //Создать новый профиль пользователя



  //Исключения ---------------------------------------------------------------------
  public static class MExeption
    extends Throwable
  {
    public enum ERR
    {
      UNKNOWN,          //Неизвестная ошибка
      PROBLEM_WITH_NET, //Проблемы с интернетом или сервером

    }
    private ERR error;
    MExeption(ERR _error)
    {
      error = _error;
    }
    public ERR getError()
    {
      return error;
    }
  }


}