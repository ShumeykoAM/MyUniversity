package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.BloodliviyKot.OurBudget.Dialogs.*;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.I_Transaction;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.SQLTransaction;
import com.BloodliviyKot.tools.DataBase.entitys.Detail;
import com.BloodliviyKot.tools.DataBase.entitys.Purchase;
import com.BloodliviyKot.tools.DataBase.entitys.Purchase.STATE_PURCHASE;
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;

import java.util.ArrayList;

public class WPurchases
  extends Activity
  implements AdapterView.OnItemClickListener, ChooseAlert.I_ChooseAlertHandler
{
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;
  private ListView list_purchases;
  private SimpleCursorAdapter list_adapter;
  private Cursor cursor;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.purchases);
    //Вместо того что бы в манифесте прописать заголовок и иконку приходится динамически их назначать
    //  если в манифесте их назначить то они подменят название и иконку приложения
    getActionBar().setTitle(R.string.purchases_caption);
    getActionBar().setIcon(R.drawable.ic_accounts);
    list_purchases = (ListView)findViewById(R.id.purchases_list_list_purchases);
    list_purchases.setOnItemClickListener(this);

//Для отладки удалим базу
//MySQLiteOpenHelper.debugDeleteDB(getApplicationContext());
//Создаем помощник управления БД
    oh = new MySQLiteOpenHelper(getApplicationContext());
    db = oh.getWritableDatabase();

    long s_date = 0, e_date = Long.MAX_VALUE;
    STATE_PURCHASE state = STATE_PURCHASE.PLAN; //Должно зависеть от фильтра
    //Не группируем, отображаем покупки в хронологическом порядке в диапазоне дат фильтра
    //Cursor обязательно должен содержать _id иначе SimpleCursorAdapter не заработает
    String q_params[] ={ Long.toString(s_date), Long.toString(e_date),
      Integer.toString(STATE_PURCHASE.EXECUTE.value), Integer.toString(state.value) };
    cursor = db.rawQuery(oh.getQuery(EQ.PURCHASES), q_params);
    list_adapter = new PurchasesAdapter(this, R.layout.purchases_item, cursor,
      new String[]{},
      new int[]{R.id.purchases_item_date_time, R.id.purchases_item_content,
        R.id.purchases_item_total_sum, R.id.purchases_item_state},
      db);
    list_purchases.setAdapter(list_adapter);

    //cursor.requery(); //Обновляет Cursor делая повторный запрос. Устарела, но для наших целей подойдет
    //  актуально если в БД изменились данные. Нужно переходить на LoaderManager CursorLoader
    //  позволяющие работать асинхронно.

    registerForContextMenu(list_purchases);
  }

  @Override //Выбрали покупку, перейдем в ее детали
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    Intent intent = new Intent(this, WDetails.class);
    intent.putExtra(getString(R.string.intent_purchases_id), id);
    startActivityForResult(intent, R.layout.details); //Запуск активности с onActivityResult
  }

  //Создаем меню
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    //Создаем меню из ресурса
    getMenuInflater().inflate(R.menu.purchases_menu, menu);
    return true;
  }

  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    //!!! На эмульке меню можно вызвать нажав на клавишу контекстного меню на клавиатуре, о как!
    Intent intent;
    switch(item.getItemId())
    {
      case R.id.m_purchases_add:
        ChooseAlert choose_alert = new ChooseAlert(this, "Добавить покупку или оплату",
          android.R.drawable.ic_dialog_alert, null, "Запланировать", "Уже оплачено");
        choose_alert.show(this);
        return true;
      case R.id.m_purchases_user_account:
        intent = new Intent(this, WUserAccount.class);
        startActivity(intent); //Запуск активности
        return true;
      case R.id.m_purchases_type:
        intent = new Intent(this, WTypes.class);
        startActivity(intent); //Запуск активности
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  @Override
  public void onClick(ChooseAlert.CHOOSE_BUTTON button)
  {
    Intent intent = new Intent(this, WMarkTypes.class);
    if(button == ChooseAlert.CHOOSE_BUTTON.BUTTON1)
      intent.putExtra("StatePurchase", STATE_PURCHASE.PLAN.value);
    else if(button == ChooseAlert.CHOOSE_BUTTON.BUTTON2)
      intent.putExtra("StatePurchase", STATE_PURCHASE.EXECUTE.value);
    startActivityForResult(intent, R.layout.mark_types); //Запуск активности с onActivityResult
  }

  //Контекстное меню для элемента списка счетов
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
  {
    super.onCreateContextMenu(menu, v, menuInfo);
    if(v == list_purchases)
    {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.purchases_context_list_purchases, menu);

      //Скроем не нужные на данный момент пункты меню
      //MenuItem shareMenuItem = menu.findItem(R.id.m_account_context_list_acc_co_owners);
      //shareMenuItem.setVisible(false);
    }
  }
  //Обрабатываем нажатие выбор пункта контекстного меню
  @Override
  public boolean onContextItemSelected(MenuItem item)
  {
    AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    final Purchase purchase = Purchase.getPurhaseFromId(list_adapter.getItemId(acmi.position), db, oh);
    switch(item.getItemId())
    {
      case R.id.m_purchases_c_execute:
        final Purchase change_purchase = purchase.clone();
        change_purchase.state = Purchase.STATE_PURCHASE.EXECUTE;
        I_DialogResult dialog_result =  new I_DialogResult()
        {
          @Override
          public void onResult(RESULT code, Intent data)
          {
            if(code == RESULT.OK)
            {
              change_purchase.date_time = data.getExtras().getLong("date_time");
              if( purchase.update(change_purchase, db) )
              {
                cursor.requery();
                list_adapter.notifyDataSetInvalidated();
              }
            }
          }
        };
        PurchaseDateTimeDialog date_time_dialog = new PurchaseDateTimeDialog(dialog_result,
          new java.util.Date().getTime(), change_purchase.state == Purchase.STATE_PURCHASE.PLAN);
        date_time_dialog.show(getFragmentManager(), null);
        return true;

    }
    return super.onContextItemSelected(item);
  }
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    switch(requestCode)
    {
      case R.layout.mark_types:
        if(resultCode == RESULT_OK)
        {
          //Выбрали товары и услуги, теперь создаем покупку с этими товарами и услугами и отображаем ее
          final ArrayList<DialogParamsSelectedType> selected = data.getParcelableArrayListExtra("Selected");
          Purchase.STATE_PURCHASE state_purchase = STATE_PURCHASE.getSTATE_PURCHASE(data.getExtras().getInt("StatePurchase"));
          long date_time = data.getExtras().getLong("date_time");
          final Purchase purchase = new Purchase(UserAccount.getIDActiveUserAccount(oh, db), null,
            date_time, state_purchase, false);
          //Создаем покупку и ее список товаров и услуг
          final long[] id_purchase = new long[1];
          SQLTransaction sql_transaction = new SQLTransaction(db, new I_Transaction(){
            @Override
            public boolean trnFunc()
            {
              id_purchase[0] = purchase.insertDateBase(db);
              for(DialogParamsSelectedType selected_type : selected)
              {
                Detail detail = new Detail(UserAccount.getIDActiveUserAccount(oh, db), id_purchase[0],
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
          }
        }
        break;
      case R.layout.details:
        cursor.requery();
        if(data.getExtras().getBoolean("PurchaseIsDeleted"))
          list_adapter.notifyDataSetChanged();
        else
          list_adapter.notifyDataSetInvalidated();
        break;
    }
  }

  //Переопределим SimpleCursorAdapter что бы форматировать данные из базы нужным образом
  private class PurchasesAdapter
    extends SimpleCursorAdapter
  {
    public static final long SECONDS_IN_DAY = 86400000L;
    private static final int MAX_LENGTH_CONTEIN = 30;
    private SQLiteDatabase db;
    public PurchasesAdapter(Context _context, int _layout, Cursor _c, String[] _from,
                            int[] _to, SQLiteDatabase _db)
    {
      super(_context, _layout, _c, _from, _to);
      db = _db;
    }
    @Override
    public void bindView(View _view, Context _context, Cursor _cursor)
    {
      Long id = _cursor.getLong(_cursor.getColumnIndex("_id"));
      STATE_PURCHASE state = STATE_PURCHASE.getSTATE_PURCHASE(_cursor.getInt(_cursor.getColumnIndex("state")));
      Long date_time = _cursor.getLong(_cursor.getColumnIndex("date_time"));
      //Подготовим отображаемые данные
      String info[] = get_dateTime_content_totalSum(id, state, date_time);
      int id_state_icon;
      //ИД Иконки состояния
      switch(state)
      {
        case PLAN:
          id_state_icon = R.drawable.ic_exclamation;
          break;
        case EXECUTE:
          id_state_icon = R.drawable.ic_tick;
          break;
        default:
          id_state_icon = R.drawable.ic_launcher;
      }
      //Сопоставляем
      TextView tv_date_time = (TextView)_view.findViewById(R.id.purchases_item_date_time);
      TextView tv_content   = (TextView)_view.findViewById(R.id.purchases_item_content);
      TextView tv_total_sum = (TextView)_view.findViewById(R.id.purchases_item_total_sum);
      ImageView iv_state = (ImageView)_view.findViewById(R.id.purchases_item_state);

      tv_date_time.setText(info[0]);
      tv_content.setText(info[1]);
      tv_total_sum.setText(info[2]);
      iv_state.setImageResource(id_state_icon);
    }
    private String[] get_dateTime_content_totalSum(long _id_purchase, STATE_PURCHASE _state, Long _date_time)
    {
      //Получим общую сумму и краткий список деталей
      String q_params[] ={ Long.toString(_id_purchase), Long.toString(0)};
      Cursor cursor = db.rawQuery(oh.getQuery(EQ.DETAILS), q_params);
      double sum = 0;
      String s_content = "";
      for(boolean stat=cursor.moveToFirst(), flag=false, fl_len=true;
          stat; flag=stat=cursor.moveToNext())
      {
        Detail detail = new Detail(cursor);
        //Unit unit = Unit.units[(int)detail.for_id_unit];
        double cost = 0;
        if(detail.calcCost(false)) //Если стоимость возможно вычислить то прибавим ее й общей сумме
          cost = detail.cost;
        sum += cost;
        if(flag && fl_len)
          s_content += ", ";
        if(fl_len) //Не будем делать краткий список сильно длинным
          s_content += cursor.getString(cursor.getColumnIndex("name"));
        if(fl_len && s_content.length() >= MAX_LENGTH_CONTEIN)
          fl_len = false;
      }

      String for_date_time = getString(R.string.details_sub_caption_date_time_plan);
      String result_date_time[] = new String[2];
      PurchaseDateTimeDialog.getStringDateTime(_date_time, result_date_time, getApplicationContext(), true);
      if(_state == Purchase.STATE_PURCHASE.PLAN)
        for_date_time += " " + getString(R.string.details_sub_caption_date_time_on) + " " + result_date_time[0] +
          " " + getString(R.string.details_sub_caption_date_time_on) + " " + result_date_time[1];
      else
        for_date_time = getString(R.string.details_sub_caption_date_time_exec) + " " + result_date_time[0] +
          " " + getString(R.string.details_sub_caption_date_time_in) + " " + result_date_time[1];

      return new String[]{for_date_time, s_content, " на сумму " + Detail.formatMoney(sum)};
    }
  }
}

















