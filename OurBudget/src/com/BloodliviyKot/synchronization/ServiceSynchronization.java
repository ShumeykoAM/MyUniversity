package com.BloodliviyKot.synchronization;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class ServiceSynchronization
  extends Service
{

  final String LOG_TAG = "myLogs";

  public void onCreate()
  {
    super.onCreate();
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
            TimeUnit.SECONDS.sleep(7);

            handler.post(new Runnable()
            {
              public void run()
              {
                Toast.makeText(getApplicationContext(), "msg", Toast.LENGTH_LONG).show();
              }
            });
          } catch(InterruptedException e)
          {
            e.printStackTrace();
          }
          //stopSelf();
        }
      }
    }).start();
  }

}