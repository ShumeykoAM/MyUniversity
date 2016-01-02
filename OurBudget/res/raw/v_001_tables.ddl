CREATE TABLE account
(
  _id                  INTEGER PRIMARY KEY,
  id_account_on_server INTEGER,
  name                 TEXT NOT NULL,
  balance              DECIMAL NOT NULL
);