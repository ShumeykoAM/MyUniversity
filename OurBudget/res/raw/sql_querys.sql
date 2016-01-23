-- USER_ACCOUNTS
SELECT user_account._id, user_account.login FROM user_account;

-- USER_ACCOUNT_ACTIYE
SELECT user_account._id, user_account.login
  FROM user_account
  WHERE user_account.is_active = ?;

-- PURCHASES
SELECT purchase._id, purchase.date_time, purchase.state FROM purchase
  WHERE (purchase.date_time > ? AND purchase.date_time < ? AND purchase.state = ?) OR purchase.state = ?
  ORDER BY purchase.state, purchase.date_time DESC;

-- DETAILS
SELECT detail._id, detail.price, detail.for_amount_unit, detail.for_id_unit,
  detail.amount, detail.id_unit, detail.cost, type.name FROM detail, type
  WHERE detail._id_purchase = ? AND type._id=detail._id_type;