package familla.mipl.familla.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by software6 on 18/01/2016.
 */
public class SwipeReceiver extends BroadcastReceiver {
    DatabaseHandler handler;
    SQLiteDatabase db, db_read;
    long org_millis, new_millis;
    Context context_i;
    Date nextdate = null;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm a", Locale.ENGLISH);
    Format formatter =  new SimpleDateFormat("dd.MM.yyyy hh:mm a",Locale.ENGLISH);
    @Override
    public void onReceive(Context context, Intent intent) {
           /* String reminderid = intent.getExtras().getString("notificationid");
        Log.e("reminderid", reminderid);*/
        context_i = context;
        handler = new DatabaseHandler(context);
        db = handler.getWritableDatabase();
        db_read = handler.getReadableDatabase();
        Bundle bundle = intent.getExtras();
        Integer reminderid = bundle.getInt("notificationid");
        Log.e("reminder", reminderid.toString());


        ContentValues cv = new ContentValues();
        cv.put("commontask_remindercancelled", 1); //These Fields should be your String values of actual column names
        Integer c = db.update(DatabaseHandler.TABLE_COMMONTASK, cv, null, null);
        Log.e("checked", c.toString());

        /*ContentValues cv = new ContentValues();
        cv.put("commontask_remindercancelled", 1); //These Fields should be your String values of actual column names
        Integer c = db.update(DatabaseHandler.TABLE_COMMONTASK, cv, "_id=" + reminderid, null);
        Log.e("checked", c.toString());*/

       /* String query = "select commontask_setrepeating , commontask_reminderdatetime from commontask where _id = "+reminderid;
        Cursor c_read = db_read.rawQuery(query, null);
        c_read.moveToFirst();
        String setrepeating = c_read.getString(c_read.getColumnIndex(DatabaseHandler.TAG_COMMONTASKSETREPEATING));
        String reminderdatetime = c_read.getString(c_read.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME));
        Log.e("reminderdatetime",reminderdatetime);
       //SimpleDateFormat sdf = new SimpleDateFormat("d.m.yyyy hh:mm a");
        try {
            nextdate = sdf.parse(reminderdatetime);
          String medha =  formatter.format(nextdate);
            org_millis = nextdate.getTime();
            Log.e("nextdate",medha);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(setrepeating.equals("Repeat Daily")){
            new_millis = org_millis+(1000*60);
            long current_millis =  System.currentTimeMillis() ;
            Integer diff = (int)(new_millis - current_millis);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(new_millis);
            String newdate = formatter.format(calendar.getTime());
            Log.e("newdate",newdate);
            ContentValues cv = new ContentValues();
            cv.put("commontask_reminderdatetime", newdate); //These Fields should be your String values of actual column names
            Integer c = db.update(DatabaseHandler.TABLE_COMMONTASK, cv, "_id=" + reminderid, null);
            Log.e("checked", c.toString());
            Log.e("diff", diff.toString());
           scheduleNotification((int)(new_millis - current_millis), reminderid);
        }*/


    }

    public void scheduleNotification(/* Notification notification, */int delay, int uniqueid) {

        Intent notificationIntent = new Intent(context_i,
                NotificationPublisher.class);
        notificationIntent.putExtra("notificationid",uniqueid);
        // notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID,
        // 1);
        // notificationIntent.putExtra(NotificationPublisher.NOTIFICATION,
        // notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context_i,  uniqueid,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationPublisher.NOTIFICATION_ID = uniqueid;

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) this.context_i.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}
