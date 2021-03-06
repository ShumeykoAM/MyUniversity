<?php

  //Перечисление запросов для метода QueryCreator::getQuery, лежащих в файле res\sql\Queries.sql
  namespace EQ
  {
    const ID_GROUP_FROM_CODE       = 0;         //Находит _id группы по указанному code_for_co_user
    const USER_GROUP_FROM_ID       = 1;         //Находит группу по указанному _id
    const USER_ACCOUNT_FROM_ID     = 2;         //Находит учетку поуказанному _id
    const TYPE_LAST_ID             = 3;         //Последний id вида указанной группы
    const TYPE_FROM_NAME           = 4;         //Вид по имени
    const TYPE_FROM_ID             = 5;         //Вид по id
    const CHRONOLOGICAL_FROM_F_KEY = 6;         //Хронология по первичному ключу
    const CHRONO_LAST_REVISION     = 7;         //Последняя существующая ревизия
    const PURCHASE_LAST_ID         = 8;         //Последний id покупки указанной группы
    const PURCHASE_FROM_ID         = 9;         //Покупка по id
    const DETAIL_LAST_ID           = 10;        //Последний id детали
    const DETAIL_FROM_ID           = 11;        //Деталь по id
    const DETAIL_FROM_ID_P_T       = 12;        //Деталь по id_purchase и id_type

  }
?>