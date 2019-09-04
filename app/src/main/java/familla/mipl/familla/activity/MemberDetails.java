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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import familla.mipl.familla.R;

public class MemberDetails extends AppCompatActivity {
    public Dialog dialog;
    DatabaseHandler handler;
    SQLiteDatabase db;
    ListView homelist;
    ImageView img1;
    String Ownername;
    String id1, intmon, dat;
    private Toolbar mToolbar;
    String mem_id;
    StringBuffer sd = new StringBuffer();
    static NotificationManagerCompat notificationManager = null;
    MyCursorAdapter adapter;
    TextView taskid;
    String task_id;
    String type;
    LinearLayout test;
    Cursor c;
    Typeface tf;
    int flag;
    Dialog dialog1;
    int sharcount;
    TextView text4;
    String shareid, shareuser;
    ArrayList<String> mem_list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //Appsee.start("0b13ec2e66ea4ea8a86687e30b39f3f0");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" T O D O ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // mToolbar.setBackgroundDrawable(new (getResources().getDrawable(R.drawable.colorgradiant));
        mToolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.todogradiant));

        handler = new DatabaseHandler(getApplicationContext());
        // handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancelAll();
        String a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());
        tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        mem_id = "";
        if (extras != null) {

            mem_id = intent.getExtras().getString("member_id").toString();
        }

        //  String str = "select _id,commontask_type, from commontask where date('now') <= datetime(commontask_reminderdatetime) order by commontask_reminderdatetime  asc ";
        //   String str =   "select _id,commontask_type,commontask_name,commontask_reminderdatetime from commontask where commontask_status='0' and commontask_ownerid='"+mem_id+"' order by commontask_reminder desc";
        String str = "select a._id as Id,* from commontask a left outer join trn_sharingdetails b on a._id=b._id where a.commontask_status='0' and a.commontask_ownerid='" + mem_id + "' union select a._id as ID,* from commontask a inner join trn_sharingdetails b on a._id = b.share_localid where a.commontask_status='0' and b.share_userid= '" + mem_id + "' order by commontask_reminder desc";

        c = db.rawQuery(str, null);
        c.moveToFirst();

        homelist = (ListView) findViewById(R.id.homelist);

        if (c.getCount() == 0) {

            Log.e("count", " medha");
            homelist.setVisibility(View.GONE);
        } else {
            homelist.setVisibility(View.VISIBLE);
            adapter = new MyCursorAdapter(getApplicationContext(), c);
            homelist.setAdapter(adapter);
        }

        homelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mem_list.isEmpty()) {
                    mem_list.clear();
                }
                String selectedFromList = (homelist.getItemAtPosition(position).toString());

                task_id = c.getString(c.getColumnIndex("Id"));

                String que = "select * from userdetails";
                Cursor c_user1 = db.rawQuery(que, null);
                c_user1.moveToFirst();
                String userid = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));

                String quer = "select * from commontask where _id= " + task_id + " ";
                Cursor c_user = db.rawQuery(quer, null);
                c_user.moveToFirst();

                String name = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)));
                type = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKTYPE)));
                String details = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));

                String Owner = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKOWNERID)));

                String Syntaskid = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKSYNCID)));

                String task = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKID)));

                String syn = "select * from trn_sharingdetails where share_localid= " + Syntaskid + " ";
                Cursor c_us = db.rawQuery(syn, null);
                c_us.moveToFirst();
                sharcount = c_us.getCount();
                for (int i = 0; i < c_us.getCount(); i++) {
                    shareid = (c_us.getString(c_us.getColumnIndex(DatabaseHandler.TAG_SHAREUSERID)));
                    c_us.moveToNext();
                    String quer1 = "select * from userdetails where ud_userid= " + shareid + " ";
                    Cursor c1 = db.rawQuery(quer1, null);
                    c1.moveToFirst();
                    if (c1.getCount() != 0) {
                        shareuser = (c1.getString(c1.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME)));
                        sd.append(shareuser);
                        sd.append(",");
                        mem_list.add(shareuser);

                    } else {
                        String quer2 = "select * from memberdetails where md_userid= " + shareid + " ";
                        Cursor c2 = db.rawQuery(quer2, null);
                        c2.moveToFirst();
                        shareuser = (c2.getString(c2.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));
                        sd.append(shareuser);
                        sd.append(",");
                        mem_list.add(shareuser);
                    }
                }

                String quer1 = "select * from userdetails where ud_userid= " + Owner + " ";
                Cursor c1 = db.rawQuery(quer1, null);
                c1.moveToFirst();
                if (c1.getCount() != 0) {
                    Ownername = (c1.getString(c1.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME)));
                    flag = 1;

                    String syn1 = "select * from trn_sharingdetails where share_localid= " + task + " ";
                    Cursor c_us1 = db.rawQuery(syn1, null);
                    c_us1.moveToFirst();
                    sharcount = c_us1.getCount();
                    for (int i = 0; i < c_us1.getCount(); i++) {
                        shareid = (c_us1.getString(c_us1.getColumnIndex(DatabaseHandler.TAG_SHAREUSERID)));
                        c_us1.moveToNext();
                        String quer3 = "select * from userdetails where ud_userid= " + shareid + " ";
                        Cursor c3 = db.rawQuery(quer3, null);
                        c3.moveToFirst();
                        if (c3.getCount() != 0) {
                            shareuser = (c3.getString(c3.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME)));
                            sd.append(shareuser);
                            sd.append(",");
                            mem_list.add(shareuser);

                        } else {
                            String quer2 = "select * from memberdetails where md_userid= " + shareid + " ";
                            Cursor c2 = db.rawQuery(quer2, null);
                            c2.moveToFirst();
                            shareuser = (c2.getString(c2.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));
                            sd.append(shareuser);
                            sd.append(",");
                            mem_list.add(shareuser);
                        }
                    }

                } else {
                    String quer2 = "select * from memberdetails where md_userid= " + Owner + " ";
                    Cursor c2 = db.rawQuery(quer2, null);
                    c2.moveToFirst();
                    Ownername = (c2.getString(c2.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));
                    flag = 2;
                }
                if (type.equals("Groceries")) {
                    Intent i = new Intent(MemberDetails.this, Groceri_List_dialog.class);
                    String abc = String.valueOf(task_id);
                    i.putExtra("Taskid", abc);
                    startActivity(i);
                    //Toast.makeText(getActivity(),"Test",Toast.LENGTH_SHORT).show();
                } else {
                    final Dialog dialog = new Dialog(MemberDetails.this);
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

                    TextView text = (TextView) dialog.findViewById(R.id.taskname);
                    Button Edit = (Button) dialog.findViewById(R.id.Edit);
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
                    }

                    // set values for custom dialog components - text, image and button

                    TextView text2 = (TextView) dialog.findViewById(R.id.detials);
                    text2.setText("Date & Time : " + details);
                    text2.setTypeface(tf, Typeface.BOLD);

                    TextView text3 = (TextView) dialog.findViewById(R.id.Owner);
                    text3.setText("Added By : " + Ownername);

                    String quer2 = "select * from memberdetails where  md_status = 1 ";
                    Cursor c2 = db.rawQuery(quer2, null);
                    int co = c2.getCount();

                    text4 = (TextView) dialog.findViewById(R.id.share);

                    if (flag == 2) {
                        if (sharcount == co) {
                            text4.setText("Task share with : " + "ALL");
                        } else {
                            String s = sd.toString();
                            s = s.substring(0, s.length() - 1);
                            text4.setText("Task share with : " + s);
                            sd.delete(0, sd.length());
                            if (s.length() > 50) {
                                String sname = s.substring(0, 50);
                                text4.setText("Task share with : " + sname + ". . . .");
                                sd.delete(0, sd.length());
                            }
                        }
                    } else {
                        if (sharcount == co) {
                            text4.setText("Task share with : " + "ALL");
                        } else {
                            String s = sd.toString();
                            s = s.substring(0, s.length() - 1);
                            text4.setText("Task share with : " + s);
                            sd.delete(0, sd.length());
                            if (s.length() > 50) {
                                String sname = s.substring(0, 50);
                                text4.setText("Task share with : " + sname + ". . . .");
                                sd.delete(0, sd.length());
                            }
                        }
                        //   text4.setVisibility(View.GONE);
                    }

                    text4.setTypeface(tf, Typeface.BOLD);
                    text4.setTypeface(tf, Typeface.BOLD);

                    text4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog1 = new Dialog(MemberDetails.this);
                            dialog1.setContentView(R.layout.member);
                            dialog1.setTitle("Person");

                            dialog1.getWindow().setTitleColor(getResources().getColor(R.color.textColorPrimary));

                            ListView memlist1 = (ListView) dialog1.findViewById(R.id.memlist);
                            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mem_list);
                            memlist1.setAdapter(arrayAdapter);

                            //memlist1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                            //memlist1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                            dialog1.setCanceledOnTouchOutside(true);

                            TextView cancel = (TextView) dialog1.findViewById(R.id.cancel);
                            TextView ok = (TextView) dialog1.findViewById(R.id.ok);

                            TextView all = (TextView) dialog1.findViewById(R.id.al);
                            all.setVisibility(View.GONE);

                            CheckBox che = (CheckBox) dialog1.findViewById(R.id.chkAll);
                            che.setVisibility(View.GONE);

                            ok.setTypeface(tf, Typeface.BOLD);
                            cancel.setTypeface(tf, Typeface.BOLD);
                            cancel.setVisibility(View.INVISIBLE);

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog1.dismiss();
                                    //   mem_list.clear();
                                }
                            });

                            dialog1.show();

                            // mem_list.clear();
                        }

                    });

                    ImageView close = (ImageView) dialog.findViewById(R.id.close);
                    text3.setTypeface(tf, Typeface.BOLD);
                    Button demodialog = (Button) dialog.findViewById(R.id.Complete);

                    demodialog.setTypeface(tf, Typeface.BOLD);
                    Edit.setTypeface(tf, Typeface.BOLD);
                    if (userid.equals(Owner)) {
                        demodialog.setVisibility(View.VISIBLE);
                        Edit.setVisibility(View.VISIBLE);
                    } else {
                        demodialog.setVisibility(View.GONE);
                        Edit.setVisibility(View.GONE);
                    }

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                            sd.delete(0, sd.length());
                            mem_list.clear();
                        }
                    });

                    demodialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ContentValues cv = new ContentValues();
                            cv.put(DatabaseHandler.TAG_COMMONTASKSTATUS, 1);
                            db.update(DatabaseHandler.TABLE_COMMONTASK, cv, "_id ='" + task_id + "' ", null);
                            Toast.makeText(getApplicationContext(), "Task is mark completed", Toast.LENGTH_LONG).show();
                            sd.delete(0, sd.length());
                            dialog.dismiss();
                            Intent i = new Intent(getApplicationContext(), Todo.class);
                            startActivity(i);
                            mem_list.clear();
                        }
                    });

                    Edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (type.equals("Bill")) {
                                Intent i = new Intent(getApplicationContext(), AddBill.class);
                                String abc = String.valueOf(task_id);
                                i.putExtra("Taskid", abc);
                                startActivity(i);
                            } else if (type.equals("Event")) {
                                Intent i = new Intent(getApplicationContext(), AddEvent.class);
                                String abc = String.valueOf(task_id);
                                i.putExtra("Taskid", abc);
                                startActivity(i);

                            } else if (type.equals("Appointment")) {
                                Intent i = new Intent(getApplicationContext(), AddAppointment.class);
                                String abc = String.valueOf(task_id);
                                i.putExtra("Taskid", abc);
                                startActivity(i);

                            } else if (type.equals("Alarm")) {
                                Intent i = new Intent(getApplicationContext(), Wake_up.class);
                                String abc = String.valueOf(task_id);
                                i.putExtra("Taskid", abc);
                                startActivity(i);

                            } else if (type.equals("Medicine")) {
                                Intent i = new Intent(getApplicationContext(), AddMedicines.class);
                                String abc = String.valueOf(task_id);
                                i.putExtra("Taskid", abc);
                                startActivity(i);
                            }
                        }
                    });
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

            try {
                DateFormat formatter = null;
                Date rem_datetime = null;
                formatter = new SimpleDateFormat("d.M.yyyy hh:mm a", Locale.ENGLISH);
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
}