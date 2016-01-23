package com.BloodliviyKot.tools.DataBase;


//Перечисление запросов хранящихся в файле res\raw\sql_querys.sql
//  Порядок запросов в этом файле должен совпадать с порядком соответствующих элементов перечисления EQ
public enum EQ
{
  USER_ACCOUNTS,                 //Получить список учетных записей пользователя
  USER_ACCOUNT_ACTIYE,           //Получить активную учетную запись
  USER_ACCOUNT,                  //Получить активную учетную запись
  PURCHASES,                     //Покупки
  DETAILS,                       //Детали покупки

  EQ_COUNT;
  public final int value;
  private EQ()
  {
    this.value = Index_i.i++;
  }

  private static class Index_i
  {
    public static int i = 0;
  }
}
