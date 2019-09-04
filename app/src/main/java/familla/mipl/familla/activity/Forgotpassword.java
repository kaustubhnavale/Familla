package familla.mipl.familla.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Forgotpassword extends AppCompatActivity {
    EditText new_email;
    private Toolbar mToolbar;
    Forgot add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("F O R G O T P A S S W O R D");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);

      /*  LayoutInflater mInflater=LayoutInflater.from(getApplicationContext());
        View mCustomView = mInflater.inflate(R.layout.custom_toolbar_one,null);
        mToolbar.addView(mCustomView);*/

        new_email = (EditText) findViewById(R.id.email);
        Button submit = (Button) findViewById(R.id.sign);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add = new Forgot(new_email.getText().toString());
                add.execute();
            }
        });
    }

    public class Forgot extends AsyncTask<String, Void, Void> {
        String result = null;
        ProgressDialog progressDialog;
        String new_email, userid, groupid, new_name;

        public Forgot(String new_email) {

            this.new_email = new_email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Forgotpassword.this);//,R.style.CustomDialogTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Waiting for server...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://thefamilla.com/android/forgotpassword.php");
            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("reg_email", new_email));

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

            if (Login.status.equals("online")) {
                if (result.trim().equals("Email ID doesnot exist!")) {

                    Toast.makeText(getApplicationContext(), "Please fill proper email id", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Details has been send to your Mail !", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(Forgotpassword.this, Login.class);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                    startActivity(i);
                    finish();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Forgotpassword.this, Login.class);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}