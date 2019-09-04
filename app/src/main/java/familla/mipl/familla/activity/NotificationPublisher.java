package familla.mipl.familla.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import familla.mipl.familla.R;

import static android.content.Context.MODE_PRIVATE;

public class NotificationPublisher extends BroadcastReceiver {
    public static int NOTIFICATION_ID;

    final static String GROUP_REMINDERS = "group_reminders";
    public static String NOTIFICATION = "notification";
    static NotificationManagerCompat notificationManager = null;
    DatabaseHandler handler;
    SQLiteDatabase db;
    Intent notify;
    Cursor c;
    Uri uri;
    PendingIntent pendingIntent;
    public static int index;
    public static String TAG_CUSTOMSOUND = "";
    public static final String MyRing = "MyPrefs";
    SharedPreferences sharedpreferences;
    String[] alertDays;
    String username;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Integer reminderid = bundle.getInt("notificationid");

        if (intent.hasExtra("EVENT_ALERT_DAYS")) {
            alertDays = bundle.getStringArray("EVENT_ALERT_DAYS");

            SimpleDateFormat dayFormatter = new SimpleDateFormat("E", Locale.ENGLISH);
            String todayText = dayFormatter.format(new Date(System.currentTimeMillis()));
//Log.d(LOG_TAG, "todayText:" + todayText);

            boolean matched = false;
            for (CharSequence item : alertDays) {
                //Log.d(LOG_TAG, "Schedule time:" + alertTimeInLong);
                if (todayText.equalsIgnoreCase(item.toString())) {
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                return;
            }
        }

        handler = new DatabaseHandler(context);
        db = handler.getReadableDatabase();

        String qry_user = "select * from userdetails";
        Cursor c_user = db.rawQuery(qry_user, null);
        c_user.moveToFirst();

        String str = "select commontask_type , commontask_name , commontask_extranote , commontask_detail , commontask_reminderdatetime ,commontask_ownerid from commontask where  _id = " + reminderid;
        Log.e("str", str);
        c = db.rawQuery(str, null);
        c.moveToFirst();
        Log.e("result", c.getString(c.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)));

        //   notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager = NotificationManagerCompat.from(context);

        Intent clearintent = new Intent(context, SwipeReceiver.class);
        clearintent.putExtra("notificationid", reminderid);

        Intent patankar = new Intent(context, MainActivity.class);
        patankar.putExtra("notificationid", reminderid);

        Log.d("customsound", TAG_CUSTOMSOUND);

        sharedpreferences = context.getSharedPreferences(MyRing, MODE_PRIVATE);

        String ringti = sharedpreferences.getString("ring", "");

        //   uri=Uri.parse(ringti);

        if (!ringti.equals("")) {
            uri = Uri.parse(ringti);
        } else {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        String qry_count = "select commontask_name,commontask_type from commontask where commontask_remindercancelled is null";
        Cursor c_count = db.rawQuery(qry_count, null);
        c_count.moveToFirst();
        Integer pending = c_count.getCount() - 1;

        notify = new Intent(context, Todo.class);
        notify.putExtra("notification", "intenet");
        notify.putExtra("notificationid", reminderid);

        notify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(context, 0, notify, PendingIntent.FLAG_UPDATE_CURRENT);

        String qry_fetch = "select _id ,commontask_name from (select * from commontask where commontask_remindercancelled is null order by _id desc limit 3) ";
        Cursor c_fetch = db.rawQuery(qry_fetch, null);
        c_fetch.moveToFirst();

        String taskname = c.getString(c.getColumnIndex(DatabaseHandler.TAG_COMMONTASKTYPE));

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("Familla")
                .setContentText("Reminder for " + taskname)
                .setSmallIcon(R.drawable.famillanotify)
                .setSound(uri)
                .setAutoCancel(true)

                //  .setDeleteIntent(deleteIntent)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.famillanotify, "show activity", pendingIntent);

        Notification notification = null;
        notification = new Notification.InboxStyle(builder)
                .addLine(c.getString(c.getColumnIndex(DatabaseHandler.TAG_COMMONTASKTYPE)))
                .setSummaryText("").build();

        /*else if(pending ==3){
            String[] values = new String[3];
            for(int i =0;i<3;i++){
                c_fetch.moveToNext();
                values[i] = c_fetch.getString(c_fetch.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME));
            }
            notification = new Notification.InboxStyle(builder)
                    .addLine(c.getString(c.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)))
                    .addLine(values[0])
                    .addLine(values[1])
                    .setSummaryText("+" + pending.toString() + " more").build();
        }*/
        // Put the auto cancel notification flag
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(NOTIFICATION_ID, notification);

        // notificationManager.cancelAll();
    }

    public static void cancelNotification() {
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }
}