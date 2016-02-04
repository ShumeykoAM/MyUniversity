package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.BloodliviyKot.OurBudget.Dialogs.*;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Type;
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;

import java.util.ArrayList;
import java.util.TreeSet;

public class WMarkTypes
  extends Activity
  implements I_DialogResult, View.OnClickListener, AdapterView.OnLongClickListener
{
  private SearchView search;
  private ListView list_types;
  private Button button_ok;

  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;

  private Cursor cursor[];
  private SimpleCursorAdapter list_adapter;
  private TypesCursorTuning types_cursor_tuning;

  private TreeSet<DialogParamsSelectedType> selected_ids; //ИДшники отмеченных видов товаров и услуг
  Integer state_purchase;

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mark_types);
    search = (SearchView)findViewById(R.id.mark_types_search);
    list_types = (ListView)findViewById(R.id.mark_types_list_types);
    button_ok = (Button)findViewById(R.id.mark_types_button_ok);
    //Читаем параметры переданные из родительской активности
    Bundle extras = getIntent().getExtras();
    if(extras.containsKey("StatePurchase"))
      state_purchase = extras.getInt("StatePurchase");
    //account_id = extras.getLong(getString(R.string.intent_purchases_id));
    selected_ids = new TreeSet<DialogParamsSelectedType>(DialogParamsSelectedType.comparator);
    //Создаем помощник управления БД
    oh = new MySQLiteOpenHelper(getApplicationContext());
    db = oh.getWritableDatabase();
    cursor = new Cursor[1];
    cursor[0] = TypesCursorTuning.getFullCursor(oh, db);
    list_adapter = new TypesAdapter(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,
                                    cursor[0], new String[]{"name"},
                                    new int[]{android.R.id.text1});
    list_types.setAdapter(list_adapter);
    list_types.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    types_cursor_tuning = new TypesCursorTuning(oh, db, cursor, list_adapter);
    list_adapter.setFilterQueryProvider(types_cursor_tuning);
    search.setOnQueryTextListener(types_cursor_tuning);
    button_ok.setOnClickListener(this);
    button_ok.setClickable(false);
  }

  //Создаем меню
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    //Создаем меню из ресурса
    getMenuInflater().inflate(R.menu.mark_types_menu, menu);
    return true;
  }

  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_mark_details_add_type:
        Type type = new Type(UserAccount.getIDActiveUserAccount(oh, db),
          search.getQuery().toString(), null, 1, 0);
        TypeDialog type_dialog = new TypeDialog(this, type, TypeDialog.REGIME.NEW);
        type_dialog.show(getFragmentManager(), null);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onResult(RESULT code, Intent data)
  {
    if(code == RESULT.OK)
    {
      Bundle extras = data.getExtras();
      long _id = extras.getLong("_id");
      selected_ids.add(new DialogParamsSelectedType(_id, false, oh, db));
      cursor[0].requery();
      list_adapter.notifyDataSetChanged();
      int position, count;
      for(position = 0, count=list_types.getCount();
          position<count && _id != list_types.getItemIdAtPosition(position); ++position );
      final int pos = position;
      list_types.post(new Runnable()
      {
        @Override
        public void run()
        {
          list_types.smoothScrollToPosition(pos);
        }
      });
      setParamsSelectedType(_id);
      button_ok.setClickable(selected_ids.size() > 0);
    }
  }

  private void setParamsSelectedType(long id)
  {
    DialogParamsSelectedType selected = selected_ids.floor(new DialogParamsSelectedType(id, true, null, null));
    selected.show(getFragmentManager(), null);
  }
  private long getIdCheckedTextView(CheckedTextView checked_text_view)
  {
    int pos = list_types.getPositionForView(checked_text_view);
    return list_types.getItemIdAtPosition(pos);
  }
  @Override
  public void onClick(View v)
  {
    if(v == button_ok)
    {
      if(selected_ids.size() == 0)
        Toast.makeText(v.getContext(), R.string.mark_types_err, Toast.LENGTH_LONG).show();
      else
      {
        PurchaseDateTimeDialog date_time_dialog = new PurchaseDateTimeDialog(new I_DialogResult(){
          @Override
          public void onResult(RESULT code, Intent data)
          {
            if(code == RESULT.OK)
            {
              Intent ires = new Intent();  //Вернем
              ArrayList<DialogParamsSelectedType> selected = new ArrayList<DialogParamsSelectedType>(selected_ids);
              ires.putParcelableArrayListExtra("Selected", selected);
              if(state_purchase != null)
                ires.putExtra("StatePurchase", state_purchase);
              ires.putExtra("date_time", data.getExtras().getLong("date_time"));
              setResult(RESULT_OK, ires);  //Возвращаемый в родительскую активность результат
              finish();
            }
          }
        },new java.util.Date().getTime());
        date_time_dialog.show(getFragmentManager(), null);
      }
    }
    else //Один из видов товаров и услуг
    {
      boolean new_state = !((CheckedTextView)v).isChecked();
      ((CheckedTextView)v).setChecked(new_state);
      long id = getIdCheckedTextView((CheckedTextView)v);
      if(new_state)
      {
        selected_ids.add(new DialogParamsSelectedType(id, false, oh, db));
        //Зададим параметры
        setParamsSelectedType(id);
      }
      else
        selected_ids.remove(new DialogParamsSelectedType(id, true, null, null));
      button_ok.setClickable(selected_ids.size() > 0);
    }
  }

  @Override
  public boolean onLongClick(View v)
  {
    if(!((CheckedTextView)v).isChecked())
      onClick(v);
    else
    {
      //Зададим параметры
      long id = getIdCheckedTextView((CheckedTextView)v);
      setParamsSelectedType(id);
    }
    return true;
  }

  //Адаптер для списка
  private class TypesAdapter
    extends SimpleCursorAdapter
  {
    public TypesAdapter(Context context, int layout, Cursor c, String[] from, int[] to)
    {
      super(context, layout, c, from, to);
    }
    @Override
    public void bindView(View _view, Context _context, Cursor _cursor)
    {
      //Здесь заполнются данными поля указанные в конструкторе
      super.bindView(_view, _context, _cursor);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
      View view = super.getView(position, convertView, parent);
      CheckedTextView checked_text_view = (CheckedTextView)view.findViewById(android.R.id.text1);
      checked_text_view.setOnClickListener(WMarkTypes.this); //передаем ссылку на Outer класс
      checked_text_view.setOnLongClickListener(WMarkTypes.this);
      long id = list_types.getItemIdAtPosition(position);
      list_types.setItemChecked(position, selected_ids.contains(
        new DialogParamsSelectedType(id, true, null, null)));
      return view;
    }
  }
}