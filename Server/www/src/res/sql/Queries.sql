--ID_GROUP_FROM_CODE
SELECT user_group._id FROM user_group WHERE user_group.code_for_co_user = ?;

--USER_GROUP_FROM_ID
SELECT user_group._id, user_group.code_for_co_user, user_group.timestamp_code
  FROM user_group WHERE user_group._id = ?;

--USER_ACCOUNT_FROM_ID
SELECT user_account._id_group , user_account._id, user_account.login, user_account.hash_password
  FROM user_account WHERE user_account._id = ?;