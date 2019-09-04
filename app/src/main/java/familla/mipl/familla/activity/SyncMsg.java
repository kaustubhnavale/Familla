package familla.mipl.familla.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by software6 on 07/04/2016.
 */

public class SyncMsg extends AsyncTask<Void, Void, Void> {

    Database h;
    DatabaseHandler hb;
    SQLiteDatabase db_read, db_write,db;
    Context context;
    long inserted_local_id;
    JSONArray jsonArray;
    String member;
    String result, result_dn;
    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
    HttpClient httpClient, httpClient_dn;
    HttpPost httpPost, httpPost_dn;
    String last_download_id;
    String last_sync_time;
    String userid;

    public SyncMsg(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e("delayed", "preexecute");
        h = new Database(context);
        hb= new DatabaseHandler(context);
        db = hb.getReadableDatabase();
        db_read = h.getReadableDatabase();
        db_write = h.getWritableDatabase();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("delayed", "background");
        String select_upmsg = "select _id, senderid, receipient , groupid , content from chat where syncid is null";
        Cursor c = db_read.rawQuery(select_upmsg, null);
        c.moveToFirst();
        int count_upmsg = c.getCount();
        if (count_upmsg > 0) {
            c.moveToFirst();
            do {
                httpClient = new DefaultHttpClient();
                httpPost = new HttpPost("http://thefamilla.com/android/upMessage.php");
                String name=c.getString(c.getColumnIndex(Database.TAG_CONTENT));

                nameValuePair.add(new BasicNameValuePair("content", c.getString(c.getColumnIndex(Database.TAG_CONTENT))));
                nameValuePair.add(new BasicNameValuePair("senderid", c.getString(c.getColumnIndex(Database.TAG_SENDERID))));
                nameValuePair.add(new BasicNameValuePair("groupid", c.getString(c.getColumnIndex(Database.TAG_GROUPID))));
                nameValuePair.add(new BasicNameValuePair("receipient", c.getString(c.getColumnIndex(Database.TAG_RECEIPIENT))));
                nameValuePair.add(new BasicNameValuePair("localid", c.getString(c.getColumnIndex(Database.TAG_CHATID))));
                try {

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    cancel(true);
                }

                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                    result = EntityUtils.toString(entity);
                    Log.e("result_medha", result);
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel(true);
                }
                int count = 0;
                try {
                    jsonArray = new JSONArray(result);
                    count = jsonArray.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                    cancel(true);
                }
                try {
                    for (int i = 0; i < count; i++) {
                        JSONObject jObj = null;

                        jObj = jsonArray.getJSONObject(i);

                        String syncid = jObj.getString("id");
                        String synctime = jObj.getString("timestamp");
                        String localid = jObj.getString("localid");

                        ContentValues cv = new ContentValues();
                        cv.put(Database.TAG_SYNCID, syncid);
                        cv.put(Database.TAG_SYNCTIME, synctime);

                        db_write.update(Database.TABLE_CHAT, cv, "_id = " + localid, null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    cancel(true);
                }

            } while (c.moveToNext());
        }

        nameValuePair.clear();

        String que="select * from userdetails";
        Cursor c_user1 = db.rawQuery(que, null);
        c_user1.moveToFirst();
         userid = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID)));

        Integer count_lastdown = 0;
        String lastdown = "select max(syncid) as max_dn_id , synctime from chat where receipient ="+userid;
        Cursor c_lastdown = db_read.rawQuery(lastdown, null);
        c_lastdown.moveToFirst();
       count_lastdown = c_lastdown.getCount();
        //Log.e("count_lastdown", count_lastdown.toString());
        if(c_lastdown.isNull(c_lastdown.getColumnIndex("max_dn_id"))){
            Log.e("zero","messages");
            last_download_id = "0";
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            //get current date time with Date()
            Date date = new Date();

            last_sync_time = dateFormat.format(date);
        }
        else
        {
            Log.e("number_ok",c_lastdown.getString(c_lastdown.getColumnIndex("max_dn_id")));
            c_lastdown.moveToFirst();
            last_download_id = c_lastdown.getString(c_lastdown.getColumnIndex("max_dn_id"));
            last_sync_time = c_lastdown.getString(c_lastdown.getColumnIndex("synctime"));
        }

        httpClient_dn = new DefaultHttpClient();
        httpPost_dn = new HttpPost("http://thefamilla.com/android/downMessage.php");
        nameValuePair.add(new BasicNameValuePair("lastid", last_download_id));
        nameValuePair.add(new BasicNameValuePair("receipient", userid));
        try {
            httpPost_dn.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            cancel(true);
        }
        try {
            HttpResponse response = httpClient_dn.execute(httpPost_dn);
            HttpEntity entity = response.getEntity();
            result_dn = EntityUtils.toString(entity);
            Log.e("result_medha_dn", result_dn + "  medha");
        } catch (IOException e) {
            e.printStackTrace();
            cancel(true);
        }
        int n = 0;
        JSONArray jArray = null;
        try {

            jArray = new JSONArray(result_dn);
            n = jArray.length();
        } catch (JSONException e) {
            Log.e("connection", "refused");
            cancel(true);
        }

        if (n > 0) {

            try {

                for (int i = 0; i < n; i++) {
                    JSONObject jsonObject = jArray.getJSONObject(i);
                    String dn_content = jsonObject.getString("dn_content");
                    Log.e("dn_content", dn_content);
                    String dn_syncid = jsonObject.getString("dn_syncid");
                    Log.e("dn_syncid", dn_syncid);
                    String dn_senderid = jsonObject.getString("dn_senderid");
                    Log.e("dn_senderid", dn_senderid);
                    String dn_receipient = jsonObject.getString("dn_receipient");
                    Log.e("dn_receipient", dn_receipient);
                    String dn_groupid = jsonObject.getString("dn_groupid");

                    String dn_status = jsonObject.getString("dn_status");

                    Log.e("dn_groupid", dn_groupid);
                    String dn_synctime = jsonObject.getString("dn_synctime");
                    Log.e("dn_synctime", dn_synctime);

                    ContentValues cv_dn = new ContentValues();
                    cv_dn.put(Database.TAG_CONTENT, dn_content);
                    cv_dn.put(Database.TAG_SYNCID, dn_syncid);
                    cv_dn.put(Database.TAG_SENDERID, dn_senderid);
                    cv_dn.put(Database.TAG_RECEIPIENT, dn_receipient);
                    cv_dn.put(Database.TAG_GROUPID, dn_groupid);
                    cv_dn.put(Database.TAG_STATUS, dn_status);
                    cv_dn.put(Database.TAG_SYNCTIME, dn_synctime);

                    db_write.insert(Database.TABLE_CHAT, null, cv_dn);

                   /* final ChatMessage message = new ChatMessage();
                    message.setMessageText(dn_content);
                    message.setUserType(UserType.SELF);
                    message.setMessageTime(new Date().getTime());
                    Chat.chatMessages.add(message);*/

                    scheduleNotification(0,Integer.parseInt(dn_syncid.toString()));
                    //MainActivity.updateAdapter();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                cancel(true);
            }
        }

        httpClient_dn = new DefaultHttpClient();
        httpPost_dn = new HttpPost("http://thefamilla.com/android/deliverMessage.php");
        //nameValuePair.add(new BasicNameValuePair("lastid", last_download_id));
        nameValuePair.add(new BasicNameValuePair("receipient", userid));
        try {
            httpPost_dn.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            HttpResponse response = httpClient_dn.execute(httpPost_dn);
            HttpEntity entity = response.getEntity();
            result_dn = EntityUtils.toString(entity);
            Log.e("result_medha_dn", result_dn + "  medha");
        } catch (IOException e) {
            e.printStackTrace();
            cancel(true);
        }

        String mem="select * from memberdetails";
        Cursor c_use = db.rawQuery(mem, null);
        c_use.moveToFirst();

        int count_u = c_use.getCount();
        if (count_u > 0) {
            c_use.moveToFirst();
            do {
                member=(c_use.getString(c_use.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));
                String messge="select * from chat where receipient='"+member+"' ";
                Cursor c_use1 = db_read.rawQuery(messge, null);
                c_use1.moveToFirst();
                int count_mes = c_use1.getCount();
                if (count_mes > 0) {
                    c_use1.moveToFirst();

                    for(int i=0;i< count_mes;i++){
                        String messageid=(c_use1.getString(c_use1.getColumnIndex(Database.TAG_CHATID)));
                        httpClient_dn = new DefaultHttpClient();
                        httpPost_dn = new HttpPost("http://thefamilla.com/android/checkdeliverstatus.php");
                        //nameValuePair.add(new BasicNameValuePair("lastid", last_download_id));
                        nameValuePair.add(new BasicNameValuePair("receipient", member));
                        nameValuePair.add(new BasicNameValuePair("senderid", userid));
                        nameValuePair.add(new BasicNameValuePair("localid", messageid));
                        c_use1.moveToNext();
                        try {
                            httpPost_dn.setEntity(new UrlEncodedFormEntity(nameValuePair));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            cancel(true);
                        }
                        try {
                            HttpResponse response = httpClient_dn.execute(httpPost_dn);
                            HttpEntity entity = response.getEntity();
                            result_dn = EntityUtils.toString(entity);
                            String sa=result_dn.toString();
                            Log.e("result_data", result_dn + "  result_data");
                            ContentValues ca=new ContentValues();
                            ca.put(Database.TAG_STATUS, sa);

                            db_write.update(Database.TABLE_CHAT, ca ,"_id ='"+messageid+"'", null);

                        } catch (IOException e) {
                            e.printStackTrace();
                            cancel(true);
                        }
                    }
                }
            }

            while (c_use.moveToNext());
        }

        return null;
    }

    public void scheduleNotification(int delay, int uniqueid) {

        Intent notificationIntent = new Intent(context,
                MyNotificationPublisher.class);
        notificationIntent.putExtra("notificationid", uniqueid);
        notificationIntent.putExtra("chat", "chat_message");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueid,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        MyNotificationPublisher.NOTIFICATION_ID = uniqueid;

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.e("delayed", "post execute");
       // Chat.updateAdapter();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new SyncMsg(context).execute();
            }
        }, 20 * 1000);
    }
}