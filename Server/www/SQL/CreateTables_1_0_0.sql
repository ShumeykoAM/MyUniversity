CREATE TABLE Profile
(
  ID_Profile           INT(8) UNSIGNED AUTO_INCREMENT,
  Profile_mail         VARCHAR(100) NOT NULL,
  Profile_HashPassword VARCHAR(40) NOT NULL,
  PRIMARY KEY (ID_Profile)
) AUTO_INCREMENT = 1;

CREATE UNIQUE INDEX XAK1Mail ON Profile
(
  Profile_mail
);

CREATE TABLE Account
(
  ID_Account           INT(8) UNSIGNED AUTO_INCREMENT,
	Balance              DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (ID_Account)
) AUTO_INCREMENT = 1;

CREATE TABLE AccessLevel
(
  ID_AccessLevel       INT(8) UNSIGNED AUTO_INCREMENT,
  PRIMARY KEY (ID_AccessLevel)
) AUTO_INCREMENT = 1;

CREATE TABLE Link
(
  ID_Profile           INT(8) UNSIGNED NOT NULL,
  ID_Account           INT(8) UNSIGNED NOT NULL,
  ID_AccessLevel       INT(8) UNSIGNED NOT NULL,
  PRIMARY KEY (ID_Profile,ID_Account),
  FOREIGN KEY R_1 (ID_Profile) REFERENCES Profile (ID_Profile),
  FOREIGN KEY R_3 (ID_Account) REFERENCES Account (ID_Account),
  FOREIGN KEY R_4 (ID_AccessLevel) REFERENCES AccessLevel (ID_AccessLevel)
);

CREATE TABLE Movement
(
  ID_Movement          INT(8) UNSIGNED AUTO_INCREMENT,
  ID_Account           INT(8) UNSIGNED NOT NULL,
  ID_Submitter         INT(8) UNSIGNED NOT NULL,
  ID_Execute           INT(8) UNSIGNED NULL,
  ID_Plan              INT(8) UNSIGNED NULL,
  PRIMARY KEY (ID_Movement),
  FOREIGN KEY R_5 (ID_Account) REFERENCES Account (ID_Account),
  FOREIGN KEY R_6 (ID_Submitter) REFERENCES Profile (ID_Profile),
  FOREIGN KEY R_7 (ID_Execute) REFERENCES Profile (ID_Profile),
  FOREIGN KEY R_8 (ID_Plan) REFERENCES Profile (ID_Profile)
) AUTO_INCREMENT = 1;

CREATE TABLE Detail
(
  ID_Detail            INT(8) UNSIGNED AUTO_INCREMENT,
  ID_SubDetail         INT(8) UNSIGNED NULL,
  ID_Movement          INT(8) UNSIGNED NOT NULL,
  PRIMARY KEY (ID_Detail),
  FOREIGN KEY R_9 (ID_Movement) REFERENCES Movement (ID_Movement),
  FOREIGN KEY R_10 (ID_SubDetail) REFERENCES Detail (ID_Detail)
) AUTO_INCREMENT = 1;