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

import familla.mipl.familla.R;

import static android.content.Context.MODE_PRIVATE;

public class MyNotificationPublisher extends BroadcastReceiver {

    public static final String MyRing = "MyPrefs";
    final static String GROUP_REMINDERS = "group_reminders";
    public static int NOTIFICATION_ID;
    public static String NOTIFICATION = "notification";
    public static int index;
    public static String TAG_CUSTOMSOUND = "";
    static NotificationManagerCompat notificationManager = null;
    Database handler;
    DatabaseHandler han;
    SQLiteDatabase db, dbread;
    Intent notify;
    String username;
    Uri uri;
    PendingIntent pendingIntent;
    SharedPreferences sharedpreferences;
    Integer reminderid;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        reminderid = bundle.getInt("notificationid");

        handler = new Database(context);
        han = new DatabaseHandler(context);

        db = handler.getReadableDatabase();
        dbread = han.getReadableDatabase();
        sharedpreferences = context.getSharedPreferences(MyRing, MODE_PRIVATE);

        String ringti = sharedpreferences.getString("ring", "");

        //   uri=Uri.parse(ringti);

        if (!ringti.equals("")) {
            uri = Uri.parse(ringti);
        } else {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        if (intent.hasExtra("chat")) {

            Cursor c = dbread.rawQuery("select * from userdetails where _id=1 ", null);
            c.moveToFirst();
            String userid = c.getString(c.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID));

            String str = "select * from chat where syncid ='" + reminderid + "'";

            Log.e("sasd", str);

            Cursor ca = db.rawQuery(str, null);
            ca.moveToFirst();

            String senderid = (ca.getString(ca.getColumnIndex(Database.TAG_SENDERID)));
            String content = (ca.getString(ca.getColumnIndex(Database.TAG_CONTENT)));

            String str1 = "select * from memberdetails where md_userid ='" + senderid + "'";
            Cursor cas = dbread.rawQuery(str1, null);
            cas.moveToFirst();

            String name = (cas.getString(cas.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));

            // String memberid= (cas.getString(cas.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));

            String groupid = (cas.getString(cas.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSGROUPID)));

            notificationManager = NotificationManagerCompat.from(context);
            notify = new Intent(context, Chat.class);
            notify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notify.putExtra("userid", userid);
            notify.putExtra("Memberid", senderid);
            notify.putExtra("groupid", groupid);

            pendingIntent = PendingIntent.getActivity(context, 0, notify, PendingIntent.FLAG_CANCEL_CURRENT);

            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle(content)
                    .setContentText("Message from " + name.toUpperCase())
                    .setSmallIcon(R.drawable.famillanotify)
                    .setSound(uri)
                    .setAutoCancel(true)

                    //  .setDeleteIntent(deleteIntent)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.famillanotify, content.toString(), pendingIntent);
            Notification notification = null;
            notification = builder.build();

            notificationManager.notify(NOTIFICATION_ID, notification);
        } else if (intent.hasExtra("download")) {

            String qry_user = "select * from userdetails";
            Cursor c_user = dbread.rawQuery(qry_user, null);
            c_user.moveToFirst();

            String str = "select * from commontask where  _id = " + reminderid;
            Cursor c_use = dbread.rawQuery(str, null);
            c_use.moveToFirst();

            notificationManager = NotificationManagerCompat.from(context);
            notify = new Intent(context, Todo.class);
            notify.putExtra("notification", "intenet");
            notify.putExtra("notificationid", reminderid);
            notify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(context, 0, notify, PendingIntent.FLAG_CANCEL_CURRENT);
            String taskname = c_use.getString(c_use.getColumnIndex(DatabaseHandler.TAG_COMMONTASKTYPE));
            String ownerid = c_use.getString(c_use.getColumnIndex(DatabaseHandler.TAG_COMMONTASKOWNERID));
            String userid = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID));

            if (ownerid == userid) {
                username = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME)).toUpperCase();
            } else {
                String quer1 = "select * from memberdetails where  md_userid=" + ownerid;
                Cursor c_user1 = dbread.rawQuery(quer1, null);
                c_user1.moveToFirst();
                username = c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)).toUpperCase();
            }

            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle("Familla")
                    .setContentText("Reminder for " + taskname + "Set by " + username)
                    .setSmallIcon(R.drawable.famillanotify)
                    .setSound(uri)
                    .setAutoCancel(true)

                    //  .setDeleteIntent(deleteIntent)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.famillanotify, "Show Details", pendingIntent);
            Notification notification = null;
            notification = builder.build();

            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            notificationManager = NotificationManagerCompat.from(context);
            notify = new Intent(context, showmember.class);

            notify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(context, 0, notify, PendingIntent.FLAG_CANCEL_CURRENT);

            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle("Familla")
                    .setContentText("New Member Added")
                    .setSmallIcon(R.drawable.famillanotify)
                    .setSound(uri)
                    .setAutoCancel(true)

                    //  .setDeleteIntent(deleteIntent)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.famillanotify, "Member", pendingIntent);
            Notification notification = null;
            notification = builder.build();

            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}