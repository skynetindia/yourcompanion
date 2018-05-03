package pkg.android.skynet.yourcompanion.app;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.services.EmergencyHelpService;
import pkg.android.skynet.yourcompanion.services.TimerService;
import pkg.android.skynet.yourcompanion.services.UpdateLocationService;

/**
 * Created by ST-3 on 11-09-2017.
 */

public class YourCompanion extends Application {

    private static String[] MULTIMEDIA_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private SessionManager sessionManager;


    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = new SessionManager(getApplicationContext());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }



    public boolean checkExternalStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }else return true;
    }

    /**
     * Used to check is the network available or not.
     * @return
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Used to check whether edittext is empty or not..
     * @param editText
     * @return
     */
    public boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().equals("");
    }


    /**
     * Used to get the edit text value..
     * @param editText
     * @return
     */
    public String getEditTextValue (EditText editText) {
        return editText.getText().toString().trim();
    }


    /**
     * Used to check the email validation..
     * @param target
     * @return
     */
    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public void showAlertDialog (Activity activity, String message, String button) {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_alert_dialog, null);
        dialogBuilder.setView(dialogView);

        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();

        if (!Constants.isAlertDialogShown) {
            alertDialog.show();
            Constants.isAlertDialogShown = true;
        }

        alertDialog.setCancelable(false);
        alertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        final TextView titleTxt, messageTxt, positiveTxt;
        ImageView alertTypeImg = null;

        messageTxt = (TextView)dialogView.findViewById(R.id.txt_message);
        titleTxt = (TextView)dialogView.findViewById(R.id.txt_title);
        positiveTxt = (TextView)dialogView.findViewById(R.id.txt_positive);

        titleTxt.setVisibility(View.GONE);
        positiveTxt.setText(button);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Constants.isAlertDialogShown = false;
            }
        });

        messageTxt.setText(message);
    }


    public void showAlertDialog (Activity activity, String title, String message, String button) {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_alert_dialog, null);
        dialogBuilder.setView(dialogView);

        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();

        if (!Constants.isAlertDialogShown) {
            alertDialog.show();
            Constants.isAlertDialogShown = true;
        }

        alertDialog.setCancelable(false);
        alertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        final TextView titleTxt, messageTxt, positiveTxt;
        ImageView alertTypeImg = null;

        messageTxt = (TextView)dialogView.findViewById(R.id.txt_message);
        titleTxt = (TextView)dialogView.findViewById(R.id.txt_title);
        positiveTxt = (TextView)dialogView.findViewById(R.id.txt_positive);

        titleTxt.setText(title);
        positiveTxt.setText(button);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Constants.isAlertDialogShown = false;
            }
        });

        messageTxt.setText(message);
    }


    /**
     * Used to confirm logout...
     */
    public void askForLogout(final Activity activity) {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_confirm_dialog, null);
        dialogBuilder.setView(dialogView);

        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();

        alertDialog.setCancelable(false);
        alertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        final TextView titleTxt, messageTxt, positiveTxt, negativeTxt;
        ImageView alertTypeImg = null;

        messageTxt = (TextView)dialogView.findViewById(R.id.txt_message);
        titleTxt = (TextView)dialogView.findViewById(R.id.txt_title);
        positiveTxt = (TextView)dialogView.findViewById(R.id.txt_positive);
        negativeTxt = (TextView)dialogView.findViewById(R.id.txt_negative);

        titleTxt.setText(getString(R.string.txt_please_confirm));
        positiveTxt.setText(R.string.txt_i_am_sure);
        messageTxt.setText(R.string.msg_logout);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceRunning(TimerService.class))
                    stopService(new Intent(getApplicationContext(), TimerService.class));

                if (isServiceRunning(EmergencyHelpService.class))
                    stopService(new Intent(getApplicationContext(), EmergencyHelpService.class));

                if (isServiceRunning(EmergencyHelpService.class))
                    stopService(new Intent(getApplicationContext(), UpdateLocationService.class));

                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                alertDialog.dismiss();
                activity.finish();
                sessionManager.logoutUser();
            }
        });

        negativeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    /**
     * Used to display date in specified format..
     * @param date
     * @return
     */
    public String getDisplayDateFormat (String date) {
        String finalDate = "";

        try {
            Date dt = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            finalDate = new SimpleDateFormat("dd MMM, yyyy").format(dt);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return finalDate;
    }


    /**
     * Used to convert drawable image in to bitmap..
     * @param drawable
     * @return
     */
    public byte[] getDrawableToBlob(Drawable drawable) {
        byte[] blobImage = null;

        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            blobImage = bos.toByteArray();

        }catch (Exception e) {
            e.printStackTrace();
        }

        return blobImage;
    }


    /**
     * Used to remove notification from particular id...
     * @param notificationId
     */
    public void removeNotificationFromId (int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }


    /**
     * Used to convert blob image to bitmap...
     * @param image
     * @return
     */
    public Bitmap getBlobToBitmap (byte[] image) {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeByteArray(image, 0 ,image.length);
        return bitmap;
    }


    /**
     * Used to get current date..
     * @return
     */
    public String getCurrentDate () {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }


    /**
     * Used to get IMEI number..
     */
    /*public String getIMEINumber() {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }*/


    /**
     * Used to check is the GPS is enabled or not..
     * @return
     */
    public boolean isGpsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        return manager.isProviderEnabled( LocationManager.GPS_PROVIDER);
    }


    /**
     * Used to get the country phone code...
     * @return
     */
    public String getCountryPhoneCode() {
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;

    }


    /**
     * Usred to check the camera permissions..
     * @param activity
     * @return
     */
    public static boolean checkCameraPermission(Activity activity) {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int camera = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED ||  camera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, MULTIMEDIA_PERMISSIONS, 1);
            return false;
        }else return true;
    }


    /**
     * Usred to check the camera permissions..
     * @param activity
     * @return
     */
    public static boolean checkStoragePermission(Activity activity) {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, MULTIMEDIA_PERMISSIONS, 1);
            return false;
        }else return true;
    }


    /**
     * Used to check the service is running or not...
     * @param serviceClass
     * @return
     */
    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Used to create directory..
     */
    public void createDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println("CREATEEEEEEEEE");
            File imgDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.IMAGE_PATH);
            File vdoDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.VIDEO_PATH);
            imgDirectory.mkdirs();
            vdoDirectory.mkdirs();
        } else {
            System.out.println("CREATEEEEEEEEE -> Else");
            File myDir = new File(getCacheDir(), File.separator + Constants.IMAGE_PATH);
            myDir.mkdir();
        }
    }


    /**
     * Used to get address from Lat-Long...
     * @param lat
     * @param lng
     * @return
     */
    public String getAddressFromLatLong(double lat, double lng, Activity activity) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            //add = add + "," + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            //add = add + "," + obj.getAdminArea();
            //add = add + "," + obj.getPostalCode();
            //add = add + "," + obj.getSubAdminArea();
            //add = add + "," + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            return add;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
}
