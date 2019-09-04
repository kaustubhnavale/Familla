package familla.mipl.familla.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import familla.mipl.familla.R;

public class Login extends AppCompatActivity {
    EditText email, password;
    String a;
    ArrayList<String> addstr = new ArrayList<String>();
    ProgressDialog progressDialog;
    Button sign;
    TextView Reg;
    CheckLogin check;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String MyPREFERENCES1 = "MyPrefs1";

    public static final String TAG_USERID = "UserID";
    public static final String TAG_USERNAME = "UserName";
    public static final String TAG_USEREMAIL = "UserEmail";
    public static final String TAG_TYPEID = "TypeID";
    public static final String TAG_GROUPID = "GroupID";
    public static final String TAG_GROUPPASSWORD = "GroupPassword";
    public static final String TAG_MEMBERS = "members";

    private Toolbar mToolbar;
    String userid_, username_, useremail_, typeid_, groupid_, grouppassword_, members_, role;
    TextView register, createaccount;
    String regerror;
    DatabaseHandler handler;
    SQLiteDatabase db, db_read;
    ConnectionDetector cd;
    public static String status;
    public static String usertype;
    Cursor c_checkrows;
    SharedPreferences sharedpreferences, sharedpreferences1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");
     /*   mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);*/

      /*  LayoutInflater mInflater=LayoutInflater.from(getApplicationContext());
        View mCustomView = mInflater.inflate(R.layout.custom_toolbar_one,null);
        mToolbar.addView(mCustomView);*/
      /*  getSupportActionBar().setLogo(null);

        ImageView id=(ImageView) mToolbar.findViewById(R.id.fam);

        id.setImageResource(R.drawable.toolbarlogo);*/

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        sharedpreferences1 = getSharedPreferences(MyPREFERENCES1, Context.MODE_PRIVATE);
        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getWritableDatabase();
        db_read = handler.getReadableDatabase();
        Log.e("logon", "logon");

        /***CHECK LOGIN STATUS*****************************************/
        c_checkrows = db_read.rawQuery("select count(*) from userdetails ", null);
        c_checkrows.moveToFirst();
        if (c_checkrows.getInt(0) != 0) {
            Log.e("first", "check");
            Log.e("user", "existing");
            usertype = "existing";
            Cursor c_statuscheck = db_read.rawQuery("select * from userdetails where _id = 1", null);
            c_statuscheck.moveToFirst();
            String islogin = c_statuscheck.getString(c_statuscheck.getColumnIndex(DatabaseHandler.TAG_USERDETAILSLOGIN));
            Log.e("status", islogin);
            if (islogin.equals("1")) {
                Intent i = new Intent(this, MainActivity.class);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***

                i.putExtra("check", "1");
                startActivity(i);
                finish();
            }
        } else {
            usertype = "new user";
        }

        /***CHECK LOGIN STATUS END HERE*****************************************/

        /***NETWORK CONNECTION CHANGES MONITORING************************************/

        /***NETWORK CONNECTION CHANGES MONITORING ENDS HERE************************************/

