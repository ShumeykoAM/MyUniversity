<?php

  //Перечисление запросов для метода QueryCreator::getQuery, лежащих в файле res\sql\Queries.sql
  namespace EQ
  {
    const ID_GROUP_FROM_CODE   = 0;                    //Находит _id группы по указанному code_for_co_user
    const USER_GROUP_FROM_ID   = 1;                    //Находит группу по указанному _id
    const USER_ACCOUNT_FROM_ID = 2;                    //Находит учетку поуказанному _id

  }
?>