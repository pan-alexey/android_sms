package panda.smsgateway.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

public class DeliverySMS extends BroadcastReceiver {

    private final static String MY_TAG = "DeliverySMS";
    public DeliverySMS() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        final String imei = telephonyManager.getDeviceId();
        final String system_id = intent.getStringExtra("KEY");
        final String url = "http://middleware.kubaninstrument.ru/sms/deliver/587539a62b7daa51fc4614b3814d1e1a/";



        switch(getResultCode()) {
            case Activity.RESULT_OK:
                    RequestQueue queue = Volley.newRequestQueue(context);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e(MY_TAG, "res" + response );
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(MY_TAG,error.toString());
                        }
                    }){
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("imei", imei);
                            params.put("system_id", system_id );
                            params.put("status", "RESULT_OK");
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                break;
            case Activity.RESULT_CANCELED:
                Log.e(MY_TAG,"Delivery RESULT_CANCELED "+intent.getStringExtra("KEY"));




                break;
        }
    }

}
