package familla.mipl.familla.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import familla.mipl.familla.R;

public class Bill extends AppCompatActivity {
    public Dialog dialog;
    DatabaseHandler handler;
    SQLiteDatabase db;
    ListView homelist;
    public static String status;
    MyCursorAdapter adapter;
    Button Bill;
    ImageView img;
    String id;
    ConnectionDetector cd;
    int task_id;
    private Toolbar mToolbar;
    String Ownername;
    static NotificationManagerCompat notificationManager = null;
    Typeface tf;
    private AdView mAdView, mAdView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" B I L L S ");
        mToolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorbilldata)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorbilldata));
            //  window.setNavigationBarColor(this.getResources().getColor(R.color.colorbilldata));
        }
        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();
        String s = "Bill";
        String str = "select * from commontask where commontask_type='" + s + "' and commontask_status ='0' order by commontask_createddatetime desc  ";

        Cursor c1 = db.rawQuery(str, null);
        c1.moveToFirst();
        tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");

        if (c1.getCount() == 0) {

            Intent intent = getIntent();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String task = intent.getExtras().getString("Activity").toString();

                if (task.equals("Add Bill")) {
                    Intent i = new Intent(Bill.this, MainActivity.class);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    Intent i = new Intent(Bill.this, AddBill.class);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        }

        Bill = (Button) findViewById(R.id.bill);
        Bill.setTypeface(tf, Typeface.BOLD);

        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancelAll();

        String add = "Bill";
        String qry = "select * from commontask where commontask_type='" + add + "' and commontask_status ='0' order by commontask_createddatetime desc   ";

        Cursor c = db.rawQuery(qry, null);
        c.moveToFirst();
        homelist = (ListView) findViewById(R.id.homelist);

        adapter = new MyCursorAdapter(getApplicationContext(), c);
        homelist.setAdapter(adapter);
        Bill.setTypeface(tf, Typeface.BOLD);
        Bill.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(Bill.this, AddBill.class);
                                        startActivity(i);
                                    }
                                }
        );

        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
            status = "online";
        } else {
            status = "offline";
        }

        homelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedFromList = (homelist.getItemAtPosition(position).toString());
                task_id = (int) parent.getItemIdAtPosition(position);
                String que = "select * from userdetails";
                Cursor c_user1 = db.rawQuery(que, null);
                c_user1.moveToFirst();
                String userid = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));

                String quer = "select * from commontask where _id= " + task_id + " ";
                Cursor c_user = db.rawQuery(quer, null);
                c_user.moveToFirst();

                String name = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)));
                String type = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKTYPE)));
                String details = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
                String Owner = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKOWNERID)));

                String quer1 = "select * from userdetails where ud_userid= " + Owner + " ";
                Cursor c1 = db.rawQuery(quer1, null);
                c1.moveToFirst();
                if (c1.getCount() != 0) {
                    Ownername = (c1.getString(c1.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME)));
                } else {
                    String quer2 = "select * from memberdetails where md_userid= " + Owner + " ";
                    Cursor c2 = db.rawQuery(quer2, null);
                    c2.moveToFirst();
                    Ownername = (c2.getString(c2.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));
                }
                final Dialog dialog = new Dialog(Bill.this);
                // Include dialog.xml file

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.taskdetails);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                Window window = dialog.getWindow();
                WindowManager.LayoutParams param = window.getAttributes();
                // set the layout at right bottom

                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.40);

                dialog.getWindow().setLayout(width, height);

                // set values for custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.taskname);
                text.setText("Bill Name : " + name);
                text.setTypeface(tf, Typeface.BOLD);
                TextView text2 = (TextView) dialog.findViewById(R.id.detials);
                text2.setText("Date & Time : " + details);
                text2.setTypeface(tf, Typeface.BOLD);
                TextView text3 = (TextView) dialog.findViewById(R.id.Owner);
                text3.setText("Added By : " + Ownername);
                text3.setTypeface(tf, Typeface.BOLD);
                Button demodialog = (Button) dialog.findViewById(R.id.Complete);
                Button Edit = (Button) dialog.findViewById(R.id.Edit);

                if (userid.equals(Owner)) {
                    demodialog.setVisibility(View.VISIBLE);
                    Edit.setVisibility(View.VISIBLE);
                } else {
                    demodialog.setVisibility(View.INVISIBLE);
                    Edit.setVisibility(View.INVISIBLE);
                }
                ImageView close = (ImageView) dialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                demodialog.setTypeface(tf, Typeface.BOLD);
                demodialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseHandler.TAG_COMMONTASKSTATUS, 1);
                        db.update(DatabaseHandler.TABLE_COMMONTASK, cv, "_id ='" + task_id + "' ", null);
                        Toast.makeText(getApplicationContext(), "Task is mark completed", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        Intent i = new Intent(getApplicationContext(), Bill.class);
                        startActivity(i);
                    }
                });

                Edit.setTypeface(tf, Typeface.BOLD);
                Edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), AddBill.class);
                        String abc = String.valueOf(task_id);
                        i.putExtra("Taskid", abc);
                        startActivity(i);
                    }
                });

                dialog.show();

                Log.e("sid", selectedFromList);
            }
        });
    }

    public class MyCursorAdapter extends CursorAdapter {
        public MyCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.homeitem, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // TextView tasktype = (TextView) view.findViewById(R.id.tasktype);
            TextView taskname = (TextView) view.findViewById(R.id.taskname);
            //  TextView tasknote = (TextView) view.findViewById(R.id.tasknote);

            TextView taskid = (TextView) view.findViewById(R.id.taskid);
            ImageView img1 = (ImageView) view.findViewById(R.id.todoimg);
            TextView textname = (TextView) view.findViewById(R.id.tasktype);
            img = (ImageView) view.findViewById(R.id.checkbox);

         /*   String type = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_TASKTYPE));
            if (type.equals("Event")) {
                tasktype.setText("E");
                tasktype.setBackgroundResource(R.drawable.event);
            }
            if (type.equals("Groceries")) {
                tasktype.setText("G");
                tasktype.setBackgroundResource(R.drawable.groceries);
            }
            if (type.equals("Bills")) {
                tasktype.setText("B");
                tasktype.setBackgroundResource(R.drawable.bills);
            }
            if (type.equals("Medicines")) {
                tasktype.setText("M");
                tasktype.setBackgroundResource(R.drawable.medicines);
            }
            if (type.equals("Kids")) {
                tasktype.setText("K");
                tasktype.setBackgroundResource(R.drawable.kids);
            }
            if (type.equals("Appointment")) {
                tasktype.setText("A");
                tasktype.setBackgroundResource(R.drawable.appointment);
            }
            if (type.equals("Other")) {
                tasktype.setText("O");
                tasktype.setBackgroundResource(R.drawable.other);
            }*/
            taskname.setTypeface(tf, Typeface.BOLD);
            String type = (cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKTYPE)));
            taskname.setText(type.toUpperCase());

            textname.setTypeface(tf, Typeface.BOLD);
            textname.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)).toUpperCase());
            TextView teskdat = (TextView) view.findViewById(R.id.day);
            TextView teskmon = (TextView) view.findViewById(R.id.mon);
            String dat = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME));
            Date rem_datetime = null;
            DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a", Locale.ENGLISH);
            try {

                rem_datetime = formatter.parse(dat);

                Log.e("date", rem_datetime.toString());

                int s = rem_datetime.getDate();
                String intMonth = (String) android.text.format.DateFormat.format("MMM", rem_datetime);
                String day = (String) android.text.format.DateFormat.format("dd", rem_datetime);
                teskdat.setText(day);
                teskmon.setText(intMonth);
                Log.e("da", intMonth);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("err", e.toString());
            }

            textname.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)));
            //      tasknote.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
            img1.setImageResource(R.drawable.electricity_bill_icon);
            id = (cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_TASKID)));
            taskid.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_TASKID)));
            taskid.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        if (mAdView1 != null) {
            mAdView1.resume();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (mAdView1 != null) {
            mAdView1.resume();
        }
    }

    @Override
    public void onDestroy() {
        Log.e("on", "destroy");
        new CallSyncData(getApplicationContext()).stopDownloading();
        if (mAdView != null) {
            mAdView.pause();
        }
        if (mAdView1 != null) {
            mAdView1.resume();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        Log.e("on", "stop");
        new CallSyncData(getApplicationContext()).stopDownloading();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Bill.this, MainActivity.class);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_home) {
            Intent i = new Intent(Bill.this, MainActivity.class);
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
}