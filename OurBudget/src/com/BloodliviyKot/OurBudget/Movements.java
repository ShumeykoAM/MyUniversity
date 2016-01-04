package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Movements
  extends Activity
{
  private SQLiteDatabase db;
  private long account_id;
  private ListView list_movements;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.movements);

    list_movements = (ListView)findViewById(R.id.movements_list_movements);

    //Читаем параметры переданные из родительской активности
    Bundle extras = getIntent().getExtras();
    account_id = extras.getLong(getString(R.string.intent_account_id));

    //Создаем помощник управления БД
    db = (new MySQLiteOpenHelper(getApplicationContext())).getWritableDatabase();
    //Cursor обязательно должен содержать _id иначе SimpleCursorAdapter не заработает
    String query =
    "SELECT movement._id, type_movement.name FROM movement, type_movement " +
    "WHERE movement._id_type_movement=type_movement._id " +
    "AND type_movement._id_account=? " +
    "AND type_movement._paren_type_movement IS NULL;";
    Cursor cursor = db.rawQuery(query, new String[]{Long.toString(account_id)});
    ListAdapter list_adapter = new SimpleCursorAdapter(this, R.layout.accounts_item, cursor,
      new String[]{"name", "name"}, new int[]{R.id.accounts_item_name, R.id.accounts_item_balance});
    list_movements.setAdapter(list_adapter);
    //c.requery(); //Обновляет Cursor делая повторный запрос. Устарела, но для наших целей подойдет
    //  актуально если в БД изменились данные. Нужно переходить на LoaderManager CursorLoader
    //  позволяющие работать асинхронно.

    //registerForContextMenu(list_accounts);





  }


}










