package com.BloodliviyKot.OurBudget;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.FilterQueryProvider;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

public class TypesCursorTuning
  implements FilterQueryProvider, SearchView.OnQueryTextListener
{
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;
  private Cursor cursor[];
  private SimpleCursorAdapter list_adapter;

  public TypesCursorTuning(MySQLiteOpenHelper _oh, SQLiteDatabase _db,
                           Cursor _cursor[], SimpleCursorAdapter _list_adapter)
  {
    oh = _oh;
    db = _db;
    cursor = _cursor;
    list_adapter = _list_adapter;
  }

  public static Cursor getFullCursor(MySQLiteOpenHelper _oh, SQLiteDatabase _db)
  {
    Cursor result = _db.rawQuery(_oh.getQuery(EQ.USER_ACCOUNT_COUNT), null);
    int count = result.getCount();
    result.close();
    if(count > 0)
      result = _db.rawQuery(_oh.getQuery(EQ.TYPES_USER_ACC), null);
    else
      result = _db.rawQuery(_oh.getQuery(EQ.TYPES_USER_NOT_ACC), null);
    return result;
  }

  //Фильтруем отображаемый список согласно тексту написанному в строке поиска
  //Для курсор_адаптера задаем новый Cursur с отфильтрованным набором данных
  @Override
  public Cursor runQuery(CharSequence constraint)
  {
    if(constraint.toString().compareTo("") == 0)
      return getFullCursor(oh, db);
    else
    {
      Cursor c = db.rawQuery(oh.getQuery(EQ.USER_ACCOUNT_COUNT), null);
      int count = c.getCount();
      if(count > 0)
        return cursor[0] = db.rawQuery(oh.getQuery(EQ.TYPES_USER_ACC_LIKE_NAME),
          new String[]{"%" + constraint + "%"});
      else
        return cursor[0] = db.rawQuery(oh.getQuery(EQ.TYPES_USER_NOT_ACC_LIKE_NAME),
          new String[]{"%" + constraint + "%"});
    }
  }
  @Override
  public boolean onQueryTextSubmit(String query)
  {
    return false;
  }
  //Обрабатываем изменение текста в строке поиска
  @Override
  public boolean onQueryTextChange(String newText)
  {
    list_adapter.getFilter().filter(newText);
    return true;
  }

}
