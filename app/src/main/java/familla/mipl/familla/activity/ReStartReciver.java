package familla.mipl.familla.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.app.AlarmManager.INTERVAL_DAY;


public class ReStartReciver extends BroadcastReceiver {
    String query;
    int taskid;
    DatabaseHandler handler;
    SQLiteDatabase db,dbwrite;
    Cursor c,c_user2;
    @Override
    public void onReceive(Context context, Intent intent) {


        intent = new Intent(context, MyService.class);
       context.startService(intent);


        Log.e("Restart", "System Restart");
        Toast.makeText(context, "Booting Completed", Toast.LENGTH_LONG).show();
        handler = new DatabaseHandler(context);
        db = handler.getReadableDatabase();
        dbwrite=handler.getWritableDatabase();

        String quer = "select * from userdetails ";
        Cursor c_user = db.rawQuery(quer, null);
        c_user.moveToFirst();

        String ab = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_REBOOT)));

        query="SELECT * FROM commontask WHERE CAST(strftime('%s', commontask_reminder)  AS  integer) >= CAST(strftime('%s', '"+ab+"')  AS  integer)" ;
        c=db.rawQuery(query,null);
        c.moveToFirst();
        Log.e("Query", query);

        if (c.moveToFirst()) {
            do {
                Toast.makeText(context, "Data found", Toast.LENGTH_LONG).show();
                handler = new DatabaseHandler(context);
                String commontask_setrepeating = (c.getString(c.getColumnIndex(DatabaseHandler.TAG_COMMONTASKSETREPEATING)));
                String commontask_reminderdatetime = (c.getString(c.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
                taskid  = (c.getInt(c.getColumnIndex(DatabaseHandler.TAG_COMMONTASKID)));

                if (commontask_setrepeating.equals("JUST ONCE")) {
                    Long value = null;
                    int val = 0;
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    Log.v("time in millisecond", "" + c.getTime());
                    Log.v("time in millisecond", "" + c.getTimeInMillis());
                    Date rem_datetime = null;
                    DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a", Locale.ENGLISH);
                    try {
                        Log.e("selecteddate", commontask_reminderdatetime);
                        rem_datetime = formatter.parse(commontask_reminderdatetime);
                        Date thisdate = new Date();
                        String thid = formatter.format(thisdate);
                        Log.e("this", thid);
                        Log.e("date", rem_datetime.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("err", e.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    value = rem_datetime.getTime();
                    val = (int) (value - c.getTimeInMillis());

                    Log.e("values difference", "" + val);

                    /*Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, val / 1000);*/

                    Intent notificationIntent = new Intent(context, NotificationPublisher.class);
                    notificationIntent.putExtra("notificationid", taskid);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskid,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationPublisher.NOTIFICATION_ID = taskid;
                    long futureInMillis = SystemClock.elapsedRealtime() + val;
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                }
                else  if (commontask_setrepeating.equals("REPEAT WEEKLY")) {
                    Long value = null;
                    int val = 0;
                    int del=604800000;
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    Log.v("time in millisecond", "" + c.getTime());
                    Log.v("time in millisecond", "" + c.getTimeInMillis());
                    Date rem_datetime = null;
                    long interval=c.getTimeInMillis();
                    DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a",Locale.ENGLISH);
                    try {
                        Log.e("selecteddate", commontask_reminderdatetime);
                        rem_datetime = formatter.parse(commontask_reminderdatetime);
                        Date thisdate = new Date();
                        String thid = formatter.format(thisdate);
                        Log.e("this", thid);
                        Log.e("date", rem_datetime.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("err", e.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    value = rem_datetime.getTime();
                    val = (int) (value - c.getTimeInMillis());

                    Log.e("values difference", "" + val);

                    /*Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, val / 1000);*/

                    Intent notificationIntent = new Intent(context, NotificationPublisher.class);
                    notificationIntent.putExtra("notificationid", taskid);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskid,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationPublisher.NOTIFICATION_ID = taskid;
                    long futureInMillis = SystemClock.elapsedRealtime() + val;
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis,del, pendingIntent);

                }
                else  if (commontask_setrepeating.equals("REPEAT MONTHLY")) {
                    Long value = null;
                    int val = 0;
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    Log.v("time in millisecond", "" + c.getTime());
                    Log.v("time in millisecond", "" + c.getTimeInMillis());
                    Date rem_datetime = null;
                    c.add(Calendar.MONTH,1);
                    long interval=c.getTimeInMillis();
                    DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a",Locale.ENGLISH);
                    try {
                        Log.e("selecteddate", commontask_reminderdatetime);
                        rem_datetime = formatter.parse(commontask_reminderdatetime);
                        Date thisdate = new Date();
                        String thid = formatter.format(thisdate);
                        Log.e("this", thid);
                        Log.e("date", rem_datetime.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("err", e.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    value = rem_datetime.getTime();
                    val = (int) (value - c.getTimeInMillis());

                    Log.e("values difference", "" + val);

                    /*Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, val / 1000);*/

                    Intent notificationIntent = new Intent(context, NotificationPublisher.class);
                    notificationIntent.putExtra("notificationid", taskid);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskid,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationPublisher.NOTIFICATION_ID = taskid;
                    long futureInMillis = SystemClock.elapsedRealtime() + val;
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis,interval, pendingIntent);

                    //temp.clear();
            }
                else {
                    Long value = null;
                    int val = 0;
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    Log.v("time in millisecond", "" + c.getTime());
                    Log.v("time in millisecond", "" + c.getTimeInMillis());
                    Date rem_datetime = null;

                    DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a",Locale.ENGLISH);
                    try {
                        Log.e("selecteddate", commontask_reminderdatetime);
                        rem_datetime = formatter.parse(commontask_reminderdatetime);
                        Date thisdate = new Date();
                        String thid = formatter.format(thisdate);
                        Log.e("this", thid);
                        Log.e("date", rem_datetime.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("err", e.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    value = rem_datetime.getTime();
                    val = (int) (value - c.getTimeInMillis());

                    Log.e("values difference","" + val);

                    /*Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, val / 1000);*/

                    Intent notificationIntent = new Intent(context, NotificationPublisher.class);
                    notificationIntent.putExtra("notificationid", taskid);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskid,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationPublisher.NOTIFICATION_ID = taskid;
                    long futureInMillis = SystemClock.elapsedRealtime() + val;
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis,INTERVAL_DAY, pendingIntent);

                    //temp.clear();
                }
            }
                while (c.moveToNext()) ;
            }


    }


}
