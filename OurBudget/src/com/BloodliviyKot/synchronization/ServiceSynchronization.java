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
import com.BloodliviyKot.OurBudget.AlertConnect;
import com.BloodliviyKot.OurBudget.R;
import com.BloodliviyKot.OurBudget.WPurchases;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.I_Transaction;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.SQLTransaction;
import com.BloodliviyKot.tools.DataBase.entitys.Chronological;
import com.BloodliviyKot.tools.DataBase.entitys.I_Entity;
import com.BloodliviyKot.tools.DataBase.entitys.Type;
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerGetEntity;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerSendEntity;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import com.BloodliviyKot.tools.Protocol.Requests.ARequestGetEntityN;
import com.BloodliviyKot.tools.Protocol.Requests.ARequestGetEntityT;
import com.BloodliviyKot.tools.Protocol.Requests.ARequestSendEntity;
import org.json.JSONException;

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
        int loop = 4;//количество секунд между циклами проверки
        while(true)
        {
          final UserAccount user_account = UserAccount.getActiveUserAccount(oh, db);
          if(!user_account.login.equals("r") &&
            new AlertConnect(getApplicationContext()).getServerAccess(false) == AlertConnect.SERVER_ACCES.ACCES)
          {
            //Запрашиваем все измененные записи ориентируясь на хронологию
            try
            {
              long timestamp = user_account.timestamp;
              do
              {
                ARequestGetEntityT get_entity = new ARequestGetEntityT(timestamp);
                AnswerGetEntity answer = get_entity.send();
                switch(answer.table)
                {
                  case TYPE:
                    Type type = Type.getFromIdServer(answer.entity.getLong("_id"), user_account._id, db, oh);
                    if(type != null)
                    {
                      Type new_type = type.clone();
                      new_type.name = answer.entity.getString("name");
                      new_type.is_delete = answer.entity.getInt("is_delete") == 1;
                      new_type.id_unit = answer.entity.getInt("id_unit");
                      type.update(new_type, db, oh);
                    }
                    break;
                }
                timestamp = answer.timestamp;
              }while(true);
            }
            catch(E_MESSID.MException e)
            {
              e.printStackTrace();
            }
            catch(JSONException e)
            {
              e.printStackTrace();
            }
            //Запрашиваем все новые записи видов (которых нет у нас) ориентируясь на _id_server записей
            try
            {
              long max_id_server = Type.getMaxServerID(db, oh);
              do
              {
                ARequestGetEntityN get_entity = new ARequestGetEntityN(Chronological.TABLE.TYPE, max_id_server);
                AnswerGetEntity answer = get_entity.send();
                Type type = new Type(user_account._id, answer.entity.getString("name"), answer.entity.getLong("_id"),
                  answer.entity.getInt("id_unit"), answer.entity.getInt("is_delete") == 1);
                if(type.insertDateBase(db, answer.timestamp) != -1)
                  ;//Не удалось вставить из за дублирования имени
                else
                  ;//Найдем запись по имени и обновим если хронология
                max_id_server = answer.entity.getLong("_id");
              }while(true);
            }
            catch(E_MESSID.MException e)
            {
              e.printStackTrace();
            }
            catch(JSONException e)
            {
              e.printStackTrace();
            }

            //Скидываем на сервак все записи ориентируясь на хронологию




            //Проверяем наличие записей в хронологии, которые еще не синхронизировались
            //Перебираем все записи в хронологии, timestamp которых > timestamp в user_account
            Cursor cursor_ch = db.rawQuery(oh.getQuery(EQ.CHRONOLOGICAL_TIMESTAMP),
              new String[]{new Long(user_account._id).toString(), new Long(user_account.timestamp).toString()});
            for(boolean ch = cursor_ch.moveToFirst(); ch; ch = cursor_ch.moveToNext())
            {
              final Chronological chronological = new Chronological(cursor_ch);
              //Отправляем запись на сервак, и если их timestamp > чем timestamp на серваке, то они там сохранятся
              final I_Entity i_entity[] = new I_Entity[1];
              switch(chronological.table)
              {
                case TYPE:
                  i_entity[0] = Type.getFromId(chronological._id_rec, db, oh);
                  break;
                default:
                  i_entity[0] = null;
              }
              try
              {
                ARequestSendEntity send_entity = new ARequestSendEntity(i_entity[0], chronological.timestamp);
                final AnswerSendEntity answer = send_entity.send();
                if(answer != null)
                {
                  //Сохраним id записи на сервере и изменим timestamp учетки на timestamp текущей записи
                  SQLTransaction sql_transaction = new SQLTransaction(db, new I_Transaction()
                  {
                    @Override
                    public boolean trnFunc()
                    {
                      boolean result = i_entity[0].set_idServerIfUnset(answer._id_server, db, oh);
                      if(result)
                      {
                        UserAccount new_rec = user_account.clone();
                        new_rec.timestamp = chronological.timestamp;
                        result = user_account.update(new_rec, db, oh);
                      }
                      return result;
                    }
                  });
                  sql_transaction.runTransaction();
                }
              }
              catch(E_MESSID.MException e)
              {
                e.printStackTrace();
              }
            }
          }
          try
          {
            TimeUnit.SECONDS.sleep(loop);
          }
          catch(InterruptedException e)
          {
            e.printStackTrace();
          }
          catch(Exception e)
          {

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