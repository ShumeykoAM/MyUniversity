package com.BloodliviyKot.tools.DataBase.entitys;

import android.database.Cursor;

import java.text.NumberFormat;

public class Detail
{
  public static final String table_name = "detail";
  public Long _id;
  public Double price;           //Цена
  public double for_amount_unit; //  количество единиц измерения за которое куказана цена
  public long for_id_unit;       //  единица измерения за которую указана цена
  public Double amount;          //Количество
  public long id_unit;           //  единица измерения количества
  public Double cost;            //Стоимость

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
  public Detail(Double _price, double _for_amount_unit, long _for_id_unit,
                Double _amount, long _id_unit, Double _cost)
  {
    Double price = _price;
    double for_amount_unit = _for_amount_unit;
    long for_id_unit = _for_id_unit;
    Double amount = _amount;
    long id_unit = _id_unit;
    Double cost = _cost;
  }

  public boolean calcCost(boolean fl_recalc)
  {
    boolean success = false;
    //стоимость = (цена/за_кол_единиц) * кол_единиц * (множитель_единиц/за_множитель_единиц);
    if(fl_recalc || cost == null)
    {
      if(price != null && amount != null)
      {
        Unit o_for_amount_unit = Unit.units[(int)for_amount_unit];
        Unit o_unit = Unit.units[(int)id_unit];
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
