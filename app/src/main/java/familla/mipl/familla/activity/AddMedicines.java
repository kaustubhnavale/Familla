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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class AddMedicines extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {
    public static final String ActicityName = "Add Medicines";
    String[] listArr;
    String[] types = {"Morning", "Afternoon", "Evening", "Night"};
    String[] repeat = {"JUST ONCE", "REPEAT DAILY", "REPEAT WEEKLY", "REPEAT MONTHLY"};
    String session;
    TextView textdose;
    static String selectedFromList1, shareuser_id, storedate, storetime, useridtest;
    String pername;
    ArrayAdapter<String> adapter;
    ArrayList<String> userid = new ArrayList<String>();
    Button dateset, save;
    static Button timeset;
    EditText name, instruction, count, dname, medicinename;
    int Img[] = {R.drawable.repeaticon, R.drawable.repeaticon, R.drawable.repeaticon, R.drawable.repeaticon};
    static String selectedevent, dropdownevent, repeatstatus, selectdate, selecttime, instr, dosedrop, taskid1, task_id, selectetdrop = "";
    int po;
    DatabaseHandler handler;
    SQLiteDatabase db, db_read, db_write, db1;
    Cursor c, c1;
    Cursor c_user, red;
    ListView l;
    final Context context = this;
    SyncData syncnow;
    Calendar calendar;
    static int Chour, Cminute;
    private Toolbar mToolbar;
    DialogFragment dialogfragment;
    TextView type, Partday;
    static TimePickerDialog timepickerdialog1;
    Typeface tf;
    TextView test;
    ConnectionDetector cd;
    public static String status = "";
    int usercont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_medicines_test);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" M E D I C I N E S ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colormedicine)));
        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();
        db_write = handler.getWritableDatabase();
        db_read = handler.getReadableDatabase();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colormedicine));
            //  window.setNavigationBarColor(this.getResources().getColor(R.color.colorbilldata));
        }

        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
            status = "online";
        } else {
            status = "offline";
        }

        String qry_user = "select * from userdetails";
        c_user = db.rawQuery(qry_user, null);
        c_user.moveToFirst();

        calendar = Calendar.getInstance();
        Chour = calendar.get(Calendar.HOUR_OF_DAY);
        Cminute = calendar.get(Calendar.MINUTE);

        /*SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        session = sharedpreferences.getString("User_id", "");
        Log.e("ses", session);*/

        save = (Button) findViewById(R.id.addevent);
        instruction = (EditText) findViewById(R.id.addinfo);
        count = (EditText) findViewById(R.id.count);
        dname = (EditText) findViewById(R.id.docname);
        medicinename = (EditText) findViewById(R.id.Medicinename);
        count.setTypeface(tf, Typeface.BOLD);
        dname.setTypeface(tf, Typeface.BOLD);
        instruction.setTypeface(tf, Typeface.BOLD);

        medicinename.setTypeface(tf, Typeface.BOLD);
        final ArrayList<String> data = new ArrayList<String>();

        type = (TextView) findViewById(R.id.name);

        Partday = (TextView) findViewById(R.id.type);

        type.setTypeface(tf, Typeface.BOLD);

        Partday.setTypeface(tf, Typeface.BOLD);

        //////Date Time
        dateset = (Button) findViewById(R.id.Date);
        timeset = (Button) findViewById(R.id.Time);

        dateset.setTypeface(tf, Typeface.BOLD);
        timeset.setTypeface(tf, Typeface.BOLD);

        String s = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID));
        data.add(c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME)).toUpperCase());
        userid.add(c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSID)));
        useridtest = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSID));
        String qry_user1 = "select * from memberdetails where md_status = 1";
        red = db.rawQuery(qry_user1, null);
        red.moveToFirst();
        usercont = red.getCount();
        if (red.moveToFirst()) {
            do {
                String word = red.getString(red.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME));
                data.add(word.toUpperCase());
                userid.add(red.getString(red.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));
            } while (red.moveToNext());
        }

        Partday.setTypeface(tf, Typeface.BOLD);

        Partday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog);

                l = (ListView) dialog.findViewById(R.id.dia);
                final ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < types.length; ++i) {
                    list.add(types[i].toUpperCase());
                }
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                Button ok = (Button) dialog.findViewById(R.id.ok);
                ok.setTypeface(tf, Typeface.BOLD);
                cancel.setTypeface(tf, Typeface.BOLD);
                Custom_list_adapter customAdapter1 = new Custom_list_adapter(getApplicationContext(), types);
                l.setAdapter(customAdapter1);

                dialog.setTitle("Select A Part Of Day");
                dialog.show();
                l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedFromList = types[position];
                        Partday.setText(selectedFromList.toUpperCase());
                        dropdownevent = Partday.getText().toString();
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

                        dropdownevent = Partday.getText().toString();
                        dialog.dismiss();
                    }
                });
            }
        });

        type.setTypeface(tf, Typeface.BOLD);

        if (usercont != 0) {
            type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_dialog);
                    Button cancel = (Button) dialog.findViewById(R.id.cancel);
                    Button ok = (Button) dialog.findViewById(R.id.ok);
                    ok.setTypeface(tf, Typeface.BOLD);
                    cancel.setTypeface(tf, Typeface.BOLD);
                    l = (ListView) dialog.findViewById(R.id.dia);
                    final ArrayList<String> list = new ArrayList<String>();
                    for (int i = 0; i < types.length; ++i) {
                        list.add(types[i].toUpperCase());
                    }

                    listArr = new String[data.size()];
                    listArr = data.toArray(listArr);
                    Custom_list_adapter adapter = new Custom_list_adapter(getApplicationContext(), listArr);
                    l.setAdapter(adapter);

                    dialog.setTitle("Select Person's Name");
                    dialog.show();
                    l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selectedFromList = listArr[position];
                            shareuser_id = userid.get(position);
                            type.setText(selectedFromList.toUpperCase());
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
                            pername = type.getText().toString();

                            dialog.dismiss();
                        }
                    });
                }
            });
        } else {
            type.setText(c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME)).toUpperCase());
            shareuser_id = useridtest;
            pername = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME)).toUpperCase();
        }
       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        l.setAdapter(adapter);*/

        dateset.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           Log.e("Hi", "Error");
                                           Calendar now = Calendar.getInstance();
                                           DatePickerDialog dpd = DatePickerDialog.newInstance(
                                                   AddMedicines.this,
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
                                           android.app.FragmentManager fm = AddMedicines.this.getFragmentManager();
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

        test = (TextView) findViewById(R.id.addre);

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
                ok.setTypeface(tf, Typeface.BOLD);
                cancel.setTypeface(tf, Typeface.BOLD);
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
            type.setText(ab.toUpperCase());
            pername = ab;

            medicinename.setText(c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKDETAIL)));

            String note = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKEXTRANOTE)));
            if (!note.equals("")) {
                Partday.setText(note.toUpperCase());
            }

            dropdownevent = note.toUpperCase();

            String remdate = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
            String rep = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKSETREPEATING)));
            test.setText(rep);

            Date rem_datetime = null;
            DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a", Locale.ENGLISH);
            try {
                rem_datetime = formatter.parse(remdate);

                Log.e("date", rem_datetime.toString());

                int s1 = rem_datetime.getDate();
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
            /////////Medicine table

            String quer1 = "select * from medicine where task_id= " + task_id + " ";
            Cursor c_medi = db.rawQuery(quer1, null);
            c_medi.moveToFirst();

            String docname = (c_medi.getString(c_medi.getColumnIndex(DatabaseHandler.TAG_DOCTORNAME)));

            dname.setText(docname);

            String cou = (c_medi.getString(c_medi.getColumnIndex(DatabaseHandler.TAG_MEDICINECOUNT)));

            count.setText(cou);

            String inst = (c_medi.getString(c_medi.getColumnIndex(DatabaseHandler.TAG_SPECIALINSTRUCTION)));

            instruction.setText(inst);

            save.setText("Update");
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             /*   AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Do You Want To Add Medicines");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();*/

                if (type.getText().toString().toLowerCase().equals("select person name")) {

                    Toast.makeText(getApplicationContext(), "Please fill proper details", Toast.LENGTH_LONG).show();
                } else if (dateset.getText().toString().toLowerCase().equals("set a date")) {
                    Toast.makeText(getApplicationContext(), "Please fill proper Date", Toast.LENGTH_LONG).show();
                } else if (timeset.getText().toString().toLowerCase().equals("set a time")) {
                    Toast.makeText(getApplicationContext(), "Please fill proper Time", Toast.LENGTH_LONG).show();
                } else if (test.getText().toString().equals("SELECT REMINDER")) {
                    Toast.makeText(getApplicationContext(), "Please select Reminder", Toast.LENGTH_LONG).show();

                } else {

                    String repeatstatus = test.getText().toString();

                    String rem_date = selectdate + " " + selecttime;
                    Log.e("rem_date", rem_date);
                    Log.e("now", new SimpleDateFormat("dd.MM.yyyy HH:mm a", Locale.ENGLISH).format(new Date()));
                    String docname, medicinecount, medicine;
                    if (instruction.getText().toString().equals("")) {

                        instr = "";

                    } else {
                        instr = instruction.getText().toString();
                    }

                    if (dname.getText().toString().equals("")) {
                        docname = "";
                    } else {
                        docname = dname.getText().toString();
                    }
                    if (Partday.getText().toString().toLowerCase().equals("select part of day")) {
                        selectedevent = "";
                    } else {
                        selectedevent = Partday.getText().toString();
                    }

                    if (count.getText().toString().equals("")) {
                        medicinecount = "";
                    } else {
                        medicinecount = count.getText().toString();
                    }

                    if (medicinename.getText().toString().equals("")) {
                        medicine = "";
                    } else {
                        medicine = medicinename.getText().toString();
                    }

                    long taskid;
                    String rem_date1 = storedate + " " + storetime;
                    if (task_id != "") {

                        taskid = Long.parseLong(task_id);
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseHandler.TAG_COMMONTASKTYPE, "Medicine");
                        cv.put(DatabaseHandler.TAG_COMMONTASKNAME, pername);
                        cv.put(DatabaseHandler.TAG_COMMONTASKEXTRANOTE, selectedevent);
                        cv.put(DatabaseHandler.TAG_COMMONTASKDETAIL, medicine);
                        cv.put(DatabaseHandler.TAG_COMMONTASKSETREPEATING, repeatstatus);
                        cv.put(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME, selectdate + " " + selecttime);
                        cv.put(DatabaseHandler.TAG_COMMONTASKEDITEDDATETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                        cv.put(DatabaseHandler.TAG_COMMONTASKOWNERID, c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));
                        cv.put(DatabaseHandler.TAG_COMMONTASKGROUPID, c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID)));
                        cv.put(DatabaseHandler.TAG_COMMONTASKREMINDER, rem_date1);
                        db.update(DatabaseHandler.TABLE_COMMONTASK, cv, "_id ='" + task_id + "' ", null);

                        ContentValues cv1 = new ContentValues();

                        cv1.put(DatabaseHandler.TAG_PERSONNAME, pername);
                        cv1.put(DatabaseHandler.TAG_PARTOFDAY, selectedevent);
                        cv1.put(DatabaseHandler.TAG_SPECIALINSTRUCTION, instr);
                        cv1.put(DatabaseHandler.TAG_FREQUENCY, repeatstatus);
                        cv1.put(DatabaseHandler.TAG_MEDICINECOUNT, medicinecount);
                        cv1.put(DatabaseHandler.TAG_DOCTORNAME, docname);

                        db.update(DatabaseHandler.TABLE_MEDICINE, cv1, "task_id='" + task_id + "' ", null);

                    } else {

                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseHandler.TAG_COMMONTASKTYPE, "Medicine");
                        cv.put(DatabaseHandler.TAG_COMMONTASKNAME, pername);
                        cv.put(DatabaseHandler.TAG_COMMONTASKEXTRANOTE, selectedevent);
                        cv.put(DatabaseHandler.TAG_COMMONTASKDETAIL, medicine);
                        cv.put(DatabaseHandler.TAG_COMMONTASKSETREPEATING, repeatstatus);
                        cv.put(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME, selectdate + " " + selecttime);
                        cv.put(DatabaseHandler.TAG_COMMONTASKCREATEDDATETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                        cv.put(DatabaseHandler.TAG_COMMONTASKOWNERID, c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));
                        cv.put(DatabaseHandler.TAG_COMMONTASKGROUPID, c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID)));
                        cv.put(DatabaseHandler.TAG_COMMONTASKREMINDER, rem_date1);
                        taskid = db.insert(DatabaseHandler.TABLE_COMMONTASK, null, cv);
                        if (!useridtest.equals(shareuser_id)) {
                            ContentValues cv2 = new ContentValues();
                            //Log.e("share_count",((Integer)member_list.size()).toString());
                            cv2.put(DatabaseHandler.TAG_SHARELOCALID, taskid);
                            cv2.put(DatabaseHandler.TAG_SHAREUSERID, shareuser_id);

                            db_write.insert(DatabaseHandler.TABLE_SHARINGDETAILS, null, cv2);
                        }

                        String qry_user = "select * from commontask";
                        c1 = db_read.rawQuery(qry_user, null);
                        c1.moveToLast();
                        taskid1 = c1.getString(c1.getColumnIndex(DatabaseHandler.TAG_TASKID));

                        ContentValues cv1 = new ContentValues();
                        cv1.put(DatabaseHandler.TAG_TASK_ID, taskid1);
                        cv1.put(DatabaseHandler.TAG_PERSONNAME, pername);
                        cv1.put(DatabaseHandler.TAG_PARTOFDAY, selectedevent);
                        cv1.put(DatabaseHandler.TAG_SPECIALINSTRUCTION, instr);
                        cv1.put(DatabaseHandler.TAG_FREQUENCY, repeatstatus);
                        cv1.put(DatabaseHandler.TAG_MEDICINECOUNT, medicinecount);
                        cv1.put(DatabaseHandler.TAG_DOCTORNAME, docname);
                        db_write.insert(DatabaseHandler.TABLE_MEDICINE, null, cv1);
                        Log.v("Taskid", taskid1);
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
                    else{*/
                        Log.e("webservice", "fetch entries to be uploaded and call webservice");
                        syncnow = new SyncData(getApplicationContext());
                        syncnow.execute();
                        //   }
                    }
                    if (status.equals("offline")) {
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
                //   storetime = selecttime.replace("AM","");
            } else if (hourOfDay > 12) {
                hourOfDay = hourOfDay - 12;
                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                selecttime = hourString + ":" + minuteString + " PM";
                timeset.setText(selecttime);
                //    storetime = selecttime.replace("PM","");
            } else if (hourOfDay == 12) {
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                selecttime = "12:" + minuteString + " PM";
                timeset.setText(selecttime);
                //   storetime = selecttime.replace("PM","");
            } else {
                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                selecttime = hourString + ":" + minuteString + " AM";
                timeset.setText(selecttime);
                // storetime = selecttime.replace("AM","");
            }
        }
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
        //  Toast.makeText(getApplicationContext(), "Medicine Reminder Set Successfully", Toast.LENGTH_LONG).show();
        AlertDialog.Builder alert = new AlertDialog.Builder(AddMedicines.this, R.style.AlertDialogCustom);

        // alert.setTitle("Doctor");
        alert.setMessage("Medicine Reminder Set Successfully!");
        //alert.setPositiveButton("OK",null);

        alert.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //    member_list.clear();
                        userid.clear();
                        Intent i = new Intent(AddMedicines.this, MainActivity.class);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });

        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            userid.clear();
            onBackPressed();
            return true;
        }
        if (id == R.id.action_home) {
            userid.clear();
            db.close();
            db_write.close();
            Intent i = new Intent(AddMedicines.this, MainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_home);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        userid.clear();
        db.close();
        db_write.close();
        Intent i = new Intent(AddMedicines.this, Medicine.class);
        i.addCategory(Intent.CATEGORY_HOME);
        i.putExtra("Activity", ActicityName);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}