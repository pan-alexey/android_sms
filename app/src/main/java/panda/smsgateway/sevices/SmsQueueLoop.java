package panda.smsgateway.sevices;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;


import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import panda.smsgateway.MainActivity;
import panda.smsgateway.R;


import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;

import panda.smsgateway.database.DbHelper;
import panda.smsgateway.receivers.SentSMS;
import panda.smsgateway.receivers.DeliverySMS;
/*
Севрси обрабатывает очередь сообщений
*/
public class SmsQueueLoop extends Service {
    final String LOG_TAG = "SmsQueueLoop";
    DbHelper myDBHelper;
    public Runnable mRunnable = null;

    SentSMS sentSMS = new SentSMS();
    DeliverySMS deliverySMS = new DeliverySMS();


    //DefaultHttpClient httpClient = new DefaultHttpClient();




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e(LOG_TAG ,"SmsQueueLoop Service start");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(LOG_TAG, "onCreate");
    }


    //-----------------------------------------------------------------------//
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(LOG_TAG, "onStartCommand");
        myDBHelper = new DbHelper(getApplicationContext());
        final Handler mHandler = new Handler();
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                    while(true) {
                        DbHelper myDBHelper = new DbHelper(getApplicationContext());
                        HashMap<String, String> pullFromSmsQuery = myDBHelper.pullFromSmsQuery(getApplicationContext());

                        if( !pullFromSmsQuery.isEmpty() ){
                            //Отправляем смс;
                            sendSms((String) pullFromSmsQuery.get("PHONE"),  (String) pullFromSmsQuery.get("SYSTEM_ID") , (String) pullFromSmsQuery.get("TEXT"));
                        }

                        //Ставим поток на ожидание
                        try { TimeUnit.MILLISECONDS.sleep(1000);}
                        catch (InterruptedException e) { e.printStackTrace(); }
                    }
            }
        }).start();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG, "onDestroy");
        myDBHelper.close();
    }
    //-------------------------------------------//
    public void sendSms(String phone, String ID, String text){
        registerReceiver(sentSMS, new IntentFilter("SENT"));
        PendingIntent sentPending = PendingIntent.getBroadcast(this, ID.hashCode(), new Intent("SENT").putExtra("KEY",ID), PendingIntent.FLAG_ONE_SHOT);
        registerReceiver(deliverySMS, new IntentFilter("DELIVERED"));
        PendingIntent deliveryPending = PendingIntent.getBroadcast(this, ID.hashCode(), new Intent("DELIVERED").putExtra("KEY",ID), PendingIntent.FLAG_ONE_SHOT);
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> messageParts = sms.divideMessage(text);
        int partsCount = messageParts.size();
        ArrayList<PendingIntent> sentPendings = new ArrayList<PendingIntent>(partsCount);
        ArrayList<PendingIntent> deliveredPendings = new ArrayList<PendingIntent>(partsCount);
        for (int i = 0; i < partsCount; i++) {
            sentPendings.add(sentPending);
            deliveredPendings.add(deliveryPending);
        }
        sms.sendMultipartTextMessage(phone, null, messageParts, sentPendings, deliveredPendings);
    }
    //-------------------------------------------//








    //  Нотификация
    //-----------------------------------------------//
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void sendNotif() {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                // большая картинка
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker("Последнее китайское предупреждение!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("Напоминание")
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText("Пора покормить кота"); // Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
    //--------------------------------------------------//



}
