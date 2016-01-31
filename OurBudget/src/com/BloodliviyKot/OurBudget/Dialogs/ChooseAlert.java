package com.BloodliviyKot.OurBudget.Dialogs;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ChooseAlert
  implements DialogInterface.OnClickListener
{
  AlertDialog alert;
  CHOOSE_BUTTON type_alert;
  I_ChooseAlertHandler choose_alert_handler;
  public ChooseAlert(Context context, String title, Integer id_icon, String message,
                     String name_button1)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title).setMessage(message);
    if(id_icon != null)
      builder.setIcon(id_icon);
    builder.setPositiveButton(name_button1, this);
    alert = builder.create();
    type_alert = CHOOSE_BUTTON.BUTTON1;
  }
  public ChooseAlert(Context context, String title, Integer id_icon, String message,
                     String name_button1, String name_button2)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title).setMessage(message);
    if(id_icon != null)
      builder.setIcon(id_icon);
    builder.setPositiveButton(name_button1, this);
    builder.setNegativeButton(name_button2, this);
    alert = builder.create();
    type_alert = CHOOSE_BUTTON.BUTTON2;
  }
  public ChooseAlert(Context context, String title, Integer id_icon, String message,
                     String name_button1, String name_button2, String name_button3)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title).setMessage(message);
    if(id_icon != null)
      builder.setIcon(id_icon);
    builder.setPositiveButton(name_button1, this);
    builder.setNeutralButton(name_button2, this);
    builder.setNegativeButton(name_button3, this);
    alert = builder.create();
    type_alert = CHOOSE_BUTTON.BUTTON3;
  }
  public void show(I_ChooseAlertHandler _choose_alert_handler)
  {
    choose_alert_handler = _choose_alert_handler;
    alert.show();
  }
  @Override
  public void onClick(DialogInterface dialog, int which)
  {
    CHOOSE_BUTTON choose_button = CHOOSE_BUTTON.BUTTON1;
    switch(which)
    {
      case DialogInterface.BUTTON_POSITIVE:
        choose_button = CHOOSE_BUTTON.BUTTON1;
        break;
      case DialogInterface.BUTTON_NEGATIVE:
        choose_button = type_alert;
        break;
      case DialogInterface.BUTTON_NEUTRAL:
        choose_button = CHOOSE_BUTTON.BUTTON2;
        break;
    }
    choose_alert_handler.onKlick(choose_button);
  }
  public enum CHOOSE_BUTTON { BUTTON1, BUTTON2, BUTTON3 }
  public interface I_ChooseAlertHandler
  {
    void onKlick(CHOOSE_BUTTON button);
  }

}




/*
builder.setNegativeButton("No", new DialogInterface.OnClickListener()
  {
public void onClick(DialogInterface dialog, int which)
  {
  dialog.dismiss();
  }
  });
  builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
  {
@Override
public void onClick(DialogInterface dialog, int which)
  {

  }
  });
  */