package com.BloodliviyKot.entitys;


//Единицы измерения (в нашей программе имеют фиксированный набор, поэтому
public class Unit
{
  public long id;
  public String name;
  public double multiplier;
  public long id_group; //группа например г-граммы в которую войдут Кг, мл, г
  public String nameGroup; //Объем, длина, количество, вес и тд.

  public Unit(long _id, String _name, double _multiplier, long _id_group)
  {
    id =_id;
    name = _name;
    multiplier = _multiplier;
    if(_id_group == 0)
      id_group = id;
    else
      id_group = _id_group;
  }

  //Фиксированный набор единиц измерения
  public static Unit units[];
  static
  {
    final int COUNT_UNITS = 1 + 11;
    int id = 1/*0 не используем*/, g_id;
    g_id = id;
    units = new Unit[COUNT_UNITS];
    //штуки
    units[id] = new Unit(id, "шт", 1, 0); id++;
    //граммы
    g_id = id;
    units[id] = new Unit(id, "г", 1, 0); id++;
    units[id] = new Unit(id, "Кг", 1000, g_id); id++;
    //литры
    g_id = id;
    units[id] = new Unit(id, "л", 1, 0); id++;
    units[id] = new Unit(id, "мл", 1000, g_id); id++;
    //десяток
    g_id = id;
    units[id] = new Unit(id, "дес.", 1, 0); id++;
    //метры
    g_id = id;
    units[id] = new Unit(id, "м", 1, 0); id++;
    units[id] = new Unit(id, "см", 0.1, g_id); id++;
    //кубометры
    g_id = id;
    units[id] = new Unit(id, "куб.м", 1, 0); id++;
    //Киловаты в час
    g_id = id;
    units[id] = new Unit(id, "КВт.ч", 1, 0); id++;
    //тонны
    g_id = id;
    units[id] = new Unit(id, "т", 1, 0); id++;
  }

}
