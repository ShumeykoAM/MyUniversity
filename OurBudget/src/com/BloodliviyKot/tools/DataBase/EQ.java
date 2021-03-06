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
  PURCHASE_FROM_ID,              //Покупка по ее id_шнику
  DETAILS,                       //Товары и услуги покупки удаленные или неудаленные
  DETAILS_ALL,                   //Товары и услуги покупки все
  DETAIL_FROM_ID,                //Деталь по _id
  DETAIL_FROM_ID_SERVER,         //Деталь по _id_server
  TYPES_USER_ACC,                //Виды товаров и услуг активной учетки
  TYPES_USER_ACC_LIKE_NAME,      //Виды товаров и услуг активной учетки c фильтром по %имени%
  TYPE_FROM_ID,                  //Вид товара или услуги по _id
  TYPE_FROM_ID_SERVER,           //Вид товара или услуги по _id_server и учетки
  TYPE_FROM_NAME,                //Вид товара или услуги по имени name_lower
  LAST_PRICE,                    //Последние цены для вида товара
  DETAIL_FOR_GROUP,              //Список видов товаров по всем покупкам за период
  ALL_DETAIL_FOR_GROUP,          //Список всех деталей с указанным видом товара по всем покупкам за период
  CHRONOLOGICAL_INDEX1,          //Хронология конкретной записи
  CHRONOLOGICAL_NOT_SYNC,        //Первая по time_stamp несинхронизированная запись
  MAX_SERVER_ID_TYPE,            //Максимальный idшник сервера в таблице видов товаров и услуг
  PURCHASE_FROM_ID_SERVER,       //Покупка по _id_server и учетки
  PURCHASE_UNTIL_DATE,           //Запланированные покупки с датой меньшей чем указана
  ALL_PURCHASES,                 //
  ALL_DETAILS,                   //
  ALL_TYPE,                      //
  ALL_CHRONOLOGICAL,             //

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
