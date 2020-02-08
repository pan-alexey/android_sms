package panda.smsgateway.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by alexey on 05.10.2017.
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInsIDListenerService";

    @Override
    public void onTokenRefresh() {
        String NewToken = FirebaseInstanceId.getInstance().getToken();
        //В момент обновления token
        Log.d(TAG, "NEW FCM TOKEN:" + NewToken);
    }



}
