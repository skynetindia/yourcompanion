package pkg.android.skynet.yourcompanion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pkg.android.skynet.yourcompanion.activity.IncomingFakeCallActivity;

public class FakeCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentObject = new Intent(context.getApplicationContext(), IncomingFakeCallActivity.class);
        intentObject.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentObject);
    }

}
