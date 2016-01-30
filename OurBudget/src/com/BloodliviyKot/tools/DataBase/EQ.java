package com.BloodliviyKot.tools.DataBase;


//Перечисление запросов хранящихся в файле res\raw\sql_querys.sql
//  Порядок запросов в этом файле должен совпадать с порядком соответствующих элементов перечисления EQ
public enum EQ
{
  USER_ACCOUNTS,                 //Получить список учетных записей пользователя
  USER_ACCOUNT_ACTIYE,           //Получить активную учетную запись
  USER_ACCOUNT_ID,               //Получить учетную запись по id
  USER_ACCOUNT_LOGIN,            //Получить учетную запись по login
  PURCHASES,                     //Покупки
  DETAILS,                       //Товары и услуги покупки
  TYPES_USER_ACC,                //Виды товаров и услуг активной учетки
  TYPES_USER_ACC_LIKE_NAME,      //Виды товаров и услуг активной учетки c фильтром по %имени%

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
