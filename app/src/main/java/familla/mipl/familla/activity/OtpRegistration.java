package familla.mipl.familla.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

import familla.mipl.familla.R;

public class OtpRegistration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText name, familyname;
    public static final String TAG_USERID = "UserID";
    public static final String TAG_USERNAME = "UserName";
    public static final String TAG_UserNumber = "mobileno";
    public static final String TAG_TYPEID = "TypeID";
    public static final String TAG_GROUPID = "GroupID";
    public static final String TAG_GroupName = "GroupName";
    String userid_, username_, usernumber, typeid_, groupid_, groupname, members_, fRole;
    WebView sms;
    String Name, Number, Fname, Message, memberrelation, val;
    Button Register;
    String[] Relation = {"Father", "Mother", "Husband", "Wife", "Son", "Daughter", "Grandfather", "Grandmother"};
    int Img[] = {R.drawable.father, R.drawable.mother, R.drawable.father, R.drawable.mother, R.drawable.boy, R.drawable.girl, R.drawable.grandfather, R.drawable.grandmother};
    private Toolbar mToolbar;
    DatabaseHandler handler;
    SQLiteDatabase db, db_read;
    ConnectionDetector cd;
    Cursor c_checkrows;
    String currentDateandTime;
    public static String status;
    public static String usertype;
    CreateFamilla newgroup;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_registration);

        token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getWritableDatabase();
        db_read = handler.getReadableDatabase();
        Log.e("logon", "logon");

        Number = getIntent().getExtras().getString("mobileno");

        /***CHECK LOGIN STATUS*****************************************/
      /*  c_checkrows = db_read.rawQuery("select count(*) from userdetails ", null);
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
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);/*//***Change Here***

         i.putExtra("check","1");
         startActivity(i);
         finish();
         }
         }
         */
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setLogo(null);
        ImageView id = (ImageView) mToolbar.findViewById(R.id.fam);
        Typeface tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");
        id.setImageResource(R.drawable.toolbarlogo);

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        AddMemberSpiner customAdapter = new AddMemberSpiner(getApplicationContext(), Img, Relation);
        spin.setAdapter(customAdapter);
        name = (EditText) findViewById(R.id.Uname);
        familyname = (EditText) findViewById(R.id.fname);
        sms = (WebView) findViewById(R.id.sms);
        Register = (Button) findViewById(R.id.Register);

        Register.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Name = name.getText().toString();
                                            Fname = familyname.getText().toString();
                                            newgroup = new CreateFamilla(Name, Number, Fname, memberrelation);
                                            newgroup.execute();
                                        }
                                    }
        );
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int position, long l) {
        switch (arg0.getId()) {

            case R.id.spinner:
                memberrelation = Relation[position];
                break;
        }
    }

    public class CreateFamilla extends AsyncTask<String, Void, String> {

        ContentValues cv_user;
        String name_new, new_number, familyname, memberrelation;
        Integer n = 0;
        ProgressDialog progressDialog;

        public CreateFamilla(String name_new, String new_number, String familyname, String memberrelation) {
            this.name_new = name_new;
            this.new_number = new_number;
            this.familyname = familyname;
            this.memberrelation = memberrelation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OtpRegistration.this);//,R.style.CustomDialogTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Registering...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("create", "familla");
            String result = null;

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.thefamilla.com/android/createFamilla.php");
            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("name_new", name_new));
            nameValuePair.add(new BasicNameValuePair("mobile_no", new_number));
            nameValuePair.add(new BasicNameValuePair("role_familla_new", memberrelation));
            nameValuePair.add(new BasicNameValuePair("group_name", familyname));
            nameValuePair.add(new BasicNameValuePair("token", token));

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
                Log.e("result", result);

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

            Log.e("check result", result);

            JSONArray jArray = null;
            try {

                jArray = new JSONArray(result);
                n = jArray.length();
            } catch (JSONException e) {
                Log.e("connection", "refused");
            }

            if (n == 0) {
                result = "invalid user";
            } else {

                try {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObj = jArray.getJSONObject(i);
                        userid_ = jObj.getString(TAG_USERID);
                        username_ = jObj.getString(TAG_USERNAME);
                        usernumber = jObj.getString(TAG_UserNumber);
                        typeid_ = jObj.getString(TAG_TYPEID);
                        groupid_ = jObj.getString(TAG_GROUPID);
                        groupname = jObj.getString(TAG_GroupName);
                        fRole = jObj.getString("role_familla");

                        ContentValues cv_user = new ContentValues();
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERID, userid_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERNAME, username_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSTYPEID, typeid_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSGROUPID, groupid_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSLOGIN, true);
                        cv_user.put(DatabaseHandler.TAG_Role, fRole);
                        cv_user.put(DatabaseHandler.TAG_PHONE, usernumber);
                        cv_user.put(DatabaseHandler.TAG_FAMILYNAME, groupname);

                        db.insert(DatabaseHandler.TABLE_USERDETAILS, null, cv_user);
                    }

                } catch (JSONException e) {
                    Log.e("log_tag", "Error parsing data " + e.toString());
                    result = "invalid user";
                }
                result = "logged in";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String check = "invalid user";
            Log.e("post execute", result);
            progressDialog.dismiss();
            if (result.equals(check)) {
                Toast.makeText(getApplicationContext(), "This account is already in use! Please use different Number.", Toast.LENGTH_LONG).show();

            } else {
                Intent i = new Intent(OtpRegistration.this, MainActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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