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

  private static MatrixCursor cursor;

  public Unit(long _id)
  {
    //Ну как бы position всегда равно _id-1
    if(cursor.moveToPosition((int)_id-1))
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
    return cursor;
  }
  public static Cursor cursorForGroup(long _id_group)
  {
    return null;
  }
  static
  {
    //Фиксированный набор единиц измерения
    String[] columns = new String[] { "_id", "name", "multiplier", "_id_group" };
    cursor = new MatrixCursor(columns);

    long id = 1/*0 не используем*/, g_id;
    //штуки
    g_id = id;
    cursor.addRow(new Object[]{id++, "шт.", 1, g_id});
    cursor.addRow(new Object[]{id++, "дес.", 1, g_id}); //десяток
    //граммы
    g_id = id;
    cursor.addRow(new Object[]{id++, "г", 1, g_id});
    cursor.addRow(new Object[]{id++, "Кг", 1, g_id});
    //литры
    g_id = id;
    cursor.addRow(new Object[]{id++, "л", 1, g_id});
    cursor.addRow(new Object[]{id++, "мл", 1, g_id});
    //метры
    g_id = id;
    cursor.addRow(new Object[]{id++, "м", 1, g_id});
    cursor.addRow(new Object[]{id++, "см", 1, g_id});
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
