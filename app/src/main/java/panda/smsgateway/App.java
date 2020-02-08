package panda.smsgateway;
/**
 * Created by alexey on 22.08.2017.
 */
import android.app.Application;
import android.util.Log;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("App" ,"App start");
    }
}