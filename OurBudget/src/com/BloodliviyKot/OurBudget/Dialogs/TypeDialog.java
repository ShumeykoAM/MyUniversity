package com.BloodliviyKot.OurBudget.Dialogs;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.DataBase.entitys.Type;
import com.BloodliviyKot.tools.DataBase.entitys.Unit;

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

  public static enum REGIME
  {
    NEW,
    EDIT
  }

  @SuppressLint("ValidFragment")
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
    View v = inflater.inflate(R.layout.type_dialog, null);
    et_name = (EditText)v.findViewById(R.id.type_dialog_name);
    sp_unit = (Spinner)v.findViewById(R.id.type_dialog_unit);
    button_save = (Button)v.findViewById(R.id.type_dialog_save);

    et_name.setText(type.name);
    button_save.setOnClickListener(this);
    //Список единиц измерения
    Cursor cursor_unit;
    if(regime == REGIME.NEW)
    {
      cursor_unit = Unit.getCursor();
    }
    else
    {
      //Сформировать список из ед. измер. входящих в группу которой принадлежит текущая заданная ед. измер.
      cursor_unit = Unit.cursorForGroup(1);
    }
    SimpleCursorAdapter unit_adapter = new SimpleCursorAdapter(v.getContext(),
      android.R.layout.simple_list_item_1, cursor_unit, new String[]{"name"}, new int[]{android.R.id.text1});
    sp_unit.setAdapter(unit_adapter);

    return v;
  }
  @Override
  public void onClick(View v)
  {
    if(v == button_save)
    {
      //Сохраним изменённый или новый вид товара или услуги

      dismiss();
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
