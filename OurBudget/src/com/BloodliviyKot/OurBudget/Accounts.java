package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class Accounts
  extends Activity
{

  //Создание активности
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.accounts);

  }


  private boolean fl_hide = false; //Для примера сокрытия ПМ
  //Создаем меню
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    //Создаем меню из ресурса
    getMenuInflater().inflate(R.menu.m_accounts, menu);

    //Найдем элемент меню
    MenuItem share_menu_item_s2 = menu.findItem(R.id.m_account_item4_s2);
    MenuItem share_menu_item_s3 = menu.findItem(R.id.m_account_item4_s3);
    //Сделаем его активным (этот пункт входит в группу радиобуттон)
    share_menu_item_s2.setChecked(true);
    //Скроем пункт меню
    share_menu_item_s3.setVisible(!fl_hide);

    return true;
  }
  //Обрабатываем выбор пункта меню
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.m_account_item1:
        return true;

      //Для примера
      case R.id.m_account_item4_s1:
        fl_hide = true;
        invalidateOptionsMenu(); //Заставим переинициализироваться меню
      case R.id.m_account_item4_s2:
      case R.id.m_account_item4_s3:
        item.setChecked(true);
        return true;
      case R.id.m_account_item4_h1:
        item.setChecked(!item.isChecked());
        return true;
    }
    return super.onOptionsItemSelected(item);
  }




}
