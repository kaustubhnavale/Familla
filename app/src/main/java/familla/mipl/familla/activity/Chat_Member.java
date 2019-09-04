package familla.mipl.familla.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import familla.mipl.familla.R;

public class Chat_Member extends AppCompatActivity {
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
    String type;
    LinearLayout test;
    Cursor c;
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__member);
        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancelAll();
        final String a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());

        String str = "select * from memberdetails where md_status = 1";
        c = db.rawQuery(str, null);
        c.moveToFirst();

        homelist = (ListView) findViewById(R.id.chat_member_list);
        LinearLayout notask = (LinearLayout) findViewById(R.id.notask);
        test = (LinearLayout) findViewById(R.id.test);

        if (c.getCount() == 0) {
            homelist.setVisibility(View.GONE);
            notask.setVisibility(View.VISIBLE);
        } else {
            homelist.setVisibility(View.VISIBLE);
            notask.setVisibility(View.GONE);
            adapter = new MyCursorAdapter(getApplicationContext(), c);
            homelist.setAdapter(adapter);
        }

        Button back = (Button) findViewById(R.id.button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Chat_Member.this, MainActivity.class);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);
                finish();
            }
        });

        homelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedFromList = (homelist.getItemAtPosition(position).toString());
                task_id = (int) parent.getItemIdAtPosition(position);

                String que = "select * from userdetails";
                Cursor c_user1 = db.rawQuery(que, null);
                c_user1.moveToFirst();
                String userid = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));

                String str = "select * from memberdetails where _id='" + task_id + "' ";
                c = db.rawQuery(str, null);
                c.moveToFirst();

                String memberid = (c.getString(c.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));
                String groupid = (c.getString(c.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSGROUPID)));

                Intent i = new Intent(getApplicationContext(), Chat.class);
                i.putExtra("userid", userid);
                i.putExtra("Memberid", memberid);
                i.putExtra("groupid", groupid);
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
            return LayoutInflater.from(context).inflate(R.layout.chat_member_list, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // TextView tasktype = (TextView) view.findViewById(R.id.tasktype);
            TextView taskname = (TextView) view.findViewById(R.id.taskname);

            ImageView img = (ImageView) view.findViewById(R.id.todoimg);
            img.setImageResource(R.drawable.father);
            taskname.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));
            //tasknote.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
        }
    }
}