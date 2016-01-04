package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

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
    "SELECT movement._id, type_movement.name, type_movement.trend FROM movement, type_movement " +
    "WHERE movement._id_type_movement=type_movement._id " +
    "AND type_movement._id_account=? " +
    "AND type_movement._paren_type_movement IS NULL;";
    Cursor cursor = db.rawQuery(query, new String[]{Long.toString(account_id)});
    String from[] = {"trend", "name"};//, "info", "status"};
    int to[] = {R.id.movements_item_trend, R.id.movements_item_name,
                R.id.movements_item_info, R.id.movements_item_state};
    Movements_CursorAdapter list_adapter = new Movements_CursorAdapter(this, R.layout.movements_item, cursor,
      from, to);
    list_movements.setAdapter(list_adapter);

  }


  //Переопределим SimpleCursorAdapter что бы форматировать данные из базы нужным образом
  private static class Movements_CursorAdapter
    extends SimpleCursorAdapter
  {
    public Movements_CursorAdapter(Context _context, int _layout, Cursor _c, String[] _from, int[] _to)
    {
      super(_context, _layout, _c, _from, _to);
    }
    @Override
    public void bindView(View _view, Context _context, Cursor _cursor)
    {
      int trend = _cursor.getInt(_cursor.getColumnIndex("trend"));
      String name = _cursor.getString(_cursor.getColumnIndex("name"));

      ImageView item_trend = (ImageView)_view.findViewById(R.id.movements_item_trend);
      TextView item_name = (TextView)_view.findViewById(R.id.movements_item_name);
      TextView item_info = (TextView)_view.findViewById(R.id.movements_item_info);
      ImageView item_state = (ImageView)_view.findViewById(R.id.movements_item_state);

      if(trend == 1)
        item_trend.setImageResource(R.drawable.ic_movement_increase);
      else
        item_trend.setImageResource(R.drawable.ic_movement_reduce);
      item_name.setText(name);
      item_info.setText("Завтра");

      if(trend == 1)
        item_state.setImageResource(R.drawable.ic_exclamation);
      else
        item_state.setImageResource(R.drawable.ic_tick);
    }
  }

}










