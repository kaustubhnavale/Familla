package familla.mipl.familla.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import familla.mipl.familla.R;

public class FirstRegistraion extends AppCompatActivity {
    public static String status;
    public static String usertype;
    DatabaseHandler handler;
    SQLiteDatabase db, db_read;
    ConnectionDetector cd;
    Cursor c_checkrows;
    EditText Number;
    Button sign;
    String number;
    WebView sms;
    String currentDateandTime, Message, val;
    String tokenidtest;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_registraion);

        sharedpreferences = getSharedPreferences("mypreference", Context.MODE_PRIVATE);

        String token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
        //isAppIsInBackground(getApplicationContext());

        Intent intent = getIntent();
        if (intent.hasExtra("AnotherActivity")) {

            Log.e("Another", "chaphekar");

            Intent i = new Intent(FirstRegistraion.this, Todo.class);
            startActivity(i);

        } else {
            //      Toast.makeText(getApplicationContext(),token,Toast.LENGTH_LONG).show();
            handler = new DatabaseHandler(getApplicationContext());
            db = handler.getWritableDatabase();
            db_read = handler.getReadableDatabase();
            Log.e("logon", "logon");

            val = "" + ((int) (Math.random() * 9000) + 1000);

            Message = val + " is your One Time Password for Familla. Please do not share this with anyone.";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
            currentDateandTime = sdf.format(new Date());

//            **CHECK LOGIN STATUS****************************************
           /* c_checkrows = db_read.rawQuery("select count(*) from userdetails ", null);
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
                    Intent i = new Intent(this, MainActivity.class);//***Change Here***
                    i.putExtra("check", "1");
                    startActivity(i);
                    finish();
                }
            }*/

            if (sharedpreferences.contains("Login")) {
                Intent i = new Intent(this, MainActivity.class);//***Change Here***
                i.putExtra("check", "1");
                startActivity(i);
                finish();
            }
        }

        Number = (EditText) findViewById(R.id.Number);
        sign = (Button) findViewById(R.id.sign);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendotp();
            }

        });

        Number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Do whatever you want here
                    sendotp();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void sendotp() {
        number = Number.getText().toString().trim();
        if (number != "") {
            if (Patterns.PHONE.matcher(number).matches() == true && number.length() == 10) {
                sms = (WebView) findViewById(R.id.sms);
                String strUrl = "http://bulksms.leegansoftwares.com/smsProcess.aspx?smsUsername=familla&smsPassword=familla@123&smsSendto=" + number + "&smsMessage=" + Message + "&smsSender=MIPL&smsCustomer=website&smsMessageType=ALERT&smsTime=" + currentDateandTime;
                sms.loadUrl(strUrl);
                Intent i = new Intent(FirstRegistraion.this, VerifyOtp.class);
                i.putExtra("Phone number", number);
                i.putExtra("OTP", val);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), "Please insert proper phone number", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}