package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.BloodliviyKot.OurBudget.Dialogs.DetailDialog;
import com.BloodliviyKot.OurBudget.Dialogs.I_DialogResult;
import com.BloodliviyKot.OurBudget.Dialogs.RESULT;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.entitys.Detail;
import com.BloodliviyKot.tools.DataBase.entitys.Unit;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

public class WDetails
  extends Activity
  implements AdapterView.OnItemClickListener, I_DialogResult
{
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;
  private long account_id;
  private ListView list_details;
  private TextView status;
  private Cursor cursor;
  private DetailDialog detailDialog;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.details);

    list_details = (ListView)findViewById(R.id.details_list_details);
    status = (TextView)findViewById(R.id.details_status);
    list_details.setOnItemClickListener(this);

    //Читаем параметры переданные из родительской активности
    Bundle extras = getIntent().getExtras();
    account_id = extras.getLong(getString(R.string.intent_purchases_id));

    //Создаем помощник управления БД
    oh = new MySQLiteOpenHelper(getApplicationContext());
    db = oh.getWritableDatabase();
    //Cursor обязательно должен содержать _id иначе SimpleCursorAdapter не заработает
    cursor = db.rawQuery(oh.getQuery(EQ.DETAILS), new String[]{Long.toString(account_id)});
    int to[] = {R.id.details_item_type, R.id.details_item_price,
                R.id.details_item_amount, R.id.details_item_cost};
    DetailsAdapter list_adapter = new DetailsAdapter(this, R.layout.details_item, cursor,
      new String[]{}, to);
    list_details.setAdapter(list_adapter);

    registerForContextMenu(list_details);
    detailDialog = new DetailDialog(this);
  }

  @Override //Выбрали деталь, просмотрим ее характеристики
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    cursor.requery();
    Detail detail = new Detail(cursor);


    detailDialog.use(getFragmentManager(), "d1", detail);

    int fdfdf=0;
    fdfdf++;
    //Intent intent = new Intent(this, Details.class);
    //intent.putExtra(getString(R.string.intent_purchases_id), id);
    //startActivityForResult(intent, R.layout.details); //Запуск активности с onActivityResult
  }
  @Override
  public void onResult(RESULT code, Intent data)
  {
    int fdfd=0;
    fdfd++;
  }

  //Создаем меню
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    //Создаем меню из ресурса
    getMenuInflater().inflate(R.menu.details_menu, menu);
    return true;
  }

  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_details_add:
        Intent intent = new Intent(this, WMarkTypes.class);
        //intent.putExtra(getString(R.string.intent_purchases_id), id);
        startActivityForResult(intent, R.layout.mark_types); //Запуск активности с onActivityResult
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  //Контекстное меню для элемента списка счетов
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
  {
    super.onCreateContextMenu(menu, v, menuInfo);
    if(v == list_details)
    {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.details_context_list_details, menu);

    }
  }
  //Обрабатываем нажатие выбор пункта контекстного меню
  @Override
  public boolean onContextItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_details_c_delete:
        return true;

    }
    return super.onContextItemSelected(item);
  }

  //Переопределим SimpleCursorAdapter что бы форматировать данные из базы нужным образом
  private static class DetailsAdapter
    extends SimpleCursorAdapter
  {
    public DetailsAdapter(Context _context, int _layout, Cursor _c, String[] _from, int[] _to)
    {
      super(_context, _layout, _c, _from, _to);
    }
    @Override
    public void bindView(View _view, Context _context, Cursor _cursor)
    {
      Detail detail = new Detail(_cursor);
      String name = _cursor.getString(_cursor.getColumnIndex("name"));
      //Получим значения
      String price  = "";
      String amount = "";
      String cost   = "";
      if(detail.calcPrice())
        price = "ц. " + Detail.formatmoney(detail.price);
      if(detail.calcAmount())
      {
        if(detail.amount == (int)(double)detail.amount)
          amount = Integer.toString((int)(double)detail.amount) + new Unit(detail.id_unit).name;
        else
          amount = detail.amount.toString() + new Unit(detail.id_unit).name;
      }
      if(detail.calcCost(false));
        cost = Detail.formatmoney(detail.cost);
      //Сопоставляем
      TextView tv_name   = (TextView)_view.findViewById(R.id.details_item_type);
      TextView tv_price  = (TextView)_view.findViewById(R.id.details_item_price);
      TextView tv_amount = (TextView)_view.findViewById(R.id.details_item_amount);
      TextView tv_cost   = (TextView)_view.findViewById(R.id.details_item_cost);
      tv_name.setText(name);
      tv_price.setText(price);
      tv_amount.setText(amount);
      tv_cost.setText(cost);

      //Представление
      if(cost.length() >= 11)
        tv_cost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
      else
        tv_cost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
      if(price.length() >= 10)
        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
      else
        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
      if(amount.length() >= 8)
        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
      else
        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
      if(name.length() >= 10)
        tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
      else
        tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    }
  }

}










