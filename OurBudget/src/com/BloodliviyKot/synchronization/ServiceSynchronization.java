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
import com.BloodliviyKot.OurBudget.WTypes;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.Chronological;
import com.BloodliviyKot.tools.DataBase.entitys.I_Entity;
import com.BloodliviyKot.tools.DataBase.entitys.Type;
import com.BloodliviyKot.tools.DataBase.entitys.UserAccount;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerGetEntity;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerSendEntity;
import com.BloodliviyKot.tools.Protocol.Answers.AnswerTimeServer;
import com.BloodliviyKot.tools.Protocol.E_MESSID;
import com.BloodliviyKot.tools.Protocol.Requests.ARequestGetEntity;
import com.BloodliviyKot.tools.Protocol.Requests.ARequestSendEntity;
import com.BloodliviyKot.tools.Protocol.Requests.RequestTimeServer;

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
main_loop:
        while(true)
        {
          try
          {
            final UserAccount user_account = UserAccount.getActiveUserAccount(oh, db);
            if(!user_account.login.equals("r") && new AlertConnect(getApplicationContext()).getServerAccess(false) == AlertConnect.SERVER_ACCES.ACCES)
            {
              long ts_correct = 0;
              try
              {
                RequestTimeServer request_time_server = new RequestTimeServer();
                AnswerTimeServer answer_ts = request_time_server.send();
                ts_correct = answer_ts.server_time - new Date().getTime();

                AnswerGetEntity answer_ge;
                for(ARequestGetEntity get_entity = new ARequestGetEntity(user_account.current_rev + 1);
                    (answer_ge = get_entity.send()).exist;
                    get_entity = new ARequestGetEntity(user_account.current_rev + 1))
                {
                  answer_ge.server_timestamp -= ts_correct;
                  switch(answer_ge.table)
                  {
                    case TYPE:
                      boolean need_update = false;
                      //Пытаемся найти запись по id_server
                      Type type = Type.getFromIdServer(answer_ge.entity.getLong("_id"), user_account._id, db, oh);
                      if(type == null) //Попытаемся найти по названию вида товара или услуги
                      {
                        type = Type.getFromName(answer_ge.entity.getString("name"), user_account._id, db, oh);
                      }
                      if(type == null) //Такого вида еще нет
                      {
                        type = new Type(user_account._id, answer_ge.entity.getString("name"), answer_ge.entity.getLong("_id"),
                          answer_ge.entity.getInt("id_unit"), answer_ge.entity.getInt("is_delete") == 1);
                        if( type.insertDateBase(db, answer_ge.server_timestamp, true) == -1 )
                        { //Очевидно дублируется имя
                          //Такое может быть: 1-добавил вид А, второй переименовал вид А в Б а 1 офлайн добавил А
                        }
                        need_update = true;
                      }
                      else //Такой вид уже есть
                      {
                        Chronological chronological = Chronological.getFromIndex1(user_account._id,
                          Chronological.TABLE.TYPE, type._id, db, oh);
                        if(chronological == null || answer_ge.server_timestamp >= chronological.timestamp)
                        {
                          Type new_type = type.clone();
                          new_type.name = answer_ge.entity.getString("name");
                          new_type.is_delete = answer_ge.entity.getInt("is_delete") == 1;
                          new_type.id_unit = answer_ge.entity.getInt("id_unit");
                          type.update(new_type, db, oh, true);
                          need_update = true;
                          chronological.timestamp = answer_ge.server_timestamp;
                          chronological.update(db, oh);
                        }
                      }
                      if(need_update && WTypes.w_types != null)
                        WTypes.postUpdate();
                      break;
                    case DETAIL:
                      break;
                    case PURCHASE:
                      break;
                  }
                  UserAccount new_rec = user_account.clone();
                  new_rec.current_rev += 1;
                  user_account.update(new_rec, db, oh);
                }

                //Проверяем наличие записей в хронологии, которые еще не синхронизировались
                while(true)
                {
                  Cursor cursor_ch = db.rawQuery(oh.getQuery(EQ.CHRONOLOGICAL_NOT_SYNC),
                    new String[]{new Long(user_account._id).toString()});
                  if(cursor_ch.moveToFirst())
                  {
                    final Chronological chronological = new Chronological(cursor_ch);
                    //Отправляем запись на сервак
                    final I_Entity i_entity;
                    switch(chronological.table)
                    {
                      case TYPE:
                        i_entity = Type.getFromId(chronological._id_rec, db, oh);
                        break;
                      default:
                        i_entity = null;
                    }
                    ARequestSendEntity send_entity = new ARequestSendEntity(i_entity,
                      chronological.timestamp + ts_correct, user_account.current_rev);
                    AnswerSendEntity answer = send_entity.send();
                    if(answer != null && answer.result != AnswerSendEntity.RESULT.NOT_LAST_REV)
                    {
                      i_entity.set_idServerIfUnset(answer._id_server, db, oh);
                      UserAccount new_rec = user_account.clone();
                      new_rec.current_rev += 1;
                      user_account.update(new_rec, db, oh);
                    }
                    else
                      continue main_loop;
                  }
                  else
                    break;
                }
              }
              catch(E_MESSID.MException e)
              {
                e.printStackTrace();
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