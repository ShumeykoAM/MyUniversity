package com.BloodliviyKot.OurBudget;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.BloodliviyKot.OurBudget.Dialogs.DetailParamsDialog;
import com.BloodliviyKot.OurBudget.Dialogs.I_DialogResult;
import com.BloodliviyKot.OurBudget.Dialogs.PurchaseDateTimeDialog;
import com.BloodliviyKot.OurBudget.Dialogs.RESULT;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Detail;
import com.BloodliviyKot.tools.DataBase.entitys.Purchase;
import com.BloodliviyKot.tools.DataBase.entitys.Unit;

public class WDetails
  extends Activity
  implements AdapterView.OnItemClickListener, I_DialogResult
{
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;
  private long _id_purchase;
  private ListView list_details;
  private TextView sub_caption;
  private TextView status;
  private Cursor cursor;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.details);

    list_details = (ListView)findViewById(R.id.details_list_details);
    status = (TextView)findViewById(R.id.details_status);
    sub_caption = (TextView)findViewById(R.id.details_caption);
    list_details.setOnItemClickListener(this);

    //Читаем параметры переданные из родительской активности
    Bundle extras = getIntent().getExtras();
    _id_purchase = extras.getLong(getString(R.string.intent_purchases_id));

    //Создаем помощник управления БД
    oh = new MySQLiteOpenHelper(getApplicationContext());
    db = oh.getWritableDatabase();
    //Cursor обязательно должен содержать _id иначе SimpleCursorAdapter не заработает
    cursor = db.rawQuery(oh.getQuery(EQ.DETAILS), new String[]{Long.toString(_id_purchase)});
    int to[] = {R.id.details_item_type, R.id.details_item_amount, R.id.details_item_cost};
    DetailsAdapter list_adapter = new DetailsAdapter(this, R.layout.details_item, cursor,
      new String[]{}, to);
    list_details.setAdapter(list_adapter);
    calcCaptionStatus();

    registerForContextMenu(list_details);
  }
  private void calcCaptionStatus()
  {
    String for_date_time = getString(R.string.details_sub_caption_date_time_plan);
    Purchase purchase = Purchase.getPurhaseFromId(_id_purchase, db, oh);
    if(purchase.date_time != 0)
    {
      String result_date_time[] = new String[2];
      PurchaseDateTimeDialog.getStringDateTime(purchase.date_time, result_date_time, getApplicationContext(), true);
      if(purchase.state == Purchase.STATE_PURCHASE.PLAN)
        for_date_time += " " + getString(R.string.details_sub_caption_date_time_on) + " " + result_date_time[0] +
          " " + getString(R.string.details_sub_caption_date_time_on) + " " + result_date_time[1];
      else
        for_date_time = getString(R.string.details_sub_caption_date_time_exec) + " " + result_date_time[0] +
          " " + getString(R.string.details_sub_caption_date_time_in) + " " + result_date_time[1];
    }
    double total = 0;
    for(boolean status=cursor.moveToFirst(); status; status=cursor.moveToNext())
    {
      Detail detail = new Detail(cursor);
      if(detail.calcCost(false))
        total += detail.cost;
    }
    sub_caption.setText(for_date_time);
    status.setText("Итого на сумму " + Detail.formatMoney(total));
  }

  @Override //Выбрали деталь, просмотрим ее характеристики
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    cursor.moveToPosition(position);
    Detail detail = new Detail(cursor);
    DetailParamsDialog detailDialog = new DetailParamsDialog(this, oh, db);
    detailDialog.use(getFragmentManager(), "d1", detail);
  }
  //Обработчик диалога переметров детали
  @Override
  public void onResult(RESULT code, Intent data)
  {
    if(code == RESULT.OK)
    {


    }
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
        startActivityForResult(intent, R.layout.mark_types); //Запуск активности с onActivityResult
        return true;
      case R.id.m_details_plan:


        break;
      case R.id.m_details_execute:


        break;
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
      String amount = "";
      String cost   = "";
      if(detail.calcAmount())
      {
        if(detail.amount == (int)(double)detail.amount)
          amount = Integer.toString((int)(double)detail.amount) + new Unit(detail.id_unit).name;
        else
          amount = new Double(detail.amount).toString() + new Unit(detail.id_unit).name;
      }
      boolean cost_is_null = false;
      if(detail.calcCost(false))
      {
        cost = Detail.formatMoney(detail.cost);
        if(detail.cost == 0.0)
          cost_is_null = true;
      }
      else
      {
        cost = Detail.formatMoney(0.0);
        cost_is_null = true;
      }
      //Сопоставляем
      TextView tv_name   = (TextView)_view.findViewById(R.id.details_item_type);
      TextView tv_amount = (TextView)_view.findViewById(R.id.details_item_amount);
      TextView tv_cost   = (TextView)_view.findViewById(R.id.details_item_cost);
      tv_name.setText(name);
      tv_amount.setText(amount);
      tv_cost.setText(cost);
      if(cost_is_null)
        tv_cost.setTextColor(_context.getResources().getColor(R.color.detail_cost_null));
    }
  }

}










