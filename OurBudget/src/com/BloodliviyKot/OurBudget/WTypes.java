package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Unit;

public class WTypes
  extends Activity
{
  private SearchView search;
  private ListView list_types;

  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.types);

    search = (SearchView)findViewById(R.id.types_search);
    list_types = (ListView)findViewById(R.id.types_list_types);

    oh = new MySQLiteOpenHelper(getApplicationContext());
    db = oh.getWritableDatabase();

    Cursor cursor = db.rawQuery(oh.getQuery(EQ.USER_ACCOUNT_COUNT), null);
    int count = cursor.getCount();
    cursor.close();
    if(count > 0)
      cursor = db.rawQuery(oh.getQuery(EQ.TYPES_USER_ACC), null);
    else
      cursor = db.rawQuery(oh.getQuery(EQ.TYPES_USER_NOT_ACC), null);

    SimpleCursorAdapter list_adapter = new TypesAdapter(getApplicationContext(), R.layout.types_item,
      cursor, new String[]{"name"},
      new int[]{R.id.types_item_name});
    list_types.setAdapter(list_adapter);


  }
  private class TypesAdapter
    extends SimpleCursorAdapter
  {
    public TypesAdapter(Context context, int layout, Cursor c, String[] from, int[] to)
    {
      super(context, layout, c, from, to);
    }
    @Override
    public void bindView(View _view, Context _context, Cursor _cursor)
    {
      //Здесь заполнются данными поля указанные в конструкторе
      super.bindView(_view, _context, _cursor);
      //А сдесь заполним поля которые нужно вычислять
      int id_unit = _cursor.getInt(_cursor.getColumnIndex("id_unit"));
      //Сопоставляем
      ((TextView)_view.findViewById(R.id.types_item_unit)).setText("     " + Unit.units[id_unit].name);
    }
  }

}
