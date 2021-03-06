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
import com.BloodliviyKot.OurBudget.*;
import com.BloodliviyKot.tools.DataBase.EQ;
import com.BloodliviyKot.tools.DataBase.MySQLiteOpenHelper;
import com.BloodliviyKot.tools.DataBase.entitys.*;
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
        //dropSynchronization();
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
                      if(need_update)
                      {
                        WTypes.postUpdate();
                        WPurchases.postUpdate();
                        WDetails.postUpdate();
                      }
                      break;
                    case DETAIL:
                      need_update = false;
                      //Пытаемся найти запись по id_server
                      Detail detail = Detail.getFromIdServer(answer_ge.entity.getLong("_id"), user_account._id, db, oh);
                      if(detail == null) //Если такой покупки еще нет, то добавим ее
                      {
                        detail = new Detail(user_account._id,
                          Purchase.getFromIdServer(answer_ge.entity.getLong("_id_purchase"), user_account._id, db, oh)._id,
                          Type.getFromIdServer(answer_ge.entity.getLong("_id_type"), user_account._id, db, oh)._id,
                          answer_ge.entity.getLong("_id"),
                          answer_ge.entity.has("price") ? answer_ge.entity.getDouble("price") : null,
                          answer_ge.entity.getDouble("for_amount_unit"),
                          answer_ge.entity.getLong("for_id_unit"),
                          answer_ge.entity.getDouble("amount"),
                          answer_ge.entity.getLong("id_unit"),
                          answer_ge.entity.has("cost") ? answer_ge.entity.getDouble("cost") : null,
                          answer_ge.entity.getInt("is_delete") == 1);
                        detail.insertDateBase(db, answer_ge.server_timestamp, true);
                        need_update = true;
                      }
                      else //Такая покупка уже есть
                      {
                        Chronological chronological = Chronological.getFromIndex1(user_account._id,
                          Chronological.TABLE.DETAIL, detail._id, db, oh);
                        if(chronological == null || answer_ge.server_timestamp >= chronological.timestamp)
                        {
                          Detail new_rec = detail.clone();
                          new_rec.price = answer_ge.entity.has("price") ? answer_ge.entity.getDouble("price") : null;
                          new_rec.for_amount_unit = answer_ge.entity.getDouble("for_amount_unit");
                          new_rec.for_id_unit = answer_ge.entity.getLong("for_id_unit");
                          new_rec.amount = answer_ge.entity.getDouble("amount");
                          new_rec.id_unit = answer_ge.entity.getLong("id_unit");
                          new_rec.cost = answer_ge.entity.has("cost") ? answer_ge.entity.getDouble("cost") : null;
                          new_rec.is_delete = answer_ge.entity.getInt("is_delete") == 1;
                          detail.update(new_rec, db, oh, true);
                          need_update = true;
                          chronological.timestamp = answer_ge.server_timestamp;
                          chronological.update(db, oh);
                        }
                      }
                      if(need_update)
                      {
                        WPurchases.postUpdate();
                        WDetails.postUpdate();
                      }
                      break;
                    case PURCHASE:
                      need_update = false;
                      //Пытаемся найти запись по id_server
                      Purchase purchase = Purchase.getFromIdServer(answer_ge.entity.getLong("_id"), user_account._id, db, oh);
                      if(purchase == null) //Если такой покупки еще нет, то добавим ее
                      {
                        purchase = new Purchase(user_account._id, answer_ge.entity.getLong("_id"),
                          answer_ge.entity.getLong("date_time"),
                          Purchase.STATE_PURCHASE.values()[answer_ge.entity.getInt("state")],
                          answer_ge.entity.getInt("is_delete") == 1);
                        purchase.insertDateBase(db, answer_ge.server_timestamp, true);
                        need_update = true;
                        if(!purchase.is_delete)
                          sendNotif(getString(R.string.notify_purchase_planned), purchase._id);
                      }
                      else //Такая покупка уже есть
                      {
                        Chronological chronological = Chronological.getFromIndex1(user_account._id,
                          Chronological.TABLE.PURCHASE, purchase._id, db, oh);
                        if(chronological == null || answer_ge.server_timestamp >= chronological.timestamp)
                        {
                          Purchase new_rec = purchase.clone();
                          new_rec.date_time = answer_ge.entity.getLong("date_time");
                          new_rec.state = Purchase.STATE_PURCHASE.values()[answer_ge.entity.getInt("state")];
                          new_rec.is_delete = answer_ge.entity.getLong("is_delete") == 1;
                          purchase.update(new_rec, db, oh, true);
                          need_update = true;
                          chronological.timestamp = answer_ge.server_timestamp;
                          chronological.update(db, oh);
                        }
                      }
                      if(need_update)
                        WPurchases.postUpdate();
                      break;
                  }
                  UserAccount new_rec = user_account.clone();
                  new_rec.current_rev += 1;
                  user_account.update(new_rec, db, oh);
                }

                //Проверяем наличие записей в хронологии, которые еще не синхронизировались
                Chronological.TABLE table = Chronological.TABLE.TYPE;
                while(true)
                {
                  Cursor cursor_ch = db.rawQuery(oh.getQuery(EQ.CHRONOLOGICAL_NOT_SYNC),
                    new String[]{new Long(user_account._id).toString(), new Integer(table.ordinal()).toString()});
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
                      case PURCHASE:
                        i_entity = Purchase.getPurhaseFromId(chronological._id_rec, db, oh);
                        break;
                      case DETAIL:
                        i_entity = Detail.getDetailFromId(chronological._id_rec, db, oh);
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
                      if(answer.s_revision > user_account.current_rev)
                      {
                        UserAccount new_rec = user_account.clone();
                        new_rec.current_rev += 1;
                        user_account.update(new_rec, db, oh);
                      }
                    }
                    else
                      continue main_loop;
                  }
                  else
                  {
                    if(table == Chronological.TABLE.TYPE)
                      table = Chronological.TABLE.PURCHASE;
                    else if(table == Chronological.TABLE.PURCHASE)
                      table = Chronological.TABLE.DETAIL;
                    else
                     break;
                  }
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
              UserAccount user_account = UserAccount.getActiveUserAccount(oh, db);
              long n_datetime = date.getTime() / 60000 * 60000;
              Cursor cursor = db.rawQuery(oh.getQuery(EQ.PURCHASE_UNTIL_DATE),
                new String[]{new Long(user_account._id).toString(), new Long(n_datetime+60000-1).toString()});
              for(boolean status = cursor.moveToFirst(); status; status = cursor.moveToNext())
              {
                Purchase purchase = new Purchase(cursor);
                long p_datetime = purchase.date_time / 60000 * 60000;
                if(p_datetime == n_datetime)
                {
                  sendNotif(getString(R.string.notify_purchase_time), purchase._id);
                }
              }
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

  static int notif_id = 0;
  //Создаем уведомление
  void sendNotif(String notify, long id_purchase)
  {
    Notification notif = new Notification(R.drawable.ic_launcher, notify, System.currentTimeMillis());
    Intent intent = new Intent(this, WPurchases.class);
    intent.putExtra("_id", id_purchase);
    PendingIntent pIntent = PendingIntent.getActivity(this, notif_id++, intent, 0);
    notif.setLatestEventInfo(this, "", notify, pIntent);
    Uri ringURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    notif.sound = ringURI;
    // ставим флаг, чтобы уведомление пропало после нажатия
    notif.flags |= Notification.FLAG_AUTO_CANCEL;
    // отправляем
    notification_manager.notify(1, notif);
  }

  private void dropSynchronization()
  {
    Cursor csr = db.rawQuery(oh.getQuery(EQ.ALL_PURCHASES), new String[]{});
    for(boolean status = csr.moveToFirst(); status; status = csr.moveToNext())
    {
      Purchase p = new Purchase(csr);
      Purchase n = p.clone();
      n.id_server = null;
      p.update(n , db, oh, false, false);
    }
    csr = db.rawQuery(oh.getQuery(EQ.ALL_DETAILS), new String[]{});
    for(boolean status = csr.moveToFirst(); status; status = csr.moveToNext())
    {
      Detail d = new Detail(csr);
      Detail n = d.clone();
      n.id_server = null;
      d.update(n, db, oh, false, false);
    }
    csr = db.rawQuery(oh.getQuery(EQ.ALL_TYPE), new String[]{});
    for(boolean status = csr.moveToFirst(); status; status = csr.moveToNext())
    {
      Type t = new Type(csr);
      Type n = t.clone();
      n.id_server = null;
      t.update(n, db, oh, false, false);
    }
    csr = db.rawQuery(oh.getQuery(EQ.ALL_CHRONOLOGICAL), new String[]{});
    for(boolean status = csr.moveToFirst(); status; status = csr.moveToNext())
    {
      Chronological c = new Chronological(csr);
      c.is_sync = false;
      c.update(db, oh);
    }
    csr = db.rawQuery(oh.getQuery(EQ.USER_ACCOUNTS), new String[]{});
    for(boolean status = csr.moveToFirst(); status; status = csr.moveToNext())
    {
      UserAccount ua = new UserAccount(csr);
      UserAccount n = ua.clone();
      n.current_rev = 0;
      ua.update(n, db, oh);
    }

  }

}