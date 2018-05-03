package pkg.android.skynet.yourcompanion.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.activity.MainActivity;
import pkg.android.skynet.yourcompanion.app.Constants;

/**
 * Created by ST-3 on 21-11-2017.
 */

public class FollowMeService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendAlarmNotification();

        return START_STICKY;
    }


    /**
     * Used to send the custom notification when alarm is on..
     */
    private void sendAlarmNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(icon)
                .setContentTitle("Follow Me/Alarm Active")
                .setAutoCancel(false)
                .setContentIntent(pendingIntent);
        //.setStyle(new NotificationCompat.BigTextStyle().bigText(description));

        //notificationBuilder.setDefaults(Notification.DEFAULT_ALL);

        Notification notification;
        notification = notificationBuilder.build();

        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(Constants.ALARM_NOTIFICATION_ID, notification);
    }
}
