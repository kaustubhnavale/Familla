package familla.mipl.familla.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import familla.mipl.familla.R;

public class AddMember extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] Relation = {"Father", "Mother", "Husband", "Wife", "Son", "Daughter", "Boy", "Girl", "Grandfather", "Grandmother"};
    int Img[] = {R.drawable.father, R.drawable.mother, R.drawable.husband, R.drawable.wife, R.drawable.boy, R.drawable.girl, R.drawable.boy, R.drawable.girl, R.drawable.grandfather, R.drawable.grandmother};
    TextView skip;
    private Toolbar mToolbar;
    String memberrelation;
    DatabaseHandler handler;
    SQLiteDatabase db;
    EditText new_name, new_email;
    Cursor c_user;
    AddMember1 add;
    Button back;
    String Role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        handler = new DatabaseHandler(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        Typeface tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");
      /*  LayoutInflater mInflater=LayoutInflater.from(getApplicationContext());
        View mCustomView = mInflater.inflate(R.layout.custom_toolbar_one,null);
        mToolbar.addView(mCustomView);*/
        getSupportActionBar().setLogo(null);

        ImageView id = (ImageView) mToolbar.findViewById(R.id.fam);

        id.setImageResource(R.drawable.toolbarlogo);

        db = handler.getReadableDatabase();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
            //  window.setNavigationBarColor(this.getResources().getColor(R.color.colorbilldata));
        }
        String qry = "select * from userdetails ";
        c_user = db.rawQuery(qry, null);
        c_user.moveToFirst();
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        AddMemberSpiner customAdapter = new AddMemberSpiner(getApplicationContext(), Img, Relation);
        spin.setAdapter(customAdapter);

        skip = (TextView) findViewById(R.id.skip);
        skip.setTypeface(tf, Typeface.BOLD);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddMember.this, MainActivity.class);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        new_email = (EditText) findViewById(R.id.email);
        new_email.setTypeface(tf, Typeface.BOLD);
        new_name = (EditText) findViewById(R.id.name);
        new_name.setTypeface(tf, Typeface.BOLD);

        Button submit = (Button) findViewById(R.id.addmem);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailInput = new_email.getText().toString().trim();

                String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

                if (new_email.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter email address", Toast.LENGTH_SHORT).show();
                } else if (new_name.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter Member name", Toast.LENGTH_SHORT).show();
                } else if (!emailInput.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "Please enter valid email address", Toast.LENGTH_SHORT).show();
                } else {
                    String userid = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID));
                    String groupid = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID));
                    //continue to webservice..........................

                    add = new AddMember1(userid, groupid, new_email.getText().toString(), new_name.getText().toString(), memberrelation);
                    add.execute();
                }
            }
        });
    }

    public class AddMember1 extends AsyncTask<String, Void, Void> {
        String result = null;
        ProgressDialog progressDialog;
        String new_email, userid, groupid, new_name, memberrelation;

        public AddMember1(String userid, String groupid, String new_email, String new_name, String memberrelation) {
            this.userid = userid;
            this.groupid = groupid;
            this.new_email = new_email;
            this.new_name = new_name;
            this.memberrelation = memberrelation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddMember.this);//,R.style.CustomDialogTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading..");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.thefamilla.com/android/addmember.php");
            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("userid", userid));
            nameValuePair.add(new BasicNameValuePair("groupid", groupid));
            nameValuePair.add(new BasicNameValuePair("new_name", new_name));
            nameValuePair.add(new BasicNameValuePair("new_email", new_email));
            nameValuePair.add(new BasicNameValuePair("new_role_familla", memberrelation));
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

               /* Log.e("check result", result);

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

                            cv_user = new ContentValues();
                            cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERID, userid_);
                            cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERNAME, username_);
                            cv_user.put(DatabaseHandler.TAG_USERDETAILSEMAIL, useremail_);
                            cv_user.put(DatabaseHandler.TAG_USERDETAILSTYPEID, typeid_);
                            cv_user.put(DatabaseHandler.TAG_USERDETAILSGROUPID, groupid_);
                            cv_user.put(DatabaseHandler.TAG_USERDETAIL SGROUPPASSWORD, grouppassword_);
                            cv_user.put(DatabaseHandler.TAG_USERDETAILSLOGIN, true);

                            db.insert(DatabaseHandler.TABLE_USERDETAILS, null, cv_user);
                        }

                    } catch (JSONException e) {
                        Log.e("log_tag", "Error parsing data " + e.toString());
                        result = "invalid user";

                    }
                    result = "logged in";
                }*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (result.equals("user already exists")) {
                Toast.makeText(getApplicationContext(), "User already exists ", Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent(AddMember.this, MainActivity.class);
                startActivity(i);
            }
        }
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
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AddMember.this, MainActivity.class);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}