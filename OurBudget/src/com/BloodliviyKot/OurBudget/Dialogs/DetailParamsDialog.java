package com.BloodliviyKot.OurBudget.Dialogs;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.Common.MyDecimalFormat;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Detail;
import com.BloodliviyKot.tools.DataBase.entitys.Type;
import com.BloodliviyKot.tools.DataBase.entitys.Unit;

@SuppressLint("ValidFragment")
public class DetailParamsDialog
  extends DialogFragment
  implements View.OnClickListener, View.OnFocusChangeListener
{
  private I_DialogResult result_handler = null;
  public Detail detail;
  MySQLiteOpenHelper oh;
  SQLiteDatabase db;

  private View v;
  private TextView tv_name_detail;
  private EditText et_price;
  private EditText et_for_amount_unit;
  private Spinner sp_for_id_unit;
  private EditText et_amount;
  private Spinner sp_id_unit;
  private EditText et_cost;
  private Button button_save;

  public static final MyDecimalFormat FORMAT_MONEY = new MyDecimalFormat("###.##");
  public static final MyDecimalFormat FORMAT_AMOUNT = new MyDecimalFormat("###.###");

  private SimpleCursorAdapter unit_adapter;
  private SimpleCursorAdapter for_amount_unit_adapter;

  public DetailParamsDialog(I_DialogResult _result_handler, MySQLiteOpenHelper oh, SQLiteDatabase db)
  {
    result_handler = _result_handler;
    this.oh = oh;
    this.db = db;
  }
  public void use(FragmentManager manager, String tag, Detail _detail)
  {
    detail = _detail.clone();
    detail.calcAll();
    super.show(manager, tag);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    v = inflater.inflate(R.layout.detail_params_dialog, null);
    tv_name_detail     = (TextView)v.findViewById(R.id.detail_params_name_detail);
    et_price           = (EditText)v.findViewById(R.id.detail_params_price);
    et_for_amount_unit = (EditText)v.findViewById(R.id.detail_params_for_amount_unit);
    sp_for_id_unit     = (Spinner )v.findViewById(R.id.detail_params_for_id_unit);
    et_amount          = (EditText)v.findViewById(R.id.detail_params_amount);
    sp_id_unit         = (Spinner )v.findViewById(R.id.detail_params_id_unit);
    et_cost            = (EditText)v.findViewById(R.id.detail_params_cost);
    button_save        = (Button  )v.findViewById(R.id.detail_params_save);

    Type type = Type.getFromId(detail._id_type, db, oh);
    tv_name_detail.setText(type.name);
    et_price.setText(FORMAT_MONEY.double_format(detail.price));
    et_for_amount_unit.setText(FORMAT_AMOUNT.double_format(detail.for_amount_unit));
    et_amount.setText(FORMAT_AMOUNT.double_format(detail.amount));
    et_cost.setText(FORMAT_MONEY.double_format(detail.cost));

    et_price.addTextChangedListener(new TextChangeListener(et_price));
    et_for_amount_unit.addTextChangedListener(new TextChangeListener(et_for_amount_unit));
    et_amount.addTextChangedListener(new TextChangeListener(et_amount));
    et_cost.addTextChangedListener(new TextChangeListener(et_cost));

    et_price.setOnFocusChangeListener(this);
    et_for_amount_unit.setOnFocusChangeListener(this);
    et_amount.setOnFocusChangeListener(this);
    et_cost.setOnFocusChangeListener(this);

    button_save.setOnClickListener(this);

    Unit current_unit = new Unit(detail.id_unit);

    Cursor cursor_for_amount_unit = Unit.getCursor();
    for_amount_unit_adapter = new SimpleCursorAdapter(v.getContext(), android.R.layout.simple_list_item_1,
      cursor_for_amount_unit, new String[]{"name"}, new int[]{android.R.id.text1});
    sp_for_id_unit.setAdapter(for_amount_unit_adapter);
    int pos = 0;
    for(boolean status=cursor_for_amount_unit.moveToFirst();
        status;
        status=cursor_for_amount_unit.moveToNext(), pos++)
      if(cursor_for_amount_unit.getLong(cursor_for_amount_unit.getColumnIndex("_id")) == detail.for_id_unit)
      {
        sp_for_id_unit.setSelection(pos);
        break;
      }
    sp_for_id_unit.setOnItemSelectedListener(new ItemClickListener(sp_for_id_unit));

    Cursor cursor_unit = Unit.cursorForGroup(current_unit._id_group);
    unit_adapter = new SimpleCursorAdapter(v.getContext(),
      android.R.layout.simple_list_item_1, cursor_unit, new String[]{"name"}, new int[]{android.R.id.text1});
    sp_id_unit.setAdapter(unit_adapter);
    pos = 0;
    for(boolean status=cursor_unit.moveToFirst(); status ;status=cursor_unit.moveToNext(), pos++)
      if(cursor_unit.getLong(cursor_unit.getColumnIndex("_id")) == detail.id_unit)
      {
        sp_id_unit.setSelection(pos);
        break;
      }
    sp_id_unit.setOnItemSelectedListener(new ItemClickListener(sp_id_unit));

    //Клавиатуру для конкретного view можно корректно вызвать только так
    et_price.post(new Runnable()
    {
      @Override
      public void run()
      {
        et_price.setSelection(0, et_price.getText().length());
        InputMethodManager imm = (InputMethodManager)
          et_price.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_price, InputMethodManager.SHOW_IMPLICIT);
        et_price.requestFocus();
      }
    });

    return v;
  }

  @Override
  public void onClick(View v)
  {
    if(v == button_save)
    {
      dismiss();
      result_handler.onResult(RESULT.OK, null);
    }
  }
  @Override
  public void onCancel(DialogInterface dialog)
  {
    super.onCancel(dialog);
    result_handler.onResult(RESULT.CANCEL, null);
  }

  @Override
  public void onFocusChange(final View v, boolean hasFocus)
  {
    final EditText edit_text = (EditText)v;
    if(hasFocus)
    {
      edit_text.post(new Runnable(){
        @Override
        public void run()
        {
          edit_text.setSelection(0, edit_text.getText().length());
        }
      });
    }
    else
    {
      MyDecimalFormat decimal_format = null;
      String s_value = edit_text.getText().toString();
      Double value = s_value.length()>0 ? new Double(s_value) : null;
      //Подправим поля
      if(edit_text == et_price)
      {
        if(value == null)
          value = new Double(TextChangeListener.DEFAULT_PRICE);
        decimal_format = FORMAT_MONEY;
      }
      else if(edit_text == et_for_amount_unit)
      {
        if(value == null || value == 0.0)
          value = new Double(TextChangeListener.DEFAULT_FOR_AMOUNT_UNIT);
        decimal_format = FORMAT_AMOUNT;
      }
      else if(edit_text == et_amount)
      {
        if(value == null || value == 0.0)
          value = new Double(TextChangeListener.DEFAULT_AMOUNT);
        decimal_format = FORMAT_AMOUNT;
      }
      else if(edit_text == et_cost)
      {
        if(value == null)
          value = new Double(TextChangeListener.DEFAULT_COST);
        decimal_format = FORMAT_MONEY;
      }
      if(decimal_format != null)
        edit_text.setText(decimal_format.double_format(value));
    }
  }

  private class TextChangeListener
    implements TextWatcher
  {
    public static final double DEFAULT_PRICE = 0.0;
    public static final double DEFAULT_FOR_AMOUNT_UNIT = 1.0;
    public static final double DEFAULT_AMOUNT = 1.0;
    public static final double DEFAULT_COST = 0.0;


    private EditText view;
    public TextChangeListener(EditText view)
    {
      this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
      String string = s.toString();
      if(string.compareTo(".") == 0)
      {
        view.setText("0.");
        view.setSelection(view.getText().length());
      }
      else
      {
        String s_value = view.getText().toString();
        Double value = s_value.length()>0 ? new Double(s_value) : null;
        if(value != null)
        {
          String normalise = null;
          if(view == et_price || view == et_cost)
            normalise = FORMAT_MONEY.double_format(value);
          else if(view == et_for_amount_unit || view == et_amount)
            normalise = FORMAT_AMOUNT.double_format(value);
          if(normalise != null && normalise.compareTo(s_value) != 0 && (normalise+".").compareTo(s_value) != 0)
          {
            int index_selection = view.getSelectionStart();
            view.setText(normalise);
            int length_text = view.getText().toString().length();
            index_selection = index_selection <= length_text ? index_selection : length_text;
            view.setSelection(index_selection);
          }
        }
      }
    }
    @Override
    public void afterTextChanged(Editable s)
    {
      if(s.toString().compareTo(".") == 0)
        return;
      if(view == et_price)
      {
        double price;
        String s_price = et_price.getText().toString();
        if(s_price.length() > 0)
          price = new Double(s_price);
        else
          price = DEFAULT_PRICE;
        if(price != detail.price)
        {
          detail.price = price;
          detail.calcCost(true);
          et_cost.setText(FORMAT_MONEY.double_format(detail.cost));
        }
      }
      else if(view == et_for_amount_unit)
      {
        double for_amount_unit;
        String s_for_amount_unit = et_for_amount_unit.getText().toString();
        if(s_for_amount_unit.length() > 0)
          for_amount_unit = new Double(s_for_amount_unit);
        else
          for_amount_unit = DEFAULT_FOR_AMOUNT_UNIT;
        if(for_amount_unit == 0.0)
          for_amount_unit = DEFAULT_FOR_AMOUNT_UNIT;
        if(for_amount_unit != detail.for_amount_unit)
        {
          detail.for_amount_unit = for_amount_unit;
          detail.calcCost(true);
          et_cost.setText(FORMAT_MONEY.double_format(detail.cost));
        }
      }
      else if(view == et_amount)
      {
        double amount;
        String s_amount = et_amount.getText().toString();
        if(s_amount.length() > 0)
          amount = new Double(s_amount);
        else
          amount = DEFAULT_AMOUNT;
        if(amount == 0.0)
          amount = DEFAULT_AMOUNT;
        if(amount != detail.amount)
        {
          detail.amount = amount;
          detail.calcCost(true);
          et_cost.setText(FORMAT_MONEY.double_format(detail.cost));
        }
      }
      else if(view == et_cost)
      {
        double cost;
        String s_cost = et_cost.getText().toString();
        if(s_cost.length() > 0)
          cost = new Double(s_cost);
        else
          cost = DEFAULT_COST;
        if(cost != detail.cost)
        {
          detail.cost = cost;
          detail.calcPrice(true);
          et_price.setText(FORMAT_MONEY.double_format(detail.price));
        }
      }
    }
  }

  private class ItemClickListener
    implements AdapterView.OnItemSelectedListener
  {
    private Spinner spinner;
    public ItemClickListener(Spinner spinner)
    {
      this.spinner = spinner;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
      Unit new_unit = new Unit(id);
      Unit for_amount_unit = new Unit(detail.for_id_unit);
      Unit unit = new Unit(detail.id_unit);
      if(spinner == sp_for_id_unit)
      {
        detail.for_id_unit = new_unit._id;
        if(new_unit._id_group != unit._id_group)
        {
          detail.id_unit = detail.for_id_unit;

          Cursor cursor_unit = Unit.cursorForGroup(new_unit._id_group);
          unit_adapter = new SimpleCursorAdapter(v.getContext(),
            android.R.layout.simple_list_item_1, cursor_unit, new String[]{"name"}, new int[]{android.R.id.text1});
          sp_id_unit.setAdapter(unit_adapter);
          int pos = 0;
          for(boolean status=cursor_unit.moveToFirst(); status ;status=cursor_unit.moveToNext(), pos++)
            if(cursor_unit.getLong(cursor_unit.getColumnIndex("_id")) == detail.id_unit)
            {
              sp_id_unit.setSelection(pos);
              break;
            }
          unit_adapter.notifyDataSetChanged();
        }
        else
        {
          detail.calcCost(true);
          et_cost.setText(FORMAT_MONEY.double_format(detail.cost));
        }
      }
      else if(spinner == sp_id_unit)
      {
        detail.id_unit = new_unit._id;
        detail.calcCost(true);
        et_cost.setText(FORMAT_MONEY.double_format(detail.cost));
      }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
  }
}
