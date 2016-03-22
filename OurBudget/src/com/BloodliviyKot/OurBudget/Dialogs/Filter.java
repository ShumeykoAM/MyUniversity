package com.BloodliviyKot.OurBudget.Dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.BloodliviyKot.OurBudget.R;

import java.util.Date;

@SuppressLint("ValidFragment")
public class Filter
  extends DialogFragment
  implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
  public enum TERM
  {
    TODAY,
    LAST_WEEK,
    LAST_MONTH,
    ALL,
    ANY_PERIOD;

    public final int value;
    private TERM()
    {
      this.value = Index_i.i++;
    }
    private static class Index_i
    {
      public static int i = 0;
    }
  }

  private View v;
  private Spinner sp_filter_term;
  private EditText ed_start_date;
  private EditText ed_end_date;
  private Button button_save;
  private RelativeLayout rl;

  private I_DialogResult result_handler;
  private TERM term;
  private long start_date, end_date;

  public Filter(I_DialogResult result_handler, TERM term, long start_date, long end_date)
  {
    this.result_handler = result_handler;
    this.term = term;
    this.start_date = start_date;
    this.end_date = end_date;
  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    v = inflater.inflate(R.layout.filter_dialog, null);
    rl = (RelativeLayout)v.findViewById(R.id.filter_container_dates);
    sp_filter_term = (Spinner)v.findViewById(R.id.filter_term);
    ed_start_date = (EditText)v.findViewById(R.id.filter_from_date);
    ed_end_date = (EditText)v.findViewById(R.id.filter_to_date);
    button_save = (Button)v.findViewById(R.id.filter_save);

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item,
      getResources().getStringArray(R.array.filter_terms));
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    sp_filter_term.setOnItemSelectedListener(this);
    sp_filter_term.setAdapter(adapter);
    sp_filter_term.setSelection(term.value);

    updateView();
    ed_start_date.setOnClickListener(this);
    ed_end_date.setOnClickListener(this);
    button_save.setOnClickListener(this);
    return v;
  }
  private void updateView()
  {
    if(term != TERM.ANY_PERIOD)
    {
      //Скроем элементы и уменьшим размер
      rl.setVisibility(View.INVISIBLE);
      ViewGroup.LayoutParams params = rl.getLayoutParams();
      params.height = 1;
      rl.setLayoutParams(params);
    }
    else
    {
      //Отобразим элементы и вернем размер
      rl.setVisibility(View.VISIBLE);
      ViewGroup.LayoutParams params = rl.getLayoutParams();
      params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
      rl.setLayoutParams(params);

      String string_date_time[] = new String[2];
      PurchaseDateTimeDialog.getStringDateTime(start_date, string_date_time, v.getContext(), true);
      ed_start_date.setText(string_date_time[0]);
      PurchaseDateTimeDialog.getStringDateTime(end_date, string_date_time, v.getContext(), true);
      ed_end_date.setText(string_date_time[0]);
    }
  }
  @Override
  public void onClick(View v)
  {
    //Вот тут пеример использования дата пикера
    //http://developer.alexanderklimov.ru/android/views/datepicker.php
    if(v == ed_start_date)
    {
      Date date = new Date(start_date);
      DatePickerDialog dpd = new DatePickerDialog(v.getContext(), new DateSetListener(DATE.START),
        date.getYear() + PurchaseDateTimeDialog.YEAR_CORRECTOR, date.getMonth(), date.getDate());
      dpd.show();
    }
    else if(v == ed_end_date)
    {
      Date date = new Date(end_date);
      DatePickerDialog dpd = new DatePickerDialog(v.getContext(), new DateSetListener(DATE.END),
        date.getYear() + PurchaseDateTimeDialog.YEAR_CORRECTOR, date.getMonth(), date.getDate());
      dpd.show();
    }
    else if(v == button_save)
    {
      dismiss();
      Intent data = new Intent();
      data.putExtra("term", term);
      data.putExtra("start_date", start_date);
      data.putExtra("end_date", end_date);
      result_handler.onResult(RESULT.OK, data);
    }
  }
  @Override
  public void onCancel(DialogInterface dialog)
  {
    super.onCancel(dialog);
    Intent data = new Intent();
    data.putExtra("term", term);
    data.putExtra("start_date", start_date);
    data.putExtra("end_date", end_date);
    result_handler.onResult(RESULT.CANCEL, data);
  }
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
  {
    TERM last_term = term;
    term = TERM.values()[position];
    if(last_term != term)
    {
      Pair<Long, Long> dates = getDatesFromTERM(term == TERM.ANY_PERIOD ? TERM.TODAY : term);
      start_date = dates.first;
      end_date = dates.second;
      updateView();
    }
  }
  @Override
  public void onNothingSelected(AdapterView<?> parent)
  {  }
  public static Pair<Long, Long> getDatesFromTERM(TERM term)
  {
    Long s_date = null;
    Long e_date = null;
    switch(term)
    {
      case TODAY:
        s_date = new Date().getTime()/PurchaseDateTimeDialog.SECONDS_IN_DAY*PurchaseDateTimeDialog.SECONDS_IN_DAY;
        e_date = s_date + PurchaseDateTimeDialog.SECONDS_IN_DAY - 1;
        break;
      case LAST_WEEK:
        s_date = new Date().getTime()/PurchaseDateTimeDialog.SECONDS_IN_DAY*PurchaseDateTimeDialog.SECONDS_IN_DAY -
          PurchaseDateTimeDialog.SECONDS_IN_DAY * 7;
        e_date = new Date().getTime()/PurchaseDateTimeDialog.SECONDS_IN_DAY*PurchaseDateTimeDialog.SECONDS_IN_DAY +
          PurchaseDateTimeDialog.SECONDS_IN_DAY - 1;
        break;
      case LAST_MONTH:
        s_date = new Date().getTime()/PurchaseDateTimeDialog.SECONDS_IN_DAY*PurchaseDateTimeDialog.SECONDS_IN_DAY -
          PurchaseDateTimeDialog.SECONDS_IN_DAY * 30;
        e_date = new Date().getTime()/PurchaseDateTimeDialog.SECONDS_IN_DAY*PurchaseDateTimeDialog.SECONDS_IN_DAY +
          PurchaseDateTimeDialog.SECONDS_IN_DAY - 1;
        break;
      default:
        s_date = new Long(0);
        e_date = Long.MAX_VALUE;
    }
    return Pair.create(s_date, e_date);
  }
  public enum DATE{START, END}
  private class DateSetListener
    implements DatePickerDialog.OnDateSetListener
  {
    DATE t_date;
    public DateSetListener(DATE t_date)
    {
      this.t_date = t_date;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
      long l_date = t_date == DATE.START ? start_date : end_date;
      l_date = l_date/PurchaseDateTimeDialog.SECONDS_IN_DAY*PurchaseDateTimeDialog.SECONDS_IN_DAY;
      Date date = new Date(l_date);
      date.setYear(year - PurchaseDateTimeDialog.YEAR_CORRECTOR);
      date.setMonth(monthOfYear);
      date.setDate(dayOfMonth);
      if(t_date == DATE.START)
        start_date = date.getTime();
      else
        end_date = date.getTime() + PurchaseDateTimeDialog.SECONDS_IN_DAY - 1;
      if(end_date < start_date)
        end_date = start_date;
      updateView();
    }
  }
}
