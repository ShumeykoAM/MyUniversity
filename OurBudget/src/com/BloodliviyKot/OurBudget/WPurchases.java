package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.BloodliviyKot.OurBudget.Dialogs.ChooseAlert;
import com.BloodliviyKot.OurBudget.Dialogs.DialogParamsSelectedType;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.I_Transaction;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.SQLTransaction;
import com.BloodliviyKot.tools.DataBase.entitys.Detail;
import com.BloodliviyKot.tools.DataBase.entitys.Purchase;
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WPurchases
  extends Activity
  implements AdapterView.OnItemClickListener, ChooseAlert.I_ChooseAlertHandler
{
  //Перечислим состояния покупок константами
  public static final int STATE_NONE = -1;   //Такого состояния не существует (не может быть в базе)
  public static final int STATE_PLAN = 0;    //Запланировано
  public static final int STATE_EXECUTE = 1; //Исполнено

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

    list_purchases = (ListView)findViewById(R.id.purchases_list_list_purchases);
    list_purchases.setOnItemClickListener(this);

//Для отладки удалим базу
//MySQLiteOpenHelper.debugDeleteDB(getApplicationContext());
//Создаем помощник управления БД
    oh = new MySQLiteOpenHelper(getApplicationContext());
    db = oh.getWritableDatabase();

    long s_date = 0, e_date = Long.MAX_VALUE;
    int state = STATE_PLAN; //Должно зависеть от фильтра
    //Не группируем, отображаем покупки в хронологическом порядке в диапазоне дат фильтра
    //Cursor обязательно должен содержать _id иначе SimpleCursorAdapter не заработает
    String q_params[] ={ Long.toString(s_date), Long.toString(e_date),
      Integer.toString(STATE_EXECUTE), Integer.toString(state) };
    cursor = db.rawQuery(oh.getQuery(EQ.PURCHASES), q_params);
    list_adapter = new PurchasesAdapter(this, R.layout.purchases_item, cursor,
      new String[]{},
      new int[]{R.id.purchases_item_contnt, R.id.purchases_item_info, R.id.purchases_item_state},
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
    Intent intent;
    switch(item.getItemId())
    {
      case R.id.m_purchases_add:
        //На эмульке меню не робя так что приходится сюда временно тыкать
        //intent = new Intent(this, WTypes.class);
        //startActivity(intent); //Запуск активности
        //return true;


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
  public void onKlick(ChooseAlert.CHOOSE_BUTTON button)
  {
    Intent intent = new Intent(this, WMarkTypes.class);
    if(button == ChooseAlert.CHOOSE_BUTTON.BUTTON1)
      intent.putExtra("StatePurchase", STATE_PLAN);
    else if(button == ChooseAlert.CHOOSE_BUTTON.BUTTON2)
      intent.putExtra("StatePurchase", STATE_EXECUTE);
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
    switch(item.getItemId())
    {
      case R.id.m_purchases_c_execute:
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
          int state_purchase = data.getExtras().getInt("StatePurchase");
          long date_time = data.getExtras().getLong("date_time");
          final Purchase purchase = new Purchase(UserAccount.getIDActiveUserAccount(oh, db), null,
            date_time, state_purchase, 0);
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
                  selected_type.id_type, null, null, 1, selected_type.id_unit, selected_type.count,
                  selected_type.id_unit, 0.0, 0);
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
    }
  }

  //Переопределим SimpleCursorAdapter что бы форматировать данные из базы нужным образом
  private static class PurchasesAdapter
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
      int state = _cursor.getInt(_cursor.getColumnIndex("state"));
      Long date_time = _cursor.getLong(_cursor.getColumnIndex("date_time"));
      //Подготовим отображаемые данные
      String s_content_info[] = get_contein_and_info(id, state, date_time);
      int id_state_icon;
      //ИД Иконки состояния
      switch(state)
      {
        case WPurchases.STATE_PLAN:
          id_state_icon = R.drawable.ic_exclamation;
          break;
        case WPurchases.STATE_EXECUTE:
          id_state_icon = R.drawable.ic_tick;
          break;
        default:
          id_state_icon = R.drawable.ic_launcher;
      }
      //Сопоставляем
      TextView tv_content = (TextView)_view.findViewById(R.id.purchases_item_contnt);
      TextView tv_info    = (TextView)_view.findViewById(R.id.purchases_item_info);
      ImageView iv_state = (ImageView)_view.findViewById(R.id.purchases_item_state);

      tv_content.setText(s_content_info[0]);
      tv_info.setText(s_content_info[1]);
      iv_state.setImageResource(id_state_icon);
    }
    private String[] get_contein_and_info(long _id_purchase, int _state, Long _date_time)
    {
      //Получим общую сумму и краткий список деталей
      String query =
        "SELECT detail._id, detail.price, detail.for_amount_unit, detail.for_id_unit, " +
        "detail.amount, detail.id_unit, detail.cost, type.name FROM detail, type " +
        "WHERE detail._id_purchase = ? AND type._id=detail._id_type;";
      String q_params[] ={ Long.toString(_id_purchase)};
      Cursor cursor = db.rawQuery(query, q_params);
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
      //Получим описание времени коджа была сделана или на когда запланирована покупка
      //"завтра hh:mm", "сегодня hh:mm", "вчера hh:mm", "dd.MM.yyyy"
      SimpleDateFormat y_t_t_format = new SimpleDateFormat("hh:mm");
      SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy");
      String s_date;
      long normalize_cur_time = ((new java.util.Date()).getTime())/SECONDS_IN_DAY*SECONDS_IN_DAY; //Получить текушее время в секундах
      java.sql.Date yesterday = new java.sql.Date(normalize_cur_time - SECONDS_IN_DAY);
      java.sql.Date today = new java.sql.Date(normalize_cur_time);
      java.sql.Date tomorrow = new java.sql.Date(normalize_cur_time + SECONDS_IN_DAY);
      java.sql.Date purchase_date = new java.sql.Date(_date_time/SECONDS_IN_DAY*SECONDS_IN_DAY);
      java.util.Date purchase_date_time = new java.util.Date(_date_time);
      if(purchase_date.compareTo(yesterday) == 0)
        s_date = "вчера в " + y_t_t_format.format(purchase_date_time);
      else if(purchase_date.compareTo(today) == 0)
        s_date = "сегодня в " + y_t_t_format.format(purchase_date_time);
      else if(purchase_date.compareTo(tomorrow) == 0)
        s_date = "завтра в " + y_t_t_format.format(purchase_date_time);
      else
        s_date = date_format.format(purchase_date_time);
      return new String[]{s_content, s_date + " на сумму " + Detail.formatmoney(sum)};
    }
  }
}

















