package familla.mipl.familla.activity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InternetConnectionChange extends BroadcastReceiver {
    DatabaseHandler handler;
    SQLiteDatabase db, db_read;
    SyncData sync;

    @Override
    public void onReceive(Context context, Intent intent) {
        //super.onReceive(context, intent);
        handler = new DatabaseHandler(context);
        db = handler.getWritableDatabase();
        db_read = handler.getReadableDatabase();
        Log.d("app", "Network connectivity change");
        if (intent.getExtras() != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Log.i("app", "Network " + ni.getTypeName() + " connected");
              //  Toast.makeText(context, "connected", Toast.LENGTH_LONG).show();

                Login.status = "online";

                ContentValues cv  =new ContentValues();
                cv.put(DatabaseHandler.TAG_SYNCSTATUSONILNETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                db.update(DatabaseHandler.TABLE_SYNCSTATUS, cv, "_id = 1", null);
                Log.e("call", "status online, call webservice");
                Log.e("syncadata", "ready to be called");

                //String check= "select syncstatus_done from syncstatus where _id=1";
                //Cursor c_check = db_read.rawQuery(check, null);
                //c_check.moveToFirst();
                //int i = c_check.getInt(c_check.getColumnIndex(DatabaseHandler.TAG_SYNCSTATUSDONE));
                //if(i!=0 && Login.status.equals("online")){
                    Log.e("c_check_session","can sync now");
                   sync = new SyncData(context);
                    sync.execute();
                //}

            } else  {
                Log.d("app", "There's no network connectivity");
               // Toast.makeText(context, "disconnected",Toast.LENGTH_LONG).show();

                Login.status = "offline";
                ContentValues cv  =new ContentValues();
                cv.put(DatabaseHandler.TAG_SYNCSTATUSOFFLINETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH).format(new Date()));
                db.update(DatabaseHandler.TABLE_SYNCSTATUS,cv, "_id = 1", null );
                sync = new SyncData(context);
               sync.cancel(true);
                Log.e("call","status offline, sync stopped for now");
            }
        }
    }
}