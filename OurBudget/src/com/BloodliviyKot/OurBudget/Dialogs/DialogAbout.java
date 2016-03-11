package com.BloodliviyKot.OurBudget.Dialogs;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import com.BloodliviyKot.OurBudget.R;

public class DialogAbout
  extends DialogFragment
  implements View.OnClickListener
{
  private View v;
  private Button ok;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    v = inflater.inflate(R.layout.about, null);
    ok = (Button)v.findViewById(R.id.dialog_about_ok);
    ok.setOnClickListener(this);
    return v;
  }

  @Override
  public void onClick(View v)
  {
    dismiss();
  }
}
