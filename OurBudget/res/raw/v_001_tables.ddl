CREATE TABLE user_account
(
  _id                  INTEGER PRIMARY KEY,
  login                TEXT NOT NULL,
  password             TEXT NOT NULL,
  is_active            INTEGER NOT NULL,
  timestamp            INTEGER NOT NULL,
  current_rev          INTEGER NOT NULL
);

CREATE UNIQUE INDEX unique_login ON user_account
(
  login
);

CREATE TABLE type
(
  _id                  INTEGER PRIMARY KEY,
  _id_user_account     INTEGER,
  name                 TEXT NOT NULL,
  name_lower           TEXT NOT NULL, -- для поддержки LIKE здесь будем хранить копию name в нижнем регистре
  id_server            INTEGER,
  id_unit              INTEGER NOT NULL,
  is_delete            INTEGER NOT NULL,
  FOREIGN KEY (_id_user_account) REFERENCES user_account (_id)
);

CREATE UNIQUE INDEX unique_type ON type
(
  _id_user_account,
  name_lower
);

CREATE TABLE purchase
(
  _id                  INTEGER PRIMARY KEY,
  _id_user_account     INTEGER,
  id_server            INTEGER,
  date_time            INTEGER NOT NULL,  -- as Unix Time the number of seconds since 1970-01-01 00:00:00 UTC.
  state                INTEGER NOT NULL,
  is_delete            INTEGER NOT NULL,
  FOREIGN KEY (_id_user_account) REFERENCES user_account (_id)
);

CREATE TABLE detail
(
  _id                  INTEGER PRIMARY KEY,
  _id_user_account     INTEGER,
  _id_purchase         INTEGER NOT NULL,
  _id_type             INTEGER NOT NULL,
  id_server            INTEGER,
  price                REAL,
  for_amount_unit      REAL NOT NULL,
  for_id_unit          INTEGER NOT NULL,
  amount               REAL NOT NULL,
  id_unit              INTEGER NOT NULL,
  cost                 REAL,
  is_delete            INTEGER NOT NULL,
  FOREIGN KEY (_id_type) REFERENCES type (_id),
  FOREIGN KEY (_id_purchase) REFERENCES purchase (_id),
  FOREIGN KEY (_id_user_account) REFERENCES user_account (_id)
);

CREATE UNIQUE INDEX unique_detail ON detail
(
  _id_user_account,
  _id_purchase,
  _id_type
);

CREATE TABLE chronological
(
  _id                  INTEGER PRIMARY KEY,
  _id_user_account     INTEGER,
  table_db             INTEGER NOT NULL,
  _id_record           INTEGER NOT NULL,
  timestamp            INTEGER NOT NULL,  -- as Unix Time the number of seconds since 1970-01-01 00:00:00 UTC.
  is_sync              INTEGER NOT NULL,
  FOREIGN KEY (_id_user_account) REFERENCES user_account (_id)
);

CREATE INDEX duplicate_timestamp ON chronological
(
  _id_user_account,
  timestamp,
  _id
);

CREATE UNIQUE INDEX unique_chronological ON chronological
(
  _id_user_account,
  table_db,
  _id_record
);