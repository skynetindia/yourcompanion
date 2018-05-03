package pkg.android.skynet.yourcompanion.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.activity.MainActivity;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;

/**
 * Created by ST-3 on 07-10-2017.
 */

public class TimerService extends Service {

    private Intent mBroadcastIntent;
    private CountDownTimer countDownTimer;

    private SessionManager sessionManager;
    private YourCompanion yourCompanion;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getExtras() != null) {
            long timer = intent.getLongExtra(Constants.SELECTED_TIME, 0);
            timer = TimeUnit.MINUTES.toMillis(timer);
            countDownTimer = new MyCountDownTimer(timer, 1000);
            countDownTimer.start();

            System.out.println("TIMER -> " + timer);

            sendTimerNotification();
        }

        return Service.START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        sessionManager = new SessionManager(getApplicationContext());
        yourCompanion = (YourCompanion)getApplication();

        sendBroadcast(true);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        sendBroadcast(false);
        yourCompanion.removeNotificationFromId(Constants.TIMER_NOTIFICATION_ID);

        countDownTimer.cancel();
    }


    /**
     * Used to send broadcast to Main Fragment class...
     */
    private void sendBroadcast(boolean isServiceStart) {
        mBroadcastIntent = new Intent();
        mBroadcastIntent.setAction(Constants.TIMER_SERVICE);
        mBroadcastIntent.putExtra("SERVICE_STATUS", isServiceStart);
        sendBroadcast(mBroadcastIntent);
    }


    /**
     * Used to send the emergency notification...
     */
    private void sendEmergencyNotification() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        String msgTitle = "Alarm";
        String msgText = user.get(SessionManager.FIRST_NAME) + " triggered a personal safety alarm";

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.sendEmergencyNotifications(getApplicationContext(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        Toast.makeText(getApplicationContext(), resMessage, Toast.LENGTH_SHORT).show();
                    }else {
                        stopSelf();
                    }
                }
            }, userId, "0", Constants.NOTIFY_TYPE_EMERGENCY, "", msgTitle, msgText, false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long interval) {
            super(millisInFuture, interval);
        }

        @Override
        public void onFinish() {
            startService(new Intent(getApplicationContext(), EmergencyHelpService.class));
            stopSelf();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //System.out.println("CountDownTimer -> " + millisUntilFinished / 1000);
            long second = millisUntilFinished / 1000;

            long sec = second % 60;
            long min = (second / 60)%60;
            long hours = (second/60)/60;

            System.out.println(hours + ":" + min + ":" + sec);
        }
    }


    private void sendTimerNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle("Timer Active")
                .setAutoCancel(false)
                .setContentIntent(pendingIntent);
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(description));

        //notificationBuilder.setDefaults(Notification.DEFAULT_ALL);

        Notification notification;
        notification = notificationBuilder.build();

        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.TIMER_NOTIFICATION_ID, notification);
    }
}
