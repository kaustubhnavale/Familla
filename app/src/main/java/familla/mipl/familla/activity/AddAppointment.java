package familla.mipl.familla.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import familla.mipl.familla.R;

import static android.app.AlarmManager.INTERVAL_DAY;

public class AddAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {
    public static final String ActicityName = "Add Appointment";
    public static String status = "";
    final Context context = this;
    public Dialog dialog;
    int checkisize = 0;
    String[] repeat = {"JUST ONCE", "REPEAT DAILY", "REPEAT WEEKLY", "REPEAT MONTHLY"};
    Button dateset, save;
    static Button timeset;
    static String storedate, storetime;
    EditText appointmentwith, otherdetails;
    int Img[] = {R.drawable.repeaticon, R.drawable.repeaticon, R.drawable.repeaticon, R.drawable.repeaticon};
    static String otherdetail, appointment, repeatstatus, selectdate, selecttime, billamount;
    DatabaseHandler handler;
    SQLiteDatabase db;
    Cursor c;
    CheckBox che;
    ListView l;
    Cursor c_user;
    TextView sharetext;
    ConnectionDetector cd;
    String task_id;
    String selectetdrop = "", selectedFromList1 = "";
    int po;
    TextView test;
    SyncData syncnow;
    Calendar calendar;
    static int Chour, Cminute;
    DialogFragment dialogfragment;
    static TimePickerDialog timepickerdialog1;
    ArrayList<String> userid = new ArrayList<String>();
    ListView memlist1;
    ArrayList<String> member_list = new ArrayList<String>();
    StringBuffer showname = new StringBuffer();
    SparseBooleanArray checked;
    ArrayList<String> mem_list = new ArrayList<String>();
    Typeface tf;
    String name;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addappointment);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" A P P O I N T M E N T ");
        mToolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorappointment)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorappointment));
            //  window.setNavigationBarColor(this.getResources().getColor(R.color.colorbilldata));
        }
        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();

        String qry_user = "select * from userdetails";
        c_user = db.rawQuery(qry_user, null);
        c_user.moveToFirst();

        calendar = Calendar.getInstance();

        Chour = calendar.get(Calendar.HOUR_OF_DAY);
        Cminute = calendar.get(Calendar.MINUTE);

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), Img, repeat);

        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
            status = "online";
        } else {
            status = "offline";
        }
        //////Date Time
        dateset = (Button) findViewById(R.id.Date);
        dateset.setTypeface(tf, Typeface.BOLD);
        timeset = (Button) findViewById(R.id.Time);
        timeset.setTypeface(tf, Typeface.BOLD);
        sharetext = (TextView) findViewById(R.id.sharetext);
        save = (Button) findViewById(R.id.addevent);

        otherdetails = (EditText) findViewById(R.id.addinfo);
        otherdetails.setTypeface(tf, Typeface.BOLD);
        appointmentwith = (EditText) findViewById(R.id.name);
        appointmentwith.setTypeface(tf, Typeface.BOLD);
        test = (TextView) findViewById(R.id.addre);
        appointmentwith.requestFocus();
        name = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME)));

        String quer1 = "select * from memberdetails where  md_status = 1";
        Cursor c_user1 = db.rawQuery(quer1, null);
        c_user1.getCount();

        if (c_user1.getCount() != 0)

        {
            sharetext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mem_list.clear();
                    member_list.clear();
                    showmember();
                    sharetext.setText("SELECT SHARE WITH");
                }
            });
        } else {
            sharetext.setText(name);
           /* person.setVisibility(View.GONE);
            share_with.setVisibility(View.GONE);*/
        }
        dateset.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           Log.e("Hi", "Error");
                                           Calendar now = Calendar.getInstance();
                                           DatePickerDialog dpd = DatePickerDialog.newInstance(
                                                   AddAppointment.this,
                                                   now.get(Calendar.YEAR),
                                                   now.get(Calendar.MONTH),
                                                   now.get(Calendar.DAY_OF_MONTH)
                                           );
                                           dpd.setThemeDark(false);
                                           dpd.vibrate(true);
                                           dpd.dismissOnPause(true);
                                           Calendar c = Calendar.getInstance();
                                           c.setTimeInMillis(System.currentTimeMillis() - 1000);
                                           dpd.setMinDate(c);
                                           dpd.showYearPickerFirst(false);
                                           dpd.setAccentColor(Color.parseColor("#38588A"));
                                           android.app.FragmentManager fm = AddAppointment.this.getFragmentManager();
                                           dpd.show(fm, "ok");
                                       }
                                   }
        );

        timeset.setOnClickListener(new View.OnClickListener()

                                   {

                                       @Override
                                       public void onClick(View v) {
                                           dialogfragment = new TimePickerTheme1class();
                                           dialogfragment.show(getFragmentManager(), "Time Picker with Theme 1");
                                       }
                                   }
        );

        setPickerValues();
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        task_id = "";
        if (extras != null) {

            task_id = intent.getExtras().getString("Taskid").toString();
            String quer = "select * from commontask where _id= " + task_id + " ";
            Cursor c_user = db.rawQuery(quer, null);
            c_user.moveToFirst();
            String ab = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)));
            appointmentwith.setText(ab);
            otherdetails.setText(c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKDETAIL)));
            String remdate = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
            String rep = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKSETREPEATING)));
            repeatstatus = rep;
            test.setText(repeatstatus);

            Date rem_datetime = null;
            DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a", Locale.ENGLISH);
            try {
                rem_datetime = formatter.parse(remdate);

                Log.e("date", rem_datetime.toString());

                int s = rem_datetime.getDate();
                String times = (String) android.text.format.DateFormat.format("hh:mm a", rem_datetime);
                String date = (String) android.text.format.DateFormat.format("d.M.yyyy", rem_datetime);
                storedate = (String) android.text.format.DateFormat.format("yyyy-MM-dd", rem_datetime);
                storetime = (String) android.text.format.DateFormat.format("HH:mm", rem_datetime);
                selectdate = date;
                selecttime = times;
                dateset.setText(date);
                timeset.setText(times);

            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("err", e.toString());
            }
            save.setText("Update");

            /*if(ab!="Birthday"||ab!="Anniversary"||ab!="Family get together")
            {
             type.setText("Other");
             otherevent.setText(ab);
            }
            else
            {
                type.setText(ab);
                addeve.setVisibility(View.GONE);
            }*/
        }

        test.setTypeface(tf, Typeface.BOLD);
        test.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog);

                l = (ListView) dialog.findViewById(R.id.dia);

                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), Img, repeat);
                l.setAdapter(customAdapter);

                dialog.getWindow().setTitleColor(getResources().getColor(R.color.textColorPrimary));
                dialog.getWindow().setBackgroundDrawableResource(R.color.colordialog);

                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                Button ok = (Button) dialog.findViewById(R.id.ok);
                dialog.getWindow().setTitle("SELECT A TYPE OF REMINDER");
                dialog.show();

                l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //  selectetdrop=(l.getItemAtPosition(position).toString());
                        selectetdrop = repeat[position];

                        //Log.e("sad","ds");
                    }


                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (selectetdrop.equals("")) {
                            test.setText("SELECT REMINDER");
                            dialog.dismiss();
                        } else {
                            test.setText(selectetdrop);
                            repeatstatus = selectetdrop;
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Do You Want To Add Appointment");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();*/
                if (appointmentwith.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill proper details", Toast.LENGTH_LONG).show();
                } else if (dateset.getText().toString().toLowerCase().equals("set a date")) {
                    Toast.makeText(getApplicationContext(), "Please fill proper Date ", Toast.LENGTH_LONG).show();
                } else if (timeset.getText().toString().toLowerCase().equals("set a time")) {
                    Toast.makeText(getApplicationContext(), "Please fill proper Time", Toast.LENGTH_LONG).show();
                } else if (test.getText().toString().equals("SELECT REMINDER")) {
                    Toast.makeText(getApplicationContext(), "Please select Reminder", Toast.LENGTH_LONG).show();

                } else {
                    appointment = appointmentwith.getText().toString();

                    if (otherdetails.getText().toString().equals("")) {
                        otherdetail = otherdetails.getText().toString();
                    }
                    String repeatstatus1 = repeatstatus;
                    Log.e("repeat", repeatstatus1);
                    String rem_date = selectdate + " " + selecttime;
                    Log.e("rem_date", rem_date);
                    Log.e("now", new SimpleDateFormat("dd.MM.yyyy HH:mm a", Locale.ENGLISH).format(new Date()));
                    long taskid;
                    String rem_date1 = storedate + " " + storetime;

                    if (task_id != "") {
                        taskid = Long.parseLong(task_id);
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseHandler.TAG_COMMONTASKTYPE, "Appointment");
                        cv.put(DatabaseHandler.TAG_COMMONTASKNAME, appointment);
                        cv.put(DatabaseHandler.TAG_COMMONTASKEXTRANOTE, "");
                        cv.put(DatabaseHandler.TAG_COMMONTASKDETAIL, otherdetail);
                        cv.put(DatabaseHandler.TAG_COMMONTASKSETREPEATING, repeatstatus1);
                        cv.put(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME, selectdate + " " + selecttime);
                        cv.put(DatabaseHandler.TAG_COMMONTASKCREATEDDATETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                        cv.put(DatabaseHandler.TAG_COMMONTASKOWNERID, c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));
                        cv.put(DatabaseHandler.TAG_COMMONTASKGROUPID, c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID)));
                        cv.put(DatabaseHandler.TAG_COMMONTASKREMINDER, rem_date1);
                        db.update(DatabaseHandler.TABLE_COMMONTASK, cv, "_id ='" + task_id + "' ", null);

                    } else {
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseHandler.TAG_COMMONTASKTYPE, "Appointment");
                        cv.put(DatabaseHandler.TAG_COMMONTASKNAME, appointment);
                        cv.put(DatabaseHandler.TAG_COMMONTASKEXTRANOTE, "");
                        cv.put(DatabaseHandler.TAG_COMMONTASKDETAIL, otherdetail);
                        cv.put(DatabaseHandler.TAG_COMMONTASKSETREPEATING, repeatstatus1);
                        cv.put(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME, selectdate + " " + selecttime);
                        cv.put(DatabaseHandler.TAG_COMMONTASKCREATEDDATETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                        cv.put(DatabaseHandler.TAG_COMMONTASKOWNERID, c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));
                        cv.put(DatabaseHandler.TAG_COMMONTASKGROUPID, c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID)));
                        cv.put(DatabaseHandler.TAG_COMMONTASKREMINDER, rem_date1);
                        taskid = db.insert(DatabaseHandler.TABLE_COMMONTASK, null, cv);

                        ContentValues cv1 = new ContentValues();
                        Log.e("share_count", ((Integer) member_list.size()).toString());
                        for (int i = 0; i < member_list.size(); i++) {
                            Log.e("member_share", member_list.get(i));
                            cv1.put(DatabaseHandler.TAG_SHARELOCALID, taskid);
                            cv1.put(DatabaseHandler.TAG_SHAREUSERID, member_list.get(i));
                            db.insert(DatabaseHandler.TABLE_SHARINGDETAILS, null, cv1);
                        }
                    }
                    Long value = null;
                    if (repeatstatus.equals("JUST ONCE")) {

                        int val = 0;
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());
                        Log.v("time in millisecond", "" + c.getTime());
                        Log.v("time in millisecond", "" + c.getTimeInMillis());
                        Date rem_datetime = null;
                        DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a", Locale.ENGLISH);
                        try {
                            Log.e("selecteddate", selectdate);
                            rem_datetime = formatter.parse(selectdate + " " + selecttime);
                            Date thisdate = new Date();
                            String thid = formatter.format(thisdate);
                            Log.e("this", thid);
                            Log.e("date", rem_datetime.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e("err", e.toString());
                        }

                        value = rem_datetime.getTime();
                        val = (int) (value - c.getTimeInMillis());

                        Log.e("values difference", "" + val);

                    /*Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, val / 1000);*/

                        scheduleNotification(val, (int) taskid);

                    } else if (repeatstatus.equals("REPEAT WEEKLY")) {

                        int val = 0;
                        int del = 604800000;
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());
                        Log.v("time in millisecond", "" + c.getTime());
                        Log.v("time in millisecond", "" + c.getTimeInMillis());
                        Date rem_datetime = null;
                        DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a", Locale.ENGLISH);
                        try {
                            Log.e("selecteddate", selectdate);
                            rem_datetime = formatter.parse(selectdate + " " + selecttime);
                            Date thisdate = new Date();
                            String thid = formatter.format(thisdate);
                            Log.e("this", thid);
                            Log.e("date", rem_datetime.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e("err", e.toString());
                        }

                        value = rem_datetime.getTime();
                        val = (int) (value - c.getTimeInMillis());

                        Log.e("values difference", "" + val);

                    /*Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, val / 1000);*/

                        scheduleNotificationRepeat(val, del, (int) taskid);

                    } else if (repeatstatus.equals("REPEAT MONTHLY")) {

                        int val = 0;
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());
                        Log.v("time in millisecond", "" + c.getTime());
                        Log.v("time in millisecond", "" + c.getTimeInMillis());
                        Date rem_datetime = null;
                        long time = c.getTimeInMillis();
                        c.add(Calendar.MONTH, 1);
                        long interval = c.getTimeInMillis();
                        DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a", Locale.ENGLISH);
                        try {
                            Log.e("selecteddate", selectdate);
                            rem_datetime = formatter.parse(selectdate + " " + selecttime);
                            Date thisdate = new Date();
                            String thid = formatter.format(thisdate);
                            Log.e("this", thid);
                            Log.e("date", rem_datetime.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e("err", e.toString());
                        }

                        value = rem_datetime.getTime();
                        val = (int) (value - time);

                        Log.e("values difference", "" + val);
                    /*Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, val / 1000);*/

                        scheduleNotificationRepeat(val, interval, (int) taskid);

                    } else {
                        int val = 0;
                        int del = 1;
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());
                        Log.v("time in millisecond", "" + c.getTime());
                        Log.v("time in millisecond", "" + c.getTimeInMillis());
                        Date rem_datetime = null;
                        DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a", Locale.ENGLISH);
                        try {
                            Log.e("selecteddate", selectdate);
                            rem_datetime = formatter.parse(selectdate + " " + selecttime);
                            Date thisdate = new Date();
                            String thid = formatter.format(thisdate);
                            Log.e("this", thid);
                            Log.e("date", rem_datetime.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e("err", e.toString());
                        }

                        value = rem_datetime.getTime();
                        val = (int) (value - c.getTimeInMillis());

                        Log.e("values difference", "" + val);
                    /*Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, val / 1000);*/

                        scheduleNotificationRepeat(val, del, (int) taskid);
                    }

                    if (status.equals("online")) {
                        Log.e("call", "status online, call webservice");
                        String checkdone = "select * from syncstatus where _id = 1";
                        Cursor c_process = db.rawQuery(checkdone, null);
                        c_process.moveToFirst();
                        Integer process = c_process.getInt(c_process.getColumnIndex(DatabaseHandler.TAG_SYNCSTATUSDONE));
                        Log.e("process", process.toString());
                      /*  if(process == 0){
                            Log.e("webservice ", "alread running in background , do nothing for now");
                        }
                        else{*/
                        Log.e("webservice", "fetch entries to be uploaded and call webservice");
                        syncnow = new SyncData(getApplicationContext());
                        syncnow.execute();
                        //   }


                    } else if (status.equals("offline")) {
                        Log.e("call", "status offline, sync cancelled for now");
                    }
                    showOtherFragment();
                }
            }

        });

               /* builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });*/
    }

    public void scheduleNotificationRepeat(int delay, long repeat, int uniqueid) {

        Intent notificationIntent = new Intent(getApplicationContext(),
                NotificationPublisher.class);
        notificationIntent.putExtra("notificationid", uniqueid);
        // notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID,
        // 1);
        // notificationIntent.putExtra(NotificationPublisher.NOTIFICATION,
        // notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), uniqueid,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationPublisher.NOTIFICATION_ID = uniqueid;

        long futureInMillis = SystemClock.elapsedRealtime() + delay;

        if (repeat == 1) {
            AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, INTERVAL_DAY, pendingIntent);
            Log.e("siddesh", String.valueOf(repeat));
        } else {
            AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, repeat, pendingIntent);
            Log.e("siddesh", String.valueOf(repeat));
        }
    }

    public void setPickerValues() {
        Calendar caln = Calendar.getInstance();
        Integer yr = caln.get(Calendar.YEAR);
        Integer mth = caln.get(Calendar.MONTH);
        Integer dy = caln.get(Calendar.DAY_OF_MONTH);
        Integer hr = caln.get(Calendar.HOUR_OF_DAY);
        Integer min = caln.get(Calendar.MINUTE);

        selecttime = dy + "." + (++mth) + "." + yr;

        if (hr > 12) {
            hr = hr - 12;
            String hourString = hr < 10 ? "0" + hr : "" + hr;
            String minuteString = min < 10 ? "0" + min : "" + min;
            selecttime = hourString + ":" + minuteString + " PM";

        } else {

            String hourString = hr < 10 ? "0" + hr : "" + hr;
            String minuteString = min < 10 ? "0" + min : "" + min;
            selecttime = hourString + ":" + minuteString + " AM";
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        switch (arg0.getId()) {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "." + (++monthOfYear) + "." + year;

        selectdate = date;

        dateset.setText(selectdate);
        String monthString = String.valueOf(monthOfYear);
        if (monthString.length() == 1) {
            monthString = "0" + monthString;
        }
        storedate = year + "-" + monthString + "-" + dayOfMonth;
    }

    public void scheduleNotification(/* Notification notification, */int delay, int uniqueid) {

        Intent notificationIntent = new Intent(getApplicationContext(),
                NotificationPublisher.class);
        notificationIntent.putExtra("notificationid", uniqueid);
        // notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID,
        // 1);
        // notificationIntent.putExtra(NotificationPublisher.NOTIFICATION,
        // notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), uniqueid,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationPublisher.NOTIFICATION_ID = uniqueid;
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public void showOtherFragment() {
        // Toast.makeText(getApplicationContext(), "Appointment Reminder Set Successfully", Toast.LENGTH_LONG).show();
        AlertDialog.Builder alert = new AlertDialog.Builder(AddAppointment.this, R.style.AlertDialogCustom);

        // alert.setTitle("Doctor");
        alert.setMessage("Appointment Reminder Set Successfully!");
        //alert.setPositiveButton("OK",null);

        alert.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        member_list.clear();
                        userid.clear();
                        Intent i = new Intent(AddAppointment.this, MainActivity.class);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });

        alert.show();
    }

    @Override
    public void onBackPressed() {
        member_list.clear();
        userid.clear();
        mem_list.clear();
        db.close();
        Intent i = new Intent(AddAppointment.this, Appointment.class);
        i.putExtra("Activity", ActicityName);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void showmember() {

        String quer1 = "select * from memberdetails where  md_status = 1";
        Cursor c_user1 = db.rawQuery(quer1, null);
        c_user1.moveToFirst();

        String listdata;
        while (!c_user1.isAfterLast()) {
            listdata = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));
            mem_list.add(listdata.toUpperCase());
            userid.add(c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));
            c_user1.moveToNext();
        }

        dialog = new Dialog(AddAppointment.this);
        dialog.setContentView(R.layout.member);
        dialog.setTitle("Select Person");

        dialog.getWindow().setTitleColor(getResources().getColor(R.color.textColorPrimary));

        memlist1 = (ListView) dialog.findViewById(R.id.memlist);
        che = (CheckBox) dialog.findViewById(R.id.chkAll);

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, mem_list);

        memlist1.setAdapter(arrayAdapter);

        //memlist1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        memlist1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        dialog.setCanceledOnTouchOutside(true);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView ok = (TextView) dialog.findViewById(R.id.ok);
        ok.setTypeface(tf, Typeface.BOLD);
        cancel.setTypeface(tf, Typeface.BOLD);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mem_list.clear();

            }
        });

        memlist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // change the background color of the selected element
                selectedFromList1 = (memlist1.getItemAtPosition(position).toString());
                //  dialog.dismiss();*//**//*
                for (int i = 0; i < memlist1.getAdapter().getCount(); i++) {
                    if (checked.get(i) == false) {
                        che.setChecked(false);
                    }
                }

            }
        });

        che.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (che.isChecked() == true) {
                    for (int i = 0; i < memlist1.getCount(); i++) {
                        memlist1.setItemChecked(i, true);
                    }
                } else {
                    for (int i = 0; i < memlist1.getCount(); i++) {
                        memlist1.setItemChecked(i, false);
                    }
                }
            }
        });

        checked = memlist1.getCheckedItemPositions();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showname.delete(0, showname.length());
                if (checked.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Please select member", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < memlist1.getAdapter().getCount(); i++) {
                        if (checked.get(i) == true) {
                            member_list.add(userid.get(i));
                            showname.append(memlist1.getItemAtPosition(i).toString());
                            showname.append(",");
                            checkisize++;
                        }
                    }
                    if (showname.length() == 0) {
                        checkisize = 0;
                        Toast.makeText(getApplicationContext(), "Please select member", Toast.LENGTH_SHORT).show();
                    } else {
                        if (memlist1.getAdapter().getCount() == checkisize) {
                            sharetext.setText("ALL");

                        } else {
                            String s = showname.toString();
                            s = s.substring(0, s.length() - 1);
                            sharetext.setText(s);
                        }
                    }
                    checkisize = 0;
                    dialog.dismiss();
                    mem_list.clear();
                    userid.clear();
                }
            }
        });

        dialog.show();
    }

    @SuppressLint("validFragment")
    public static class TimePickerTheme1class extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            timepickerdialog1 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT, this, Chour, Cminute, false);

            return timepickerdialog1;
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            storetime = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);

            if (hourOfDay == 0) {
                String hourString = "12";
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                selecttime = hourString + ":" + minuteString + " AM";
                timeset.setText(selecttime);
                //  storetime = selecttime.replace("AM", "");
            } else if (hourOfDay > 12) {
                hourOfDay = hourOfDay - 12;
                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                selecttime = hourString + ":" + minuteString + " PM";
                timeset.setText(selecttime);
                // storetime = selecttime.replace("PM", "");
            } else if (hourOfDay == 12) {
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                selecttime = "12:" + minuteString + " PM";
                timeset.setText(selecttime);
                //  storetime = selecttime.replace("PM", "");
            } else {
                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                selecttime = hourString + ":" + minuteString + " AM";
                timeset.setText(selecttime);
                // storetime = selecttime.replace("AM", "");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_home);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_home) {
            member_list.clear();
            mem_list.clear();
            userid.clear();
            db.close();
            Intent i = new Intent(AddAppointment.this, MainActivity.class);
            startActivity(i);
        }
        if (id == android.R.id.home) {
            member_list.clear();
            mem_list.clear();
            userid.clear();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}