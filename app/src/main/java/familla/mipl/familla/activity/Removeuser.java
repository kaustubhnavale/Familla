package familla.mipl.familla.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import familla.mipl.familla.R;

public class Removeuser extends AppCompatActivity {
    public Dialog dialog;
    DatabaseHandler handler;
    SQLiteDatabase db, dbwrite, db_read;
    ListView homelist;
    ImageView img1;
    String Ownername;
    String id1, intmon, dat;
    static NotificationManagerCompat notificationManager = null;
    MyCursorAdapter adapter;
    TextView taskid;
    int task_id;
    String type;
    private Toolbar mToolbar;
    LinearLayout test;
    Cursor c;
    int flag;
    Forgot add;
    Typeface tf;
    String userid, memid;
    ConnectionDetector cd;
    public static String status = "";
    StringRequest stringRequest;
    Cursor c_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removeuser);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" R E M O V E  U S E R ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();
        dbwrite = handler.getWritableDatabase();
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancelAll();
        final String a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());
        tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");

        db_read = handler.getReadableDatabase();

        String user_check = "select * from userdetails ";
        c_user = db_read.rawQuery(user_check, null);
        c_user.moveToFirst();

        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
            status = "online";
        } else {
            status = "offline";
        }

        String que = "select * from userdetails";
        Cursor c_user1 = db.rawQuery(que, null);
        c_user1.moveToFirst();
        String userid1 = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));

//        String str = "select * from memberdetails where md_addedby='"+userid1+"' and md_status='1' ";
        String str = "select * from memberdetails";
        c = db.rawQuery(str, null);
        c.moveToFirst();

        homelist = (ListView) findViewById(R.id.chat_member_list);
        LinearLayout notask = (LinearLayout) findViewById(R.id.notask);
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

                try {
                    String selectedFromList = (homelist.getItemAtPosition(position).toString());
                    task_id = (int) parent.getItemIdAtPosition(position);

                    String que = "select * from userdetails";
                    Cursor c_user1 = db.rawQuery(que, null);
                    c_user1.moveToFirst();
                    userid = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));

//                    String que1 = "select * from memberdetails where _id='" + task_id + "'";
                    String que1 = "select * from memberdetails";
                    Cursor c_user2 = db.rawQuery(que1, null);
                    c_user2.moveToFirst();
                    memid = (c_user2.getString(c_user2.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));
                    Log.e("memid", memid);

                    new AlertDialog.Builder(Removeuser.this)
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete Member?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
//                                add = new Forgot(userid, memid);
//                                add.execute();
                                    if (cd.isConnectingToInternet()) {
                                        removeMemberAPI();
                                    } else {
                                        Toast.makeText(Removeuser.this, "Not Connected to internet", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

               /* String str = "select * from memberdetails where _id='"+task_id+"' ";
                c = db.rawQuery(str, null);
                c.moveToFirst();

                String memberid= (c.getString(c.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));
                String groupid= (c.getString(c.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSGROUPID)));

                Intent i = new Intent(getApplicationContext(), Chat.class);
                i.putExtra("userid",userid);
                i.putExtra("Memberid",memberid);
                i.putExtra("groupid",groupid);
                startActivity(i);*/
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
            taskname.setTypeface(tf, Typeface.BOLD);


            taskname.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));
            //tasknote.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
            String role = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_MemberRole));
            Log.i("Role", role);

            if (role.equals("Father")) {
                img.setImageResource(R.drawable.father);
            } else if (role.equals("Mother")) {
                img.setImageResource(R.drawable.mother);
            } else if (role.equals("Husband")) {
                img.setImageResource(R.drawable.husband);
            } else if (role.equals("Wife")) {
                img.setImageResource(R.drawable.wife);
            } else if (role.equals("Son")) {
                img.setImageResource(R.drawable.boy);
            } else if (role.equals("BOY")) {
                img.setImageResource(R.drawable.boy);
            } else if (role.equals("GIRL")) {
                img.setImageResource(R.drawable.girl);
            } else if (role.equals("Daughter")) {
                img.setImageResource(R.drawable.girl);
            } else if (role.equals("Grandfather")) {
                img.setImageResource(R.drawable.grandfather);
            } else if (role.equals("Grandmother")) {
                img.setImageResource(R.drawable.grandmother);
            }
        }
    }

    public class Forgot extends AsyncTask<String, Void, Void> {

        String result = null;
        ProgressDialog progressDialog;
        String new_email, userid, memid;

        public Forgot(String userid, String memid) {

            this.userid = userid;
            this.memid = memid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Removeuser.this);//,R.style.CustomDialogTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://thefamilla.com/android/removemember.php");
            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("addedby", userid));
            nameValuePair.add(new BasicNameValuePair("user_to_be_removed", memid));

            // Url Encoding the POST parameters
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }
            // Making HTTP Request
            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();

                result = EntityUtils.toString(entity);
                Log.e("result@admin", result);

            } catch (ClientProtocolException e) {
                // writing exception to log
                // e.printStackTrace();
                result = "connection error";

            } catch (HttpHostConnectException e) {
                //  e.printStackTrace();
                result = "connection error";
            } catch (IOException e) {
                // writing exception to log
                //e.printStackTrace();
                result = "connection error";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (status.equals("online")) {
                if (result.trim().contains("member deactivated successfully")) {

                    ContentValues cv = new ContentValues();
                    String value = "0";
                    Log.e("Hello", "Hi");
                    cv.put(DatabaseHandler.TAG_MEMBERSTATUS, value);
                    dbwrite.update(DatabaseHandler.TABLE_MEMBERDETAILS, cv, "md_userid = '" + memid + "'", null);

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "member deactivated successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Removeuser.this, Removeuser.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
    }


    public void removeMemberAPI() {

        final ProgressDialog progressDialog = new ProgressDialog(Removeuser.this);//,R.style.CustomDialogTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        stringRequest = new StringRequest(Request.Method.POST, "http://thefamilla.com/android/removemember.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.contains("member deactivated successfully")) {

                            ContentValues cv = new ContentValues();
                            String value = "0";
                            Log.e("Hello", "Hi");
                            cv.put(DatabaseHandler.TAG_MEMBERSTATUS, value);
                            dbwrite.update(DatabaseHandler.TABLE_MEMBERDETAILS, cv, "md_userid = '" + memid + "'", null);

                            Toast.makeText(getApplicationContext(), "member deactivated successfully", Toast.LENGTH_LONG).show();
                            getData();

                        } else {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Removeuser.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("addedby", userid);
                params.put("user_to_be_removed", memid);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Removeuser.this);
        requestQueue.add(stringRequest);
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
                                        Log.e("insertid", id.toString());
                                    } else {
                                        Integer medha = db.update(DatabaseHandler.TABLE_MEMBERDETAILS, cv_member, "md_userid=" + member_userid, null);
                                        Log.e("medha_update", medha.toString());
                                        //update memberdetails
                                    }
                                }

                            } catch (JSONException e) {
                                Log.e("log_tag", "Error parsing data " + e.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Intent intent = new Intent(Removeuser.this, Removeuser.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                        startActivity(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Removeuser.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(Removeuser.this);
        requestQueue.add(stringRequest);
    }
}