package familla.mipl.familla.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

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
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import familla.mipl.familla.R;
import familla.mipl.familla.model.ChatMessage;
import familla.mipl.familla.model.Status;
import familla.mipl.familla.model.UserType;
import familla.mipl.familla.widget.SizeNotifierRelativeLayout;

public class Chat extends ActionActivity implements SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutListener, NotificationCenter.NotificationCenterDelegate {
    public static ListView chatListView;
    public static String status1;
    public static String chatstatussee = "";
    public static ArrayList<ChatMessage> chatMessages;
    private static ChatListAdapter listAdapter;
    public int count = 0;
    SQLiteDatabase db, dbread;
    Database h;
    String status = "";
    DatabaseHandler hb;
    long inserted_local_id;
    JSONArray jsonArray;
    String result, chatresult;
    JSONArray jarray_m;
    Integer count_member;
    String memname;
    ImageView showon;
    CheckSession session;
    String user_id, memberid = "", groupid, test;
    private Toolbar mToolbar;
    private EditText chatEditText1;
    private ImageView enterChatView1, emojiButton;

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };

    private SizeNotifierRelativeLayout sizeNotifierRelativeLayout;
    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if (v == chatEditText1) {
                    sendMessage(editText.getText().toString(), UserType.OTHER);
                }

                chatEditText1.setText("");
                return true;
            }
            return false;
        }
    };

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ChatMessage message = new ChatMessage();
            if (v == enterChatView1) {
                sendMessage(chatEditText1.getText().toString(), UserType.OTHER);
            }

            chatEditText1.setText("");

            /*if (chatstatussee.equals("online")) {
                message.setMessageStatus(Status.DELIVERED);
            } else {
                message.setMessageStatus(Status.SENT);
            } */
        }
    };

    public static void updateAdapter() {
        if (listAdapter != null)
            listAdapter.notifyDataSetChanged();
        chatListView.setSelection(chatListView.getAdapter().getCount());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        Intent intent = getIntent();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // getSupportActionBar().setCustomView(R.layout.member_details);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        View mCustomView = mInflater.inflate(R.layout.custom_toolbar, null);
        mToolbar.addView(mCustomView);
        showon = (ImageView) mCustomView.findViewById(R.id.dot);
        user_id = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            user_id = intent.getExtras().getString("userid").toString();
            memberid = intent.getExtras().getString("Memberid").toString();
            groupid = intent.getExtras().getString("groupid").toString();
        }
        session = new CheckSession(memberid);
        session.execute();

      /*  Toast.makeText(getApplicationContext(),user_id,Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),memberid,Toast.LENGTH_LONG).show();
*/
        h = new Database(this);
        hb = new DatabaseHandler(this);

        db = h.getWritableDatabase();
        dbread = hb.getReadableDatabase();

        String str = "select * from memberdetails where md_userid ='" + memberid + "'  ";
        Cursor c = dbread.rawQuery(str, null);
        c.moveToFirst();

        memname = (c.getString(c.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));

        getSupportActionBar().setTitle(memname.toUpperCase());

        AndroidUtilities.statusBarHeight = getStatusBarHeight();

        chatMessages = new ArrayList<>();

        chatListView = (ListView) findViewById(R.id.chat_list_view);

        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);

        // Hide the emoji on click of edit text

        showdata();
       /* emojiButton = (ImageView) findViewById(R.id.emojiButton);

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmojiPopup(!showingEmoji);
            }
        });*/

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showdata();

                        session = new CheckSession(memberid);
                        session.execute();
                    }
                });

            }
        }, 0, 30000);

        chatEditText1.setOnKeyListener(keyListener);
        enterChatView1.setOnClickListener(clickListener);
        chatEditText1.addTextChangedListener(watcher1);
        sizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.chat_layout);
        sizeNotifierRelativeLayout.listener = this;

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    }

    public void showonline(String param) {
        String data = param;
        if (param.equals("online")) {
            showon.setImageResource(R.drawable.greendot);
        } else {
            showon.setImageResource(R.drawable.reddot);
        }
    }

    private void sendMessage(final String messageText, final UserType userType) {
        if (messageText.trim().length() == 0)
            return;

        final ChatMessage message = new ChatMessage();
        // message.setMessageStatus(Status.SENT);
        message.setMessageText(messageText);
        message.setUserType(userType);
        message.setMessageTime(new Date().getTime());
        chatMessages.add(message);

        Log.e("medha chaphekar", ((Integer) chatMessages.size()).toString());

        for (ChatMessage medha : chatMessages) {

            String ok = medha.getMessageText();
            Log.e("msg", ok);
        }

        // Mark message as delivered after one second
        ContentValues cv = new ContentValues();
        cv.put("senderid", user_id);
        cv.put("receipient", memberid);
        cv.put("groupid", groupid);
        cv.put("status", 1);
        cv.put("content", messageText);
        inserted_local_id = db.insert("chat", null, cv);
        Log.e("inserted_local_id", String.valueOf(inserted_local_id));

        showdata();
        //updateAdapter();
       /* if (listAdapter != null)
            listAdapter.notifyDataSetChanged();*/
        /*upload = new UploadMessage(messageText, "1", "1", "2", inserted_local_id);
        upload.execute();
*/
    }

    private Activity getActivity() {
        return this;
    }

    public void showdata() {

        if (!chatMessages.isEmpty()) {
            chatMessages.clear();
        }

        String fetch = "select  * from chat where ( senderid='" + user_id + "'  and  receipient='" + memberid + "') or (receipient='" + user_id + "'  and  senderid='" + memberid + "')";
        Log.e("ftech_qry", fetch);
        Cursor c_fetch = db.rawQuery(fetch, null);
        c_fetch.moveToFirst();
        Log.e("countbefore", String.valueOf(count));

        Log.e("countafter", String.valueOf(count));
        if (c_fetch != null) {
            while (c_fetch.moveToNext()) {
                final ChatMessage message = new ChatMessage();

                String status = c_fetch.getString(c_fetch.getColumnIndex(Database.TAG_STATUS));
                if (status.equals("2")) {
                    message.setMessageStatus(Status.DELIVERED);
                } else {
                    message.setMessageStatus(Status.SENT);
                }
                message.setMessageText(c_fetch.getString(c_fetch.getColumnIndex(Database.TAG_CONTENT)));

                String receipient = c_fetch.getString(c_fetch.getColumnIndex(Database.TAG_RECEIPIENT));
                if (receipient.equals(user_id)) {
                    message.setUserType(UserType.SELF);
                } else {
                    message.setUserType(UserType.OTHER);
                }
                message.setMessageTime(new Date().getTime());

                chatMessages.add(message);

                /*if (chatstatussee.equals("online")) {
                    message.setMessageStatus(Status.DELIVERED);
                } else {
                    message.setMessageStatus(Status.SENT);
                }*/
            }
        }
        Log.e("listsize_chat", (String.valueOf(chatMessages.size())));
        listAdapter = new ChatListAdapter(chatMessages, this);
        chatListView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        chatListView.setSelection(chatListView.getAdapter().getCount());
        //  updateAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        chatMessages.clear();
        Intent i = new Intent(Chat.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onSizeChanged(int keyboardHeight) {

    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    class CheckSession extends AsyncTask<String, Void, Void> {

        String memberid1;

        public CheckSession(String memid) {
            this.memberid1 = memid;
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://thefamilla.com/android/chatStatus.php");
            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
            nameValuePair.add(new BasicNameValuePair("recid", memberid1));

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
                Log.e("Hello", entity.toString());
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
            try {
                jarray_m = new JSONArray(result);
                count_member = jarray_m.length();
            } catch (JSONException e) {
                e.printStackTrace();
                cancel(true);
            }
            if (count_member > 0) {

                try {
                    for (int i = 0; i < count_member; i++) {

                        JSONObject jObj = jarray_m.getJSONObject(i);
                        boolean abc = jObj.getString("message").equals(JSONObject.NULL); // should be false

                        if (abc == false) {
                            status1 = jObj.getString("message");
                        } else {
                            status1 = "Session stop";
                        }
                        Log.e("assd", status1);
                    }
                    if (status1.equals("session started")) {

                        //  getSupportActionBar().setTitle(memname +" "+"Online");

                        showonline("online");
                        chatstatussee = "online";
                    } else {

                        showonline("offline");
                        chatstatussee = "offline";
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
            return null;
        }
    }
}