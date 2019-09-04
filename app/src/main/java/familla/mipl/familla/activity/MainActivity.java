package familla.mipl.familla.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import familla.mipl.familla.R;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, FragmentChangeListener {
    public Dialog dialog;

    public static final String KEY_USERNAME = "username";
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    String username, role;
    int mem_id;
    Cursor c_user1;
    DatabaseHandler handler;
    SQLiteDatabase db, db_read;
    CreateSession session;
    MyCursorAdapter adapter;
    ConnectionDetector cd;
    public static String status = "";
    public static String ActicityName;
    Typeface tf;
    String userid;
    String a = "";
    ListView memlist1;
    public static String type_of_task;
    public static final String TAG_DATE = "date";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);

        tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");
        sharedpreferences = getSharedPreferences("mypreference", Context.MODE_PRIVATE);

        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getWritableDatabase();
        db_read = handler.getReadableDatabase();

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
            //  window.setNavigationBarColor(this.getResources().getColor(R.color.colorbilldata));
        }

        // set Click listener

        Intent intent = getIntent();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String a = intent.getExtras().getString("check").toString();

            if (a.equals("1")) {
                //showCustomAlert();
            }
        }

        if (getIntent().hasExtra(TAG_DATE)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, new HomeFragment());
            fragmentTransaction.commit();

            // set the toolbar title
            //getSupportActionBar().setTitle(title);
        } else if (getIntent().hasExtra("notificationid")) {
            Integer reminderid = getIntent().getIntExtra("notificationid", 0);
            Log.e("reminderid", reminderid.toString());
            ContentValues cv = new ContentValues();
            cv.put("commontask_remindercancelled", 1); //These Fields should be your String values of actual column names
            Integer c = db.update(DatabaseHandler.TABLE_COMMONTASK, cv, "_id=" + reminderid, null);
            Log.e("checked", c.toString());
        }

        /***CONNECTION CHECK*****************************************/
        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
            status = "online";

        } else {
            status = "offline";
            // onStop();
        }

        if (status.equals("online")) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHandler.TAG_SYNCSTATUSONILNETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            db.update(DatabaseHandler.TABLE_SYNCSTATUS, cv, "_id = 1", null);
        }

        if (status.equals("offline")) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHandler.TAG_SYNCSTATUSOFFLINETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            db.update(DatabaseHandler.TABLE_SYNCSTATUS, cv, "_id = 1", null);
        }
        /***CONNECTION CHECK ENDS HERE*****************************************/

        Cursor c = db_read.rawQuery("select * from userdetails where _id=1 ", null);
        c.moveToFirst();
        userid = c.getString(c.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID));
        role = c.getString(c.getColumnIndex(DatabaseHandler.TAG_Role));
        if (status.equals("online")) {
            session = new CreateSession(userid, "session started");
            session.execute();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

//        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Home");
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colormain));
        }
        mToolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colormain)));
        displayView(0);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = "";
        //  String title = "medha";
        switch (position) {
            case 0:
                fragment = new GridHome();
                title = getString(R.string.nav_item_home);
                type_of_task = "";

                break;

            case 1:
                Intent intent1 = new Intent(MainActivity.this, Add_memberbycontact.class);
                startActivity(intent1);

                title = getString(R.string.nav_item_addmem);
                type_of_task = "";

                break;

            case 2:
                Intent intent2 = new Intent(MainActivity.this, History.class);
                startActivity(intent2);
                title = getString(R.string.nav_item_History);
                type_of_task = "";

                break;
            case 3:
                Intent intent3 = new Intent(MainActivity.this, ShowContact.class);
                startActivity(intent3);
                title = getString(R.string.nav_item_contact);
                type_of_task = "";

                break;

         /*   case 4:
                if(Login.status.equals("offline"))
                {
                    Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_LONG).show();
                }
                else {
                   // CustomDialog();
                   /*//* Dialog dialog=new Dialog(MainActivity.this);

                   *//* Intent intent4 = new Intent(MainActivity.this, Chat_Member.class);
                    startActivity(intent4);
                    title = getString(R.string.nav_item_notifications);
                    type_of_task = "";*//*
                }
                break;*/

            case 4:
                Intent intent4 = new Intent(MainActivity.this, Settings.class);
                startActivity(intent4);
                title = getString(R.string.nav_item_Settings);
                type_of_task = "Settings";

                break;

            case 5:
                //   Toast.makeText(getApplicationContext(),"Hi",Toast.LENGTH_LONG).show();

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                /*ContentValues cv_logout = new ContentValues();
                                cv_logout.put(DatabaseHandler.TAG_USERDETAILSLOGIN, false);
                                db.update(DatabaseHandler.TABLE_USERDETAILS, cv_logout, "_id = 1", null);*/

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.clear();
                                editor.commit();

                                // new CallSyncData(getApplicationContext()).stopDownloading();
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    class CreateSession extends AsyncTask<String, Void, Void> {

        String userid, result, message;

        public CreateSession(String userid, String message) {
            this.userid = userid;
            this.message = message;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /*String check= "select syncstatus_done from syncstatus where _id=1";
            Cursor c_check = db_read.rawQuery(check, null);
            c_check.moveToFirst();
            int i = c_check.getInt(c_check.getColumnIndex(DatabaseHandler.TAG_SYNCSTATUSDONE));
            if(i!=0 && Logon.status.equals("online")){
                Log.e("c_check_session","can sync now");
                SyncData sync = new SyncData(getApplicationContext());
                sync.execute();
            }*/
            SyncData sync = new SyncData(getApplicationContext());
            sync.execute();

            SyncMsg msg = new SyncMsg(getApplicationContext());
            msg.execute();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.thefamilla.com/android/createSession.php");
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("userid", userid));
            nameValuePair.add(new BasicNameValuePair("message", message));

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

            return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        session = new CreateSession(userid, "session stopped");
        session.execute();
    }

    public void onDestroy() {
        Log.e("on", "destroy");
        new CallSyncData(getApplicationContext()).stopDownloading();
        super.onDestroy();
        session = new CreateSession(userid, "session stopped");
        session.execute();
    }

    @Override
    public void onStop() {
        Log.e("on", "stop");
        new CallSyncData(getApplicationContext()).stopDownloading();
        super.onStop();
        session = new CreateSession(userid, "session started");
        session.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        session = new CreateSession(userid, "session started");
        session.execute();
    }

    public void showCustomAlert() {
        Context context = getApplicationContext();
        LayoutInflater inflater = getLayoutInflater();

        View toastRoot = inflater.inflate(R.layout.toast, null);
        TextView tr = (TextView) toastRoot.findViewById(R.id.test);

        if (status.equals("offline")) {
            tr.setText("You are using familla in offline mode");
        } else {
            tr.setText("You are using familla in online mode");
        }

        Toast toast = new Toast(context);

        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void CustomDialog() {

        String quer1 = "select * from memberdetails where md_status=1 ";
        c_user1 = db_read.rawQuery(quer1, null);
        c_user1.moveToFirst();

        dialog = new Dialog(MainActivity.this);
        // it remove the dialog title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set the laytout in the dialog
        dialog.setContentView(R.layout.member_list);
        // set the background partial transparent

        // set the layout at right bottom

        // it dismiss the dialog when click outside the dialog frame
        dialog.setCanceledOnTouchOutside(true);

        // initialize the item of the dialog box, whose id is demo1
        // View demodialog =(View) dialog.findViewById(R.id.cross);

        memlist1 = (ListView) dialog.findViewById(R.id.memlist);
        ImageView imd = (ImageView) dialog.findViewById(R.id.but);

        adapter = new MyCursorAdapter(getApplicationContext(), c_user1);
        memlist1.setAdapter(adapter);

        Window window = dialog.getWindow();

        WindowManager.LayoutParams param = window.getAttributes();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        // dialog.getWindow().setBackgroundDrawable();
        param.gravity = Gravity.CENTER;
        // it call when click on the item whose id is demo1.

        memlist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedFromList = (memlist1.getItemAtPosition(position).toString());
                mem_id = (int) parent.getItemIdAtPosition(position);

                String quer1 = "select * from memberdetails where _id='" + mem_id + "' ";
                c_user1 = db.rawQuery(quer1, null);
                c_user1.moveToFirst();

                //   String Owner=(c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));

                String memberid = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));
                String groupid = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSGROUPID)));

                Intent i = new Intent(MainActivity.this, Chat.class);
                i.putExtra("userid", userid);
                i.putExtra("Memberid", memberid);
                i.putExtra("groupid", groupid);

                startActivity(i);

                //Toast.makeText(getActivity(),String.valueOf(Owner),Toast.LENGTH_LONG).show();
            }

        });

        imd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // diss miss the dialog
                dialog.dismiss();
            }
        });
        // it show the dialog box
        dialog.show();
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
            text.setTextColor(getResources().getColor(R.color.colorPrimary));
            text.setTypeface(tf, Typeface.BOLD);
            text.setText(name);

            ImageView ro = (ImageView) view.findViewById(R.id.img_1);
            if (role.equals("Father")) {
                ro.setImageResource(R.drawable.husband);
            } else if (role.equals("Mother")) {
                ro.setImageResource(R.drawable.wife);
            } else if (role.equals("Husband")) {
                ro.setImageResource(R.drawable.husband);
            } else if (role.equals("Wife")) {
                ro.setImageResource(R.drawable.wife);
            } else if (role.equals("Son")) {
                ro.setImageResource(R.drawable.boy);
            } else if (role.equals("Daughter")) {
                ro.setImageResource(R.drawable.girl);
            } else if (role.equals("Grandfather")) {
                ro.setImageResource(R.drawable.grandfather);
            } else if (role.equals("Grandmother")) {
                ro.setImageResource(R.drawable.grandmother);
            } else {
                ro.setImageResource(R.drawable.husband);
            }
        }
    }

    @Override
    public void onBackPressed() {
        session = new CreateSession(userid, "session stopped");
        session.execute();

        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();

      /*  Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);/*//***Change Here***
         startActivity(intent);
         finish();*/
    }
}