package com.BloodliviyKot.tools.Protocol;

//Енум не используем что бы точно задавать номера синхронно с php файлами сервера
public interface E_MESSID
{
  //0 - не используем ни когда
  public static final int TEST_CONNECT_SERVER      = 1; //Протестировать доступность сервера
  public static final int CREATE_NEW_PROFILE       = 2; //Создать новый профиль пользователя
  public static final int TEST_GOOGLE              = 3; //Создать новый профиль пользователя
  public static final int TEST_LOGIN               = 4; //Проверить свободен ли логин
  public static final int TEST_PAIR_LOGIN_PASSWORD = 5; //Проверить логин пароль и, если нужно, залогиниться
  public static final int CREATE_GROUP_CODE        = 6; //Создать код длягруппы для присоединения пользователей
  public static final int NOT_IDENTIFY             = 7; //Ответ о том что нет идентификации на сервере
  public static final int BECOME_MEMBER            = 8; //Запрос на присоединение пользователя к группе
  public static final int SEND_ENTITY              = 9; //Отправить запись таблицы на сервак для синхронизации



  //Адреса
  public static final String URL_GOOGLE = "https://www.google.ru";

  //Исключения ---------------------------------------------------------------------
  public static class MException
    extends Throwable
  {
    public enum ERR
    {
      OK,                   //Ошибок нет
      UNKNOWN,              //Неизвестная ошибка
      PROBLEM_WITH_SERVER,  //Проблемы с сервером
      NOT_IDENTIFY          //Нет идентификации на сервере (массив $_SESSION не содержит идентификации учетки)

    }
    private ERR error;
    public MException(ERR _error)
    {
      error = _error;
    }
    public ERR getError()
    {
      return error;
    }
  }


}