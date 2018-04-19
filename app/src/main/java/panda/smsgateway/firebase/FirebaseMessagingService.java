package panda.smsgateway.firebase;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;


import java.util.Map;

import panda.smsgateway.MainActivity;
import panda.smsgateway.R;

/**
 * Created by alexey on 05.10.2017.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    MediaPlayer mediaPlayer = new MediaPlayer();;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        //Производиться в случае если
        if(remoteMessage.getData().size()>0){
            Map data = remoteMessage.getData();
            Log.e(TAG, "MSG DATA: " + remoteMessage.getData());
            //Play();
            //sendNotif();
        }



        //Тут производиться обработка в случае передачи нотификации
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getTitle());
        }

    }





/*
    void PlaySignal(){
        mediaPlayer = MediaPlayer.create(this, R.raw.b);
        mediaPlayer.setLooping(false);
    }
*/

    public void Play()
    {
        try {
            if (!mediaPlayer.isPlaying()) {
                AssetFileDescriptor descriptor = getAssets().openFd("badabums.mp3");
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




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




}
