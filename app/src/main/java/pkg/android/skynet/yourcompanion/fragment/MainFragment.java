package pkg.android.skynet.yourcompanion.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Struct;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.adapter.FollowingMeAdapter;
import pkg.android.skynet.yourcompanion.adapter.HelplineNumberAdapter;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.custom.GPSTracker;
import pkg.android.skynet.yourcompanion.dialogfragment.AlarmActivatedDialog;
import pkg.android.skynet.yourcompanion.dialogfragment.AlarmTriggerDialog;
import pkg.android.skynet.yourcompanion.dialogfragment.FollowMeDialog;
import pkg.android.skynet.yourcompanion.dialogfragment.ShareLocationDialog;
import pkg.android.skynet.yourcompanion.helper.DatabaseHelper;
import pkg.android.skynet.yourcompanion.models.FollowMeData;
import pkg.android.skynet.yourcompanion.models.FriendsData;
import pkg.android.skynet.yourcompanion.models.UpdateFollowLocationData;
import pkg.android.skynet.yourcompanion.models.UserDetails;
import pkg.android.skynet.yourcompanion.services.EmergencyHelpService;
import pkg.android.skynet.yourcompanion.services.FollowMeService;
import pkg.android.skynet.yourcompanion.services.FollowingService;
import pkg.android.skynet.yourcompanion.services.TimerService;
import pkg.android.skynet.yourcompanion.services.UpdateLocationService;

/**
 * Created by ST-3 on 28-09-2017.
 */

public class MainFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener, View.OnClickListener, SurfaceHolder.Callback {

    private RecyclerView mFollowMeRec;
    private MapView mMapView;
    private GoogleMap mMap;
    private ImageView mAddFriendIcImg, mSoundIcImg, mBroadcastIcImg, mZoomInIcImg, mStopFollowMeImg, mPanicImg;
    private RelativeLayout mBottomLayout, mBottomTurnOffLayout, mFollowMeLayout;
    private LinearLayout mFollowingLayout;
    private TextView mTimerTxt, mFollowingUserTxt, mServiceTypeTxt, mTriggeredDateTimeTxt, mFollowingYouTxt, mFollowMeTxt, mPhoneyTxt, mCurrentLocationTxt;

    private AlertDialog mFollowMeReqDialog;

    private TimerTask timerTask;
    private Timer timer;

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private Boolean isTimerStart = false;

