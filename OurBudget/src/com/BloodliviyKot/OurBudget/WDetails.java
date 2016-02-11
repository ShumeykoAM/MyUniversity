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
import com.BloodliviyKot.OurBudget.Dialogs.*;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.I_Transaction;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.SQLTransaction;
import com.BloodliviyKot.tools.DataBase.entitys.Detail;
import com.BloodliviyKot.tools.DataBase.entitys.Purchase;
import com.BloodliviyKot.tools.DataBase.entitys.Unit;

import java.util.ArrayList;

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
  private DetailsAdapter list_adapter;

  private DetailParamsDialog detailDialog;
  private Detail detail_for_dialog;

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
    list_adapter = new DetailsAdapter(this, R.layout.details_item, cursor,
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
    detail_for_dialog = new Detail(cursor);
    detailDialog = new DetailParamsDialog(this, oh, db);
    detailDialog.use(getFragmentManager(), "d1", detail_for_dialog);
  }
  //Обработчик диалога переметров детали
  @Override
  public void onResult(RESULT code, Intent data)
  {
    if(code == RESULT.OK)
    {
      if(detail_for_dialog.update(detailDialog.detail, db))
      {
        cursor.requery();
        list_adapter.notifyDataSetInvalidated();
        calcCaptionStatus();
      }
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

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    switch(requestCode)
    {
      case R.layout.mark_types:
        if(resultCode == RESULT_OK)
        {
          //Выбрали товары и услуги, теперь добавляем или обновляем их в текущую покупку,
          //  а те с которых сняли галочку удаляем (помечаем как удаленные), или восстанавливаем если они в удаленных
          final ArrayList<DialogParamsSelectedType> selected = data.getParcelableArrayListExtra("Selected");
          Purchase.STATE_PURCHASE state_purchase = Purchase.STATE_PURCHASE.getSTATE_PURCHASE(data.getExtras().getInt("StatePurchase"));
          SQLTransaction sql_transaction = new SQLTransaction(db, new I_Transaction(){
            @Override
            public boolean trnFunc()
            {
              //Пометим как удаленные те детали с которых сняли галочку (перебираем в базе все записи и ищем
              //  их в selected, если нету значит удалили
              String exist_details_query_params[] ={ Long.toString(_id_purchase)};
              Cursor exist_details = db.rawQuery(oh.getQuery(EQ.DETAILS), exist_details_query_params);
              label_exist:
              for(boolean status=exist_details.moveToFirst(); status; status=exist_details.moveToNext())
              {
                Detail exist_detail = new Detail(exist_details);
                for(DialogParamsSelectedType selected_type : selected)
                {
                  if(exist_detail._id_type == selected_type.id_type)
                    continue label_exist;
                }
                if(!exist_detail.is_delete)
                {
                  //Пометим как удаленная
                  Detail detail = exist_detail.clone();
                  detail.is_delete = true;
                  exist_detail.update(detail, db);
                }
              }
              //Снимем признак удаленности и изменим количество с тех деталей которые находятся в удаленных


              //Добавим новые делали (которые есть в selected и нет среди неудаленных или удаленных)




              //Изменим количество с деталей которые в неудаленных

              /*
              for(DialogParamsSelectedType selected_type : selected)
              {
                //Найдем последнюю оплаченную, если нету то неоплаченную запись с данным видом товара и
                //  возьмем от туда цену
                Double last_price = null;
                Cursor cursor_last_price = db.rawQuery(oh.getQuery(EQ.LAST_PRICE),
                  new String[]{new Long(selected_type.id_type).toString()});
                long id_unit_for = selected_type.id_unit;
                for(boolean status=cursor_last_price.moveToFirst(); status; status = cursor_last_price.moveToNext())
                {
                  double price = cursor_last_price.getDouble(cursor_last_price.getColumnIndex("price"));
                  long id_unit = cursor_last_price.getLong(cursor_last_price.getColumnIndex("id_unit"));
                  if(selected_type.id_unit == id_unit)
                  {
                    last_price = new Double(price);
                    break;
                  }
                  else
                  {
                    Unit selected_unit = new Unit(selected_type.id_unit);
                    Unit unit = new Unit(id_unit);
                    if(selected_unit._id_group == unit._id_group)
                    {
                      last_price = new Double(price);
                      id_unit_for = id_unit;
                      break;
                    }
                  }
                }
                Detail detail = new Detail(UserAccount.getIDActiveUserAccount(oh, db), id_purchase[0],
                  selected_type.id_type, null, last_price, 1, id_unit_for, selected_type.count,
                  selected_type.id_unit, null, 0);
                detail.calcCost(true);
                detail.insertDateBase(db);

              }*/
              return true;
            }
          });
          if(sql_transaction.runTransaction())
          {
            cursor.requery();
            list_adapter.notifyDataSetChanged();

            /*
            int position, count;
            for(position = 0, count=list_purchases.getCount();
                position<count && id_purchase[0] != list_purchases.getItemIdAtPosition(position);
                ++position );
            final int pos = position;
            list_purchases.post(new Runnable()
            {
              @Override
              public void run()
              {
                list_purchases.smoothScrollToPosition(pos);
              }
            });
            //Переходим в окно товаров и услуг данной покупки
            Intent intent = new Intent(this, WDetails.class);
            intent.putExtra(getString(R.string.intent_purchases_id), id_purchase[0]);
            startActivityForResult(intent, R.layout.details); //Запуск активности с onActivityResult
            */
          }
        }
        break;
    }
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
      else
        tv_cost.setTextColor(_context.getResources().getColor(R.color.detail_cost));
    }
  }

}










