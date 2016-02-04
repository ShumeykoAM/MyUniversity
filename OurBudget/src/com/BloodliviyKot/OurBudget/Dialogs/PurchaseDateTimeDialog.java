package com.BloodliviyKot.OurBudget.Dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

import java.util.Date;
import java.text.SimpleDateFormat;

@SuppressLint("ValidFragment")
public class PurchaseDateTimeDialog
  extends DialogFragment
  implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
  public static final int YEAR_CORRECTOR = 1900;
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;

  private EditText et_date;
  private EditText et_time;
  private Button button_save;

  I_DialogResult result_handler;
  public long date_time;

  public PurchaseDateTimeDialog(I_DialogResult _result_handler, long _date_time)
  {
    result_handler = _result_handler;
    date_time = _date_time;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    final View v = inflater.inflate(R.layout.purchase_date_time_dialog, null);
    et_date = (EditText)v.findViewById(R.id.purchase_date_time_date);
    et_time = (EditText)v.findViewById(R.id.purchase_date_time_time);
    button_save = (Button)v.findViewById(R.id.purchase_date_time_save);

    oh = new MySQLiteOpenHelper(v.getContext());
    db = oh.getWritableDatabase();
    updateView();
    et_date.setOnClickListener(this);
    et_time.setOnClickListener(this);
    button_save.setOnClickListener(this);

    return v;
  }
  private void updateView()
  {
    String string_date_time[] = new String[2];
    getStringDateTime(date_time, string_date_time);
    et_date.setText(string_date_time[0]);
    et_time.setText(string_date_time[1]);
  }
  @Override
  public void onClick(View v)
  {
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
      handlerResult();
      dismiss();
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
    handlerResult();
  }
  private void handlerResult()
  {
    if(result_handler != null)
    {
      Intent data = new Intent();
      data.putExtra("date_time", date_time);
      result_handler.onResult(RESULT.OK, data);
    }
  }

  //Строковое представление даты и времени
  public static void getStringDateTime(long date_time, String result_date_time[])
  {
    SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat time_format = new SimpleDateFormat("HH:mm"); //HH 0-23  hh 0-12am-pm
    result_date_time[0] = date_format.format(date_time);
    result_date_time[1] = time_format.format(date_time);
  }


}
