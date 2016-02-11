package com.BloodliviyKot.tools.DataBase.entitys;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
  public double amount;          //Количество
  public long id_unit;           //  единица измерения количества
  public Double cost;            //Стоимость
  public boolean is_delete;

  public Detail(Cursor cursor)
  {
    _id              = cursor.getLong(  cursor.getColumnIndex("_id"));
    _id_user_account = cursor.getLong(  cursor.getColumnIndex("_id_user_account"));
    _id_purchase     = cursor.getLong(  cursor.getColumnIndex("_id_purchase"));
    _id_type         = cursor.getLong(  cursor.getColumnIndex("_id_type"));
    if(!cursor.isNull(cursor.getColumnIndex("id_server")))
      id_server      = cursor.getLong(  cursor.getColumnIndex("id_server"));
    if(!cursor.isNull(cursor.getColumnIndex("price")))
      price          = cursor.getDouble(cursor.getColumnIndex("price"));
    for_amount_unit  = cursor.getDouble(cursor.getColumnIndex("for_amount_unit"));
    for_id_unit      = cursor.getLong(  cursor.getColumnIndex("for_id_unit"));
    if(!cursor.isNull(cursor.getColumnIndex("amount")))
      amount         = cursor.getDouble(cursor.getColumnIndex("amount"));
    id_unit          = cursor.getLong(  cursor.getColumnIndex("id_unit"));
    if(!cursor.isNull(cursor.getColumnIndex("cost")))
      cost           = cursor.getDouble(cursor.getColumnIndex("cost"));
    is_delete        = cursor.getInt(   cursor.getColumnIndex("is_delete")) == 1;
  }
  public Detail(long __id_user_account, long __id_purchase, long __id_type, Long _id_server,
                Double _price, double _for_amount_unit, long _for_id_unit,
                Double _amount, long _id_unit, Double _cost, boolean _is_delete )
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
    values.put("amount"          , amount           );
    values.put("id_unit"         , id_unit          );
    if(cost != null)
      values.put("cost"          , cost             );
    values.put("is_delete"       , is_delete ? 1 : 0);
    return db.insert(table_name, null, values);
  }

  public boolean calcCost(boolean fl_recalc)
  {
    boolean success = false;
    //стоимость = (цена/за_кол_единиц) * кол_единиц * (множитель_единиц/за_множитель_единиц);
    if(fl_recalc || cost == null)
    {
      if(price != null)
      {
        Unit o_for_amount_unit = new Unit(for_id_unit);
        Unit o_unit = new Unit(id_unit);
        cost = (price/for_amount_unit)*amount*(o_unit.multiplier/o_for_amount_unit.multiplier);
        cost = new BigDecimal(cost).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        success = true;
      }
    }
    else
      success = true;
    return success;
  }
  public boolean calcPrice(boolean fl_recalc)
  {
    boolean success = false;
    //цена = за_кол_единиц * стоимость / (кол_единиц * (множитель_единиц/за_множитель_единиц));
    if(fl_recalc || price == null)
    {
      if(cost != null)
      {
        Unit o_for_amount_unit = new Unit(for_id_unit);
        Unit o_unit = new Unit(id_unit);
        price = for_amount_unit * cost / (amount * (o_unit.multiplier/o_for_amount_unit.multiplier));
        price = new BigDecimal(price).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        success = true;
      }
    }
    else
      success = true;
    return success;
  }
  public boolean calcAmount()
  {
    //стоимость = (цена/за_кол_единиц) * кол_единиц * (множитель_единиц/за_множитель_единиц);
    return true;
  }

  public Detail clone()
  {
    Detail result = new Detail(_id_user_account, _id_purchase, _id_type, id_server, price, for_amount_unit,
      for_id_unit, amount, id_unit, cost, is_delete);
    result._id = _id;
    return result;
  }

  public void calcAll()
  {
    if(cost == null)
    {
      if(price == null)
        price = new Double(0.0);
      calcCost(false);
    }
    if(price == null)
    {
      price = new Double(0.0);

    }

  }
  //Найдем последнюю цену и единицы измерения для этой цены по единицам измерения количества id_unit
  public boolean find_and_fill_last_price_and_unit_for(SQLiteDatabase db, MySQLiteOpenHelper oh)
  {
    boolean result = false;
    //Находим все последние цены для вида товара
    Cursor cursor = db.rawQuery(oh.getQuery(EQ.LAST_PRICE), new String[]{new Long(_id_type).toString()});
    for(boolean status=cursor.moveToFirst(); status; status = cursor.moveToNext())
    {
      boolean found = false;
      Double price = cursor.isNull(cursor.getColumnIndex("price")) ? null :
        cursor.getDouble(cursor.getColumnIndex("price"));
      double for_amount_unit = cursor.getDouble(cursor.getColumnIndex("for_amount_unit"));
      long for_id_unit       = cursor.getInt(cursor.getColumnIndex("for_id_unit"));
      //Если совпадают единицы измерения
      if(this.id_unit == for_id_unit)
      {
        this.price           = price;
        this.for_amount_unit = for_amount_unit;
        this.for_id_unit     = for_id_unit;
        result = found = true;
      }
      else
      {
        //Если совпадают группы единиц измерения
        Unit c_unit = new Unit(for_id_unit);
        Unit t_unit   = new Unit(this.id_unit);
        if(c_unit._id_group == t_unit._id_group)
        {
          this.price           = price;
          this.for_amount_unit = for_amount_unit;
          this.for_id_unit     = for_id_unit;
          result = found = true;
        }
      }
      if(found && price != null) //Если найденный price не задан, то поищем еще, иначе все что искали нашли
        break;
    }
    return result;
  }

  //Обновляет запись если есть что обновлять
  public boolean update(Detail new_detail, SQLiteDatabase db)
  {
    if(_id == null || new_detail._id == null || !_id.equals(new_detail._id))
      throw new Error();
    ContentValues values = new ContentValues();
    if(_id_user_account != new_detail._id_user_account)
      values.put("_id_user_account", new Long(new_detail._id_user_account).toString());
    if(_id_purchase != new_detail._id_purchase)
      values.put("_id_purchase", new Long(new_detail._id_purchase).toString());
    if(_id_type != new_detail._id_type)
      values.put("_id_type", new Long(new_detail._id_type).toString());
    if(id_server != null && new_detail.id_server == null)
      values.putNull("id_server");
    else if(id_server == null && new_detail.id_server != null ||
            id_server != null && new_detail.id_server != null && id_server.compareTo(new_detail.id_server) != 0)
      values.put("id_server", new Long(new_detail.id_server).toString());
    if(price != null && new_detail.price == null)
      values.putNull("price");
    else if(price == null && new_detail.price != null ||
            price != null && new_detail.price != null && price.compareTo(new_detail.price) != 0)
      values.put("price", new Double(new_detail.price).toString());
    if(for_amount_unit != new_detail.for_amount_unit)
      values.put("for_amount_unit", new Double(new_detail.for_amount_unit).toString());
    if(for_id_unit != new_detail.for_id_unit)
      values.put("for_id_unit", new Long(new_detail.for_id_unit).toString());
    if(amount != new_detail.amount)
      values.put("amount", new Double(new_detail.amount).toString());
    if(id_unit != new_detail.id_unit)
      values.put("id_unit", new Long(new_detail.id_unit).toString());
    if(cost != null && new_detail.cost == null)
      values.putNull("cost");
    else if(cost == null && new_detail.cost != null ||
            cost != null && new_detail.cost != null && cost.compareTo(new_detail.cost) != 0)
      values.put("cost", new Double(new_detail.cost).toString());
    if(is_delete != new_detail.is_delete)
      values.put("is_delete", new Long(new_detail.is_delete ? 1 : 0).toString());
    if(values.size() > 0)
      return db.update(table_name, values, "_id=?", new String[]{new Long(_id).toString()}) == 1;
    else
      return false;
  }

  static public String formatMoney(double money)
  {
    //Locale rus = new Locale("ru", "RU");
    NumberFormat r_format = NumberFormat.getCurrencyInstance();
    return r_format.format(money);
  }
}
