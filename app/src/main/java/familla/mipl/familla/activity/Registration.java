package familla.mipl.familla.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

import familla.mipl.familla.R;

public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView signin, error;
    Button Reg;
    String[] Relation = {"Father", "Mother", "Husband", "Wife", "Son", "Daughter", "Grandfather", "Grandmother"};
    int Img[] = {R.drawable.father, R.drawable.mother, R.drawable.father, R.drawable.mother, R.drawable.boy, R.drawable.girl, R.drawable.grandfather, R.drawable.grandmother};
    EditText name_new, email_new, password_new;
    public static final String TAG_USERID = "UserID";
    public static final String TAG_USERNAME = "UserName";
    public static final String TAG_USEREMAIL = "UserEmail";
    public static final String TAG_TYPEID = "TypeID";
    public static final String TAG_GROUPID = "GroupID";
    public static final String TAG_GROUPPASSWORD = "GroupPassword";
    public static final String TAG_MEMBERS = "members";
    CreateFamilla newgroup;
    String memberrelation;
    String userid_, username_, useremail_, typeid_, groupid_, grouppassword_, members_, Role;
    String regerror;
    private Toolbar mToolbar;

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
        setContentView(R.layout.activity_registration);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setLogo(null);
        ImageView id = (ImageView) mToolbar.findViewById(R.id.fam);
        Typeface tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");

        id.setImageResource(R.drawable.toolbarlogo);
        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getWritableDatabase();
        db_read = handler.getReadableDatabase();
        Log.e("logon", "logon");
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        AddMemberSpiner customAdapter = new AddMemberSpiner(getApplicationContext(), Img, Relation);
        spin.setAdapter(customAdapter);
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
                Intent i = new Intent(Registration.this, MainActivity.class);
                startActivity(i);
            }

        } else {
            usertype = "new user";
        }
        signin = (TextView) findViewById(R.id.signin);
        signin.setTypeface(tf, Typeface.BOLD);
        String text = "<font color=#58595B>If you want to be member of existing FAMILLA account, then </font> <font color=#007A87><strong>Sign In</strong></font>";
        signin.setText(Html.fromHtml(text));
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this, Login.class));
            }
        });

        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
            status = "online";
            Log.e("conn_status", status);

        } else {
            status = "offline";
            Log.e("conn_status", status);
        }

        name_new = (EditText) findViewById(R.id.Uname);
        name_new.setTypeface(tf, Typeface.BOLD);
        email_new = (EditText) findViewById(R.id.email);
        email_new.setTypeface(tf, Typeface.BOLD);
        password_new = (EditText) findViewById(R.id.password);
        password_new.setTypeface(tf, Typeface.BOLD);
        Reg = (Button) findViewById(R.id.Register);
        Reg.setTypeface(tf, Typeface.BOLD);
        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailInput = email_new.getText().toString().trim();

                String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

                if (!emailInput.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "Pleasse enter valid email address", Toast.LENGTH_SHORT).show();
                }

                if (status.equals("offline")) {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

                } else if ((email_new.getText().toString().equals("")) || (password_new.getText().toString().equals("")) || (name_new.getText().toString().equals(""))) {
                    Toast.makeText(getApplicationContext(), "Please enter all details.", Toast.LENGTH_LONG).show();

                } else if (usertype.equals("existing")) {
                    Toast.makeText(getApplicationContext(), "This device uses different account.", Toast.LENGTH_LONG).show();

                } else {
                    newgroup = new CreateFamilla(name_new.getText().toString(), email_new.getText().toString(), password_new.getText().toString(), memberrelation);
                    newgroup.execute();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        switch (arg0.getId()) {

            case R.id.spinner:
                memberrelation = Relation[position];
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class CreateFamilla extends AsyncTask<String, Void, String> {

        ContentValues cv_user;
        String name_new, email_new, password_new, memberrelation;
        Integer n = 0;
        ProgressDialog progressDialog;

        public CreateFamilla(String name_new, String email_new, String password_new, String memberrelation) {
            this.name_new = name_new;
            this.email_new = email_new;
            this.password_new = password_new;
            this.memberrelation = memberrelation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Registration.this);//,R.style.CustomDialogTheme);
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
            nameValuePair.add(new BasicNameValuePair("email_new", email_new));
            nameValuePair.add(new BasicNameValuePair("password_new", password_new));
            nameValuePair.add(new BasicNameValuePair("role_familla_new", memberrelation));

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
                        useremail_ = jObj.getString(TAG_USEREMAIL);
                        typeid_ = jObj.getString(TAG_TYPEID);
                        groupid_ = jObj.getString(TAG_GROUPID);
                        grouppassword_ = jObj.getString(TAG_GROUPPASSWORD);
                        Role = jObj.getString("role_familla");

                        cv_user = new ContentValues();
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERID, userid_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERNAME, username_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSEMAIL, useremail_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSTYPEID, typeid_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSGROUPID, groupid_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSGROUPPASSWORD, grouppassword_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSLOGIN, true);
                        cv_user.put(DatabaseHandler.TAG_Role, Role);

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
                Toast.makeText(getApplicationContext(), "This account is already in use! Please use different EmailID.", Toast.LENGTH_LONG).show();

            } else {
                Intent i = new Intent(Registration.this, Add_memberbycontact.class);
                startActivity(i);
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