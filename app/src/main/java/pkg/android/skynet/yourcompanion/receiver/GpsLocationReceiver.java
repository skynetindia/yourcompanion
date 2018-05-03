package pkg.android.skynet.yourcompanion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import pkg.android.skynet.yourcompanion.app.Constants;

public class GpsLocationReceiver extends BroadcastReceiver {

    boolean isGpsEnabled;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            Intent i = new Intent("PROVIDERS_CHANGED");
            i.putExtra(Constants.LOCATION_STATUS, isGpsEnabled);
            context.sendBroadcast(i);
        }
    }
}
