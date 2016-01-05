CREATE TABLE account
(
  _id                  INTEGER PRIMARY KEY,
  id_account_on_server INTEGER,
  name                 TEXT NOT NULL,
  balance              DECIMAL NOT NULL
);

CREATE TABLE type_detail
(
  _id                  INTEGER PRIMARY KEY,
  _paren_type_detail   INTEGER,
  name                 TEXT NOT NULL,
  trend                INTEGER NOT NULL,
  _id_account          INTEGER NOT NULL,
  FOREIGN KEY (_paren_type_detail) REFERENCES type_detail (_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (_id_account) REFERENCES account (_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE movement
(
  _id                  INTEGER PRIMARY KEY,
  _id_account          INTEGER NOT NULL,
  trend                INTEGER NOT NULL,
  FOREIGN KEY (_id_account) REFERENCES account (_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE detail
(
  _id                  INTEGER PRIMARY KEY,
  _id_type_detail      INTEGER NOT NULL,
  _id_movement         INTEGER NOT NULL,
  FOREIGN KEY (_id_type_detail) REFERENCES type_detail (_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (_id_movement) REFERENCES movement (_id) ON DELETE RESTRICT ON UPDATE CASCADE
);