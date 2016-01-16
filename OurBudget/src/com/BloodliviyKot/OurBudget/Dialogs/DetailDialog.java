package com.BloodliviyKot.OurBudget.Dialogs;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.DataBase.entitys.Detail;


public class DetailDialog
  extends DialogFragment
  implements View.OnClickListener
{
  final String LOG_TAG = "myLogs";
  private I_DetailDialogResult result_handler = null;
  private Detail detail;

  public DetailDialog(I_DetailDialogResult _result_handler)
  {
    result_handler = _result_handler;
  }
  public void use(FragmentManager manager, String tag, Detail _detail)
  {
    super.show(manager, tag);
    detail = _detail;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    View v = inflater.inflate(R.layout.detail_dialog, null);
    v.findViewById(R.id.btnYes).setOnClickListener(this);
    return v;
  }

  @Override
  public void onClick(View v)
  {
    dismiss();
    result_handler.onResult(RESULT.CANCEL);
  }
  @Override
  public void onDismiss(DialogInterface dialog)
  {
    super.onDismiss(dialog);
  }
  @Override
  public void onCancel(DialogInterface dialog)
  {
    super.onCancel(dialog);
    result_handler.onResult(RESULT.CANCEL);
  }

  public enum RESULT
  {
    OK,
    CANCEL
  }
  //Интерфейс для обработчика результата работы диалога
  public static interface I_DetailDialogResult
  {
    void onResult(RESULT code);
  }
}
