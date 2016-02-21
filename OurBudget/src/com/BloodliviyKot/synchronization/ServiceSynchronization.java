package com.BloodliviyKot.synchronization;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.OurBudget.WPurchases;

import java.util.concurrent.TimeUnit;

public class ServiceSynchronization
  extends Service
{

  final String LOG_TAG = "myLogs";

  public void onCreate()
  {
    super.onCreate();
    nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    Log.d(LOG_TAG, "onCreate");
  }

  public int onStartCommand(Intent intent, int flags, int startId)
  {
    Log.d(LOG_TAG, "onStartCommand");
    someTask();
    return super.onStartCommand(intent, flags, startId);
  }

  public void onDestroy()
  {
    super.onDestroy();
    Log.d(LOG_TAG, "onDestroy");
  }

  @Override
  public IBinder onBind(Intent intent)
  {
    Log.d(LOG_TAG, "onBind");
    return null;
  }
  private Handler handler = new Handler();
  void someTask()
  {
    new Thread(new Runnable() {
      public void run()
      {
        while(true)
        {
          try
          {
            TimeUnit.SECONDS.sleep(17);
            handler.post(new Runnable()
            {
              public void run()
              {
                Toast.makeText(getApplicationContext(), "msg", Toast.LENGTH_LONG).show();
                sendNotif();
              }
            });
          } catch(InterruptedException e)
          {
            e.printStackTrace();
          }
          stopSelf();
          break;
        }
      }
    }).start();
  }

  NotificationManager nm;
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
    nm.notify(1, notif);
  }

}