        /***CONNECTION CHECK*****************************************/

        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
            status = "online";
            Log.e("conn_status", status);
        } else {
            status = "offline";
            Log.e("conn_status", status);
        }

        Button re = (Button) findViewById(R.id.register);
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Registration.class);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(i);
                finish();
            }
        });

        /***CONNECTION CHECK ENDS HERE*****************************************/
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        email.setTypeface(tf, Typeface.BOLD);
        password.setTypeface(tf, Typeface.BOLD);
        sign = (Button) findViewById(R.id.sign);
        TextView forgot = (TextView) findViewById(R.id.forgot);
        forgot.setTypeface(tf, Typeface.BOLD);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Login.this, Forgotpassword.class);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(i);
                finish();
            }
        });

        sign.setTypeface(tf, Typeface.BOLD);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((email.getText().toString().equals("")) || (password.getText().toString().equals(""))) {
                    Toast.makeText(getApplicationContext(), "Please check your email ID or password!", Toast.LENGTH_LONG).show();

                } else {
                    check = new CheckLogin(email.getText().toString(), password.getText().toString());
                    check.execute();
                }
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    //Toast.makeText(getApplicationContext(),"Hi",Toast.LENGTH_SHORT).show();
                    if ((email.getText().toString().equals("")) || (password.getText().toString().equals(""))) {
                        Toast.makeText(getApplicationContext(), "Please check your email ID or password!", Toast.LENGTH_LONG).show();

                    } else {
                        check = new CheckLogin(email.getText().toString(), password.getText().toString());
                        check.execute();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public class CheckLogin extends AsyncTask<String, Void, String> {

        String email, password;
        Integer n = 0, m = 0;
        ProgressDialog progressDialog;
        ContentValues cv_user, cv_member;
        String ud_email_offline;
        HttpClient httpClient;
        HttpPost httpPost;
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        JSONArray jArray = null;

        public CheckLogin(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Login.this);//,R.style.CustomDialogTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;

            if (status.equals("online")) {
                Log.e("status", status);
                Cursor c_existing = db_read.rawQuery("select * from userdetails", null);
                c_existing.moveToFirst();
                Log.e("usertype", usertype);

                if (usertype.equals("existing")) {
                    if (email.equals(c_existing.getString(c_existing.getColumnIndex(DatabaseHandler.TAG_USERDETAILSEMAIL)))) {
                        Log.v("Siddesh", "Siddesh");
                        Log.e("existing_useremail", c_existing.getString(c_existing.getColumnIndex(DatabaseHandler.TAG_USERDETAILSEMAIL)));
                        httpClient = new DefaultHttpClient();
                        httpPost = new HttpPost
                                ("http://www.thefamilla.com/android/checkLogin.php");

                        nameValuePair.add(new BasicNameValuePair("email", email));
                        nameValuePair.add(new BasicNameValuePair("password", password));
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
                            Log.d("result", result);

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

                        try {

                            jArray = new JSONArray(result);
                            n = jArray.length();
                        } catch (JSONException e) {
                            Log.e("log_tag", "Error parsing data " + e.toString());
                            //  Log.e("connection", "refused");
                        }
                        if (n == 0) {
                            result = "invalid user";
                        } else {

                            try {
                                for (int i = 0; i < jArray.length(); i++) {

                                    JSONObject jObj = jArray.getJSONObject(i);
                                    userid_ = jObj.getString(TAG_USERID);
                                    username_ = jObj.getString(TAG_USERNAME);
                                    typeid_ = jObj.getString(TAG_TYPEID);
                                    groupid_ = jObj.getString(TAG_GROUPID);
                                    grouppassword_ = jObj.getString(TAG_GROUPPASSWORD);
                                    members_ = jObj.getString(TAG_MEMBERS);
                                    useremail_ = jObj.getString(TAG_USEREMAIL);
                                    role = jObj.getString("role_familla");
                                    a = userid_;

                                    Log.e("abc", a);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();

                                    editor.putString("User_id", a);
                                    editor.commit();
                                    Log.e("session", editor.toString());
                                    cv_user = new ContentValues();
                                    cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERID, userid_);
                                    cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERNAME, username_);
                                    cv_user.put(DatabaseHandler.TAG_USERDETAILSEMAIL, useremail_);
                                    cv_user.put(DatabaseHandler.TAG_USERDETAILSTYPEID, typeid_);
                                    cv_user.put(DatabaseHandler.TAG_USERDETAILSGROUPID, groupid_);
                                    cv_user.put(DatabaseHandler.TAG_USERDETAILSGROUPPASSWORD, grouppassword_);
                                    cv_user.put(DatabaseHandler.TAG_USERDETAILSLOGIN, true);
                                    cv_user.put(DatabaseHandler.TAG_Role, role);
                                    cv_user.put(DatabaseHandler.TAG_LOGIN_STATUS, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new java.util.Date()));

                                    db.update("userdetails", cv_user, "_id" + "=" + 1,null);

                                    db.execSQL("delete from " + DatabaseHandler.TABLE_MEMBERDETAILS);

                                    try {
                                        JSONArray member_jarray = new JSONArray(members_);

                                        m = member_jarray.length();
                                        if (m == 0) {

                                        } else
                                            for (int j = 0; j < m; j++) {
                                                JSONObject member_jobj =
                                                        member_jarray.getJSONObject(j);
                                                HashMap<String, String> map = new
                                                        HashMap<>();
                                                String member_userid =
                                                        member_jobj.getString("member_userid");
                                                addstr.add(j, member_userid);
                                                Log.e("member_userid", member_userid);
                                                String member_username =
                                                        member_jobj.getString("member_username");
                                                Log.e("member_username",
                                                        member_username);
                                                String member_useremail =
                                                        member_jobj.getString("member_useremail");
                                                Log.e("member_useremail",
                                                        member_useremail);
                                                String member_typeid =
                                                        member_jobj.getString("member_typeid");
                                                Log.e("member_typeid", member_typeid);
                                                String member_typename =
                                                        member_jobj.getString("member_typename");
                                                Log.e("member_typename",
                                                        member_typename);
                                                String member_groupid =
                                                        member_jobj.getString("member_groupid");
                                                Log.e("member_groupid", member_groupid);
                                                String member_timestamp =
                                                        member_jobj.getString("member_timestamp");

                                                String memrole = member_jobj.getString("member_role_familla");
                                                Log.e("member_timestamp", member_timestamp);

                                                cv_member = new ContentValues();
                                                cv_member.put
                                                        (DatabaseHandler.TAG_MEMBERDETAILSUSERID, member_userid);
                                                cv_member.put
                                                        (DatabaseHandler.TAG_MEMBERDETAILSUSERNAME, member_username);
                                                cv_member.put
                                                        (DatabaseHandler.TAG_MEMBERDETAILSUSEREMAIL, member_useremail);
                                                cv_member.put
                                                        (DatabaseHandler.TAG_MEMBERDETAILSTYPEID, member_typeid);
                                                cv_member.put
                                                        (DatabaseHandler.TAG_MEMBERDETAILSTYPENAME, member_typename);
                                                cv_member.put
                                                        (DatabaseHandler.TAG_MEMBERDETAILSGROUPID, member_groupid);
                                                cv_member.put
                                                        (DatabaseHandler.TAG_MEMBERDETAILSTIMESTAMP, member_timestamp);
                                                cv_member.put
                                                        (DatabaseHandler.TAG_MemberRole, memrole);
                                                db.insert
                                                        (DatabaseHandler.TABLE_MEMBERDETAILS, null, cv_member);
                                            }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e("log_tag", "Error parsing data " + e.toString());
                                result = "invalid user";
                            }

                            result = "logged in";
                        }
                    } else {
                        Log.v("Joshi", "Joshi");
                        result = "invalid user";
                    }

                    SharedPreferences.Editor editor = sharedpreferences1.edit();
                    Set<String> set = new HashSet<String>();
                    set.addAll(addstr);
                    editor.putStringSet("Array", set);
                    editor.commit();
                } else if (usertype.equals("new user")) {
                    httpClient = new DefaultHttpClient();
                    httpPost = new HttpPost
                            ("http://www.thefamilla.com/android/checkLogin.php");

                    nameValuePair.add(new BasicNameValuePair("email", email));
                    nameValuePair.add(new BasicNameValuePair("password", password));
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
                        Log.d("result", result);

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

                   /* try {

                        jArray = new JSONArray(result);
                        n = jArray.length();
                    } catch (JSONException e) {
                        Log.e("log_tag", "Error parsing data " + e.toString());
                        //  Log.e("connection", "refused");
                    }
                    if (n == 0) {
                        result = "invalid user";
                    }*/

                    Log.e("result_wrong", "medha" + result + "medha");
                    //result = "wrong login";
                    result = result.trim();
                    if (result.equals("wrong login")) {
                        result = "invalid user";
                    } else {

                        try {
                            jArray = new JSONArray(result);
                            for (int i = 0; i < jArray.length(); i++) {

                                JSONObject jObj = jArray.getJSONObject(i);
                                userid_ = jObj.getString(TAG_USERID);
                                username_ = jObj.getString(TAG_USERNAME);
                                Log.e("usernamefirsttimelogin", username_);
                                useremail_ = jObj.getString(TAG_USEREMAIL);
                                typeid_ = jObj.getString(TAG_TYPEID);
                                groupid_ = jObj.getString(TAG_GROUPID);
                                grouppassword_ = jObj.getString(TAG_GROUPPASSWORD);
                                members_ = jObj.getString(TAG_MEMBERS);
                                role = jObj.getString("role_familla");
                                String a = userid_;
                                Log.e("pqr", a);
                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                editor.putString("User_id", a);
                                editor.commit();
                                cv_user = new ContentValues();
                                cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERID,
                                        userid_);
                                cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERNAME,
                                        username_);
                                cv_user.put(DatabaseHandler.TAG_USERDETAILSEMAIL,
                                        useremail_);
                                cv_user.put(DatabaseHandler.TAG_USERDETAILSTYPEID,
                                        typeid_);
                                cv_user.put(DatabaseHandler.TAG_USERDETAILSGROUPID,
                                        groupid_);
                                cv_user.put(DatabaseHandler.TAG_USERDETAILSGROUPPASSWORD,
                                        grouppassword_);
                                cv_user.put(DatabaseHandler.TAG_USERDETAILSLOGIN, true);

                                cv_user.put(DatabaseHandler.TAG_Role, role);
                                cv_user.put(DatabaseHandler.TAG_LOGIN_STATUS, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new java.util.Date()));
                                db.insert(DatabaseHandler.TABLE_USERDETAILS, null,
                                        cv_user);

                                try {
                                    JSONArray member_jarray = new JSONArray
                                            (members_);

                                    m = member_jarray.length();
                                    if (m == 0) {

                                    } else
                                        for (int j = 0; j < m; j++) {
                                            JSONObject member_jobj =
                                                    member_jarray.getJSONObject(j);
                                            HashMap<String, String> map = new
                                                    HashMap<>();
                                            String member_userid =
                                                    member_jobj.getString("member_userid");
                                            Log.e("member_userid", member_userid);
                                            String member_username =
                                                    member_jobj.getString("member_username");
                                            Log.e("member_username",
                                                    member_username);
                                            String member_useremail =
                                                    member_jobj.getString("member_useremail");
                                            Log.e("member_useremail",
                                                    member_useremail);
                                            String member_typeid =
                                                    member_jobj.getString("member_typeid");
                                            Log.e("member_typeid", member_typeid);
                                            String member_typename =
                                                    member_jobj.getString("member_typename");
                                            Log.e("member_typename",
                                                    member_typename);
                                            String member_groupid =
                                                    member_jobj.getString("member_groupid");
                                            Log.e("member_groupid", member_groupid);
                                            String member_timestamp =
                                                    member_jobj.getString("member_timestamp");
                                            String memrole =
                                                    member_jobj.getString("member_role_familla");

                                            Log.e("member_timestamp", member_timestamp);

                                            cv_member = new ContentValues();
                                            cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERID, member_userid);
                                            cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME, member_username);
                                            cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSEREMAIL, member_useremail);
                                            cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSTYPEID, member_typeid);
                                            cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSTYPENAME, member_typename);
                                            cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSGROUPID, member_groupid);
                                            cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSTIMESTAMP, member_timestamp);
                                            cv_member.put(DatabaseHandler.TAG_MemberRole, memrole);
                                            db.insert(DatabaseHandler.TABLE_MEMBERDETAILS, null, cv_member);
                                        }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    result = "invalid user";
                                }
                            }

                        } catch (JSONException e) {
                            Log.e("log_tag", "Error parsing data " + e.toString());
                            result = "invalid user";
                        }

                        result = "logged in";
                    }
                } else {
                    result = "invalid user";
                }
            }

            if (status.equals("offline")) {
                Log.e("status", status);
                if (usertype.equals("existing")) {
                    Log.e("type", usertype);
                    Cursor c_existing_off = db_read.rawQuery("select * from userdetails where _id=1", null);
                    c_existing_off.moveToFirst();
                    if ((email.equals(c_existing_off.getString(c_existing_off.getColumnIndex(DatabaseHandler.TAG_USERDETAILSEMAIL))))
                            && (password.equals(c_existing_off.getString(c_existing_off.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPPASSWORD))))) {
                        result = "logged in";

                    } else {
                        result = "invalid user";
                    }
                } else {
                    result = "offline mode";
                }
            }
            Log.e("result_offline", result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("post execute", result);
            progressDialog.dismiss();
            if (result.equals("invalid user")) {
                Toast.makeText(getApplicationContext(), "Please check your email ID or password!", Toast.LENGTH_LONG).show();

            } else if (result.equals("invalid user :" + ud_email_offline)) {

                Toast.makeText(getApplicationContext(), "account is already in use by -" + ud_email_offline, Toast.LENGTH_LONG).show();

            } else if (result.equals("offline mode")) {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_LONG).show();

            } else {
                //Log.e("ok", "login successful");
                Intent i = new Intent(Login.this, MainActivity.class);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(i);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
    }
}