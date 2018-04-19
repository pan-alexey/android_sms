package panda.smsgateway;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import panda.smsgateway.sevices.GetSMS;
import panda.smsgateway.sevices.SmsQueueLoop;
import panda.smsgateway.web.WebApi;


import android.Manifest;

import com.google.firebase.iid.FirebaseInstanceId;


import java.util.ArrayList;
import java.util.List;




/*


https://habrahabr.ru/post/278945/  --Проверка прав

 */



public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 100;
    private static final String LOG = "MainActivity";
    private static final String TAG = "MainActivity";


    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }

        webView.setVerticalScrollBarEnabled(false);        // отключили прокрутку
        webView.setHorizontalScrollBarEnabled(false);      // отключили прокрутку
        webView.getSettings().setJavaScriptEnabled(true);  // включили JavaScript
        webView.getSettings().setDomStorageEnabled(true);  // включили localStorage и т.п.
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); //Включили КЭШ
        webView.getSettings().setSupportZoom(false);       // отключили зум, т.к. нормальные приложения подобным функционалом не обладают
        webView.getSettings().setSupportMultipleWindows(false);   // отключили поддержку вкладок.
        webView.addJavascriptInterface(new WebApi(this), "AndroidAPI");   // прокидываем объект в JavaScript.
        webView.setWebChromeClient(new WebChromeClient() {
            // Необходимые разрешения для работы с аудио и видео
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                Log.d(TAG, "onPermissionRequest");
                MainActivity.this.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        request.grant(request.getResources());
                    }
                });
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            //Проверка внешних ссылок
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Проверка Внешних ссылок
                view.loadUrl(url);
                //Если ссылка на внешний сайт
                //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                //startActivity(i);
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("TAG", "onPageStarted");
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("TAG", "onPageFinished");
                if (Build.VERSION.SDK_INT >= 23) {
                    if( requestApplicationPermissions() ){
                        ApplicationInit();
                    }else{
                        ApplicationInitWithDenided();
                    }
                }else{
                    ApplicationInit();
                }
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("TAG", "onReceivedError");
            }
        });
        webView.loadUrl("file:///android_asset/html/index.html");// загрузили нашу страничку
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.




        //обязательно разрешаем данные для

/*

         String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("MainActivity" ,"FirebaseInstanceId = "+token );

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        Log.e("MainActivity" ,"imei = "+imei );


*/
/*
        buttonAdd = (Button)findViewById(R.id.buttonAdd);
        View.OnClickListener oclBtnOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper myDBHelper = new DbHelper(getApplicationContext());
                Log.e("MainActivity" ,"onClick running");
                ContentValues cv = new ContentValues();
                cv.put("SYSTEM_ID", currentTimeMillis()   );
                cv.put("PHONE", "89283331431" );
                cv.put("TEXT", "ОСЕННИЕ СКИДКИ 20% только 28-29 сентября на весь ассортимент (кроме спецпредложений). Доставка по городу бесплатно. Звоните 89182195742" );
                long insertSmsQuery = myDBHelper.insertSmsQuery(getApplicationContext(),cv);
                //Log.e("MainActivity" ,"insert to DM LAST ID_ROW"+Long.toString(insertSmsQuery) );
                myDBHelper.close();
            }
        };
        // присвоим обработчик кнопке OK (btnOk)
        buttonAdd.setOnClickListener(oclBtnOk);
*/
        /*
        Log.e("MainActivity" ,"MainActivity start");
        //Запуск сервиса обработки очереди СМС
        if (!isServiceRunning(SmsQueueLoop.class)) {
            startService(new Intent(this, SmsQueueLoop.class) );
            Log.e("MainActivity" ,"SmsQueueLoop not running");
        } else {
            Log.e("MainActivity" ,"SmsQueueLoop running");
        }
        */





    }



    //--------------------------------------------------------------------------------//
    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) {
            if( requestApplicationPermissions() ){
                ApplicationInit();
            }else{
                ApplicationInitWithDenided();
            }
        }else{
            ApplicationInit();
        }

    }
    //--------------------------------------------------------------------------------//


    //------------------------------------------------------------------------------------------//
    //Метод проверки работы сервиса
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    //----------------------------------------------------------------------------------------//
    //---- Проверка разрешений для создания вызова
    //----------------------------------------------------------------------------------------//

    //###########################################################################################//
    //#################  Работа с разрешениями для Android 6+  ##################################//
    //###########################################################################################//
    //-------------------------------------------------------------------------------------------//
    //----------------- Запрос необходимых для приложения разрешений  ---------------------------//
    public boolean requestApplicationPermissions() {
        final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
        int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int permissionReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);}
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {listPermissionsNeeded.add(Manifest.permission.SEND_SMS);}
        if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);}

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    //-------------------------------------------------------------------------------------------//
    //----------------   Обработка запросов разрешения приложения -------------------------------//
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int permissionReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (  (locationPermission == PackageManager.PERMISSION_GRANTED)
                &&(permissionSendMessage == PackageManager.PERMISSION_GRANTED)
                &&(permissionReadPhoneState == PackageManager.PERMISSION_GRANTED) ) {
            ApplicationInit();
            //-----------------------------------------------------------------------------------//
            //-----------------------------------------------------------------------------------//
        }else{
            ApplicationInitWithDenided();
        }
    }
    //-------------------------------------------------------------------------------------------//
    public static int currentTimeMillis() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public void ApplicationInitWithDenided(){
        //webView.loadUrl("javascript: Materialize.toast('Не все разрешения были полученны -', 8000, 'rounded')");
        Log.e("MainActivity" ,"Не все  разрешения полученны -");
        webView.loadUrl("javascript: window.openActivityWithDenided();");
    }
    //Инициализация приложения в случае если все разрешения полученны
    public void ApplicationInit(){
        String token = FirebaseInstanceId.getInstance().getToken();
        //Log.e("MainActivity" ,"FirebaseInstanceId = "+token );
        Log.e("MainActivity" ,"Все разрешения полученны -");

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Log.e("MainActivity" ,"IMEI УСТРОЙСТВА - "+ telephonyManager.getDeviceId()  );

        webView.loadUrl("javascript: window.openActivityWithGranted();");
        //--------  Запуск сервиса отправки смс ---------------------//
        if (!isServiceRunning(SmsQueueLoop.class)) {startService(new Intent(this, SmsQueueLoop.class) );}
        if (!isServiceRunning(GetSMS.class)) { startService(new Intent(this, GetSMS.class)); }
        //----------------------------------------------------------//

    }






}