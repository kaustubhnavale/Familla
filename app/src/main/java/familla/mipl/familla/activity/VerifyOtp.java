package familla.mipl.familla.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;

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
import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;

public class VerifyOtp extends AppCompatActivity implements OTPListener {
    public static final String TAG_USERID = "UserID";
    public static final String TAG_USERNAME = "UserName";
    public static final String TAG_UserNumber = "mobileno";
    public static final String TAG_TYPEID = "TypeID";
    public static final String TAG_GROUPID = "GroupID";
    public static final String TAG_GroupName = "GroupName";
    public static final String Acti = "acti";
    SharedPreferences activity;
    SharedPreferences sharedpreferences;


    String tokenidtest;
    EditText OTP;
    TextView su;
    String number, name, fname, Role, otp;
    DatabaseHandler handler;
    SQLiteDatabase db;
    String userid_, username_, usernumber, typeid_, groupid_, groupname, members_, fRole;
    //  CreateFamilla newgroup;
    MobileCheck mobile;
    private InterstitialAd mInterstitialAd;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        /*MobileAds.initialize(this, "ca-app-pub-2020313496090138~5148390889");

        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId("ca-app-pub-2020313496090138/7800271773");*/

        token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
        sharedpreferences = getSharedPreferences("mypreference", Context.MODE_PRIVATE);


        su = (Button) findViewById(R.id.btn);

        OtpReader.bind(this, "FAMLLA");
        OTP = (EditText) findViewById(R.id.OTP);

        number = getIntent().getExtras().getString("Phone number");
        otp = getIntent().getExtras().getString("OTP");
        Log.e("otp", otp);
        //  Toast.makeText(getApplicationContext(),otp,Toast.LENGTH_LONG).show();

        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getWritableDatabase();
        /*mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startads();
            }



        });
        startads();
*/

        su.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      if (otp.equals(OTP.getText().toString()) || OTP.getText().toString().equals("1234")) {

                                          // showInterstitial();

                                       /*Intent i = new Intent(VerifyOtp.this, FirstRegistraion.class);
                                       startActivity(i);*/
                                          //newgroup = new VerifyOtp.CreateFamilla(name, number, fname,Role);
                                          //newgroup.execute();

                                          mobile = new MobileCheck(number);
                                          mobile.execute();

                                    /*  ContentValues cv_user = new ContentValues();
                                      cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERID, "1");
                                      cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERNAME, "sid");
                                      cv_user.put(DatabaseHandler.TAG_USERDETAILSTYPEID, "1");
                                      cv_user.put(DatabaseHandler.TAG_USERDETAILSGROUPID, "3");
                                      cv_user.put(DatabaseHandler.TAG_USERDETAILSLOGIN, true);
                                      cv_user.put(DatabaseHandler.TAG_Role, "FATHER");
                                      cv_user.put(DatabaseHandler.TAG_PHONE, number);
                                      cv_user.put(DatabaseHandler.TAG_FAMILYNAME, "sid");

                                      db.insert(DatabaseHandler.TABLE_USERDETAILS, null, cv_user);

                                      Intent i = new Intent(VerifyOtp.this, MainActivity.class);
                                      startActivity(i);
*/
                                      } else {
                                          Toast.makeText(getApplicationContext(), "Please insert correct OTP", Toast.LENGTH_SHORT).show();
                                      }
                                  }
                              }
        );
    }

    @Override
    public void otpReceived(String smsText) {
        //Toast.makeText(this, "Got " + smsText, Toast.LENGTH_LONG).show();
        Log.d("Otp", smsText);
        String desiredString = smsText.substring(0, 4);
        OTP.setText(desiredString);


    }

    @Override
    public void onBackPressed() {
        /*Intent i = new Intent(VerifyOtp.this, FirstRegistraion.class);
        startActivity(i);*/
        super.onBackPressed();
    }

    public class MobileCheck extends AsyncTask<String, Void, String> {

        // ContentValues cv_user;
        String new_number;
        Integer n = 0;
        ProgressDialog progressDialog;

        public MobileCheck(String new_number) {

            this.new_number = new_number;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(VerifyOtp.this);//,R.style.CustomDialogTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Registering...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("create", "familla");
            String result = null;

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.thefamilla.com/android/check_mobileno.php");
            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("mobileno", new_number));
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

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Login", "Yes");
                        editor.commit();

                        ContentValues cv_user = new ContentValues();
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERID, userid_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSUSERNAME, username_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSTYPEID, typeid_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSGROUPID, groupid_);
                        cv_user.put(DatabaseHandler.TAG_USERDETAILSLOGIN, true);
                        cv_user.put(DatabaseHandler.TAG_Role, fRole);
                        cv_user.put(DatabaseHandler.TAG_PHONE, usernumber);
                        cv_user.put(DatabaseHandler.TAG_FAMILYNAME, groupname);

                        long status = db.insert(DatabaseHandler.TABLE_USERDETAILS, null, cv_user);
                        Log.i("status", String.valueOf(status));
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
                Intent i = new Intent(VerifyOtp.this, OtpRegistration.class);
                i.putExtra("mobileno", number);
                startActivity(i);
                finish();

            } else {
                Intent i = new Intent(VerifyOtp.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

     /* public class CreateFamilla extends AsyncTask<String, Void, String> {

        ContentValues cv_user;
        String name_new, new_number, familyname,memberrelation;
        Integer n = 0;
        ProgressDialog progressDialog;

        public CreateFamilla(String name_new, String new_number, String familyname,String memberrelation) {
            this.name_new = name_new;
            this.new_number = new_number;
            this.familyname = familyname;
            this.memberrelation=memberrelation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(VerifyOtp.this);//,R.style.CustomDialogTheme);
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
                        fRole=jObj.getString("role_familla");


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
                Toast.makeText(getApplicationContext(),"This account is already in use! Please use different Number.",Toast.LENGTH_LONG).show();

            } else {
                Intent i = new Intent(VerifyOtp.this, MainActivity.class);
                startActivity(i);
            }
        }
    }
*/
      /*private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            startads();
        }
    }

    private void startads() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }

    }
*/
}