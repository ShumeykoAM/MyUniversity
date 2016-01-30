package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Type;
import com.BloodliviyKot.tools.DataBase.entitys.Unit;
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;

public class WTypes
  extends Activity
  implements I_DialogResult, AdapterView.OnItemClickListener
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
    setContentView(R.layout.types);

    search = (SearchView)findViewById(R.id.types_search);
    list_types = (ListView)findViewById(R.id.types_list_types);

    oh = new MySQLiteOpenHelper(getApplicationContext());
    db = oh.getWritableDatabase();
    cursor = new Cursor[1];
    cursor[0] = TypesCursorTuning.getFullCursor(oh, db);
    list_adapter = new TypesAdapter(getApplicationContext(), R.layout.types_item,
      cursor[0], new String[]{"name"},
      new int[]{R.id.types_item_name});
    list_types.setAdapter(list_adapter);
    list_types.setOnItemClickListener(this);
    types_cursor_tuning = new TypesCursorTuning(oh, db, cursor, list_adapter);
    list_adapter.setFilterQueryProvider(types_cursor_tuning);
    search.setOnQueryTextListener(types_cursor_tuning);
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
        Type type = new Type(UserAccount.getIDActiveUserAccount(oh, db),
          search.getQuery().toString(), null, 1, 0);
        TypeDialog type_dialog = new TypeDialog(this, type, TypeDialog.REGIME.NEW);
        type_dialog.show(getFragmentManager(), null);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  @Override
  public void onResult(RESULT code, Intent data)
  {
    if(code == RESULT.OK)
    {
      cursor[0].requery();
      list_adapter.notifyDataSetChanged();
    }
  }
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    Type type = new Type(list_adapter.getCursor());
    TypeDialog type_dialog = new TypeDialog(this, type, TypeDialog.REGIME.EDIT);
    type_dialog.show(getFragmentManager(), null);
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
