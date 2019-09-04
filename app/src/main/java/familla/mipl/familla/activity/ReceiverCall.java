package familla.mipl.familla.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by android on 1/3/2018.
 */

public class ReceiverCall extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Service Stops", "Ohhhhhhh");
        context.startService(new Intent(context, MyService.class));;
    }
}