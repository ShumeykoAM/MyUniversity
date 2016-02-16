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
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;

import java.util.ArrayList;

public class WDetails
  extends Activity
  implements AdapterView.OnItemClickListener, I_DialogResult
{
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;
  private long _id_purchase;
  Purchase purchase;
  private ListView list_details;
  private TextView sub_caption;
  private TextView status;
  private Cursor cursor;
  private DetailsAdapter list_adapter;

  private DetailParamsDialog detailDialog;
  private Detail detail_for_dialog;
  private boolean purchase_is_deleted = false;
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
    cursor = db.rawQuery(oh.getQuery(EQ.DETAILS), new String[]{Long.toString(_id_purchase), Long.toString(0)});
    int to[] = {R.id.details_item_type, R.id.details_item_amount, R.id.details_item_cost};
    list_adapter = new DetailsAdapter(this, R.layout.details_item, cursor, new String[]{}, to);
    list_details.setAdapter(list_adapter);
    calcCaptionStatus();

    registerForContextMenu(list_details);
  }
  private void calcCaptionStatus()
  {
    String for_date_time = getString(R.string.details_sub_caption_date_time_plan);
    purchase = Purchase.getPurhaseFromId(_id_purchase, db, oh);
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
    if(purchase.state == Purchase.STATE_PURCHASE.EXECUTE)
    {
      //Скроем не нужные на данный момент пункты меню
      MenuItem shareMenuItem = menu.findItem(R.id.m_details_plan);
      shareMenuItem.setVisible(false);
      shareMenuItem = menu.findItem(R.id.m_details_execute);
      shareMenuItem.setVisible(false);
    }
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

        ArrayList<DialogParamsSelectedType> selected = new ArrayList<DialogParamsSelectedType>();
        String exist_details_query_params[] ={ Long.toString(_id_purchase), Long.toString(0) };
        Cursor exist_details = db.rawQuery(oh.getQuery(EQ.DETAILS), exist_details_query_params);
        for(boolean status=exist_details.moveToFirst(); status; status=exist_details.moveToNext())
        {
          Detail exist_detail = new Detail(exist_details);
          selected.add(new DialogParamsSelectedType(exist_detail._id_type, true, null, null,
            exist_detail.amount, exist_detail.id_unit));
        }
        intent.putParcelableArrayListExtra("Selected", selected);
        startActivityForResult(intent, R.layout.mark_types); //Запуск активности с onActivityResult
        return true;
      case R.id.m_details_plan:
        I_DialogResult dialog_result =  new I_DialogResult()
        {
          @Override
          public void onResult(RESULT code, Intent data)
          {
            if(code == RESULT.OK)
            {
              Purchase change_purchase = purchase.clone();
              change_purchase.date_time = data.getExtras().getLong("date_time");
              purchase.update(change_purchase, db);
              calcCaptionStatus();
            }
          }
        };
        PurchaseDateTimeDialog date_time_dialog = new PurchaseDateTimeDialog(dialog_result,
          new java.util.Date().getTime(), purchase.state == Purchase.STATE_PURCHASE.PLAN);
        date_time_dialog.show(getFragmentManager(), null);
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
          SQLTransaction sql_transaction = new SQLTransaction(db, new I_Transaction(){
            @Override
            public boolean trnFunc()
            {
              //Пометим как удаленные те детали с которых сняли галочку (перебираем в базе все записи и ищем
              //  их в selected, если нету значит удалили
              String exist_details_query_params[] ={ Long.toString(_id_purchase) };
              Cursor exist_details = db.rawQuery(oh.getQuery(EQ.DETAILS_ALL), exist_details_query_params);
              label_exist:
              for(boolean status=exist_details.moveToFirst(); status; status=exist_details.moveToNext())
              {
                Detail exist_detail = new Detail(exist_details);
                for(DialogParamsSelectedType selected_type : selected)
                {
                  if(exist_detail._id_type == selected_type.id_type) //Деталь выбрана и она уже есть в покупке
                  {
                    Detail detail = exist_detail.clone();
                    boolean need_udate = false;
                    //Меняем если нужно количество
                    if(exist_detail.amount != selected_type.amount)
                    {
                      need_udate = true;
                      detail.amount = selected_type.amount;
                    }
                    //Меняем если нужно единицы измерения
                    if(exist_detail.id_unit != selected_type.id_unit)
                    {
                      need_udate = true;
                      detail.id_unit = selected_type.id_unit;
                      //Проверим группу id_unit_for
                      Unit unit_for = new Unit(exist_detail.for_id_unit);
                      Unit unit = new Unit(selected_type.id_unit);
                      //Единицы измерения детали покупки и единицы измерения выделенной деьали в разных группах
                      if(unit_for._id_group != unit._id_group)
                      {
                        //  надо искать последнюю цену и единицы измерения для этой группы
                        detail.find_and_fill_last_price_and_unit_for(db, oh);
                      }
                    }
                    if(need_udate)
                    {
                      detail.calcCost(true);
                    }
                    //Снимем признак удаленности если он установлен
                    if(exist_detail.is_delete)
                    {
                      need_udate = true;
                      detail.is_delete = false;
                    }
                    if(need_udate)
                    {
                      exist_detail.update(detail, db);
                    }
                    continue label_exist;
                  }
                }
                if(!exist_detail.is_delete) //Деталь есть в покупке но она не выбрана (сняли с нее выбор) и не удалена
                {
                  //Пометим как удаленная
                  Detail detail = exist_detail.clone();
                  detail.is_delete = true;
                  exist_detail.update(detail, db);
                }
              }
              //Добавим новые детали (которые есть в selected и нет среди неудаленных или удаленных)
              label_selected:
              for(DialogParamsSelectedType selected_type : selected)
              {
                for(boolean status = exist_details.moveToFirst(); status; status = exist_details.moveToNext())
                {
                  Detail exist_detail = new Detail(exist_details);
                  if(exist_detail._id_type == selected_type.id_type) //Деталь выбрана и она уже есть в покупке
                    continue label_selected;
                }
                //Такой детали в покупке нету, добавим
                Detail detail = new Detail(UserAccount.getIDActiveUserAccount(oh, db), _id_purchase,
                  selected_type.id_type, null, null, 1, selected_type.id_unit, selected_type.amount,
                  selected_type.id_unit, null, false);
                detail.find_and_fill_last_price_and_unit_for(db, oh);
                detail.calcCost(true);
                detail.insertDateBase(db);
              }
              return true;
            }
          });
          if(sql_transaction.runTransaction())
          {
            cursor.requery();
            list_adapter.notifyDataSetChanged();
            calcCaptionStatus();
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
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final long id_detail = list_adapter.getItemId(acmi.position);
        boolean need_delete_detail = false;
        if(cursor.getCount() == 1)
        {
          //Удаляем единственную запись, спросим нужно ли удалить покупку
          ChooseAlert choose_alert = new ChooseAlert(this, "Попытка удалить единственную запись",
            android.R.drawable.ic_dialog_alert, "Удалить покупку?", "Да", "Нет");
          choose_alert.show(new ChooseAlert.I_ChooseAlertHandler(){
            @Override
            public void onClick(ChooseAlert.CHOOSE_BUTTON button)
            {
              if(button == ChooseAlert.CHOOSE_BUTTON.BUTTON1)
              {
                delete_detail(id_detail);
                //Удалим пукупку
                Purchase purchase = Purchase.getPurhaseFromId(_id_purchase, db, oh);
                Purchase new_purchase = purchase.clone();
                new_purchase.is_delete = true;
                purchase.update(new_purchase, db);
                purchase_is_deleted = true;
                finish();
              }
            }
          });
        }
        else
          need_delete_detail = true;
        if(need_delete_detail)
          delete_detail(id_detail);
        return true;
    }
    return super.onContextItemSelected(item);
  }
  private void delete_detail(long id_detail)
  {
    //Пометим как удаленная
    Detail exist_detail = Detail.getDetailFromId(id_detail, db, oh);
    Detail deleted_detail = exist_detail.clone();
    deleted_detail.is_delete = true;
    if(exist_detail.update(deleted_detail, db))
    {
      cursor.requery();
      list_adapter.notifyDataSetChanged();
      calcCaptionStatus();
    }
  }
  @Override
  public void finish()
  {
    Intent ires = new Intent();
    ires.putExtra("PurchaseIsDeleted", purchase_is_deleted);
    setResult(RESULT_OK, ires);
    super.finish();
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










