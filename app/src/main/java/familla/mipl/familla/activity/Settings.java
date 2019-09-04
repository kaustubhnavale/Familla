package familla.mipl.familla.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import familla.mipl.familla.R;

public class Settings extends AppCompatActivity {
    DatabaseHandler handler;
    Database handler1;
    SQLiteDatabase db,db1;
    public static final String MyRing = "MyPrefs";
    Ringtone ringTone;
    private Toolbar mToolbar;
    public static String ring;
    public static String status;
    Uri uri;
    Uri uriAlarm, uriNotification, uriRingtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" S E T T I N G S ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        handler = new DatabaseHandler(getApplicationContext());
        handler1=new Database((getApplicationContext()));
        db = handler.getReadableDatabase();
        db1=handler1.getReadableDatabase();
        Typeface tf = Typeface.createFromAsset(getAssets(),"bariol.ttf");


        Button myprofile=(Button)findViewById(R.id.myprofile) ;
        myprofile.setTypeface(tf, Typeface.BOLD);
        myprofile.setOnClickListener(new View.OnClickListener()
                                 {
                                     @Override
                                     public void onClick(View v) {
                                        /*  Intent i = new Intent(Settings.this,SoundSettings.class);
                                          i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                          startActivity(i);*/
                                         Intent i = new Intent(Settings.this,MyProfile.class);
                                         i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                         startActivity(i);
                                     }
                                 }

        );

        Button remove=(Button)findViewById(R.id.remove) ;
        remove.setTypeface(tf, Typeface.BOLD);
        remove.setOnClickListener(new View.OnClickListener()
                                     {
                                         @Override
                                         public void onClick(View v) {
                                             Intent i = new Intent(Settings.this,Removeuser.class);
                                             i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                             startActivity(i);

                                         }
                                     }

        );
        Button removecon=(Button)findViewById(R.id.removecon) ;
        removecon.setTypeface(tf, Typeface.BOLD);
        removecon.setOnClickListener(new View.OnClickListener()
                                  {
                                      @Override
                                      public void onClick(View v) {
                                          Intent i = new Intent(Settings.this,RemoveContact.class);
                                          i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                          startActivity(i);

                                      }
                                  }

        );


        Button sound=(Button)findViewById(R.id.Sound) ;
        sound.setTypeface(tf, Typeface.BOLD);
        sound.setOnClickListener(new View.OnClickListener()
                                  {
                                      @Override
                                      public void onClick(View v) {
                                        /*  Intent i = new Intent(Settings.this,SoundSettings.class);
                                          i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                          startActivity(i);*/
                                          Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                                          startActivityForResult(intent,1);

                                      }
                                  }

        );

        Button clearhistory=(Button)findViewById(R.id.clearhistory) ;
        clearhistory.setTypeface(tf, Typeface.BOLD);
        clearhistory.setOnClickListener(new View.OnClickListener()
                                  {
                                      @Override
                                      public void onClick(View v) {

                                          new AlertDialog.Builder(Settings.this)
                                                  .setTitle("Clear History")
                                                  .setMessage("Are you sure you want to Clear History?")
                                                  .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog, int which) {
                                                          // continue with delete
                                                          db.delete(DatabaseHandler.TABLE_COMMONTASK,"1", null);

                                                          db.delete(DatabaseHandler.TABLE_Groceries,"1", null);

                                                          db1.delete(Database.TABLE_CHAT,"1", null);

                                                          db.delete(DatabaseHandler.TABLE_MEMBERDETAILS,"1", null);

                                                          db.delete(DatabaseHandler.TABLE_CONTACT,"1",null);

                                                          db.delete(DatabaseHandler.TABLE_MEDICINE,"1", null);

                                                          db.delete(DatabaseHandler.TABLE_SHARINGDETAILS,"1", null);


                                                          Toast.makeText(getApplicationContext(),"Clear History Completed",Toast.LENGTH_LONG).show();
                                                          ContentValues cv = new ContentValues();
                                                          cv.put(DatabaseHandler.TAG_LOGIN_STATUS, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH).format(new Date()));
                                                          db.update(DatabaseHandler.TABLE_USERDETAILS,cv,"_id=1", null);
                                                          dialog.dismiss();

                                                      }
                                                  })
                                                  .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog, int which) {
                                                          // do nothing
                                                      }
                                                  })
                                                  .setIcon(android.R.drawable.ic_dialog_alert)
                                                  .show();

                                      }
                                  }
        );





    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK){
            uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            ringTone = RingtoneManager.getRingtone(getApplicationContext(), uri);
            NotificationPublisher.TAG_CUSTOMSOUND = uri.toString();

            SharedPreferences ring;
            ring = getSharedPreferences(MyRing, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = ring.edit();

            editor.putString("ring",uri.toString());

            editor.commit();

            Toast.makeText(getApplicationContext(),"Ringtone has been set",Toast.LENGTH_SHORT).show();
            //RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(),RingtoneManager.TYPE_NOTIFICATION,uri);
           // Toast.makeText(Settings.this,ringTone.getTitle(Settings.this),Toast.LENGTH_LONG).show();
           // Toast.makeText(Settings.this,uri.toString(),Toast.LENGTH_LONG).show();


        }

    }
}
