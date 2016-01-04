CREATE TABLE account
(
  _id                  INTEGER PRIMARY KEY,
  id_account_on_server INTEGER,
  name                 TEXT NOT NULL,
  balance              DECIMAL NOT NULL
);

CREATE TABLE type_movement
(
  _id                  INTEGER PRIMARY KEY,
  _paren_type_movement INTEGER,
  name                 TEXT NOT NULL,
  trend                INTEGER NOT NULL,
  _id_account          INTEGER NOT NULL,
  FOREIGN KEY (_paren_type_movement) REFERENCES type_movement (_id),
  FOREIGN KEY (_id_account) REFERENCES account (_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE movement
(
  _id                  INTEGER PRIMARY KEY,
  _id_type_movement    INTEGER NOT NULL,
  id_union             INTEGER NOT NULL,
  FOREIGN KEY (_id_type_movement) REFERENCES type_movement (_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);