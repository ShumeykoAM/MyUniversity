--ID_GROUP_FROM_CODE
SELECT user_group._id FROM user_group WHERE user_group.code_for_co_user = ?;

--USER_GROUP_FROM_ID
SELECT user_group._id, user_group.code_for_co_user, user_group.timestamp_code
  FROM user_group WHERE user_group._id = ?;

--USER_ACCOUNT_FROM_ID
SELECT user_account._id_group , user_account._id, user_account.login, user_account.hash_password
  FROM user_account WHERE user_account._id = ?;

-- TYPE_LAST_ID
SELECT MAX(type._id) AS last_id FROM type WHERE type._id_group = ?;

--TYPE_FROM_NAME
SELECT type._id, type.name, type.id_unit, type._id_group, type.is_delete FROM type
  WHERE type._id_group = ? AND type.name = ?;

--TYPE_FROM_ID
SELECT type._id, type.name, type.id_unit, type._id_group, type.is_delete FROM type
  WHERE type._id_group = ? AND type._id = ?;

-- CHRONOLOGICAL_FROM_F_KEY
SELECT chronological._id_group, chronological.table_db, chronological._id_record,
  chronological.timestamp, chronological.operation FROM chronological
WHERE chronological._id_group = ? AND chronological.table_db = ? AND chronological._id_record = ?;