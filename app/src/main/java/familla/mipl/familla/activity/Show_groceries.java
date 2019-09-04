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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import familla.mipl.familla.R;

public class Show_groceries extends AppCompatActivity {
    public Dialog dialog;
    DatabaseHandler handler;
    SQLiteDatabase db;
    ListView homelist;

    MyCursorAdapter adapter;
    Button Bill;
    ImageView img;
    String id,Ownername;
    int task_id;
    private Toolbar mToolbar;
    Typeface tf;
    ConnectionDetector cd;

    public static String status="";
    static NotificationManagerCompat notificationManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_groceries);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle(" G R O C E R I E S ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorgroceri)));
        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();
        Bill=(Button)findViewById(R.id.bill);

        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
            status = "online";
        } else {
            status = "offline";
        }


        if(status.equals("online")){
            ContentValues cv  =new ContentValues();
            cv.put(DatabaseHandler.TAG_SYNCSTATUSONILNETIME,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH).format(new Date()));
            db.update(DatabaseHandler.TABLE_SYNCSTATUS,cv, "_id = 1", null );
        }
        if(status.equals("offline")){
            ContentValues cv  =new ContentValues();
            cv.put(DatabaseHandler.TAG_SYNCSTATUSOFFLINETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH).format(new Date()));
            db.update(DatabaseHandler.TABLE_SYNCSTATUS,cv, "_id = 1", null );
        }
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancelAll();

        String s="Groceries";
        String str = "select * from commontask where commontask_type='"+s+"' and commontask_status ='0' order by commontask_createddatetime desc  ";

        Cursor c = db.rawQuery(str, null);
        c.moveToFirst();

        if (c.getCount() == 0) {

            Intent intent = getIntent();
            Bundle extras = getIntent().getExtras();
            if(extras != null)
            {
                String task=intent.getExtras().getString("Activity").toString();

                if(task.equals("Add groceries"))
                {
                    Intent i = new Intent(Show_groceries.this, MainActivity.class);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }
                else
                {
                    Intent i = new Intent(Show_groceries.this, Groceries.class);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }
            }

        }
        homelist = (ListView) findViewById(R.id.homelist);


        adapter = new MyCursorAdapter(getApplicationContext(), c);
        homelist.setAdapter(adapter);
        Bill.setTypeface(tf, Typeface.BOLD);
        Bill.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(Show_groceries.this, Groceries.class);
                                        startActivity(i);
                                    }
                                }

        );


        homelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedFromList = (homelist.getItemAtPosition(position).toString());
                task_id= (int) parent.getItemIdAtPosition(position);

                String que="select * from userdetails";
                Cursor c_user1 = db.rawQuery(que, null);
                c_user1.moveToFirst();
                String userid = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));

                String quer="select * from commontask where _id= "+task_id+" ";
                Cursor c_user = db.rawQuery(quer, null);
                c_user.moveToFirst();

                String name = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)));
                String type=(c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKTYPE)));
                String details=(c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
                String Owner=(c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKOWNERID)));


                Intent i = new Intent(Show_groceries.this, Groceri_List_dialog.class);
                String abc = String.valueOf(task_id);
                i.putExtra("Taskid", abc);
                startActivity(i);

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
            ImageView img1=(ImageView)view.findViewById(R.id.todoimg);



            img=(ImageView)view.findViewById(R.id.checkbox);


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
            TextView textname = (TextView) view.findViewById(R.id.tasktype);
            taskname.setTypeface(tf, Typeface.BOLD);

            String type = (cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKTYPE)));
            taskname.setText(type.toUpperCase());

            textname.setTypeface(tf, Typeface.BOLD);
            TextView teskdat=(TextView) view.findViewById(R.id.day);
            TextView teskmon=(TextView) view.findViewById(R.id.mon);
            String dat=cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME));
            Date rem_datetime = null;
            DateFormat formatter = new SimpleDateFormat("d.M.yyyy hh:mm a", Locale.ENGLISH);
            try {

                rem_datetime = formatter.parse(dat);

                Log.e("date", rem_datetime.toString());

                int s=rem_datetime.getDate();
                String intMonth = (String) android.text.format.DateFormat.format("MMM", rem_datetime);
                String day = (String) android.text.format.DateFormat.format("dd", rem_datetime);
                teskdat.setText(day);
                teskmon.setText(intMonth);
                Log.e("da",intMonth);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("err", e.toString());
            }
            textname.setText("Groceries List");

            //taskname.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)));
            //      tasknote.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
            img1.setImageResource(R.drawable.electricity_bill_icon);
            id=(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_TASKID)));
            taskid.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_TASKID)));
            taskid.setVisibility(View.GONE);
        }


    }
    @Override
    public void onDestroy() {
        Log.e("on","destroy");
        new CallSyncData(getApplicationContext()).stopDownloading();
        super.onDestroy();

    }

    @Override
    public void onStop() {
        Log.e("on","stop");
        new CallSyncData(getApplicationContext()).stopDownloading();
        super.onStop();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(Show_groceries.this, MainActivity.class);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
