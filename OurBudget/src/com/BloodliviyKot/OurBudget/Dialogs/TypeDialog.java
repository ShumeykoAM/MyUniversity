package com.BloodliviyKot.OurBudget.Dialogs;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Type;
import com.BloodliviyKot.tools.DataBase.entitys.Unit;
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;

@SuppressLint("ValidFragment")
public class TypeDialog
  extends DialogFragment
  implements View.OnClickListener
{
  private Button button_save;
  private EditText et_name;
  private Spinner sp_unit;

  private I_DialogResult result_handler = null;
  private Type type;
  private REGIME regime;
  private SimpleCursorAdapter unit_adapter;
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;
  private InputMethodManager imm;

  public static enum REGIME
  {
    NEW,
    EDIT
  }

  public TypeDialog(I_DialogResult _result_handler, Type _type, REGIME _regime)
  {
    result_handler = _result_handler;
    type = _type;
    regime = _regime;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    final View v = inflater.inflate(R.layout.type_dialog, null);
    et_name = (EditText)v.findViewById(R.id.type_dialog_name);
    sp_unit = (Spinner)v.findViewById(R.id.type_dialog_unit);
    button_save = (Button)v.findViewById(R.id.type_dialog_save);

    oh = new MySQLiteOpenHelper();
    db = oh.getWritableDatabase();

    et_name.setText(type.name);
    button_save.setOnClickListener(this);
    //Список единиц измерения
    Cursor cursor_unit = Unit.getCursor();
    if(regime == REGIME.EDIT)
      button_save.setText(getString(R.string.type_button_save_change));
    unit_adapter = new SimpleCursorAdapter(v.getContext(),
      android.R.layout.simple_list_item_1, cursor_unit, new String[]{"name"}, new int[]{android.R.id.text1});
    sp_unit.setAdapter(unit_adapter);
    int pos = 0;
    for(boolean status=cursor_unit.moveToFirst(); status ;status=cursor_unit.moveToNext(), pos++)
      if(cursor_unit.getLong(cursor_unit.getColumnIndex("_id")) == type.id_unit)
      {
        sp_unit.setSelection(pos);
        break;
      }
    //Клавиатуру для конкретного view можно корректно вызвать только так
    et_name.post(new Runnable(){
      @Override
      public void run()
      {
        InputMethodManager imm = (InputMethodManager)
          et_name.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_name, InputMethodManager.SHOW_IMPLICIT);
        et_name.requestFocus();
      }
    });
    return v;
  }
  @Override
  public void onClick(View v)
  {
    if(v == button_save)
    {
      RESULT result = RESULT.CANCEL;
      //Сохраним изменённый или новый вид товара или услуги
      UserAccount active_user_account = UserAccount.getActiveUserAccount(oh, db);
      Long id_active_user_account = active_user_account != null ? active_user_account._id : UserAccount.NON;
      Type entered_type = new Type(id_active_user_account, et_name.getText().toString(), null,
        unit_adapter.getItemId(unit_adapter.getCursor().getPosition()), false);
      if(entered_type.name.compareTo(new String("")) != 0)
      {
        long _id = 0;
        try
        {
          if(regime == REGIME.NEW)
          {
            if( (_id = entered_type.insertDateBase(db, false)) == -1)
              result = RESULT.ERROR;
            else
              result = RESULT.OK;
          }
          else if( regime == REGIME.EDIT &&
                  (type.name.compareTo(entered_type.name) != 0 || type.id_unit != entered_type.id_unit) )
          {
            _id = type._id;
            Type new_type = type.clone();
            new_type.name = entered_type.name;
            new_type.id_unit = entered_type.id_unit;
            if(!type.update(new_type, db, oh, false))
              result = RESULT.ERROR;
            else
              result = RESULT.OK;
          }
        }
        catch(SQLException e)
        {
          result = RESULT.ERROR;
        }
        if(result == RESULT.ERROR)
        {
          Toast err = Toast.makeText(v.getContext(), R.string.type_err_save, Toast.LENGTH_LONG);
          err.show();
        }
        else
        {
          dismiss();
          Intent data = new Intent();
          data.putExtra("_id", _id);
          result_handler.onResult(result, data);
        }
      }
    }
  }
  /*
  @Override
  public void onDismiss(DialogInterface dialog)
  {
    super.onDismiss(dialog);
  }
  @Override
  public void onCancel(DialogInterface dialog)
  {
    super.onCancel(dialog);
  }
  */

}
