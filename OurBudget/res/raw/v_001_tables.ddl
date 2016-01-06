CREATE TABLE type
(
  _id                  INTEGER PRIMARY KEY,
  name                 TEXT NOT NULL,
  id_unit              INTEGER NOT NULL
);

CREATE TABLE purchase
(
  _id                  INTEGER PRIMARY KEY,
  date_time            INTEGER NOT NULL, -- as Unix Time the number of seconds since 1970-01-01 00:00:00 UTC.
  state                INTEGER NOT NULL
);

CREATE TABLE detail
(
  _id                  INTEGER PRIMARY KEY,
  _id_type             INTEGER NOT NULL,
  _id_purchase         INTEGER NOT NULL,
  price                REAL,
  for_amount_unit      REAL NOT NULL,
  for_id_unit          INTEGER NOT NULL,
  amount               REAL,
  id_unit              INTEGER NOT NULL,
  cost                 REAL,
  FOREIGN KEY (_id_type)     REFERENCES type     (_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (_id_purchase) REFERENCES purchase (_id) ON DELETE RESTRICT ON UPDATE CASCADE
);