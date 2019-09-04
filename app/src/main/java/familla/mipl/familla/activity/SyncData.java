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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.AlarmManager.INTERVAL_DAY;
import static android.content.Context.ALARM_SERVICE;


public class SyncData extends AsyncTask<Void, Void, String> {
    List<String> myList;
    String[] arr;
    int flag;
    private final static int INTERVAL = 1000 * 30; //2 minutes
    Runnable mHandlerTask;
    Handler mhandler;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy h:mm aa", Locale.ENGLISH);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<HashMap<String, String>> list1 = new ArrayList<HashMap<String, String>>();
    Date convertedDate = new Date();
    Context context;
    DatabaseHandler handler;
    SQLiteDatabase db, db_read;
    Calendar calendar;
    Cursor c_check, c_data;
    String dateform, da, mon, time;
    int c_checkcountforupload;
    HttpClient httpClient, httpClient_dn, httpClient_members;
    HttpPost httpPost, httpPost_members;
    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
    List<NameValuePair> np = new ArrayList<NameValuePair>(2);
    String result, result_member, result_share;
    Integer count_member;
    JSONArray jarray_m;
    int userid;
    String member_userid, member_usererno, member_timestamp, member_typeid, member_groupid, member_username, member_userstatus, member_addedby, Role;
    String data, commontaskextra, user, groupid, addedby;

    public SyncData(Context context) {
        this.context = context;
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        Log.e("in", "preexecute");
        handler = new DatabaseHandler(context);
        db = handler.getWritableDatabase();
        db_read = handler.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHandler.TAG_SYNCSTATUSDONE, 0);
        db.update(DatabaseHandler.TABLE_SYNCSTATUS, cv, "_id=1", null);
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.e("in", "backgroundtask");
        //String members_check = "select max(md_timestamp) as lasttime from memberdetails ";
        String user_check = "select * from userdetails";
        Cursor c_user = db_read.rawQuery(user_check, null);
        c_user.moveToFirst();
        if (c_user.getCount() != 0) {
            user = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID));

            httpClient_members = new DefaultHttpClient();
            httpPost_members = new HttpPost("http://www.thefamilla.com/android/sync_user.php");
            //   nameValuePair.add(new BasicNameValuePair("member_timestamp", c_members.getString(c_members.getColumnIndex("lasttime"))));
            nameValuePair.add(new BasicNameValuePair("userid", c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID))));
            //  nameValuePair.add(new BasicNameValuePair("email", "chaphekar.medha@gmail.com"));
            try {
                Log.e("post", "membertimestamp");
                httpPost_members.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
                cancel(true);
            }
            try {
                HttpResponse response = httpClient_members.execute(httpPost_members);
                HttpEntity entity = response.getEntity();

                result_member = EntityUtils.toString(entity);
                jarray_m = new JSONArray(result_member);

                for (int i = 0; i < jarray_m.length(); i++) {

                    JSONObject jObj = jarray_m.getJSONObject(i);
                    groupid = jObj.getString("GroupID");
                    addedby = jObj.getString("addedBy");

                    String oldgroupid = c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID));

                    if (!groupid.equals(oldgroupid)) {
                        db.delete(DatabaseHandler.TABLE_MEMBERDETAILS, null, null);

                        ContentValues cv_member1 = new ContentValues();
                        // cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERID, member_userid);
                        cv_member1.put(DatabaseHandler.TAG_USERDETAILSGROUPID, groupid);
                        cv_member1.put(DatabaseHandler.TAG_USERADDEDBY, addedby);
                        Integer medha = db.update(DatabaseHandler.TABLE_USERDETAILS, cv_member1, "_id=1", null);
                        Log.e("medha_update", medha.toString());
                    }
                    //update memberdetails
                }

            } catch (ClientProtocolException e) {
                // writing exception to log
                // e.printStackTrace();
                result_member = "connection error";
                cancel(true);

            } catch (HttpHostConnectException e) {
                //  e.printStackTrace();
                result_member = "connection error";
                cancel(true);

            } catch (IOException e) {
                // writing exception to log
                //e.printStackTrace();
                result_member = "connection error";
                cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }


