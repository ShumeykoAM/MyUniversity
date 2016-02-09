package com.BloodliviyKot.tools.DataBase.entitys;


import android.database.Cursor;
import android.database.MatrixCursor;

//Единицы измерения (в нашей программе имеют фиксированный набор, поэтому
//  не содержатся в БД)
public class Unit
{
  public long _id;
  public String name;
  public double multiplier;
  public long _id_group; //группа например г-граммы в которую войдут Кг, г
  //public String nameGroup; //Объем, длина, количество, вес и тд.

  private static final String[] columns = new String[] { "_id", "name", "multiplier", "_id_group" };
  private static MatrixCursor cursor;

  public Unit(long __id)
  {
    //Ну как бы position всегда равно _id-1 в курсоре cursor
    if(cursor.moveToPosition((int)__id-1))
    {
      _id        = cursor.getLong  (cursor.getColumnIndex("_id"));
      name       = cursor.getString(cursor.getColumnIndex("name"));
      multiplier = cursor.getDouble(cursor.getColumnIndex("multiplier"));
      _id_group  = cursor.getLong  (cursor.getColumnIndex("_id_group"));
    }
    else
    {
      _id = -1;
      name = null;
      multiplier = 0.0;
      _id_group = -1;
    }
  }
  private Unit(int __id, String _name, double _multiplier, long __id_group)
  {
    _id = __id;
    name = _name;
    multiplier = _multiplier;
    if(__id_group == 0)
      _id_group = _id;
    else
      _id_group = __id_group;
  }
  public static Cursor getCursor()
  {
    MatrixCursor copy_cursor = new MatrixCursor(columns);
    for(boolean status=cursor.moveToFirst(); status; status = cursor.moveToNext())
    {
      Unit unit = new Unit(cursor.getLong(cursor.getColumnIndex("_id")));
      copy_cursor.addRow(new Object[]{unit._id, unit.name, unit.multiplier, unit._id_group});
    }
    return copy_cursor;
  }
  public static Cursor cursorForGroup(long _id_group)
  {
    MatrixCursor g_cursor = new MatrixCursor(columns);
    for(boolean status=cursor.moveToFirst(); status; status = cursor.moveToNext())
    {
      Unit unit = new Unit(cursor.getLong(cursor.getColumnIndex("_id")));
      if(unit._id_group == _id_group)
        g_cursor.addRow(new Object[]{unit._id, unit.name, unit.multiplier, _id_group});
    }
    return g_cursor;
  }
  static
  {
    //Фиксированный набор единиц измерения

    cursor = new MatrixCursor(columns);

    long id = 1/*0 не используем*/, g_id;
    //штуки
    g_id = id;
    cursor.addRow(new Object[]{id++, "шт.", 1, g_id});
    cursor.addRow(new Object[]{id++, "дес.", 10, g_id}); //десяток //10шт в дес
    //граммы
    g_id = id;
    cursor.addRow(new Object[]{id++, "г", 0.001, g_id}); //1000г в Кг
    cursor.addRow(new Object[]{id++, "Кг", 1, g_id});
    //литры
    g_id = id;
    cursor.addRow(new Object[]{id++, "л", 1, g_id});
    cursor.addRow(new Object[]{id++, "мл", 0.001, g_id}); //1000мл в л
    //метры
    g_id = id;
    cursor.addRow(new Object[]{id++, "м", 1, g_id});
    cursor.addRow(new Object[]{id++, "см", 0.01, g_id}); //100см в м
    //Киловаты в час
    g_id = id;
    cursor.addRow(new Object[]{id++, "КВт.ч", 1, g_id});
    //кубометры
    g_id = id;
    cursor.addRow(new Object[]{id++, "куб.м", 1, g_id});
    //тонны
    g_id = id;
    cursor.addRow(new Object[]{id++, "тонн", 1, g_id});
    //Нету единиц измерения (развлечения например не в чем измерять)
    g_id = id;
    cursor.addRow(new Object[]{id++, "", 1, g_id});
  }

}
