package com.BloodliviyKot.OurBudget.Dialogs;


import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Type;
import com.BloodliviyKot.tools.DataBase.entitys.Unit;

import java.text.DecimalFormat;
import java.util.Comparator;

//Параметры выбранного типа
@SuppressLint("ValidFragment")
public class DialogParamsSelectedType
  extends DialogFragment
  implements Parcelable, View.OnClickListener
{
  public long id_type;
  public long id_unit;
  public double amount;

  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;

  private TextView tv_type_name;
  private Button button_ok;
  private EditText et_count;
  private Spinner sp_unit;
  private SimpleCursorAdapter unit_adapter;
  private I_DialogResult i_dialogResult;
  private Type type;

  public DialogParamsSelectedType(long _id_type, boolean isFiction, MySQLiteOpenHelper _oh, SQLiteDatabase _db)
  {
    init(_id_type, isFiction, _oh, _db);
  }
  public DialogParamsSelectedType(long _id_type, boolean isFiction, MySQLiteOpenHelper _oh, SQLiteDatabase _db,
                                  double amount, long id_unit)
  {
    init(_id_type, true, _oh, _db);
    this.amount = amount;
    this.id_unit = id_unit;
  }
  private void init(long _id_type, boolean isFiction, MySQLiteOpenHelper _oh, SQLiteDatabase _db)
  {
    id_type = _id_type;
    oh = _oh;
    db = _db;
    if(!isFiction)
    {
      init_type();
      //Подкачаем значения на основе данных из базы (по последним или по средним и т.д.)

      //Пока что не по последним
      id_unit = type.id_unit;
      this.amount = 1;
    }
  }
  private void init_type()
  {
    Cursor cursor_type = db.rawQuery(oh.getQuery(EQ.TYPE_FROM_ID),
      new String[]{new Long(id_type).toString()});
    if(cursor_type.moveToFirst())
      type = new Type(cursor_type);
  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    View v = inflater.inflate(R.layout.type_dialog_params, null);
    tv_type_name = (TextView)v.findViewById(R.id.type_dialog_params_text_name);
    button_ok    = (Button  )v.findViewById(R.id.type_dialog_params_save);
    et_count     = (EditText)v.findViewById(R.id.type_dialog_params_count);
    sp_unit      = (Spinner )v.findViewById(R.id.type_dialog_params_unit);

    if(oh == null)
      oh = new MySQLiteOpenHelper();
    if(db == null)
      db = oh.getReadableDatabase();

    button_ok.setOnClickListener(this);
    if(type == null)
      init_type();
    tv_type_name.setText(type.name);
    et_count.setText(new DecimalFormat("###.##").format(amount));

    Cursor cursor_unit = Unit.getCursor();
    unit_adapter = new SimpleCursorAdapter(v.getContext(),
      android.R.layout.simple_list_item_1, cursor_unit, new String[]{"name"}, new int[]{android.R.id.text1});
    sp_unit.setAdapter(unit_adapter);
    int pos = 0;
    for(boolean status=cursor_unit.moveToFirst(); status ;status=cursor_unit.moveToNext(), pos++)
      if(cursor_unit.getLong(cursor_unit.getColumnIndex("_id")) == id_unit)
      {
        sp_unit.setSelection(pos);
        break;
      }
    //Клавиатуру для конкретного view можно корректно вызвать только так
    et_count.post(new Runnable(){
      @Override
      public void run()
      {
        et_count.setSelection(0, et_count.getText().length());
        InputMethodManager imm = (InputMethodManager)
          et_count.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_count, InputMethodManager.SHOW_IMPLICIT);
        et_count.requestFocus();
      }
    });
    return v;
  }
  public void setOnDialogResultListener(I_DialogResult i_dialogResult)
  {
    this.i_dialogResult = i_dialogResult;
  }
  private void changeParams()
  {
    amount = Double.valueOf(et_count.getText().toString());
    id_unit = unit_adapter.getItemId(unit_adapter.getCursor().getPosition());
  }
  @Override
  public void onClick(View v)
  {
    if(v == button_ok)
    {
      changeParams();
      if(i_dialogResult != null)
        i_dialogResult.onResult(RESULT.OK, null);
      dismiss();
    }
  }
  @Override
  public void onCancel(DialogInterface dialog)
  {
    changeParams();
    if(i_dialogResult != null)
      i_dialogResult.onResult(RESULT.CANCEL, null);
    super.onCancel(dialog);
  }

  //Копаратор для быстрого описка в дереве, сравниваем id_шники -----------------
  public static Comparator comparator = new Comparator<DialogParamsSelectedType>()
  {
    @Override
    public int compare(DialogParamsSelectedType lhs, DialogParamsSelectedType rhs)
    {
      int result = 0;
      if(lhs.id_type > rhs.id_type)
        result = 1;
      else if(lhs.id_type < rhs.id_type)
        result = -1;
      return result;
    }
  };

  //Ниже реализуем Parcelable ---------------------------------------------------
  private DialogParamsSelectedType(Parcel in)
  {
    id_type = in.readLong();
    id_unit = in.readLong();
    amount = in.readDouble();
  }
  @Override
  public int describeContents()
  {
    return 0;
  }
  public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
  {
    public DialogParamsSelectedType createFromParcel(Parcel in)
    {
      return new DialogParamsSelectedType(in);
    }
    public DialogParamsSelectedType[] newArray(int size)
    {
      return new DialogParamsSelectedType[size];
    }
  };
  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeLong(id_type);
    dest.writeLong(id_unit);
    dest.writeDouble(amount);
  }

}
