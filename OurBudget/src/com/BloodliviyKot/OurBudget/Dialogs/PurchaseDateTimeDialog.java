package com.BloodliviyKot.OurBudget.Dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("ValidFragment")
public class PurchaseDateTimeDialog
  extends DialogFragment
  implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
  public static final int YEAR_CORRECTOR = 1900;
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;

  private TextView tv_title;
  private EditText et_date;
  private EditText et_time;
  private Button button_save;

  private I_DialogResult result_handler;
  public long date_time;
  private boolean is_plan;

  private View v;

  public PurchaseDateTimeDialog(I_DialogResult _result_handler, long _date_time, boolean _is_plan)
  {
    result_handler = _result_handler;
    date_time = _date_time;
    is_plan = _is_plan;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    v = inflater.inflate(R.layout.purchase_date_time_dialog, null);
    tv_title = (TextView)v.findViewById(R.id.purchase_date_time_title);
    et_date = (EditText)v.findViewById(R.id.purchase_date_time_date);
    et_time = (EditText)v.findViewById(R.id.purchase_date_time_time);
    button_save = (Button)v.findViewById(R.id.purchase_date_time_save);

    oh = new MySQLiteOpenHelper(v.getContext());
    db = oh.getWritableDatabase();
    updateView();
    if(is_plan)
      tv_title.setText(R.string.purchase_date_time_tile_plan);
    else
      tv_title.setText(R.string.purchase_date_time_tile_execute);
    et_date.setOnClickListener(this);
    et_time.setOnClickListener(this);
    button_save.setOnClickListener(this);

    return v;
  }
  private void updateView()
  {
    String string_date_time[] = new String[2];
    getStringDateTime(date_time, string_date_time, v.getContext(), true);
    et_date.setText(string_date_time[0]);
    et_time.setText(string_date_time[1]);
  }
  @Override
  public void onClick(View v)
  {
    //Вот тут пеример использования дата пикера
    //http://developer.alexanderklimov.ru/android/views/datepicker.php
    if(v == et_date)
    {
      Date date = new Date(date_time);
      DatePickerDialog dpd = new DatePickerDialog(v.getContext(), this, date.getYear() + YEAR_CORRECTOR,
                                                  date.getMonth(), date.getDate());
      dpd.show();
    }
    else if(v == et_time)
    {
      Date date = new Date(date_time);
      TimePickerDialog tpd = new TimePickerDialog(v.getContext(), this, date.getHours(),
                                                  date.getMinutes(), true);
      tpd.show();
    }
    else if(v == button_save)
    {
      dismiss();
      Intent data = new Intent();
      data.putExtra("date_time", date_time);
      result_handler.onResult(RESULT.OK, data);
    }
  }
  @Override
  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
  {
    Date date = new Date(date_time);
    date.setYear(year - YEAR_CORRECTOR);
    date.setMonth(monthOfYear);
    date.setDate(dayOfMonth);
    date_time = date.getTime();
    updateView();
  }
  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute)
  {
    Date date = new Date(date_time);
    date.setHours(hourOfDay);
    date.setMinutes(minute);
    date_time = date.getTime();
    updateView();
  }

  @Override
  public void onCancel(DialogInterface dialog)
  {
    super.onCancel(dialog);
    Intent data = new Intent();
    data.putExtra("date_time", date_time);
    result_handler.onResult(RESULT.CANCEL, data);
  }

  //Строковое представление даты и времени
  public static void getStringDateTime(long date_time, String result_date_time[], Context context,
                                       boolean string_format)
  {
    final long SECONDS_IN_DAY = 86400000L;
    SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat time_format = new SimpleDateFormat("HH:mm"); //HH 0-23  hh 0-12am-pm
    result_date_time[0] = date_format.format(date_time);
    result_date_time[1] = time_format.format(date_time);
    if(string_format)
    {
      //Получить текушее время в секундах
      long normalize_cur_time = ((new java.util.Date()).getTime()) / SECONDS_IN_DAY * SECONDS_IN_DAY;
      java.sql.Date yesterday = new java.sql.Date(normalize_cur_time - SECONDS_IN_DAY);
      java.sql.Date today = new java.sql.Date(normalize_cur_time);
      java.sql.Date tomorrow = new java.sql.Date(normalize_cur_time + SECONDS_IN_DAY);
      java.sql.Date purchase_date = new java.sql.Date(date_time / SECONDS_IN_DAY * SECONDS_IN_DAY);
      java.util.Date purchase_date_time = new java.util.Date(date_time);
      if(purchase_date.compareTo(yesterday) == 0)
        result_date_time[0] = context.getString(R.string.purchase_date_time_yesterday);
      else if(purchase_date.compareTo(today) == 0)
        result_date_time[0] = context.getString(R.string.purchase_date_time_today);
      else if(purchase_date.compareTo(tomorrow) == 0)
        result_date_time[0] = context.getString(R.string.purchase_date_time_tomorrow);
    }
  }


}
