package com.BloodliviyKot.tools.DataBase.entitys;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.NumberFormat;

public class Detail
{
  public static final String table_name = "detail";
  public Long _id;
  public long _id_user_account; //0 - ни какой учетке не принадлежит
  public long _id_purchase;
  public long _id_type;
  public Long id_server;
  public Double price;           //Цена
  public double for_amount_unit; //  количество единиц измерения за которое куказана цена
  public long for_id_unit;       //  единица измерения за которую указана цена
  public Double amount;          //Количество
  public long id_unit;           //  единица измерения количества
  public Double cost;            //Стоимость
  public int is_delete;

  public Detail(Cursor cursor)
  {
    _id             = cursor.getLong(cursor.getColumnIndex("_id"));
    if(!cursor.isNull(cursor.getColumnIndex("price")))
      price         = cursor.getDouble(cursor.getColumnIndex("price"));
    for_amount_unit = cursor.getDouble(cursor.getColumnIndex("for_amount_unit"));
    for_id_unit     = cursor.getLong(  cursor.getColumnIndex("for_id_unit"));
    if(!cursor.isNull(cursor.getColumnIndex("amount")))
      amount          = cursor.getDouble(cursor.getColumnIndex("amount"));
    id_unit         = cursor.getLong(  cursor.getColumnIndex("id_unit"));
    if(!cursor.isNull(cursor.getColumnIndex("cost")))
      cost          = cursor.getDouble(cursor.getColumnIndex("cost"));
  }
  public Detail(long __id_user_account, long __id_purchase, long __id_type, Long _id_server,
                Double _price, double _for_amount_unit, long _for_id_unit,
                Double _amount, long _id_unit, Double _cost, int _is_delete )
  {
    _id_user_account = __id_user_account;
    _id_purchase     = __id_purchase;
    _id_type         = __id_type;
    id_server        = _id_server;
    price            = _price;
    for_amount_unit  = _for_amount_unit;
    for_id_unit      = _for_id_unit;
    amount           = _amount;
    id_unit          = _id_unit;
    cost             = _cost;
    is_delete        = _is_delete;
  }

  public long insertDateBase(SQLiteDatabase db)
  {
    ContentValues values = new ContentValues();
    //values.put("_id", _id);
    values.put("_id_user_account", _id_user_account );
    values.put("_id_purchase"    , _id_purchase     );
    values.put("_id_type"        , _id_type         );
    if(id_server != null)
      values.put("id_server"     , id_server        );
    if(price != null)
      values.put("price"         , price            );
    values.put("for_amount_unit" , for_amount_unit  );
    values.put("for_id_unit"     , for_id_unit      );
    if(amount != null)
      values.put("amount"        , amount           );
    values.put("id_unit"         , id_unit          );
    if(cost != null)
      values.put("cost"          , cost             );
    values.put("is_delete"       , is_delete        );
    return db.insert(table_name, null, values);
  }

  public boolean calcCost(boolean fl_recalc)
  {
    boolean success = false;
    //стоимость = (цена/за_кол_единиц) * кол_единиц * (множитель_единиц/за_множитель_единиц);
    if(fl_recalc || cost == null)
    {
      if(price != null && amount != null)
      {
        Unit o_for_amount_unit = new Unit(for_id_unit);
        Unit o_unit = new Unit(id_unit);
        cost = (price/for_amount_unit)*amount*(o_unit.multiplier/o_for_amount_unit.multiplier);
        success = true;
      }
    }
    else
      success = true;
    return success;
  }
  public boolean calcPrice()
  {
    //стоимость = (цена/за_кол_единиц) * кол_единиц * (множитель_единиц/за_множитель_единиц);
    return price != null;
  }
  public boolean calcAmount()
  {
    //стоимость = (цена/за_кол_единиц) * кол_единиц * (множитель_единиц/за_множитель_единиц);
    return amount != null;
  }
  static public String formatmoney(double money)
  {
    //Locale rus = new Locale("ru", "RU");
    NumberFormat r_format = NumberFormat.getCurrencyInstance();
    return r_format.format(money);
  }
}
