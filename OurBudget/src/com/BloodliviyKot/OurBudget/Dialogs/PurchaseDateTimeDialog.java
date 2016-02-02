package com.BloodliviyKot.OurBudget.Dialogs;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;

import java.text.SimpleDateFormat;

@SuppressLint("ValidFragment")
public class PurchaseDateTimeDialog
  extends DialogFragment
  implements View.OnClickListener
{
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

    String string_date_time[] = new String[2];
    getStringDateTime(date_time, string_date_time);
    et_date.setText(string_date_time[0]);
    et_time.setText(string_date_time[1]);

    et_date.setOnClickListener(this);
    et_time.setOnClickListener(this);
    button_save.setOnClickListener(this);

    return v;
  }
  @Override
  public void onClick(View v)
  {
    if(v == et_date)
    {


    }
    else if(v == et_time)
    {


    }
    else if(v == button_save)
    {
      handlerResult();
      dismiss();
    }
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
    SimpleDateFormat time_format = new SimpleDateFormat("hh:mm");
    result_date_time[0] = date_format.format(date_time);
    result_date_time[1] = time_format.format(date_time);
  }


}
