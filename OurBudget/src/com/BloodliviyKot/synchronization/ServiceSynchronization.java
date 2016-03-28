package com.BloodliviyKot.synchronization;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.OurBudget.WPurchases;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Chronological;
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ServiceSynchronization
  extends Service
{
  private static boolean is_started = false; //Флаг того что сервис уже запущен
  NotificationManager notification_manager;  //Для уведомления в строке уведомлений
  private MySQLiteOpenHelper oh;
  private SQLiteDatabase db;

  @Override
  public void onCreate()
  {
    super.onCreate();
    notification_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    oh = new MySQLiteOpenHelper();
    db = oh.getWritableDatabase();
  }
  @Override
  public int onStartCommand(Intent intent, int flags, int startId)
  {
    if(!is_started)
      task();
    is_started = true;
    return super.onStartCommand(intent, flags, startId);
  }
  @Override
  public void onDestroy()
  {
    super.onDestroy();
  }
  @Override
  public IBinder onBind(Intent intent)
  {
    return null;
  }

  //основной цикл
  void task()
  {
    //Поток для синхронизации
    new Thread(new Runnable() {
      public void run()
      {
        int loop = 6;//количество секунд между циклами проверки
        while(true)
        {
          //Проверяем наличие записей в хронологии, которые еще не синхронизировались
          //Перебираем все учетки пользователя, кроме учетки по умолчанию
          Cursor cursor_ua = db.rawQuery(oh.getQuery(EQ.USER_ACCOUNTS), null);
          for(boolean ua = cursor_ua.moveToFirst(); ua; ua = cursor_ua.moveToNext())
          {
            UserAccount next_user_account = new UserAccount(cursor_ua);
            //Перебираем все записи в хронологии, timestamp которых > timestamp в user_account
            Cursor cursor_ch = db.rawQuery(oh.getQuery(EQ.CHRONOLOGICAL_TIMESTAMP),
              new String[]{new Long(next_user_account._id).toString(), new Long(next_user_account.timestamp).toString()});
            for(boolean ch = cursor_ch.moveToFirst(); ch; ch = cursor_ch.moveToNext())
            {
              Chronological chronological = new Chronological(cursor_ch);
              //Отправляем запись на сервак, и если их timestamp > чем timestamp на серваке, то они там сохранятся



            }
            //Запрашиваем все записи на сервере которые еще не синхронизированы

          }
          try
          {
            TimeUnit.SECONDS.sleep(loop);
          } catch(InterruptedException e)
          {
            e.printStackTrace();
          }
        }
      }
    }).start();
    //Поток слежения за запланированным
    new Thread(new Runnable(){
      @Override
      public void run()
      {
        try
        {
          int last_minutes = -1;
          while(true)
          {
            TimeUnit.SECONDS.sleep(1);
            Date date = new Date();
            if(last_minutes != date.getMinutes())
            {
              last_minutes = date.getMinutes();
              //Найдем все запланированные на данную минуту пукупки и платы




            }
          }
        }
        catch(InterruptedException e)
        {
          e.printStackTrace();
        }
      }
    }).start();
  }

  //Создаем уведомление
  void sendNotif()
  {
    // 1-я часть
    Notification notif = new Notification(R.drawable.ic_launcher, "Text in status bar", System.currentTimeMillis());
    // 3-я часть
    Intent intent = new Intent(this, WPurchases.class);
    //intent.putExtra(Purchase.FILE_NAME, "somefile");
    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
    // 2-я часть
    notif.setLatestEventInfo(this, "Notification's title", "Notification's text", pIntent);

    Uri ringURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    notif.sound = ringURI;

    // ставим флаг, чтобы уведомление пропало после нажатия
    notif.flags |= Notification.FLAG_AUTO_CANCEL;

    // отправляем
    notification_manager.notify(1, notif);
  }

}