package com.BloodliviyKot.tools.Protocol;

//Енум не используем что бы точно задавать номера синхронно с php файлами сервера
public interface E_MESSID
{
  //0 - не используем ни когда
  public static final int TEST_CONNECT_SERVER     = 1; //Протестировать доступность сервера
  public static final int CREATE_NEW_PROFILE      = 2; //Создать новый профиль пользователя
  public static final int TEST_GOOGLE             = 3; //Создать новый профиль пользователя
  public static final int TEST_LOGIN              = 4; //Проверить свободен ли логин

  //Адреса
  public static final String URL_GOOGLE = "https://www.google.ru";

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
    public MExeption(ERR _error)
    {
      error = _error;
    }
    public ERR getError()
    {
      return error;
    }
  }


}