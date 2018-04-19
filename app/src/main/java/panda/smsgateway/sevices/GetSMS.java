package panda.smsgateway.sevices;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
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
import java.util.concurrent.TimeUnit;

import panda.smsgateway.database.DbHelper;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetSMS extends Service {

    final String LOG_TAG = "GetSMS";
    DbHelper myDBHelper;
    public Runnable mRunnable = null;

    public GetSMS() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(LOG_TAG, "onCreate");
    }
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
                    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    final String imei = telephonyManager.getDeviceId();
                    final String url = "http://middleware.kubaninstrument.ru/sms/get/oJdXH3oyfRQYMuz8Aw2pLl2a6K2trnue";

                    RequestQueue queue = Volley.newRequestQueue( getApplicationContext() );
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                ContentValues cv = new ContentValues();
                                cv.put("SYSTEM_ID", json.getString("id")   );
                                cv.put("PHONE", json.getString("phone") );
                                cv.put("TEXT", json.getString("text") );
                                long insertSmsQuery = myDBHelper.insertSmsQuery(getApplicationContext(),cv);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                    //DbHelper myDBHelper = new DbHelper(getApplicationContext());
                    //HashMap<String, String> pullFromSmsQuery = myDBHelper.pullFromSmsQuery(getApplicationContext());
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
}
