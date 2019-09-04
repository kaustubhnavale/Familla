package familla.mipl.familla.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

    Handler mhandler = new Handler();
    Runnable runnable;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        runnable = new Runnable() {
            @Override
            public void run() {
                Log.e("syncadata", "ready to be called");

                SyncData data = new SyncData(getApplicationContext());
                data.execute();
            }
        };

        mhandler.postDelayed(runnable, 12000);

        return Service.START_STICKY;
    }

    public void onDestroy() {
        Intent intent = new Intent("familla.mipl.familla.activity");
        intent.putExtra("test", "torestore");
        sendBroadcast(intent);
    }
}