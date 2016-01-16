package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.BloodliviyKot.entitys.Detail;
import com.BloodliviyKot.entitys.Unit;

public class WDetails
  extends Activity
  implements AdapterView.OnItemClickListener, DetailDialog.I_DetailDialogResult
{
  private SQLiteDatabase db;
  private long account_id;
  private ListView list_details;
  private Cursor cursor;
  private DetailDialog detailDialog;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.details);

    list_details = (ListView)findViewById(R.id.details_list_details);
    list_details.setOnItemClickListener(this);

    //Читаем параметры переданные из родительской активности
    Bundle extras = getIntent().getExtras();
    account_id = extras.getLong(getString(R.string.intent_purchases_id));

    //Создаем помощник управления БД
    db = (new MySQLiteOpenHelper(getApplicationContext())).getWritableDatabase();
    //Cursor обязательно должен содержать _id иначе SimpleCursorAdapter не заработает

    String query =
      "SELECT detail._id, detail.price, detail.for_amount_unit, detail.for_id_unit, " +
      "detail.amount, detail.id_unit, detail.cost, type.name FROM detail, type " +
      "WHERE detail._id_purchase = ? AND type._id=detail._id_type;";
    cursor = db.rawQuery(query, new String[]{Long.toString(account_id)});
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
  public void onResult(DetailDialog.RESULT code)
  {
    int fdfd=0;
    fdfd++;
  }

  //Создаем меню
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    //Создаем меню из ресурса
    getMenuInflater().inflate(R.menu.movements_menu, menu);
    return true;
  }

  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_purchases_add:
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
      inflater.inflate(R.menu.movements_context_list_movements, menu);

    }
  }
  //Обрабатываем нажатие выбор пункта контекстного меню
  @Override
  public boolean onContextItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_account_context_list_acc_attach_to_authentication:
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
          amount = Integer.toString((int)(double)detail.amount) + Unit.units[(int)detail.id_unit].name;
        else
          amount = detail.amount.toString() + Unit.units[(int)detail.id_unit].name;
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










