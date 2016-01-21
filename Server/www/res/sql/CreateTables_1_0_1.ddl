CREATE TABLE user_group
(
  _id                  INT(8) AUTO_INCREMENT,
  code_for_co_user     INT(8) NULL,
  timestamp_code       INT(8) NULL,
  PRIMARY KEY (_id)
) AUTO_INCREMENT = 1;

CREATE UNIQUE INDEX unique_code ON user_group
(
  code_for_co_user
);

CREATE TABLE user_account
(
  _id                  INT(8) NOT NULL,
  login                VARCHAR(40) NOT NULL,
  hash_password        VARCHAR(40) NOT NULL,
  _id_group            INT(8) NOT NULL,
  PRIMARY KEY (_id_group,_id),
  FOREIGN KEY (_id_group) REFERENCES user_group (_id)
);

CREATE UNIQUE INDEX unique_login ON user_account
(
  login
);

CREATE TABLE purchase
(
  _id                  INT(8) NOT NULL,
  date_time            INT(8) NOT NULL,
  state                INT(8) NOT NULL,
  _id_group            INT(8) NOT NULL,
  is_delete            INT(8) NOT NULL,
  PRIMARY KEY (_id_group,_id),
  FOREIGN KEY (_id_group) REFERENCES user_group (_id)
);

CREATE TABLE type
(
  _id                  INT(8) NOT NULL,
  name                 VARCHAR(50) NOT NULL,
  id_unit              INT(8) NOT NULL,
  _id_group            INT(8) NOT NULL,
  is_delete            INT(8) NOT NULL,
  PRIMARY KEY (_id_group,_id),
  FOREIGN KEY (_id_group) REFERENCES user_group (_id)
);

CREATE UNIQUE INDEX unique_type ON type
(
  _id_group,
  name
);

CREATE TABLE detail
(
  _id                  INT(8) NOT NULL,
  _id_group_           INT(8) NOT NULL,
  _id_purchase         INT(8) NOT NULL,
  _id_type             INT(8) NOT NULL,
  price                DOUBLE NULL,
  for_amount_unit      DOUBLE NOT NULL,
  for_id_unit          INT(8) NOT NULL,
  amount               DOUBLE NULL,
  id_unit              INT(8) NOT NULL,
  cost                 DOUBLE NULL,
  is_delete            INT(8) NOT NULL,
  PRIMARY KEY (_id_group_,_id),
  FOREIGN KEY (_id_group_, _id_purchase) REFERENCES purchase (_id_group, _id),
  FOREIGN KEY (_id_group_, _id_type) REFERENCES type (_id_group, _id),
  FOREIGN KEY (_id_group_) REFERENCES user_group (_id)
);

CREATE UNIQUE INDEX unique_detail ON detail
(
  _id_group_,
  _id_purchase,
  _id_type
);

CREATE TABLE chronological
(
  timestamp            INT(8) NULL,
  table_db             INT(8) NOT NULL,
  _id_record           INT(8) NOT NULL,
  operation            INT(8) NOT NULL,
  _id_group            INT(8) NOT NULL,
  PRIMARY KEY (_id_group,table_db,_id_record),
  FOREIGN KEY (_id_group) REFERENCES user_group (_id)
);

CREATE INDEX duplicate_chronological ON chronological
(
  _id_group,
  timestamp
);