///////////////////////////////////////////////////////////////////////////////////////////////////


            httpClient_members = new DefaultHttpClient();
            httpPost_members = new HttpPost("http://www.thefamilla.com/android/sync_members.php");
            //   nameValuePair.add(new BasicNameValuePair("member_timestamp", c_members.getString(c_members.getColumnIndex("lasttime"))));
            nameValuePair.add(new BasicNameValuePair("member_userid", c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID))));
            nameValuePair.add(new BasicNameValuePair("member_groupid", c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID))));

            //  nameValuePair.add(new BasicNameValuePair("email", "chaphekar.medha@gmail.com"));
            try {
                Log.e("post", "membertimestamp");
                httpPost_members.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
                cancel(true);
            }
            try {
                HttpResponse response = httpClient_members.execute(httpPost_members);
                HttpEntity entity = response.getEntity();

                result_member = EntityUtils.toString(entity);
                Log.e("result_members", result_member);


            } catch (ClientProtocolException e) {
                // writing exception to log
                // e.printStackTrace();
                result_member = "connection error";
                cancel(true);

            } catch (HttpHostConnectException e) {
                //  e.printStackTrace();
                result_member = "connection error";
                cancel(true);

            } catch (IOException e) {
                // writing exception to log
                //e.printStackTrace();
                result_member = "connection error";
                cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }

            try {
                jarray_m = new JSONArray(result_member);
                count_member = jarray_m.length();
            } catch (JSONException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }

            if (count_member > 0) {

                db.delete("memberdetails", null, null);

                try {
                    for (int i = 0; i < count_member; i++) {

                        JSONObject jObj = jarray_m.getJSONObject(i);
                        member_userid = jObj.getString("member_userid");
                        member_usererno = jObj.getString("member_mobileno");
                        member_timestamp = jObj.getString("member_timestamp");
                        member_typeid = jObj.getString("member_typeid");
                        member_groupid = jObj.getString("member_groupid");
                        member_username = jObj.getString("member_username");
                        member_userstatus = jObj.getString("status");
                        member_addedby = jObj.getString("addedBy");
                        Role = jObj.getString("member_role_familla");

                        ContentValues cv_member = new ContentValues();
                        // cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERID, member_userid);
                        cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSEREMAIL, member_usererno);
                        cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME, member_timestamp);
                        cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSTYPEID, member_typeid);
                        cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSGROUPID, member_groupid);
                        cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME, member_username);
                        cv_member.put(DatabaseHandler.TAG_MEMBERSTATUS, member_userstatus);
                        cv_member.put(DatabaseHandler.TAG_MEMBERADDEDBY, member_addedby);
                        cv_member.put(DatabaseHandler.TAG_MemberRole, Role);

                        String check_m = "select * from memberdetails where md_userid =" + member_userid;
                        Cursor c_checkm = db_read.rawQuery(check_m, null);
                        c_checkm.moveToFirst();
                        if (c_checkm.getCount() == 0) {
                            //insert into memberdetails
                            cv_member.put(DatabaseHandler.TAG_MEMBERDETAILSUSERID, member_userid);
                            Long id = db.insert(DatabaseHandler.TABLE_MEMBERDETAILS, null, cv_member);
                            userid = (int) (long) id;
                            Log.e("insertid", id.toString());
                            scheduleMemberNotification(1, userid);
                        } else {
                            Integer medha = db.update(DatabaseHandler.TABLE_MEMBERDETAILS, cv_member, "md_userid=" + member_userid, null);
                            Log.e("medha_update", medha.toString());
                            //update memberdetails
                            userid = medha;
                        }

                    }


                } catch (JSONException e) {
                    Log.e("log_tag", "Error parsing data " + e.toString());
                    result = "invalid user";
                    cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    cancel(true);
                }
            }
            ////////////////////////////////////////////////////////////


            // }
            //String check = "select * from commontask where commontask_syncid is null";
            String check = "select _id as commontask_localid,  commontask_type, commontask_reminderdatetime, commontask_createddatetime, commontask_name, commontask_extranote, commontask_detail, commontask_ownerid, commontask_groupid, commontask_editeddatetime, commontask_setrepeating  from commontask where commontask_syncid is null";
            Log.e("checkstring", check);
            c_check = db_read.rawQuery(check, null);
            c_check.moveToFirst();
            c_checkcountforupload = c_check.getCount();
            if (c_checkcountforupload > 0) {
                c_check.moveToFirst();
                do {
                    JSONObject rowObject = new JSONObject();
                    JSONObject commontask = new JSONObject();
                    int totalColumn = c_check.getColumnCount();
                    Integer local_id = c_check.getInt(c_check.getColumnIndex("commontask_localid"));

                    // if ((!(c_check.moveToFirst()) || c_check.getCount() == 0))
                    for (int i = 0; i < totalColumn; i++) {
                        if (c_check.getColumnName(i) != null) {


                            try {
                                if (c_check.getColumnName(i).equals(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)) {
                                    Log.e("before formatting", c_check.getString(c_check.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
                                    Date date1 = dateFormat.parse(c_check.getString(c_check.getColumnIndex(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME)));
                                    Log.e("date1", date1.toString());
                                    long time = date1.getTime();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(time);
                                    String medha = formatter.format(calendar.getTime());
                                    Log.e("medha-reminderdate", medha.toString());
                                    rowObject.put(c_check.getColumnName(i), medha);

                                } else if (c_check.getString(i) != null) {
                                    Log.d("TAG_NAME", c_check.getString(i));
                                    rowObject.put(c_check.getColumnName(i), c_check.getString(i));

                                } else {

                                    rowObject.put(c_check.getColumnName(i), "");
                                }


                            } catch (Exception e) {
                                Log.d("TAG_NAME", e.getMessage());
                                cancel(true);
                            }
                        }
                    }
                    String query = "Select share_localid,share_userid from trn_sharingdetails where share_localid = '" + local_id.toString() + "' ";
                    c_data = db_read.rawQuery(query, null);
                    int c_checkcount = c_data.getCount();

                   /* for (int j = 0; j < c_checkcount; j++) {

                       temp.put("share_taskid",c_data.getString(c_data.getColumnIndex("share_localid")));
                       Log.e("share_taskid",c_data.getString(c_data.getColumnIndex("share_localid")));
                       temp.put("share_userid",c_data.getString(c_data.getColumnIndex("share_userid")));
                       list1.add(temp);
                      // temp.clear();
                        c_data.moveToNext();
                    }*/

                    if (c_data.moveToFirst()) {
                        do {
                            HashMap<String, String> temp = new HashMap<String, String>();
                            temp.put("share_taskid", c_data.getString(c_data.getColumnIndex("share_localid")));
                            Log.e("share_taskid", c_data.getString(c_data.getColumnIndex("share_localid")));
                            temp.put("share_userid", c_data.getString(c_data.getColumnIndex("share_userid")));
                            Log.e("share_userid", c_data.getString(c_data.getColumnIndex("share_userid")));
                            list1.add(temp);
                            //temp.clear();
                        }
                        while (c_data.moveToNext());
                    }

                    // Log.e("show",temp.toString());
                    List<JSONObject> jsonObj = new ArrayList<JSONObject>();

                    for (HashMap<String, String> data : list1) {
                        JSONObject obj = new JSONObject(data);
                        jsonObj.add(obj);
                    }

                    JSONArray jsArray = new JSONArray(jsonObj);


                    try {

                        rowObject.put("commontask_sharing", jsArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("Test", jsArray.toString());
                    Log.e("commontask_up", rowObject.toString());


                    //   String check1 = "select * from trn_sharingdetails where ";


                    try {
                        commontask.put("commontask", rowObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    httpClient = new DefaultHttpClient();
                    httpPost = new HttpPost("http://www.thefamilla.com/android/sync_upload_new.php");
                    Log.e("commontask_up_string", rowObject.toString());
                    nameValuePair.add(new BasicNameValuePair("commontask", commontask.toString()));

                    //  nameValuePair.add(new BasicNameValuePair("email", "chaphekar.medha@gmail.com"));
                    try {
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                    } catch (UnsupportedEncodingException e) {
                        // writing error to Log
                        e.printStackTrace();
                        cancel(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        cancel(true);

                    }
                    // Making HTTP Request
                    try {
                        HttpResponse response = httpClient.execute(httpPost);
                        HttpEntity entity = response.getEntity();

                        result = EntityUtils.toString(entity);
                        Log.d("result_upload", result);
                        String[] res = result.split(",");

                        ContentValues cva = new ContentValues();
                        cva.put("commontask_syncid", res[0]);
                        cva.put("commontask_syncdatetime", res[1]);
                        db.update(DatabaseHandler.TABLE_COMMONTASK, cva, "_id = " + c_check.getString(c_check.getColumnIndex("commontask_localid")), null);


                    } catch (ClientProtocolException e) {
                        // writing exception to log
                        // e.printStackTrace();
                        result = "connection error";
                        cancel(true);
                        break;

                    } catch (HttpHostConnectException e) {
                        //  e.printStackTrace();
                        result = "connection error";
                        cancel(true);
                        break;

                    } catch (IOException e) {
                        // writing exception to log
                        //e.printStackTrace();
                        result = "connection error";
                        cancel(true);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        cancel(true);
                    }
                } while (c_check.moveToNext());
            }

            //  String lastsynctime = "select max(commontask_syncdatetime) as ok, commontask_groupid from commontask where commontask_ownerid != (select ud_userid from userdetails)";
            String lastsynctime = "select count(*) as othercount, max(commontask_syncdatetime) as ok, commontask_groupid from commontask where commontask_ownerid != (select ud_userid from userdetails)";
            Log.e("q_lastsynctime", lastsynctime);
            Cursor c_lastsyncid = db.rawQuery(lastsynctime, null);
            c_lastsyncid.moveToFirst();
            Log.e("othercount", ((Integer) c_lastsyncid.getInt(c_lastsyncid.getColumnIndex("othercount"))).toString());

            // String lastsyncdatetime = "";

            String str_userid = "select ud_userid , ud_groupid , ud_login_status from userdetails";
            Cursor c_userid = db_read.rawQuery(str_userid, null);
            c_userid.moveToFirst();
            httpClient_dn = new DefaultHttpClient();
            HttpPost httpPost_dn = new HttpPost("http://www.thefamilla.com/android/sync_download_new.php");
            if (c_lastsyncid.getInt(c_lastsyncid.getColumnIndex("othercount")) == 0) {
                Log.e("medha", "called for the first time");
                np.add(new BasicNameValuePair("lastsync", c_userid.getString(c_userid.getColumnIndex("ud_login_status"))));

                //"2016-02-03 10:17:00"
            } else {
                String lastsyncdatetime = c_lastsyncid.getString(c_lastsyncid.getColumnIndex("ok"));
                Log.e("ok", lastsyncdatetime);
                np.add(new BasicNameValuePair("lastsync", lastsyncdatetime));//"2016-02-03 10:17:00"
            }
            np.add(new BasicNameValuePair("groupid", c_userid.getString(c_userid.getColumnIndex(DatabaseHandler.TAG_USERDETAILSGROUPID))));
            np.add(new BasicNameValuePair("ownerid", c_userid.getString(c_userid.getColumnIndex(DatabaseHandler.TAG_USERDETAILSUSERID))));
            try {
                httpPost_dn.setEntity(new UrlEncodedFormEntity(np));
            } catch (UnsupportedEncodingException e) {
                // writingcancel(true); error to Log014
                e.printStackTrace();
                cancel(true);
            }
            // Making HTTP Request
            try {
                HttpResponse response_dn = httpClient_dn.execute(httpPost_dn);
                HttpEntity entity_dn = response_dn.getEntity();

                result = EntityUtils.toString(entity_dn);
                Log.d("result_download", response_dn.toString());
                Log.d("result", result);
                Log.e("medha", "chaphekar");


            } catch (ClientProtocolException e) {
                // writing exception to log
                // e.printStackTrace();
                result = "connection error";
                cancel(true);
                return result;


            } catch (HttpHostConnectException e) {
                //  e.printStackTrace();
                result = "connection error";
                cancel(true);
                return result;
            } catch (IOException e) {
                // writing exception to log
                //e.printStackTrace();
                result = "connection error";
                cancel(true);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }


            int n = 0;
            if (!result.equals("connection error")) {
                JSONArray jArray = null;
                try {

                    jArray = new JSONArray(result);
                    n = jArray.length();
                } catch (JSONException e) {
                    cancel(true);
                    Log.e("connection", "refused");

                } catch (Exception e) {
                    cancel(true);
                    e.printStackTrace();

                }
                ContentValues cv_download;
                if (n == 0) {
                    result = "no downloads";

                    Log.e("result", "no downloads");
                } else {

                    try {
                        for (int i = 0; i < jArray.length(); i++) {

                            JSONObject jObj = jArray.getJSONObject(i);

                            String commontask_syncid = jObj.getString("commontask_syncid");
                            String commontask_type = jObj.getString("commontask_type");
                            String commontask_reminderdatetime = jObj.getString("commontask_reminderdatetime");
                            String commontask_createddatetime = jObj.getString("commontask_createddatetime");
                            String commontask_name = jObj.getString("commontask_name");
                            String commontask_extranote = jObj.getString("commontask_extranote");
                            commontaskextra = commontask_extranote;
                            String commontask_detail = jObj.getString("commontask_detail");
                            String commontask_ownerid = jObj.getString("commontask_ownerid");
                            String commontask_groupid = jObj.getString("commontask_groupid");
                            String commontask_syncdatetime = jObj.getString("commontask_syncdatetime");
                            String commontask_editeddatetime = jObj.getString("commontask_editeddatetime");
                            String commontask_setrepeating = jObj.getString("commontask_setrepeating");


                            httpClient_dn = new DefaultHttpClient();
                            httpPost_dn = new HttpPost("http://thefamilla.com/android/getsharetask.php");
                            //nameValuePair.add(new BasicNameValuePair("lastid", last_download_id));
                            nameValuePair.add(new BasicNameValuePair("share_userid", commontask_ownerid));
                            nameValuePair.add(new BasicNameValuePair("share_syncid", commontask_syncid));

                            try {
                                httpPost_dn.setEntity(new UrlEncodedFormEntity(nameValuePair));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                cancel(true);
                            }
                            try {
                                HttpResponse response = httpClient_dn.execute(httpPost_dn);
                                HttpEntity entity = response.getEntity();
                                result_share = EntityUtils.toString(entity);

                                JSONArray jArray1 = null;
                                try {

                                    jArray1 = new JSONArray(result_share);
                                    flag = jArray1.length();
                                } catch (JSONException e) {
                                    cancel(true);
                                    Log.e("connection", "refused");

                                } catch (Exception e) {
                                    cancel(true);
                                    e.printStackTrace();

                                }
                                if (flag > 0) {
                                    for (int j = 0; j < jArray1.length(); j++) {

                                        JSONObject jObj1 = jArray1.getJSONObject(j);

                                        String shareuserid = jObj1.getString("share_userid");
                                        String sharesynid = jObj1.getString("share_syncid");

                                        ContentValues cv_share = new ContentValues();
                                        cv_share.put(DatabaseHandler.TAG_SHARELOCALID, sharesynid);
                                        cv_share.put(DatabaseHandler.TAG_SHAREUSERID, shareuserid);

                                        long thisid = db.insert(DatabaseHandler.TABLE_SHARINGDETAILS, null, cv_share);


                                    }
                                }



                             /*   result_dn = EntityUtils.toString(entity);
                                String sa = result_dn.toString();
                                Log.e("result_data", result_dn + "  result_data");
                                ContentValues ca = new ContentValues();
                                ca.put(Database.TAG_STATUS, sa);

                                db_write.update(Database.TABLE_CHAT, ca, "_id ='" + messageid + "'", null);*/


                            } catch (IOException e) {
                                e.printStackTrace();
                                cancel(true);
                            }


                            Date date5 = formatter1.parse(commontask_reminderdatetime);


                            String intMonth = (String) android.text.format.DateFormat.format("M", date5);
                            String intday = (String) android.text.format.DateFormat.format("d", date5);
                            String intyear = (String) android.text.format.DateFormat.format("yyyy", date5);
                            String inthours = (String) android.text.format.DateFormat.format("hh", date5);
                            String inthours1 = (String) android.text.format.DateFormat.format("HH", date5);

                            String intmin = (String) android.text.format.DateFormat.format("mm", date5);
                            String intam = (String) android.text.format.DateFormat.format("a", date5);

                            dateform = intday + "." + intMonth + "." + intyear + " " + inthours + ":" + intmin + " " + intam;

                            if (intMonth.length() == 1) {
                                mon = "0" + intMonth;
                            }

                            da = intyear + "-" + mon + "-" + intday + " " + inthours1 + ":" + intmin;

                            time = inthours + ":" + intmin;

                            String check_syncid = "select * from commontask where commontask_syncid = " + commontask_syncid;
                            Cursor c_checksyncid = db_read.rawQuery(check_syncid, null);

                            if (c_checksyncid.getCount() == 0) {
                                cv_download = new ContentValues();
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKSYNCID, commontask_syncid);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKTYPE, commontask_type);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME, dateform);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKCREATEDDATETIME, commontask_createddatetime);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKNAME, commontask_name);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKEXTRANOTE, commontask_extranote);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKDETAIL, commontask_detail);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKOWNERID, commontask_ownerid);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKGROUPID, commontask_groupid);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKSYNCDATETIME, commontask_syncdatetime);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKEDITEDDATETIME, commontask_editeddatetime);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKSETREPEATING, commontask_setrepeating);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKREMINDER, da);


                                long thisid = db.insert(DatabaseHandler.TABLE_COMMONTASK, null, cv_download);
                                Long value = null;
                                int val;
                                if (commontask_setrepeating.equals("JUST ONCE")) {

                                    val = 0;
                                    Calendar c = Calendar.getInstance();
                                    System.out.println("Current time => " + c.getTime());
                                    Log.v("time in millisecond", "" + c.getTime());
                                    Log.v("time in millisecond", "" + c.getTimeInMillis());
                                    Date rem_datetime = null;

                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    try {
                                        Log.e("selecteddate", commontask_reminderdatetime);
                                        rem_datetime = formatter.parse(commontask_reminderdatetime);
                                        Date thisdate = new Date();
                                        String thid = formatter.format(thisdate);
                                        Log.e("this", thid);
                                        Log.e("date", rem_datetime.toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Log.e("err", e.toString());
                                        cancel(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        cancel(true);
                                    }


                                    value = rem_datetime.getTime();
                                    val = (int) (value - c.getTimeInMillis());

                                    Log.e("values difference", "" + val);
                    /*Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, val / 1000);*/

                                    scheduleNotification(val, (int) thisid);


                                } else if (commontask_setrepeating.equals("OTHER")) {
                                    myList = new ArrayList<String>(Arrays.asList(commontaskextra.split(",")));
                                    arr = myList.toArray(new String[myList.size()]);
                                    showother((int) thisid);
                                    //  scheduleNotificationRepeat(val, del, (int) thisid);

                                } else if (commontask_setrepeating.equals("REPEAT WEEKLY")) {
                                    val = 0;
                                    int del = 604800000;
                                    Calendar c = Calendar.getInstance();
                                    System.out.println("Current time => " + c.getTime());
                                    Log.v("time in millisecond", "" + c.getTime());
                                    Log.v("time in millisecond", "" + c.getTimeInMillis());
                                    Date rem_datetime = null;

                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    try {
                                        Log.e("selecteddate", commontask_reminderdatetime);
                                        rem_datetime = formatter.parse(commontask_reminderdatetime);
                                        Date thisdate = new Date();
                                        String thid = formatter.format(thisdate);
                                        Log.e("this", thid);
                                        Log.e("date", rem_datetime.toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Log.e("err", e.toString());
                                        cancel(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        cancel(true);
                                    }
                                    value = rem_datetime.getTime();
                                    val = (int) (value - c.getTimeInMillis());
                                    Log.e("values difference", "" + val);
                                    scheduleNotificationRepeat(val, del, (int) thisid);

                                } else if (commontask_setrepeating.equals("REPEAT MONTHLY")) {

                                    val = 0;
                                    //int del = 604800000;

                                    Calendar c = Calendar.getInstance();
                                    System.out.println("Current time => " + c.getTime());
                                    Log.v("time in millisecond", "" + c.getTime());
                                    Log.v("time in millisecond", "" + c.getTimeInMillis());
                                    c.add(Calendar.MONTH, 1);
                                    long interval = c.getTimeInMillis();

                                    Date rem_datetime = null;

                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    try {
                                        Log.e("selecteddate", commontask_reminderdatetime);
                                        rem_datetime = formatter.parse(commontask_reminderdatetime);
                                        Date thisdate = new Date();
                                        String thid = formatter.format(thisdate);
                                        Log.e("this", thid);
                                        Log.e("date", rem_datetime.toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Log.e("err", e.toString());
                                        cancel(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        cancel(true);
                                    }
                                    value = rem_datetime.getTime();
                                    val = (int) (value - c.getTimeInMillis());
                                    Log.e("values difference", "" + val);
                                    scheduleNotificationRepeat(val, interval, (int) thisid);

                                } else if (commontask_setrepeating.equals("REPEAT YEARLY")) {
                                    Calendar c = Calendar.getInstance();
                                    val = (int) c.getTimeInMillis();
                                    int del = 2;
                                    scheduleNotificationRepeat(val, del, (int) thisid);
                                    //showother((int) thisid);
                                } else {
                                    Calendar c = Calendar.getInstance();
                                    int del = 1;
                                    System.out.println("Current time => " + c.getTime());
                                    Log.v("time in millisecond", "" + c.getTime());
                                    Log.v("time in millisecond", "" + c.getTimeInMillis());
                                    Date rem_datetime = null;

                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    try {
                                        Log.e("selecteddate", commontask_reminderdatetime);
                                        rem_datetime = formatter.parse(commontask_reminderdatetime);
                                        Date thisdate = new Date();
                                        String thid = formatter.format(thisdate);
                                        Log.e("this", thid);
                                        Log.e("date", rem_datetime.toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Log.e("err", e.toString());
                                        cancel(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        cancel(true);
                                    }
                                    value = rem_datetime.getTime();
                                    val = (int) (value - c.getTimeInMillis());
                                    Log.e("values difference", "" + val);
                                    scheduleNotificationRepeat(val, del, (int) thisid);
                                }


                            }
                            if (c_checksyncid.getCount() == 1) {

                                cv_download = new ContentValues();

                                cv_download.put(DatabaseHandler.TAG_COMMONTASKTYPE, commontask_type);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKREMINDERDATETIME, dateform);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKCREATEDDATETIME, commontask_createddatetime);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKNAME, commontask_name);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKEXTRANOTE, commontask_extranote);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKDETAIL, commontask_detail);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKOWNERID, commontask_ownerid);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKGROUPID, commontask_groupid);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKSYNCDATETIME, commontask_syncdatetime);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKEDITEDDATETIME, commontask_editeddatetime);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKSETREPEATING, commontask_setrepeating);
                                cv_download.put(DatabaseHandler.TAG_COMMONTASKREMINDER, da);
                                db.update(DatabaseHandler.TABLE_COMMONTASK, cv_download, "commontask_syncid =" + commontask_syncid, null);

                            }

                        }

                    } catch (JSONException e) {
                        Log.e("log_tag", "Error parsing data " + e.toString());
                        result = "connection error";
                        cancel(true);

                    } catch (ParseException e) {
                        e.printStackTrace();
                        cancel(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        cancel(true);
                    }
                    //result = "download success";


                    //String str_members = "select ";

                }
            }
        }
        return null;
    }

    public void showother(int uniqueid) {


        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra("EVENT_ALERT_DAYS", arr);
        intent.putExtra("notificationid", uniqueid);
        //intent.putExtra("EVENT_ALERT_TIME", );


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueid,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);

        SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyy-M-d H:m", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar();
        Date today = new Date();
        calendar.setTime(today);

        String scheduledStartDate = calendar.get(Calendar.YEAR) + "-" +
                (calendar.get(Calendar.MONTH) + 1) + "-" +
                calendar.get(Calendar.DAY_OF_MONTH) + " " +
                time;


        try {
            Date startDate = dayFormatter.parse(scheduledStartDate);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, startDate.getTime(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } catch (java.text.ParseException pEx) {
            Log.e("err", pEx.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("in", "postexecute");
        // Log.e("result",s);
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHandler.TAG_SYNCSTATUSDONE, 2);
        db.update(DatabaseHandler.TABLE_SYNCSTATUS, cv, "_id=1", null);

        CallSyncData callnow = new CallSyncData(context);
        callnow.CallDelay();
       /* mHandlerTask =  new Runnable() {
            @Override
            public void run() {
                Log.e("syncdata","i am ready to run again");
                mhandler.postDelayed(mHandlerTask, INTERVAL);
            }
        };*/
    }

    /* public void showother(int uniqueid) {

         String[] arrayOfStrings = selectday.toArray(new String[selectday.size()]);
         Intent intent = new Intent(Wake_up.this, NotificationPublisher.class);
         intent.putExtra("EVENT_ALERT_DAYS", arrayOfStrings);
         intent.putExtra("notificationid", uniqueid);
         //intent.putExtra("EVENT_ALERT_TIME", );


         PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), uniqueid,
                 intent, PendingIntent.FLAG_UPDATE_CURRENT);
         AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

         SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyy-M-d H:m");
         Calendar calendar = new GregorianCalendar();
         Date today = new Date();
         calendar.setTime(today);

         String scheduledStartDate = calendar.get(Calendar.YEAR) + "-" +
                 (calendar.get(Calendar.MONTH) + 1) + "-" +
                 calendar.get(Calendar.DAY_OF_MONTH) + " " +
                 storetime;


         try {
             Date startDate = dayFormatter.parse(scheduledStartDate);


             alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, startDate.getTime(), AlarmManager.INTERVAL_DAY, pendingIntent);
         } catch (java.text.ParseException pEx) {
             Log.e("err", pEx.getMessage());
         }
     }
 */
    public void scheduleNotificationRepeat(int delay, long repeat, int uniqueid) {

        Intent notificationIntent = new Intent(context,
                NotificationPublisher.class);
        notificationIntent.putExtra("notificationid", uniqueid);
        // notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID,
        // 1);
        // notificationIntent.putExtra(NotificationPublisher.NOTIFICATION,
        // notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueid,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationPublisher.NOTIFICATION_ID = uniqueid;

        long futureInMillis = SystemClock.elapsedRealtime() + delay;

        if (repeat == 1) {
            AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, INTERVAL_DAY, pendingIntent);
            Log.e("siddesh", String.valueOf(repeat));
        } else if (repeat == 2) {
            calendar = Calendar.getInstance();
            AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);
            GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
            int ye = cal.get(GregorianCalendar.YEAR);

            if (cal.isLeapYear(cal.get(GregorianCalendar.YEAR))) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 366, pendingIntent);
            } else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 365, pendingIntent);
            }
        } else {
            AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, repeat, pendingIntent);
            Log.e("siddesh", String.valueOf(repeat));
        }
    }

    public void scheduleNotification(/* Notification notification, */int delay, int uniqueid) {

        Intent notificationIntent = new Intent(context,
                NotificationPublisher.class);
        notificationIntent.putExtra("notificationid", uniqueid);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueid,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationPublisher.NOTIFICATION_ID = uniqueid;

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public void scheduleMemberNotification(/* Notification notification, */int delay, int uniqueid) {

        Intent notificationIntent = new Intent(context,
                MyNotificationPublisher.class);
        notificationIntent.putExtra("notificationid", uniqueid);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueid,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationPublisher.NOTIFICATION_ID = uniqueid;

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}