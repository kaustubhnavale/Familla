package familla.mipl.familla.activity;

import android.app.Dialog;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import familla.mipl.familla.R;

public class History extends AppCompatActivity {
    public Dialog dialog;
    DatabaseHandler handler;
    SQLiteDatabase db;
    ListView homelist;
    ImageView img1;
    String Ownername;
    String id1, intmon, dat;
    static NotificationManagerCompat notificationManager = null;
    MyCursorAdapter adapter;
    TextView taskid;
    int task_id;
    LinearLayout test;
    private Toolbar mToolbar;
    Typeface tf;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" H I S T O R Y ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancelAll();
        String a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());

        tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");
        String str = "select * from commontask order by commontask_createddatetime asc ";
        //  }

        c = db.rawQuery(str, null);
        c.moveToFirst();
        homelist = (ListView) findViewById(R.id.homelist);

        test = (LinearLayout) findViewById(R.id.test);

        if (c.getCount() == 0) {

            homelist.setVisibility(View.GONE);
        } else {
            homelist.setVisibility(View.VISIBLE);
            adapter = new MyCursorAdapter(getApplicationContext(), c);
            homelist.setAdapter(adapter);
        }

        homelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedFromList = (homelist.getItemAtPosition(position).toString());
                task_id = (int) parent.getItemIdAtPosition(position);

                String quer = "select * from commontask where _id= " + task_id + "  ";
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
                if (type.equals("Groceries")) {
                    Intent i = new Intent(History.this, Groceri_List_dialog.class);
                    String abc = String.valueOf(task_id);
                    i.putExtra("Taskid", abc);
                    i.putExtra("Act", "History");
                    startActivity(i);
                    //Toast.makeText(getActivity(),"Test",Toast.LENGTH_SHORT).show();
                } else {
                    final Dialog dialog = new Dialog(History.this);
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
                    TextView text4 = (TextView) dialog.findViewById(R.id.share);
                    text4.setVisibility(View.GONE);
                    // set values for custom dialog components - text, image and button
                    TextView text = (TextView) dialog.findViewById(R.id.taskname);
                    text.setTypeface(tf, Typeface.BOLD);
                    if (type.equals("Bill")) {
                        text.setText("Bill Name : " + name);
                    } else if (type.equals("Appointment")) {
                        text.setText("Appointment With : " + name);
                    } else if (type.equals("Event")) {
                        text.setText("Event Name : " + name);
                    } else if (type.equals("Medicine")) {
                        text.setText("Medicine for : " + name);
                    } else if (type.equals("Alarm")) {
                        text.setText("Alarm set for : " + name);
                    } else if (type.equals("Groceries")) {
                        text.setText("Grocerie list : " + name);
                    }

                    TextView text2 = (TextView) dialog.findViewById(R.id.detials);
                    text2.setText("Date & Time : " + details);
                    text2.setTypeface(tf, Typeface.BOLD);
                    TextView text3 = (TextView) dialog.findViewById(R.id.Owner);
                    text3.setText("Added By : " + Ownername);
                    text3.setTypeface(tf, Typeface.BOLD);
                    ImageView close = (ImageView) dialog.findViewById(R.id.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                        }
                    });

                    Button demodialog = (Button) dialog.findViewById(R.id.Complete);
                    demodialog.setVisibility(View.GONE);
                    demodialog.setTypeface(tf, Typeface.BOLD);
                    Button demodialog1 = (Button) dialog.findViewById(R.id.Edit);
                    demodialog1.setTypeface(tf, Typeface.BOLD);
                    demodialog1.setVisibility(View.GONE);

                    dialog.show();

                    Log.e("sid", selectedFromList);
                    Log.e("Id", id1);
                }
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
            taskid = (TextView) view.findViewById(R.id.taskid);
            TextView teskdat = (TextView) view.findViewById(R.id.day);
            TextView teskmon = (TextView) view.findViewById(R.id.mon);
            taskname.setTypeface(tf, Typeface.BOLD);
            ImageView img = (ImageView) view.findViewById(R.id.todoimg);

            String type = (cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKTYPE)));

            if (type.equals("Event")) {
                img.setImageResource(R.drawable.event_icon);
            }
            if (type.equals("Groceries")) {
                img.setImageResource(R.drawable.electricity_bill_icon);
            }
            if (type.equals("Bill")) {
                img.setImageResource(R.drawable.electricity_bill_icon);
            }
            if (type.equals("Medicine")) {
                img.setImageResource(R.drawable.medicine_icon);
            }
            if (type.equals("Appointment")) {
                img.setImageResource(R.drawable.appointment_icon);
            }
            if (type.equals("Alarm")) {
                img.setImageResource(R.drawable.wake_up);
            }
            img1 = (ImageView) view.findViewById(R.id.checkbox);

            TextView textname = (TextView) view.findViewById(R.id.tasktype);
            taskname.setTypeface(tf, Typeface.BOLD);
            taskname.setText(type.toUpperCase());
            if (type.equals("Groceries")) {
                textname.setText("Groceries List");
            } else {
                textname.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)).toUpperCase());
            }

            String dat = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME));
            //tasknote.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));

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
            id1 = (cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_TASKID)));

            taskid.setText(id1);
            taskid.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(History.this, MainActivity.class);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
    }
}