    private GPSTracker gpsTracker;
    private YourCompanion yourCompanion;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        yourCompanion = (YourCompanion)getActivity().getApplication();
        sessionManager = new SessionManager(getActivity());
        databaseHelper = new DatabaseHelper(getActivity());
        gpsTracker = new GPSTracker(getActivity());

        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo("pkg.android.skynet.yourcompanion", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initControls(view, savedInstanceState);

        if (getArguments() != null) {
            if (getArguments().getString("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_SHARE_LOCATION)) {
                UserDetails userDetails;
                userDetails = (UserDetails) getArguments().get("DATA");
                sharedLocationDialog(userDetails);

            }else if (getArguments().getString("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FOLLOW_REQ)) {
                UserDetails userDetails;
                userDetails = (UserDetails) getArguments().get("DATA");
                followMeLiveNowDialog(userDetails);
                sessionManager.setFollowMeData(userDetails);
                setFollowingUserData();

            }else if (getArguments().getString("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FOLLOW_STOPPED)) {
                clearFollowMeData();

            }
        }

        return view;
    }


    /**
     * Used to initialize all the controls..
     * @param view
     */
    private void initControls(View view, Bundle bundle) {
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(bundle);
        mMapView.onResume();
        mMapView.getMapAsync(this);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mFollowMeRec = (RecyclerView)view.findViewById(R.id.rec_follow_me);

        mAddFriendIcImg = (ImageView)view.findViewById(R.id.img_add_friend_ic);
        mSoundIcImg = (ImageView)view.findViewById(R.id.img_sound_ic);
        mBroadcastIcImg = (ImageView)view.findViewById(R.id.img_broadcast_ic);
        mZoomInIcImg = (ImageView)view.findViewById(R.id.img_zoom_in_ic);

        mStopFollowMeImg = (ImageView)view.findViewById(R.id.img_stop_follow_me);
        mStopFollowMeImg.setOnClickListener(this);

        mPanicImg = (ImageView)view.findViewById(R.id.img_panic);
        mPanicImg.setOnClickListener(this);

        mBottomLayout = (RelativeLayout)view.findViewById(R.id.rl_bottom_layout);
        mBottomTurnOffLayout = (RelativeLayout)view.findViewById(R.id.rl_bottom_trun_off);
        mFollowMeLayout = (RelativeLayout)view.findViewById(R.id.rl_follow_me);

        mFollowingLayout = (LinearLayout)view.findViewById(R.id.ll_following);

        mTimerTxt = (TextView)view.findViewById(R.id.txt_timer);
        mFollowingUserTxt = (TextView)view.findViewById(R.id.txt_following_username);
        mServiceTypeTxt = (TextView)view.findViewById(R.id.txt_service_type);
        mTriggeredDateTimeTxt = (TextView)view.findViewById(R.id.txt_triggered_date_time);
        mFollowingYouTxt = (TextView)view.findViewById(R.id.txt_following_you);
        mFollowMeTxt = (TextView)view.findViewById(R.id.txt_follow_me);
        mPhoneyTxt = (TextView)view.findViewById(R.id.txt_phoney);
        mCurrentLocationTxt = (TextView)view.findViewById(R.id.txt_current_location);

        view.findViewById(R.id.img_broadcast).setOnClickListener(this);
        view.findViewById(R.id.img_add_friend).setOnClickListener(this);
        view.findViewById(R.id.img_sound).setOnClickListener(this);
        view.findViewById(R.id.img_zoom_in).setOnClickListener(this);
        view.findViewById(R.id.img_broadcast).setOnClickListener(this);
        view.findViewById(R.id.txt_phoney).setOnClickListener(this);
        view.findViewById(R.id.txt_current_location).setOnClickListener(this);
        view.findViewById(R.id.txt_timer).setOnClickListener(this);
        view.findViewById(R.id.txt_follow_me).setOnClickListener(this);
        view.findViewById(R.id.img_turn_off_panic).setOnClickListener(this);
        view.findViewById(R.id.img_stop_following).setOnClickListener(this);
    }


    /**
     * Used to register all the receiver..
     */
    private void registerReceiver() {
        // Emergency service receiver.
        IntentFilter emergencyIntentFilter = new IntentFilter();
        emergencyIntentFilter.addAction(Constants.ACTION_EMERGENCY_SERVICE_START);
        getActivity().registerReceiver(emergencyServiceReceiver, emergencyIntentFilter);


        // Timer service receiver.
        IntentFilter timerIntentFilter = new IntentFilter();
        timerIntentFilter.addAction(Constants.TIMER_SERVICE);
        getActivity().registerReceiver(timerServiceReceiver, timerIntentFilter);

        //Notification Receiver..
        getActivity().registerReceiver(notificationReceiver, new IntentFilter("RECEIVER_NOTIFICATION"));
    }


    /**
     * Used to unregister all the receiver...
     */
    private void unregisterReceiver() {
        getActivity().unregisterReceiver(emergencyServiceReceiver);
        getActivity().unregisterReceiver(timerServiceReceiver);
        getActivity().unregisterReceiver(notificationReceiver);
    }


    /**
     * Used to setup alarm delay for the first time..
     */
    private void setData() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String alramDelay = user.get(SessionManager.ALARM_DELAY);

        if (alramDelay == null) {
            sessionManager.updateAlarmDelay("5 Sec");
        }

        if (user.get(SessionManager.IS_BROADCAST) == null) {
            sessionManager.updateBroadcastData(Constants.BROADCAST_ON);
            getActivity().startService(new Intent(getActivity(), UpdateLocationService.class));

        }else {
            if (user.get(SessionManager.IS_BROADCAST).equals(Constants.BROADCAST_OFF)) {
                mBroadcastIcImg.setImageResource(R.drawable.ic_broadcast_off);
                mBroadcastIcImg.setTag(Constants.BROADCAST_OFF);
            }else {
                mBroadcastIcImg.setImageResource(R.drawable.ic_broadcast_on);
                mBroadcastIcImg.setTag(Constants.BROADCAST_ON);

                if (!yourCompanion.isServiceRunning(UpdateLocationService.class)) {
                    getActivity().startService(new Intent(getActivity(), UpdateLocationService.class));
                }
            }
        }

        // Sete emergency sound data..
        if (user.get(SessionManager.IS_EMERGENCY_SOUND).equals("1")) {
            mSoundIcImg.setImageResource(R.drawable.ic_sound_on);
            mSoundIcImg.setTag(Constants.SOUND_ON);
        }else {
            mSoundIcImg.setImageResource(R.drawable.ic_sound_off);
            mSoundIcImg.setTag(Constants.SOUND_OFF);
        }
    }


    /**
     * Used to set the following me adapter...
     * @param followMeData
     */
    private void followingMeAdatper(List<FollowMeData> followMeData) {
        FollowingMeAdapter adapter = new FollowingMeAdapter(MainFragment.this, followMeData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFollowMeRec.setLayoutManager(linearLayoutManager);
        mFollowMeRec.setItemAnimator(new DefaultItemAnimator());
        mFollowMeRec.setAdapter(adapter);
    }


    /**
     * Used to set the following user data..
     */
    private void setFollowingUserData() {
        HashMap<String, String> userDetails = sessionManager.getUserDetails();

        if (userDetails.get(SessionManager.IS_EMERGENCY) != null && userDetails.get(SessionManager.IS_EMERGENCY).equals("0")) {
            mFollowingUserTxt.setText(userDetails.get(SessionManager.FOLLOWING_NAME));
            mServiceTypeTxt.setText("Follow Me");
            mTriggeredDateTimeTxt.setText(userDetails.get(SessionManager.FOLLOWING_DATE_TIME));

        }else {
            mFollowingUserTxt.setText(userDetails.get(SessionManager.FOLLOWING_NAME));
            mServiceTypeTxt.setText("Emergency Alarm Active");
            mTriggeredDateTimeTxt.setText(userDetails.get(SessionManager.FOLLOWING_DATE_TIME));
        }
    }


    /**
     * Used to clear follow me data when follow me request stoopped...
     */
    private void clearFollowMeData() {
        mFollowingLayout.setVisibility(View.GONE);
        sessionManager.clearFollowMeData();

        setBottomLayoutEnable(); // Enable bottom layout when follow me stopped.
    }


    /**
     * Used to manage layouts depending on service start/stop...
     */
    private void manageLayouts() {
        if (yourCompanion.isServiceRunning(EmergencyHelpService.class)) {
            mBottomTurnOffLayout.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.GONE);
            mFollowingYouTxt.setVisibility(View.GONE);
            mStopFollowMeImg.setVisibility(View.GONE);

            getFollowMeList();

        }else {
            mBottomTurnOffLayout.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.VISIBLE);
            mFollowingYouTxt.setVisibility(View.VISIBLE);
            mStopFollowMeImg.setVisibility(View.VISIBLE);
        }

        if (yourCompanion.isServiceRunning(TimerService.class)) {
            mTimerTxt.setTextColor(Color.parseColor("#8a8a8a"));
            mTimerTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_timer_disabled), null);

        }else {
            mTimerTxt.setTextColor(Color.WHITE);
            mTimerTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_timer), null);
        }

        if (yourCompanion.isServiceRunning(FollowMeService.class)) {
            getFollowMeList();
            setmBottomLayoutDisable(); // Set bottom layout disable.
        }else {
            setBottomLayoutEnable(); // Set bottom layout enable
        }

        if (yourCompanion.isServiceRunning(FollowingService.class)) {
            mFollowingLayout.setVisibility(View.VISIBLE);
            setFollowingUserData();

            setmBottomLayoutDisable(); // Set bottom the layout disable..
        }else {
            setBottomLayoutEnable(); // Set bottom layout enable
        }
    }


    /**
     * Used to set bottom layout enable...
     */
    private void setmBottomLayoutDisable() {
        mTimerTxt.setTextColor(Color.parseColor("#8a8a8a"));
        mTimerTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_timer_disabled), null);
        mTimerTxt.setEnabled(false);

        mFollowMeTxt.setTextColor(Color.parseColor("#8a8a8a"));
        mFollowMeTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_follow_me_disabled), null);
        mFollowMeTxt.setEnabled(false);

        mPhoneyTxt.setTextColor(Color.parseColor("#8a8a8a"));
        mPhoneyTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_phoney_disabled), null);
        mPhoneyTxt.setEnabled(false);

        mCurrentLocationTxt.setTextColor(Color.parseColor("#8a8a8a"));
        mCurrentLocationTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_current_location_disabled), null);
        mCurrentLocationTxt.setEnabled(false);

        mPanicImg.setEnabled(false);
    }


    /**
     * Used to set bottom layout enable...
     */
    private void setBottomLayoutEnable() {
        mTimerTxt.setTextColor(Color.parseColor("#FFFFFF"));
        mTimerTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_timer), null);
        mTimerTxt.setEnabled(true);

        mFollowMeTxt.setTextColor(Color.parseColor("#FFFFFF"));
        mFollowMeTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_follow_me), null);
        mFollowMeTxt.setEnabled(true);

        mPhoneyTxt.setTextColor(Color.parseColor("#FFFFFF"));
        mPhoneyTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_phoney), null);
        mPhoneyTxt.setEnabled(true);

        mCurrentLocationTxt.setTextColor(Color.parseColor("#FFFFFF"));
        mCurrentLocationTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_current_location), null);
        mCurrentLocationTxt.setEnabled(true);

        mPanicImg.setEnabled(true);
    }


    /**
     * Used to add marker to current location..
     */
    private void displayAllMarkers(List<FriendsData> friendsDatas) {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        GPSTracker mGPS = new GPSTracker(getActivity());
        mMap.clear();

        for (int i = 0; i < friendsDatas.size(); i++) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(friendsDatas.get(i).getLatitude()), Double.parseDouble(friendsDatas.get(i).getLongitude()))));
            marker.setTitle(friendsDatas.get(i).getFirstName());

            if (userId.equals(friendsDatas.get(i).getUserId())) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin1));
                marker.setTag(friendsDatas.get(i).getFriendId());

            }else {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin2));
                marker.setTag(friendsDatas.get(i).getUserId());
            }
        }

        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(mGPS.getLatitude(), mGPS.getLongitude())));
        marker.setTitle("Me");
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin1));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()), 13));
    }


    /**
     * Used to ask user for enable GPS..
     */
    private void openGpsDialog() {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
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
        negativeTxt.setVisibility(View.GONE);

        titleTxt.setText(getString(R.string.title_location_seting));
        positiveTxt.setText("Turn on location");
        messageTxt.setText(R.string.msg_location_disable);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                alertDialog.dismiss();
            }
        });

        negativeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    /**
     * Used to confirm user for close timer service...
     */
    private void closeTimerDialog() {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
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

        titleTxt.setText(getString(R.string.title_stop_timer));
        messageTxt.setText(R.string.msg_stop_timer);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().stopService(new Intent(getActivity(), TimerService.class));
                alertDialog.dismiss();
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
     * Used to confirm user for close timer service...
     */
    private void stopFollowMeDialog() {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
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

        titleTxt.setText(getString(R.string.title_stop_follow_me));
        messageTxt.setText(R.string.msg_stop_follow_me);
        positiveTxt.setText(R.string.txt_i_am_sure);
        negativeTxt.setText(R.string.txt_cancel);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopFollowMe();
                alertDialog.dismiss();
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
     * Used to show dialog for shared location..
     * @param userDetails
     */
    @SuppressLint("SetTextI18n")
    private void sharedLocationDialog(UserDetails userDetails) {
        //Remove notification
        yourCompanion.removeNotificationFromId(Constants.SHARE_LOCATION_NOTIFICATION_ID);

        if (userDetails.getMapLink().contains(".")) {

            String[] latLong = userDetails.getMapLink().split("/");
            String[] latLongData = latLong[latLong.length - 1].split(",");
            System.out.println("Longitude=>" + latLongData[1]);
            System.out.println("Latitude=>" + latLongData[0]);

            yourCompanion.showAlertDialog(getActivity(), userDetails.getFirstName() + ": " + userDetails.getHelpMessage(), yourCompanion.getAddressFromLatLong(Double.parseDouble(latLongData[0]), Double.parseDouble(latLongData[1]), getActivity()), "DISMISS");

            System.out.println(userDetails.getFirstName() + ": " + userDetails.getHelpMessage()
                    + "\n\n" + yourCompanion.getAddressFromLatLong(Double.parseDouble(latLongData[0]), Double.parseDouble(latLongData[1]), getActivity()));
        }else {
            yourCompanion.showAlertDialog(getActivity(), userDetails.getFirstName() + ": " + userDetails.getHelpMessage(), userDetails.getFirstName() + ": " + userDetails.getHelpMessage(), "DISMISS");
        }
    }


    /**
     * Used to show dialog for follow me request...
     * @param userDetails
     */
    @SuppressLint("SetTextI18n")
    private void followMeLiveNowDialog(final UserDetails userDetails) {
        //Remove notification
        yourCompanion.removeNotificationFromId(Constants.FOLLOW_ME_NOTIFICATION_ID);

        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_confirm_dialog, null);
        dialogBuilder.setView(dialogView);

        mFollowMeReqDialog = dialogBuilder.create();

        mFollowMeReqDialog.show();

        mFollowMeReqDialog.setCancelable(false);
        mFollowMeReqDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        final TextView titleTxt, messageTxt, positiveTxt, negativeTxt;
        ImageView alertTypeImg = null;

        messageTxt = (TextView)dialogView.findViewById(R.id.txt_message);
        titleTxt = (TextView)dialogView.findViewById(R.id.txt_title);
        positiveTxt = (TextView)dialogView.findViewById(R.id.txt_positive);
        negativeTxt = (TextView)dialogView.findViewById(R.id.txt_negative);

        if (userDetails.getIsEmergency().equals("0")) {
            titleTxt.setText(getString(R.string.title_follow_me_live_now));
            messageTxt.setText(userDetails.getFirstName() + " (" + userDetails.getPhone() + ") " + getString(R.string.msg_follow_me_live_now));

        }else {
            titleTxt.setText("SOS");
            messageTxt.setText(userDetails.getFirstName() + " (" + userDetails.getPhone() + ") " + getString(R.string.msg_triggered_alarm));
        }
        positiveTxt.setText("VIEW");
        negativeTxt.setText(R.string.txt_cancel);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRejectFollowMeReq("1");
                mFollowMeReqDialog.dismiss();
            }
        });

        negativeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFollowMeReqDialog.dismiss();
            }
        });
    }


    /**
     * Used to show dialog for Safely arrived..
     * @param userDetails
     */
    @SuppressLint("SetTextI18n")
    private void safelyArrivedDialog(UserDetails userDetails) {
        //Remove notification
        yourCompanion.removeNotificationFromId(Constants.FOLLOW_ME_NOTIFICATION_ID);

        yourCompanion.showAlertDialog(getActivity(), userDetails.getFirstName(), getString(R.string.msg_arrived_safely), "OK");

        if (mFollowMeReqDialog!= null && mFollowMeReqDialog.isShowing())
            mFollowMeReqDialog.dismiss();

        getActivity().stopService(new Intent(getActivity(), FollowingService.class));

        if (timer != null && isTimerStart) {
            timer.cancel();
            isTimerStart = false;
        }

        mFollowingLayout.setVisibility(View.GONE);

        getFriends();
    }


    /**
     * Used to open the time picker and get the selected time...
     */
    private void showTimePicker() {
        int hour = 0;
        int minute = 30;
        TimePickerDialog mTimePicker = null;

        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                long totalSelectedMinute = (selectedHour * 60) + selectedMinute;
                System.out.println("SELECTED TIME -> "+totalSelectedMinute);

                Intent intent = new Intent(getActivity(), TimerService.class);
                intent.putExtra(Constants.SELECTED_TIME, totalSelectedMinute);
                getActivity().startService(intent);

                AlarmActivatedDialog alarmActivatedDialog = new AlarmActivatedDialog();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.OPEN_FROM, Constants.TIMER_SERVICE);
                alarmActivatedDialog.setArguments(bundle);
                alarmActivatedDialog.show(getActivity().getSupportFragmentManager(), "Alarm Activated");
            }
        }, hour, minute, true);//Yes 24 hour time

        mTimePicker.show();
    }


    /**
     * Used to check overlay permission..
     */
    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);

        } else {
            if (yourCompanion.checkExternalStoragePermission(getActivity())) {
                yourCompanion.createDirectory();
                new AlarmTriggerDialog().show(getActivity().getSupportFragmentManager(), "Alarm Triggered");
            }
        }
    }


    /**
     * Used to get all the friends..
     */
    private void getFriends() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);
        String phoneNumber = user.get(SessionManager.PHONE);

        databaseHelper.deleteAllFriends();

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.getFriends(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {

                        if (!((List)resObj).isEmpty()) {
                            if (((List) resObj).get(0).getClass().equals(FriendsData.class)) {
                                List<FriendsData> friendsDatas = (List) resObj;
                                databaseHelper.saveFriendsLocal(friendsDatas);

                                displayAllMarkers(friendsDatas);
                            }
                        }
                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }
                }
            }, userId, phoneNumber, false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to get all the follow user list...
     */
    private void getFollowMeList() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.getFollowMeList(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {

                        List<FollowMeData> followMeData = (List) resObj;
                        mFollowMeLayout.setVisibility(View.VISIBLE);

                        if (followMeData.get(0).getIsEmergency().equals("0")) {
                            mFollowingYouTxt.setVisibility(View.VISIBLE);
                            mStopFollowMeImg.setVisibility(View.VISIBLE);
                        }

                        followingMeAdatper(followMeData);

                        //UserDetails userDetails = new UserDetails();
                        //userDetails.setFollowId(followMeData.get(0).getFlId());
                        //sessionManager.setFollowMeData(userDetails);

                        Location location = new Location("Service Provider");
                        location.setLatitude(gpsTracker.getLatitude());
                        location.setLongitude(gpsTracker.getLongitude());
                        updateFollowLatLong(location);

                        setmBottomLayoutDisable();
                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }
                }
            }, userId,  false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to accept follow me request..
     */
    private void acceptRejectFollowMeReq(final String status) {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String followId = user.get(SessionManager.FOLLOW_ID);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.acceptFollowMe(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {

                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }else {
                        if (status.equals("1")) {
                            mFollowingLayout.setVisibility(View.VISIBLE);
                            getActivity().startService(new Intent(getActivity(), FollowingService.class));
                            startFollowMeDataTimer();
                            setmBottomLayoutDisable(); // Disable bottom layout when following/Emergency alara active

                        }else {
                            mFollowingLayout.setVisibility(View.GONE);
                            getActivity().stopService(new Intent(getActivity(), FollowingService.class));
                            sessionManager.clearFollowMeData(); // Clear session data.
                            setBottomLayoutEnable(); // Enable bottom layout when stop following.
                            timer.cancel();
                            isTimerStart = false;

                            getFriends();
                        }
                    }
                }
            }, followId, status, Constants.NOTIFY_TYPE_FOLLOW_APPROVED,  true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to stop/cancel the follow me req.
     */
    public void stopFollowMe() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.stopFollowing(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {

                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }else {
                        if (yourCompanion.isServiceRunning(EmergencyHelpService.class)) {
                            getActivity().stopService(new Intent(getActivity(), EmergencyHelpService.class));

                        }else if (yourCompanion.isServiceRunning(FollowMeService.class)) {
                            getActivity().stopService(new Intent(getActivity(), FollowMeService.class));
                        }

                        mFollowMeLayout.setVisibility(View.GONE);
                        getFriends();
                        sessionManager.clearFollowMeData();
                        setBottomLayoutEnable(); // Enable bottom layout.
                    }
                }
            }, userId, Constants.NOTIFY_TYPE_FOLLOW_STOPPED,  true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to update follow LatLong Data..
     */
    private void updateFollowLatLong(Location location) {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);
        final String name = user.get(SessionManager.FIRST_NAME);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.updateFollowMeLatLong(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {
                        List<UpdateFollowLocationData> data = (List)resObj;

                        mMap.clear();

                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(data.get(0).getMoveLat()), Double.parseDouble(data.get(0).getMoveLong()))));
                        marker.setTitle(name);
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin1));

                        PolylineOptions polylineOptions = new PolylineOptions()
                                .add(new LatLng(Double.parseDouble(data.get(0).getStartPointLat()), Double.parseDouble(data.get(0).getStartPointLong())))
                                .add(new LatLng(Double.parseDouble(data.get(0).getMoveLat()), Double.parseDouble(data.get(0).getMoveLong())))
                                .color(Color.RED);

                        mMap.addPolyline(polylineOptions);
                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        //yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }
                }
            }, userId, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),  false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to get the followme data...
     */
    private void getFollowMeDetails() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String followId = user.get(SessionManager.FOLLOW_ID);
        final String name = user.get(SessionManager.FIRST_NAME);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.getFollowMeDetails(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {
                        List<FollowMeData> data = (List)resObj;

                        mMap.clear();

                        if (!data.get(0).getMoveLat().equals("") && !data.get(0).getMoveLong().equals("")) {
                            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(data.get(0).getMoveLat()), Double.parseDouble(data.get(0).getMoveLong()))));
                            marker.setTitle(data.get(0).getFname());
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin2));

                            PolylineOptions polylineOptions = new PolylineOptions()
                                    .add(new LatLng(Double.parseDouble(data.get(0).getStartPointLat()), Double.parseDouble(data.get(0).getStartPointLong())))
                                    .add(new LatLng(Double.parseDouble(data.get(0).getMoveLat()), Double.parseDouble(data.get(0).getMoveLong())))
                                    .color(Color.RED);

                            mMap.addPolyline(polylineOptions);
                        }

                        Marker marker1 = mMap.addMarker(new MarkerOptions().position(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude())));
                        marker1.setTitle(name);
                        marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin1));
                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        //yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }
                }
            }, followId,  false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startFollowMeDataTimer() {
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                getFollowMeDetails();
            }
        };

        timer.schedule(timerTask, 1000, 30000);
        isTimerStart = true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationChangeListener(this);

        // For showing a move to my location button
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }else {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (yourCompanion.isServiceRunning(FollowMeService.class)) {
            updateFollowLatLong(location);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                new AlarmTriggerDialog().show(getActivity().getSupportFragmentManager(), "Alarm Triggered");
            } else {
                Toast.makeText(getActivity(), "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_phoney:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new PhoneyFragment()).addToBackStack(null).commit();
                break;

            case R.id.img_add_friend:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new AddNewFriendFragment()).addToBackStack(null).commit();
                break;

            case R.id.img_zoom_in:
                if (!yourCompanion.isGpsEnabled()) {
                    openGpsDialog();
                }else {
                    GPSTracker mGPS = new GPSTracker(getActivity());
                    mMap.clear();
                    MarkerOptions mp = new MarkerOptions();
                    mp.position(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()));
                    mp.title("my position");
                    mp.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin1));
                    mMap.addMarker(mp);

                    if (mZoomInIcImg.getTag().equals("1")) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()), 16));
                        mZoomInIcImg.setImageResource(R.drawable.ic_zoom_out);
                        mZoomInIcImg.setTag("0");
                    }else {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()), 12));
                        mZoomInIcImg.setImageResource(R.drawable.ic_zoom_in);
                        mZoomInIcImg.setTag("1");
                    }
                }
                break;

            case R.id.txt_current_location:
                if (!yourCompanion.isGpsEnabled()) {
                    openGpsDialog();
                }else {
                    if (databaseHelper.getAllFriendsLocal().size() > 0) {
                        new ShareLocationDialog().show(getActivity().getSupportFragmentManager(), "Select Friend");
                    }else {
                        yourCompanion.showAlertDialog(getActivity(), getString(R.string.txt_sorry), getString(R.string.err_add_friends), "Ok");
                    }
                }
                break;

            case R.id.txt_timer:
                if (databaseHelper.getAllFriendsLocal().size() > 0) {
                    if (yourCompanion.isServiceRunning(TimerService.class)) {
                        closeTimerDialog();
                    } else {
                        showTimePicker();
                    }
                }else {
                    yourCompanion.showAlertDialog(getActivity(), getString(R.string.txt_sorry), getString(R.string.err_add_friends), "Ok");
                }
                break;

            case R.id.txt_follow_me:
                if (databaseHelper.getAllFriendsLocal().size() > 0) {
                    FollowMeDialog followMeDialog = new FollowMeDialog();
                    followMeDialog.setListener(new FollowMeDialog.Listener() {
                        @Override
                        public void returnData(String data) {
                            if (data.equals("1")) {
                                getFollowMeList();
                                getActivity().startService(new Intent(getActivity(), FollowMeService.class));
                            }
                        }
                    });
                    followMeDialog.show(getActivity().getSupportFragmentManager(), "Select Friend");

                }else {
                    yourCompanion.showAlertDialog(getActivity(), getString(R.string.txt_sorry), getString(R.string.err_add_friends), "Ok");
                }
                break;

            case R.id.img_panic:
                if (databaseHelper.getAllFriendsLocal().size() > 0) {
                    checkOverlayPermission();
                }else {
                    yourCompanion.showAlertDialog(getActivity(), getString(R.string.txt_sorry), getString(R.string.err_add_friends), "Ok");
                }
                break;

            case R.id.img_turn_off_panic:
                if (yourCompanion.isServiceRunning(EmergencyHelpService.class)) {
                    stopFollowMe();
                    getActivity().stopService(new Intent(getActivity(), EmergencyHelpService.class));
                }
                break;

            case R.id.img_broadcast:
                HashMap<String, String> userDetails = sessionManager.getUserDetails();

                if (userDetails.get(SessionManager.IS_BROADCAST).equals(Constants.BROADCAST_ON)) {
                    mBroadcastIcImg.setImageResource(R.drawable.ic_broadcast_off);
                    mBroadcastIcImg.setTag(Constants.BROADCAST_OFF);
                    sessionManager.updateBroadcastData(Constants.BROADCAST_OFF);
                    getActivity().stopService(new Intent(getActivity(), UpdateLocationService.class));

                }else {
                    mBroadcastIcImg.setImageResource(R.drawable.ic_broadcast_on);
                    mBroadcastIcImg.setTag(Constants.BROADCAST_ON);
                    sessionManager.updateBroadcastData(Constants.BROADCAST_ON);
                    getActivity().startService(new Intent(getActivity(), UpdateLocationService.class));
                }
                break;

            case R.id.img_sound:
                if (mSoundIcImg.getTag().equals(Constants.SOUND_ON)) {
                    mSoundIcImg.setImageResource(R.drawable.ic_sound_off);
                    mSoundIcImg.setTag(Constants.SOUND_OFF);
                    sessionManager.updateEmergencySound(Constants.SOUND_OFF);

                }else {
                    mSoundIcImg.setImageResource(R.drawable.ic_sound_on);
                    mSoundIcImg.setTag(Constants.SOUND_ON);
                    sessionManager.updateEmergencySound(Constants.SOUND_ON);
                }
                break;

            case R.id.img_stop_follow_me:
                stopFollowMeDialog();
                break;

            case R.id.img_stop_following:
                acceptRejectFollowMeReq("0");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (yourCompanion.isGpsEnabled()) {
            getFriends();
            setData();
        }else {
            openGpsDialog();
        }

        registerReceiver();

        // Check which service is start and manage the layouts.
        manageLayouts();
    }


    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Broadcast receiver for Emergency service...
     */
    BroadcastReceiver emergencyServiceReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                boolean isServiceStart = intent.getExtras().getBoolean("SERVICE_STATUS");

                manageLayouts();
            }
        }
    };


    /**
     * Broadcast receiver for timer service..
     */
    BroadcastReceiver timerServiceReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(getActivity(), "Bluetooth On", Toast.LENGTH_SHORT).show();
                        break;

                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(getActivity(), "Bluetooth Off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };


    /**
     * Used to manage notification...
     */
    BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_SHARE_LOCATION)) {
                UserDetails userDetails;
                userDetails = (UserDetails)intent.getExtras().get("DATA");
                sharedLocationDialog(userDetails);

            }else if (intent.getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FOLLOW_REQ)) {
                UserDetails userDetails;
                userDetails = (UserDetails)intent.getExtras().get("DATA");
                followMeLiveNowDialog(userDetails);
                sessionManager.setFollowMeData(userDetails);
                setFollowingUserData();

            }else if (intent.getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FOLLOW_STOPPED)) {
                UserDetails userDetails;
                userDetails = (UserDetails)intent.getExtras().get("DATA");
                safelyArrivedDialog(userDetails);
                clearFollowMeData();

            }else if (intent.getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FOLLOW_APPROVED)) {
                /*UserDetails userDetails;
                userDetails = (UserDetails)intent.getExtras().get("DATA");
                safelyArrivedDialog(userDetails);*/
                getFollowMeList();
            }
        }
    };


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
