package com.BloodliviyKot.OurBudget;

import android.app.Activity;
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
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

public class WMarkDetails
  extends Activity
  implements AdapterView.OnItemClickListener
{
  private SQLiteDatabase db;

  private SearchView search;
  private ListView list_types;

  private Cursor cursor;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mark_details);

    search = (SearchView)findViewById(R.id.mark_details_search);
    list_types = (ListView)findViewById(R.id.mark_details_list_types);

    //Читаем параметры переданные из родительской активности
    Bundle extras = getIntent().getExtras();
    //account_id = extras.getLong(getString(R.string.intent_purchases_id));

    //Создаем помощник управления БД
    db = (new MySQLiteOpenHelper(getApplicationContext())).getWritableDatabase();
    //Cursor обязательно должен содержать _id иначе SimpleCursorAdapter не заработает

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
    getMenuInflater().inflate(R.menu.mark_details_menu, menu);
    return true;
  }

  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_mark_details_add_type:
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


}