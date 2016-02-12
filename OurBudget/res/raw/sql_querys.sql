-- USER_ACCOUNTS
SELECT user_account._id, user_account.login, user_account.password, user_account.is_active
  FROM user_account;

-- USER_ACCOUNT_ACTIYE
SELECT user_account._id, user_account.login, user_account.password, user_account.is_active
  FROM user_account
  WHERE user_account.is_active = ?;

-- USER_ACCOUNT_ID
SELECT user_account._id, user_account.login, user_account.password, user_account.is_active
  FROM user_account WHERE user_account._id = ?;

-- USER_ACCOUNT_LOGIN
SELECT user_account._id, user_account.login, user_account.password, user_account.is_active
  FROM user_account WHERE user_account.login = ?;

-- PURCHASES
SELECT purchase._id, purchase.date_time, purchase.state FROM purchase
  WHERE ((purchase.date_time > ? AND purchase.date_time < ? AND purchase.state = ?) OR purchase.state = ?) AND
        purchase.is_delete = 0
    ORDER BY purchase.state, purchase.date_time DESC;

-- PURCHASE_FROM_ID
SELECT purchase._id, purchase._id_user_account, purchase.id_server, purchase.date_time,
  purchase.state, purchase.is_delete FROM purchase
  WHERE purchase._id = ?;

-- DETAILS
SELECT detail._id, detail._id_user_account, detail._id_purchase, detail._id_type, detail.id_server,
  detail.price, detail.for_amount_unit, detail.for_id_unit, detail.amount,
  detail.id_unit, detail.cost, detail.is_delete, type.name FROM detail, type
  WHERE detail._id_purchase = ? AND detail.is_delete = ? AND type._id=detail._id_type;

-- DETAILS_ALL
SELECT detail._id, detail._id_user_account, detail._id_purchase, detail._id_type, detail.id_server,
  detail.price, detail.for_amount_unit, detail.for_id_unit, detail.amount,
  detail.id_unit, detail.cost, detail.is_delete, type.name FROM detail, type
  WHERE detail._id_purchase = ? AND type._id=detail._id_type;

-- DETAIL_FROM_ID
SELECT detail._id, detail._id_user_account, detail._id_purchase, detail._id_type, detail.id_server,
  detail.price, detail.for_amount_unit, detail.for_id_unit, detail.amount,
  detail.id_unit, detail.cost, detail.is_delete FROM detail
  WHERE detail._id = ?;

-- TYPES_USER_ACC
SELECT type._id, type._id_user_account, type.name, type.name_lower,
  type.id_server, type.id_unit, type.is_delete
  FROM type WHERE type._id_user_account = ?;

-- TYPES_USER_ACC_LIKE_NAME
SELECT type._id, type._id_user_account, type.name, type.name_lower,
  type.id_server, type.id_unit, type.is_delete
  FROM type WHERE type._id_user_account = ? AND (type.name_lower LIKE ?);

-- TYPE_FROM_ID
SELECT type._id, type._id_user_account, type.name, type.name_lower,
  type.id_server, type.id_unit, type.is_delete
  FROM type WHERE type._id = ?;

-- LAST_PRICE
SELECT detail.price,
  detail.for_amount_unit, detail.for_id_unit
  FROM detail, purchase
  WHERE detail._id_type = ? AND detail._id_purchase = purchase._id
  AND NOT detail.price IS NULL AND detail.price != 0
  ORDER BY purchase.date_time DESC;