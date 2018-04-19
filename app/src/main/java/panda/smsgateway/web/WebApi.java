package panda.smsgateway.web;

/**
 * Created by alexey on 05.10.2017.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class WebApi {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 100;

    Context mContext;
    /** Instantiate the interface and set the context */
    public WebApi(Context c) {
        mContext = c;
    }
    /** Далее идут методы, которые появятся в JavaScript */
    @JavascriptInterface
    public void sendSms(String phoneNumber, String message) {
        Log.e("WebAppInterface",phoneNumber);
    }

    @JavascriptInterface
    public void log(String message) {
        Log.e("WebAppInterface",message);
    }

    // This function can be called in our JS script now
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void showPermission() {
        //Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + mContext.getPackageName()));
        mContext.startActivity(appSettingsIntent);

    }



}
