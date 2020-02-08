package panda.smsgateway.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ReceiverSMS extends BroadcastReceiver {
    public ReceiverSMS() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //---получить входящее SMS сообщение---
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = null;
        String str = "";
        if (bundle != null) {
            //---извлечь полученное SMS ---
            //Извлекаем номер телефона;
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            final String sms_from = messages[0].getDisplayOriginatingAddress();
            //В случае если СМС состоит из нескольких частей, объединяем их;
            StringBuilder bodyText = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                bodyText.append(messages[i].getMessageBody());
            }
            final String sms_body = bodyText.toString();



            //Отправка полученного смс на сервер
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            final String imei = telephonyManager.getDeviceId();
            final String url = "http://middleware.kubaninstrument.ru/sms/receiver/587539a62b7daa51fc4614b3814d1e1a/";

            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("RESTfull", "res" + response );
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("RESTfull",error.toString());
                }
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("imei", imei);
                    params.put("phone", sms_from);
                    params.put("text", sms_body);
                    return params;
                }
            };
            queue.add(stringRequest);
        }
    }
}
