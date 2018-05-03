package pkg.android.skynet.yourcompanion.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.activity.MainActivity;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.custom.GPSTracker;

/**
 * Created by ST-3 on 04-10-2017.
 */

public class EmergencyHelpService extends Service {

    private Intent mBroadcastIntent;
    private MediaPlayer mPlayer;

    private GPSTracker gpsTracker;

    private YourCompanion yourCompanion;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        yourCompanion = (YourCompanion) getApplication();
        sessionManager = new SessionManager(getApplicationContext());
        gpsTracker = new GPSTracker(getApplicationContext());

        // Get data from session and send sms...
        HashMap<String, String> user = sessionManager.getUserDetails();
        if (user.get(SessionManager.PRIMARY_NUMBER) != null) {
            sendSMS(user.get(SessionManager.PRIMARY_NUMBER), user.get(SessionManager.EMERGENCY_MESSAGE) + "\n My Location: \n" + "http://www.google.com/maps/place/" + gpsTracker.getLatitude() + "," + gpsTracker.getLongitude());

            placeCall(user.get(SessionManager.PRIMARY_NUMBER));
        }

        // Start video recording service..
        startService(new Intent(getApplicationContext(), VideoRecordingService.class));

        sendFollowMeRequest();

        return Service.START_STICKY;
    }


    /**
     * Used to play MP3 when emergency alarm trigerred..
     */
    private void playMP3() {
        HashMap<String, String> user = sessionManager.getUserDetails();

        if (user.get(SessionManager.IS_EMERGENCY_SOUND).equals(Constants.SOUND_ON)) {
            mPlayer = MediaPlayer.create(EmergencyHelpService.this, R.raw.siren);
            mPlayer.start();
        }
    }


    /**
     * Used to send broadcast to Main Fragment class...
     */
    private void sendBroadcast(boolean isServiceStart) {
        mBroadcastIntent = new Intent();
        mBroadcastIntent.setAction(Constants.ACTION_EMERGENCY_SERVICE_START);
        mBroadcastIntent.putExtra("SERVICE_STATUS", isServiceStart);
        sendBroadcast(mBroadcastIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(getApplicationContext(), "Emergency Service Stopped", Toast.LENGTH_LONG).show();
        yourCompanion.removeNotificationFromId(Constants.ALARM_NOTIFICATION_ID);
        sendBroadcast(false);

        if (mPlayer != null && mPlayer.isPlaying())
            mPlayer.stop();

        // Stop Videorecording service..
        stopService(new Intent(getApplicationContext(), VideoRecordingService.class));
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


    /**
     * Used to send the follow me request...
     */
    private void sendFollowMeRequest() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.sendFollowMeReq(getApplicationContext(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        Toast.makeText(getApplicationContext(), resMessage, Toast.LENGTH_LONG).show();
                    } else {
                        sendBroadcast(true);
                        playMP3();
                        sendAlarmNotification();
                    }
                }
            }, userId, "", String.valueOf(gpsTracker.getLatitude()), String.valueOf(gpsTracker.getLongitude()), Constants.NOTIFY_TYPE_FOLLOW_REQ, "1", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to send help Message to primary contact.
     * @param phoneNo
     * @param msg
     */
    private void sendSMS(String phoneNo, String msg) {
        try {
            System.out.println("SEND MESSAGE -> " + phoneNo + "->>" + msg);

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


    /**
     * Used to place phone call to primary number...
     * @param phoneNo
     */
    private void placeCall(String phoneNo) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    /**
     * Used to start recording..
     */
    private void startRecording() {

    }

}
