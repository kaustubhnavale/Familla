package familla.mipl.familla.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import familla.mipl.familla.R;

public class showmember extends AppCompatActivity {
    DatabaseHandler handler;
    SQLiteDatabase db;
    ListView homelist;
    ImageView img1;
    String Ownername;
    MyCursorAdapter adapter;
    Cursor c;
    Typeface tf;
    int usercount;
    private Toolbar mToolbar;
    SQLiteDatabase db_read;
    ConnectionDetector cd;

    Button member;
    RelativeLayout name1;

    StringRequest stringRequest;
    int userid;
    Cursor c_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showmember);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" M E M B E R ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.colorgradiant));

        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();
        db_read = handler.getReadableDatabase();
        cd = new ConnectionDetector(this);

        String user_check = "select * from userdetails ";
        c_user = db_read.rawQuery(user_check, null);
        c_user.moveToFirst();
        if (c_user.getCount() != 0) {
            if (cd.isConnectingToInternet()) {
                getData();
            }
        }

        tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");
        LinearLayout name = (LinearLayout) findViewById(R.id.name);
        name1 = (RelativeLayout) findViewById(R.id.name1);
        member = (Button) findViewById(R.id.member);
        TextView textmem = (TextView) findViewById(R.id.textmem);

        textmem.setTypeface(tf, Typeface.BOLD);
        member.setTypeface(tf, Typeface.BOLD);
        // String str = "select * from commontask where date('now') <= datetime(commontask_reminderdatetime) order by commontask_reminderdatetime  asc ";

        String str = "select * from memberdetails where md_status=1 ";

        c = db.rawQuery(str, null);
        c.moveToFirst();
        usercount = c.getCount();

        homelist = (ListView) findViewById(R.id.showmem);

        if (usercount != 0) {
            name1.setVisibility(View.GONE);
            homelist.setVisibility(View.VISIBLE);
            adapter = new MyCursorAdapter(getApplicationContext(), c);
            homelist.setAdapter(adapter);
        } else {

            name1.setVisibility(View.VISIBLE);
            homelist.setVisibility(View.GONE);

            member.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent i = new Intent(showmember.this, Add_memberbycontact.class);
                                              i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                              startActivity(i);
                                          }
                                      }
            );
        }

        homelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // String selectedFromList = (homelist.getItemAtPosition(position).toString());
                int mem_id = (int) parent.getItemIdAtPosition(position);

                String quer1 = "select * from memberdetails where _id='" + mem_id + "' ";
                c = db.rawQuery(quer1, null);
                c.moveToFirst();

                //   String Owner=(c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));

                String memberid = (c.getString(c.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));
                String groupid = (c.getString(c.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSGROUPID)));

                Intent i = new Intent(showmember.this, MemberDetails.class);
                i.putExtra("member_id", memberid);
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
            return LayoutInflater.from(context).inflate(R.layout.member_details, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView text = (TextView) view.findViewById(R.id.demotext1);
            String name = (cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));
            String role = (cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_MemberRole)));
            name = name.toUpperCase();
            text.setTypeface(tf, Typeface.BOLD);
            text.setText(name);
            ImageView ro = (ImageView) view.findViewById(R.id.img_1);
            if (role.equals("Father")) {
                ro.setImageResource(R.drawable.father);
            } else if (role.equals("Mother")) {
                ro.setImageResource(R.drawable.mother);
            } else if (role.equals("Husband")) {
                ro.setImageResource(R.drawable.husband);
            } else if (role.equals("Wife")) {
                ro.setImageResource(R.drawable.wife);
            } else if (role.equals("Son")) {
                ro.setImageResource(R.drawable.boy);
            } else if (role.equals("BOY")) {
                ro.setImageResource(R.drawable.boy);
            } else if (role.equals("GIRL")) {
                ro.setImageResource(R.drawable.girl);
            } else if (role.equals("Daughter")) {
                ro.setImageResource(R.drawable.girl);
            } else if (role.equals("Grandfather")) {
                ro.setImageResource(R.drawable.grandfather);
            } else if (role.equals("Grandmother")) {
                ro.setImageResource(R.drawable.grandmother);
            }
        }
    }


    public void getData() {

        stringRequest = new StringRequest(Request.Method.POST, "http://www.thefamilla.com/android/sync_members.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        int count_member = 0;
                        JSONArray jarray_m = null;

                        try {
                            jarray_m = new JSONArray(response);
                            count_member = jarray_m.length();
                        } catch (JSONException e) {
                            e.printStackTrace();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (count_member > 0) {

                            try {
                                db.delete("memberdetails", null, null);

                                for (int i = 0; i < count_member; i++) {

                                    JSONObject jObj = jarray_m.getJSONObject(i);
                                    String member_userid = jObj.getString("member_userid");
                                    String member_username = jObj.getString("member_username");

                                    ContentValues cv_member = new ContentValues();
                                    // cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERID, member_userid);
                                    cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSEREMAIL, jObj.getString("member_mobileno"));
                                    cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME, jObj.getString("member_timestamp"));
                                    cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSTYPEID, jObj.getString("member_typeid"));
                                    cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSGROUPID, jObj.getString("member_groupid"));
                                    cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME, jObj.getString("member_username"));
                                    cv_member.put(DatabaseHandler.TAG_MEMBERSTATUS, jObj.getString("status"));
                                    cv_member.put(DatabaseHandler.TAG_MEMBERADDEDBY, jObj.getString("addedBy"));
                                    cv_member.put(DatabaseHandler.TAG_MemberRole, jObj.getString("member_role_familla"));

                                    String check_m = "select * from memberdetails where md_userid =" + member_userid;
                                    Cursor c_checkm = db_read.rawQuery(check_m, null);
                                    c_checkm.moveToFirst();
                                    if (c_checkm.getCount() == 0) {
                                        //insert into memberdetails
                                        cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERID, member_userid);
                                        Long id = db.insert(DatabaseHandler.TABLE_MEMBERDETAILS, null, cv_member);
                                        userid = (int) (long) id;
                                        Log.e("insertid", id.toString());
//                                        scheduleMemberNotification(1, userid);
                                    } else {
                                        Integer medha = db.update(DatabaseHandler.TABLE_MEMBERDETAILS, cv_member, "md_userid=" + member_userid, null);
                                        Log.e("medha_update", medha.toString());
                                        //update memberdetails
                                        userid = medha;
                                    }
                                }

                                /*try {
                                    String getLoginUser = "select * from userdetails";
                                    Cursor c_LoginUser = db_read.rawQuery(getLoginUser, null);
                                    c_LoginUser.moveToFirst();

                                    db.execSQL("insert into memberdetails (md_groupid,md_useremail,md_username,md_typeid,md_status,mem_role,md_userid)"
                                            + "values(" + c_LoginUser.getString(c_LoginUser.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID))
                                            + "," + c_LoginUser.getString(c_LoginUser.getColumnIndex(DatabaseHandler.TAG_PHONE))
                                            + ",'" + c_LoginUser.getString(c_LoginUser.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME)) + "'"
                                            + "," + c_LoginUser.getString(c_LoginUser.getColumnIndex(DatabaseHandler.TAG_USERDETAILSTYPEID))
                                            + "," + c_LoginUser.getString(c_LoginUser.getColumnIndex(DatabaseHandler.TAG_USERDETAILSLOGIN))
                                            + ",'" + c_LoginUser.getString(c_LoginUser.getColumnIndex(DatabaseHandler.TAG_Role)) + "'"
                                            + "," + c_LoginUser.getString(c_LoginUser.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID))
                                            + ") ;");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/


                                String str = "select * from memberdetails where md_status=1 ";

                                c = db.rawQuery(str, null);
                                c.moveToFirst();
                                usercount = c.getCount();

                                homelist = (ListView) findViewById(R.id.showmem);

                                if (usercount != 0) {
                                    name1.setVisibility(View.GONE);
                                    homelist.setVisibility(View.VISIBLE);
                                    adapter = new MyCursorAdapter(getApplicationContext(), c);
                                    homelist.setAdapter(adapter);
                                } else {

                                    name1.setVisibility(View.VISIBLE);
                                    homelist.setVisibility(View.GONE);

                                    member.setOnClickListener(new View.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(View v) {
                                                                      Intent i = new Intent(showmember.this, Add_memberbycontact.class);
                                                                      i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                      startActivity(i);
                                                                  }
                                                              }
                                    );
                                }


                            } catch (JSONException e) {
                                Log.e("log_tag", "Error parsing data " + e.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(showmember.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("member_userid", c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));
                params.put("member_groupid", c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID)));
                return params;
            }
        };

        stringRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(showmember.this);
        requestQueue.add(stringRequest);
    }

    public void scheduleMemberNotification(/* Notification notification, */int delay, int uniqueid) {

        Intent notificationIntent = new Intent(showmember.this,
                MyNotificationPublisher.class);
        notificationIntent.putExtra("notificationid", uniqueid);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(showmember.this, uniqueid,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationPublisher.NOTIFICATION_ID = uniqueid;

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) showmember.this.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

}