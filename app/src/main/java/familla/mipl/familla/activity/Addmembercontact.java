package familla.mipl.familla.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import familla.mipl.familla.R;

public class Addmembercontact extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText name, contact;
    String number, fname;
    String memberrelation;
    private Toolbar mToolbar;
    AddMember1 add;
    DatabaseHandler handler;
    SQLiteDatabase db;
    Cursor c_user;
    WebView msg;
    String sendername;
    String[] Relation = {"Father", "Mother", "Husband", "Wife", "Son", "Daughter", "Boy", "Girl", "Grandfather", "Grandmother"};
    int Img[] = {R.drawable.father, R.drawable.mother, R.drawable.husband, R.drawable.wife, R.drawable.boy, R.drawable.girl, R.drawable.boy, R.drawable.girl, R.drawable.grandfather, R.drawable.grandmother};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmembercontact);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setLogo(null);

        ImageView id = (ImageView) mToolbar.findViewById(R.id.fam);
        id.setImageResource(R.drawable.toolbarlogo);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
            //  window.setNavigationBarColor(this.getResources().getColor(R.color.colorbilldata));
        }
        msg = (WebView) findViewById(R.id.sendmem);
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        AddMemberSpiner customAdapter = new AddMemberSpiner(getApplicationContext(), Img, Relation);
        spin.setAdapter(customAdapter);

        contact = (EditText) findViewById(R.id.email);
        name = (EditText) findViewById(R.id.name);

        number = getIntent().getExtras().getString("phone");
        fname = getIntent().getExtras().getString("name");
        number = number.replace(" ", "");
        number = number.replace("+91", "");
        if (number.startsWith("0") == true) {
            number = number.replaceFirst("0", "");
        }

        name.setText(fname);
        if (!number.equals("")) {
            contact.setText(number);
            contact.setEnabled(false);
        }

        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();
        String qry = "select * from userdetails ";
        c_user = db.rawQuery(qry, null);
        c_user.moveToFirst();

        Button submit = (Button) findViewById(R.id.addmem);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userid = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID));
                String groupid = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID));
                //continue to webservice..........................
                sendername = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERNAME));
                add = new Addmembercontact.AddMember1(userid, groupid, number, fname, memberrelation);
                add.execute();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int position, long l) {
        switch (arg0.getId()) {

            case R.id.spinner:
                memberrelation = Relation[position];
                break;
        }
    }

    public class AddMember1 extends AsyncTask<String, Void, Void> {
        String result = null;
        ProgressDialog progressDialog;
        String new_number, userid, groupid, new_name, memberrelation;

        public AddMember1(String userid, String groupid, String number, String new_name, String memberrelation) {
            this.userid = userid;
            this.groupid = groupid;
            this.new_number = number;
            this.new_name = new_name;
            this.memberrelation = memberrelation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Addmembercontact.this);//,R.style.CustomDialogTheme);
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
            nameValuePair.add(new BasicNameValuePair("new_mobileno", new_number));
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

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (result.equals("More than one user")) {
                Toast.makeText(getApplicationContext(), "User already exists with other family ", Toast.LENGTH_LONG).show();
            } else {
                sendmsg();
                Intent i = new Intent(Addmembercontact.this, MainActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Addmembercontact.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void sendmsg() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        String currentDateandTime = sdf.format(new Date());
        String Message = "Hi " + fname + " You have been added to Familla by " + sendername + " To download app Click on below link http://thefamilla.com";
        String strUrl = "http://bulksms.leegansoftwares.com/smsProcess.aspx?smsUsername=familla&smsPassword=familla@123&smsSendto=" + number + "&smsMessage=" + Message + "&smsSender=MIPL&smsCustomer=website&smsMessageType=ALERT&smsTime=" + currentDateandTime;
        msg.loadUrl(strUrl);
    }
}