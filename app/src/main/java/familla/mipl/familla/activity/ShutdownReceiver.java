package familla.mipl.familla.activity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by android on 3/10/2017.
 */

public class ShutdownReceiver extends BroadcastReceiver {

    DatabaseHandler handler;
    SQLiteDatabase db, db_read;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("shoutdown", "System shutting down");
        handler = new DatabaseHandler(context);
        db = handler.getWritableDatabase();
        db_read = handler.getReadableDatabase();
        ContentValues  cv_user = new ContentValues();
        cv_user.put(DatabaseHandler.TAG_REBOOT,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new java.util.Date()));
        db.update("userdetails", cv_user, "_id" + "=" + 1,  null);
    }
}
