package familla.mipl.familla.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;

import familla.mipl.familla.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by android on 2/1/2018.
 */

public class ChangeGroupNotification extends BroadcastReceiver {
    public static final String MyRing = "MyPrefs";
    static NotificationManagerCompat notificationManager = null;
    Intent notify;
    Uri uri;
    PendingIntent pendingIntent;
    SharedPreferences sharedpreferences;
    Integer reminderid;
    public static int NOTIFICATION_ID;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        reminderid = bundle.getInt("notificationid");

        sharedpreferences = context.getSharedPreferences(MyRing, MODE_PRIVATE);
        String ringti = sharedpreferences.getString("ring", "");

        //   uri=Uri.parse(ringti);

        if (!ringti.equals("")) {
            uri = Uri.parse(ringti);
        } else {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        notify = new Intent(context, Todo.class);
        notify.putExtra("notification", "intenet");
        notify.putExtra("notificationid", reminderid);

        notify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(context, 0, notify, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("Familla")
                .setContentText("You have been added to new Family")
                .setSmallIcon(R.drawable.famillanotify)
                .setSound(uri)
                .setAutoCancel(true)

                //  .setDeleteIntent(deleteIntent)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.famillanotify, "show more", pendingIntent);

        Notification notification = null;
        notification = new Notification.InboxStyle(builder)
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
    }
}