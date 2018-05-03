package pkg.android.skynet.yourcompanion.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.custom.GPSTracker;

/**
 * Created by ST-3 on 07-10-2017.
 */

public class UpdateLocationService extends Service {

    private TimerTask timerTask;
    private Timer timer;

    GPSTracker mGPS;

    private SessionManager sessionManager;
    private YourCompanion yourCompanion;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timer = new Timer();

        timerTask = new TimerTask() {
            public void run() {
                if (yourCompanion.isGpsEnabled()) {
                    updateLocation();
                }
            }
        };

        timer.schedule(timerTask, 1000, 120000);

        return Service.START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mGPS = new GPSTracker(getApplicationContext());

        yourCompanion = (YourCompanion)getApplication();
        sessionManager = new SessionManager(getApplicationContext());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Used to send the emergency notification...
     */
    private void updateLocation() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.updateLocation(getApplicationContext(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        Toast.makeText(getApplicationContext(), resMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }, userId, String.valueOf(mGPS.getLatitude()), String.valueOf(mGPS.getLongitude()), false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
