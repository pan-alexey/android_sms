package panda.smsgateway.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import panda.smsgateway.sevices.GetSMS;
import panda.smsgateway.sevices.SmsQueueLoop;


//обработчик при старте устройства
public class Boot extends BroadcastReceiver {
    public Boot() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        final String LOG_TAG = "myLogs";

        //Смотрим настройки приложения;
        //в зависимости от настроек приложения
        //Запускаем необходимый сервис
        context.startService(new Intent(context, SmsQueueLoop.class));
        context.startService(new Intent(context, GetSMS.class));
        //Toast.makeText(context,"Отработан слушатель при загрузке приложения",Toast.LENGTH_LONG).show();
    }
}
