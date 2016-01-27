-- USER_ACCOUNTS
SELECT * FROM user_account;

--USER_ACCOUNT_COUNT
SELECT COUNT(_id) FROM user_account;

-- USER_ACCOUNT_ACTIYE
SELECT user_account._id, user_account.login
FROM user_account
WHERE user_account.is_active = ?;

-- USER_ACCOUNT_ID
SELECT * FROM user_account WHERE user_account._id = ?;

-- USER_ACCOUNT_LOGIN
SELECT * FROM user_account WHERE user_account.login = ?;

-- PURCHASES
SELECT purchase._id, purchase.date_time, purchase.state FROM purchase
  WHERE (purchase.date_time > ? AND purchase.date_time < ? AND purchase.state = ?) OR purchase.state = ?
  ORDER BY purchase.state, purchase.date_time DESC;

-- DETAILS
SELECT detail._id, detail.price, detail.for_amount_unit, detail.for_id_unit,
  detail.amount, detail.id_unit, detail.cost, type.name FROM detail, type
  WHERE detail._id_purchase = ? AND type._id=detail._id_type;

-- TYPES_USER_ACC
SELECT type._id, type.name, type.id_unit FROM type, user_account WHERE
  type._id_user_account = user_account._id AND user_account.is_active = 1;

-- TYPES_USER_ACC_LIKE_NAME
SELECT type._id, type.name, type.id_unit FROM type, user_account WHERE
  type._id_user_account = user_account._id AND user_account.is_active = 1
  AND (name_lower LIKE ?);

-- TYPES_USER_NOT_ACC
SELECT type._id, type.name, type.id_unit FROM type WHERE type._id_user_account IS NULL;