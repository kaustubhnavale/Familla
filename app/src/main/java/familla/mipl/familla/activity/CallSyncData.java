package familla.mipl.familla.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

/**
 * Created by software6 on 20/01/2016.
 */
public class CallSyncData {

    Context context;
    private final static int INTERVAL = 1000 * 30; //2 minutes
    Runnable mHandlerTask;
    Handler mhandler = new Handler();
    DatabaseHandler handler;
    Runnable runnable;
    SQLiteDatabase db_read;

    public CallSyncData(Context context) {
        this.context = context;
        handler = new DatabaseHandler(context);
        db_read = handler.getReadableDatabase();
    }

    public void CallDelay() {
        Log.e("in", "calldelay");

        runnable = new Runnable() {
            @Override
            public void run() {
                Log.e("syncadata", "ready to be called");

                SyncData data = new SyncData(context);
                data.execute();
            }
        };

        mhandler.postDelayed(runnable, 12000);
    }

    public void stopDownloading() {
        Log.e("stop", "downloading");
        mhandler.removeCallbacks(runnable);
        mhandler.removeCallbacksAndMessages(context);
    }
}