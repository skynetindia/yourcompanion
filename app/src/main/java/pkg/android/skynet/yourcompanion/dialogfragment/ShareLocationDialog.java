package pkg.android.skynet.yourcompanion.dialogfragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.adapter.FriendsListAdapter;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.custom.GPSTracker;
import pkg.android.skynet.yourcompanion.helper.DatabaseHelper;

/**
 * Created by ST-3 on 12-10-2017.
 */

public class ShareLocationDialog extends DialogFragment implements View.OnClickListener {

    private RecyclerView mFriendsRec;

    public ArrayList<String> mSelectedContacts = new ArrayList<>();

    private GPSTracker gpsTracker;
    private DatabaseHelper databaseHelper;
    private YourCompanion yourCompanion;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_select_friend, container, false);

        databaseHelper = new DatabaseHelper(getActivity());
        yourCompanion = (YourCompanion)getActivity().getApplication();
        gpsTracker = new GPSTracker(getActivity());
        sessionManager = new SessionManager(getActivity());

        initControls(view);
        setFriendAdapter();

        return view;
    }


    /**
     * Used to initialize all the controls..
     * @param view
     */
    private void initControls(View view) {
        mFriendsRec = (RecyclerView)view.findViewById(R.id.rec_friends);

        view.findViewById(R.id.txt_cancel).setOnClickListener(this);
        view.findViewById(R.id.txt_share_location).setOnClickListener(this);
    }


    /**
     * Used to set the friends adapter...
     */
    private void setFriendAdapter() {
        FriendsListAdapter adapter = new FriendsListAdapter(ShareLocationDialog.this, databaseHelper.getAllFriendsLocal());
        mFriendsRec.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mFriendsRec.setItemAnimator(new DefaultItemAnimator());
        mFriendsRec.setAdapter(adapter);
    }


    /**
     * Used to get address from Lat-Long...
     * @param lat
     * @param lng
     * @return
     */
    public String getAddressFromLatLong(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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



    /**
     * Used to send the emergency notification...
     */
    private void sendEmergencyNotification() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        String selectedFriends = TextUtils.join(",", mSelectedContacts);
        String msgTitle = "Share Location";
        String msgTxt = user.get(SessionManager.FIRST_NAME) + " Shared location with you.";

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.sendEmergencyNotifications(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        Toast.makeText(getActivity(), resMessage, Toast.LENGTH_SHORT).show();
                    }else {
                        yourCompanion.showAlertDialog(getActivity(), getString(R.string.txt_share_location),
                                getString(R.string.msg_share_location_success) + "\n\n" + getAddressFromLatLong(gpsTracker.getLatitude(), gpsTracker.getLongitude()), "DISMISS");
                        getDialog().dismiss();
                    }
                }
            }, userId, "1", Constants.NOTIFY_TYPE_SHARE_LOCATION, selectedFriends, msgTitle, msgTitle,true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to send the emergency notification...
     */
    private void updateLocation() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.updateLocation(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        Toast.makeText(getActivity(), resMessage, Toast.LENGTH_SHORT).show();
                    }else {
                        sendEmergencyNotification();
                    }
                }
            }, userId, String.valueOf(gpsTracker.getLatitude()), String.valueOf(gpsTracker.getLongitude()), false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_share_location:
                if (mSelectedContacts.size() > 0) {
                    updateLocation();
                }else {
                    yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_select_friends), "Ok");
                }
                break;

            case R.id.txt_cancel:
                getDialog().dismiss();
                break;
        }
    }
}
