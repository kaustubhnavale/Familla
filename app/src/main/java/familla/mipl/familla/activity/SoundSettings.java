
package familla.mipl.familla.activity;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import familla.mipl.familla.R;

public class SoundSettings extends ActionBarActivity {


    Button optRingTonePicker;
    Button btnStart, btnStop;
    Ringtone ringTone;
    public static String ring;

    Uri uriAlarm, uriNotification, uriRingtone;

    final int RQS_RINGTONEPICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_settings);

        optRingTonePicker = (Button)findViewById(R.id.optPicker);
        btnStart = (Button)findViewById(R.id.start);
        btnStop = (Button)findViewById(R.id.stop);

        uriAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        uriNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        optRingTonePicker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                startRingTonePicker();
            }
        });
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

               // startRingTonePicker();

                Toast.makeText(SoundSettings.this,
                        ringTone.getTitle(SoundSettings.this),
                        Toast.LENGTH_LONG).show();
                ringTone.play();
            }
        });

        btnStop.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if(ringTone!=null){
                    ringTone.stop();
                    ringTone = null;
                }
            }
        });
    }

    private void startRingTonePicker(){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        startActivityForResult(intent, RQS_RINGTONEPICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RQS_RINGTONEPICKER && resultCode == RESULT_OK){
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            ringTone = RingtoneManager.getRingtone(getApplicationContext(), uri);

            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(),RingtoneManager.TYPE_NOTIFICATION,uri);
            Toast.makeText(SoundSettings.this,ringTone.getTitle(SoundSettings.this),Toast.LENGTH_LONG).show();
            ringTone.play();
        }
    }
}

