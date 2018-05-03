package pkg.android.skynet.yourcompanion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pkg.android.skynet.yourcompanion.app.Constants;

/**
 * Created by ST-3 on 03-11-2017.
 */

public class NotificationAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_SHARE_LOCATION)) {

        }
    }
}
