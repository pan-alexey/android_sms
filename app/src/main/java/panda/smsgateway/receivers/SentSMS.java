package panda.smsgateway.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SentSMS extends BroadcastReceiver {

    private final static String MY_TAG = "SentSMS";

    public SentSMS() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.e(MY_TAG,"RESULT_OK "+intent.getStringExtra("KEY"));
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Log.e(MY_TAG,"RESULT_ERROR_GENERIC_FAILURE");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Log.e(MY_TAG,"RESULT_ERROR_NO_SERVICE");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Log.e(MY_TAG,"RESULT_ERROR_NULL_PDU");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Log.e(MY_TAG,"RESULT_ERROR_RADIO_OFF");
                break;
        }
    }
}
