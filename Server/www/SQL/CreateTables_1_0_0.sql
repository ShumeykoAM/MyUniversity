CREATE TABLE Profile
(
	Profile_ID           INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,
	Profile_HASHPassword VARCHAR(40) NOT NULL,
	Profile_Mail         VARCHAR(100) NOT NULL,
	PRIMARY KEY (Profile_ID)
)
 AUTO_INCREMENT = 1;

CREATE UNIQUE INDEX Index_Unique_Mile ON Profile
(
	Profile_Mail
);