package com.BloodliviyKot.OurBudget.Dialogs;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.DataBase.entitys.Detail;

import java.text.DecimalFormat;

@SuppressLint("ValidFragment")
public class DetailParamsDialog
  extends DialogFragment
  implements View.OnClickListener
{
  private I_DialogResult result_handler = null;
  private Detail detail;

  private TextView tv_name_detail;
  private EditText et_price;
  private EditText et_for_amount_unit;
  private Spinner sp_for_id_unit;
  private EditText et_amount;
  private Spinner sp_id_unit;
  private EditText et_cost;
  private Button button_save;

  public DetailParamsDialog(I_DialogResult _result_handler)
  {
    result_handler = _result_handler;
  }
  public void use(FragmentManager manager, String tag, Detail _detail)
  {
    super.show(manager, tag);
    detail = _detail.clone();
    detail.calcAll();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    View v = inflater.inflate(R.layout.detail_params_dialog, null);
    tv_name_detail     = (TextView)v.findViewById(R.id.detail_params_name_detail);
    et_price           = (EditText)v.findViewById(R.id.detail_params_price);
    et_for_amount_unit = (EditText)v.findViewById(R.id.detail_params_for_amount_unit);
    sp_for_id_unit     = (Spinner )v.findViewById(R.id.detail_params_id_unit);
    et_amount          = (EditText)v.findViewById(R.id.detail_params_amount);
    sp_id_unit         = (Spinner )v.findViewById(R.id.detail_params_id_unit);
    et_cost            = (EditText)v.findViewById(R.id.detail_params_cost);
    button_save        = (Button  )v.findViewById(R.id.detail_params_save);

    et_price.setText(new DecimalFormat("###.##").format(detail.price));
    et_for_amount_unit.setText(new DecimalFormat("###.##").format(detail.for_amount_unit));
    //sp_for_id_unit
    et_amount.setText(new DecimalFormat("###.##").format(detail.amount));
    //sp_id_unit
    et_cost.setText(new DecimalFormat("###.##").format(detail.cost));

    button_save.setOnClickListener(this);
    return v;
  }

  @Override
  public void onClick(View v)
  {
    if(v == button_save)
    {
      dismiss();
      detail.price = Double.parseDouble(et_price.getText().toString());
      detail.cost = Double.parseDouble(et_cost.getText().toString());
      result_handler.onResult(RESULT.OK, null);
    }
  }
  @Override
  public void onCancel(DialogInterface dialog)
  {
    super.onCancel(dialog);
    result_handler.onResult(RESULT.CANCEL, null);
  }

}
