package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.BloodliviyKot.OurBudget.Dialogs.I_DialogResult;
import com.BloodliviyKot.OurBudget.Dialogs.RESULT;
import com.BloodliviyKot.OurBudget.Dialogs.TypeDialog;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Type;
import com.BloodliviyKot.tools.DataBase.entitys.Unit;

public class WTypes
  extends Activity
  implements I_DialogResult, FilterQueryProvider, SearchView.OnQueryTextListener
{
  private SearchView search;
  private ListView list_types;

  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;
  private Cursor cursor;
  private SimpleCursorAdapter list_adapter;
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

    cursor = db.rawQuery(oh.getQuery(EQ.USER_ACCOUNT_COUNT), null);
    int count = cursor.getCount();
    cursor.close();
    if(count > 0)
      cursor = db.rawQuery(oh.getQuery(EQ.TYPES_USER_ACC), null);
    else
      cursor = db.rawQuery(oh.getQuery(EQ.TYPES_USER_NOT_ACC), null);

    list_adapter = new TypesAdapter(getApplicationContext(), R.layout.types_item,
      cursor, new String[]{"name"},
      new int[]{R.id.types_item_name});
    list_types.setAdapter(list_adapter);

    list_adapter.setFilterQueryProvider(this);
    search.setOnQueryTextListener(this);
  }
  //Фильтруем отображаемый список согласно тексту написанному в строке поиска
  //Для курсор_адаптера задаем новый Cursur с отфильтрованным набором данных
  @Override
  public Cursor runQuery(CharSequence constraint)
  {
    return cursor = db.rawQuery(oh.getQuery(EQ.TYPES_USER_ACC_LIKE_NAME), new String[]{"%" + constraint + "%"});
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

  //Создаем меню
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    //Создаем меню из ресурса
    getMenuInflater().inflate(R.menu.types_menu, menu);
    return true;
  }

  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_types_add:
        Type type = new Type();
        type.name = search.getQuery().toString();
        type.id_unit = 1; //Штуки наверное самое распрастранненная мера
        TypeDialog type_dialog = new TypeDialog(this, type, TypeDialog.REGIME.NEW);
        type_dialog.show(getFragmentManager(), null);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  @Override
  public void onResult(RESULT code)
  {
    if(code == RESULT.OK)
    {
      cursor.requery();
      list_adapter.notifyDataSetChanged();
    }
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
      long id_unit = _cursor.getLong(_cursor.getColumnIndex("id_unit"));
      //Сопоставляем
      ((TextView)_view.findViewById(R.id.types_item_unit)).setText("     " + new Unit(id_unit).name);
    }
  }


}
