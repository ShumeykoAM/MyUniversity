-- USER_ACCOUNTS
SELECT user_account._id, user_account.login, user_account.password, user_account.is_active, user_account.timestamp,
  user_account.current_rev FROM user_account WHERE user_account._id != 1;

-- USER_ACCOUNT_ACTIYE
SELECT user_account._id, user_account.login, user_account.password, user_account.is_active, user_account.timestamp,
  user_account.current_rev FROM user_account
  WHERE user_account.is_active = ?;

-- USER_ACCOUNT_ID
SELECT user_account._id, user_account.login, user_account.password, user_account.is_active, user_account.timestamp,
  user_account.current_rev FROM user_account WHERE user_account._id = ?;

-- USER_ACCOUNT_LOGIN
SELECT user_account._id, user_account.login, user_account.password, user_account.is_active, user_account.timestamp,
  user_account.current_rev FROM user_account WHERE user_account.login = ?;

-- PURCHASES
SELECT purchase._id, purchase.date_time, purchase.state FROM purchase, user_account
  WHERE ((purchase.date_time >= ? AND purchase.date_time <= ? AND purchase.state = 1) OR purchase.state = 0) AND
        purchase.is_delete = 0 AND purchase._id_user_account = user_account._id AND user_account.is_active = 1
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

-- DETAIL_FROM_ID_SERVER
SELECT detail._id, detail._id_user_account, detail._id_purchase, detail._id_type, detail.id_server,
  detail.price, detail.for_amount_unit, detail.for_id_unit, detail.amount,
  detail.id_unit, detail.cost, detail.is_delete FROM detail
  WHERE detail._id_user_account = ? AND detail.id_server = ?;

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

-- TYPE_FROM_ID_SERVER
SELECT type._id, type._id_user_account, type.name, type.name_lower,
  type.id_server, type.id_unit, type.is_delete
  FROM type WHERE type._id_user_account = ? AND type.id_server = ?;

-- TYPE_FROM_NAME
SELECT type._id, type._id_user_account, type.name, type.name_lower,
  type.id_server, type.id_unit, type.is_delete
  FROM type WHERE type._id_user_account = ? AND type.name_lower = ?;

-- LAST_PRICE
SELECT detail.price,
  detail.for_amount_unit, detail.for_id_unit
  FROM detail, purchase
  WHERE detail._id_type = ? AND detail._id_purchase = purchase._id
  AND NOT detail.price IS NULL AND detail.price != 0
  ORDER BY purchase.date_time DESC;

-- DETAIL_FOR_GROUP
SELECT DISTINCT detail._id_type AS _id FROM detail, purchase, user_account
  WHERE purchase.is_delete=0
  AND purchase.state=1
  AND purchase.date_time >= ?
  AND purchase.date_time <= ?
  AND detail._id_purchase=purchase._id
  AND detail.is_delete=0
  AND purchase._id_user_account = user_account._id AND user_account.is_active = 1;

-- ALL_DETAIL_FOR_GROUP
SELECT detail._id FROM detail, purchase, user_account
  WHERE purchase.is_delete=0
  AND purchase.state=1
  AND purchase.date_time >= ?
  AND purchase.date_time <= ?
  AND detail._id_type = ?
  AND detail._id_purchase=purchase._id
  AND detail.is_delete=0
  AND purchase._id_user_account = user_account._id AND user_account.is_active = 1;;

-- CHRONOLOGICAL_INDEX1
SELECT chronological._id, chronological._id_record, chronological._id_user_account,
  chronological.is_sync, chronological.table_db, chronological.timestamp FROM chronological
  WHERE chronological._id_user_account = ?
  AND chronological.table_db = ?
  AND chronological._id_record = ?;

-- CHRONOLOGICAL_NOT_SYNC
SELECT chronological._id, chronological._id_record, chronological._id_user_account,
  chronological.is_sync, chronological.table_db, chronological.timestamp FROM chronological
  WHERE chronological._id_user_account = ? AND chronological.table_db = ? AND chronological.is_sync = 0
  ORDER BY chronological.timestamp ASC
  LIMIT 1;

-- MAX_SERVER_ID_TYPE
SELECT MAX(type.id_server) as id_server FROM type;

-- PURCHASE_FROM_ID_SERVER
SELECT purchase._id, purchase._id_user_account, purchase.id_server, purchase.date_time,
  purchase.state, purchase.is_delete FROM purchase
  WHERE purchase._id_user_account = ? AND purchase.id_server = ?;

-- PURCHASE_UNTIL_DATE
SELECT purchase._id, purchase._id_user_account, purchase.id_server, purchase.date_time,
  purchase.state, purchase.is_delete FROM purchase
  WHERE purchase._id_user_account = ? AND purchase.state = 0 AND purchase.is_delete = 0
  AND purchase.date_time <= ?;

-- ALL_PURCHASES
SELECT purchase._id, purchase._id_user_account, purchase.id_server, purchase.date_time,
  purchase.state, purchase.is_delete FROM purchase;

-- ALL_DETAILS
SELECT detail._id, detail._id_user_account, detail._id_purchase, detail._id_type, detail.id_server,
  detail.price, detail.for_amount_unit, detail.for_id_unit, detail.amount,
  detail.id_unit, detail.cost, detail.is_delete FROM detail;

-- ALL_TYPE
SELECT type._id, type._id_user_account, type.name, type.name_lower,
  type.id_server, type.id_unit, type.is_delete FROM type;

-- ALL_CHRONOLOGICAL
SELECT chronological._id, chronological._id_record, chronological._id_user_account,
  chronological.is_sync, chronological.table_db, chronological.timestamp FROM chronological;
