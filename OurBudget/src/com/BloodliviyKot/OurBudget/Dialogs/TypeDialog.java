package com.BloodliviyKot.OurBudget.Dialogs;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.tools.DataBase.entitys.Type;

public class TypeDialog
  extends DialogFragment
{
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    //getDialog().setTitle("Title!");
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    View v = inflater.inflate(R.layout.type_dialog, null);
    //v.findViewById(R.id.btnYes).setOnClickListener(this);
    return v;
  }

  public void use(FragmentManager manager, String tag, Type _detail)
  {
    super.show(manager, tag);

  }
}
