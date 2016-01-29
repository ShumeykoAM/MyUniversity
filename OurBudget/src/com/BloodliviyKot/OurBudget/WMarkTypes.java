package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import com.BloodliviyKot.OurBudget.Dialogs.I_DialogResult;
import com.BloodliviyKot.OurBudget.Dialogs.RESULT;
import com.BloodliviyKot.OurBudget.Dialogs.TypeDialog;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Type;

public class WMarkTypes
  extends Activity
  implements AdapterView.OnItemClickListener, I_DialogResult
{
  private SearchView search;
  private ListView list_types;

  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;

  private Cursor cursor[];
  private SimpleCursorAdapter list_adapter;
  private TypesCursorTuning types_cursor_tuning;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mark_types);

    search = (SearchView)findViewById(R.id.mark_types_search);
    list_types = (ListView)findViewById(R.id.mark_types_list_types);

    //Читаем параметры переданные из родительской активности
    Bundle extras = getIntent().getExtras();
    //account_id = extras.getLong(getString(R.string.intent_purchases_id));

    //Создаем помощник управления БД
    oh = new MySQLiteOpenHelper(getApplicationContext());
    db = oh.getWritableDatabase();
    cursor = new Cursor[1];
    cursor[0] = TypesCursorTuning.getFullCursor(oh, db);
    list_adapter = new TypesAdapter(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,
      cursor[0], new String[]{"name"},
      new int[]{android.R.id.text1});
    list_types.setAdapter(list_adapter);
    list_types.setOnItemClickListener(this);
    list_types.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    types_cursor_tuning = new TypesCursorTuning(oh, db, cursor, list_adapter);
    list_adapter.setFilterQueryProvider(types_cursor_tuning);
    search.setOnQueryTextListener(types_cursor_tuning);
    registerForContextMenu(list_types);
  }

  @Override //Выбрали тип, поставим шалочку
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {


  }

  //Создаем меню
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    //Создаем меню из ресурса
    getMenuInflater().inflate(R.menu.mark_types_menu, menu);
    return true;
  }

  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_mark_details_add_type:
        Type type = new Type(null, search.getQuery().toString(), null, 1, 0);
        TypeDialog type_dialog = new TypeDialog(this, type, TypeDialog.REGIME.NEW);
        type_dialog.show(getFragmentManager(), null);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  //Контекстное меню для элемента списка типов
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
  {
    super.onCreateContextMenu(menu, v, menuInfo);
    if(v == list_types)
    {
      //Не будем выдввать контекстное меню а сразу перейдем к параметрам детали


    }
  }

  @Override
  public void onResult(RESULT code)
  {
    if(code == RESULT.OK)
    {
      cursor[0].requery();
      list_adapter.notifyDataSetChanged();
      //Добавили новый тип, отметим его сразу


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

    }
  }
}