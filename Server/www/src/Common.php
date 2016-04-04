<?php

  //Пространство имен для перечисления глобальных переменных
  namespace GLOBALS_VAR
  {
    const it_is_debug_mode = true;

    const NAME_DB  = "gabrielle_diplom";
    const ID       = "_id_user";
    const EQ_ARRAY = "EQ_ARRAY";
  }

  //Пространство имен для перечисления сообщений которые обрабатывает сервер
  namespace E_MESSAGEID
  {
    //0 - не используем ни когда
    const TEST_CONNECT_SERVER         =  1; //Протестировать доступность сервера
    const CREATE_NEW_PROFILE          =  2; //Создать новый профиль пользователя
    //3 - не используем ни когда, она используется клиентами для теста сайта google.com
    const TEST_LOGIN                  =  4; //Проверяем не занят ли такой логин
    const TEST_PAIR_LOGIN_PASSWORD    =  5; //Проверяем пару логин пароль и если нужно входим, используя переменную $_SESSION
    const CREATE_GROUP_CODE           =  6; //Сгенерируем код для присоединения со-пользователя
    const NOT_IDENTIFY                =  7; //Для ответа об отсутствии идентификации
    const BECOME_MEMBER               =  8; //Запрос на присоединение пользователя к группе
    const SEND_ENTITY                 =  9; //Получить запись таблицы на для синхронизации
    const GET_ENTITY                  = 10; //Отправить измененную таблицы на синхронизации
    const GET_SERVER_TIME             = 11; //Получить время сервера

  }

  namespace E_TABLE
  {
    const TYPE     = 0; //Таблица Type виды товаров и услуг
    const DETAIL   = 1; //
    const PURCHASE = 2; //
  }

?>