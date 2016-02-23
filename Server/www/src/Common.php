<?php

  //Пространство имен для перечисления глобальных переменных
  namespace GLOBALS_VAR
  {
    const NAME_DB = "gabrielle_diplom";
    const ID = "_id_user";
  }

  //Пространство имен для перечисления сообщений которые обрабатывает сервер
  namespace E_MESSAGEID
  {
    //0 - не используем ни когда
    const TEST_CONNECT_SERVER         = 1; //Протестировать доступность сервера
    const CREATE_NEW_PROFILE          = 2; //Создать новый профиль пользователя
    //3 - не используем ни когда, она используется клиентами для теста сайта google.com
    const TEST_LOGIN                  = 4; //Проверяем не занят ли такой логин
    const TEST_PAIR_LOGIN_PASSWORD    = 5; //Проверяем пару логин пароль и если нужно входим, используя переменную $_SESSION
    const CREATE_GROUP_CODE           = 6; //Сгенерируем код для присоединения со-пользователя
    const NOT_IDENTIFY                = 7; //Для ответа об отсутствии идентификации

  }


?>