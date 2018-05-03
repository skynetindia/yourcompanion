package pkg.android.skynet.yourcompanion.dialogfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.adapter.FollowMeSelectFriendAdapter;
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

public class FollowMeDialog extends DialogFragment implements View.OnClickListener {

    private RecyclerView mFriendsRec;
    private TextView mPositiveTxt, mTitleTxt;

    public ArrayList<String> mSelectedContacts = new ArrayList<>();

    private GPSTracker gpsTracker;
    private DatabaseHelper databaseHelper;
    private YourCompanion yourCompanion;
    private SessionManager sessionManager;

    public Listener mListener;

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

        mPositiveTxt = (TextView)view.findViewById(R.id.txt_share_location);
        mPositiveTxt.setText(getString(R.string.txt_FOLLOW_ME));
        mPositiveTxt.setOnClickListener(this);

        mTitleTxt = (TextView)view.findViewById(R.id.txt_title);
        mTitleTxt.setText(getString(R.string.txt_select_follow_me_friends));

        view.findViewById(R.id.txt_cancel).setOnClickListener(this);
    }


    /**
     * Used to set the friends adapter...
     */
    private void setFriendAdapter() {
        FollowMeSelectFriendAdapter adapter = new FollowMeSelectFriendAdapter(FollowMeDialog.this, databaseHelper.getAllFriendsLocal());
        mFriendsRec.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mFriendsRec.setItemAnimator(new DefaultItemAnimator());
        mFriendsRec.setAdapter(adapter);
    }


    /**
     * Used to send the follow me request...
     */
    private void sendFollowMeRequest () {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);

        String selectedFriends = TextUtils.join(",", mSelectedContacts);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.sendFollowMeReq(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }else {
                        getDialog().dismiss();
                        mListener.returnData("1");
                    }
                }
            }, userId, selectedFriends, String.valueOf(gpsTracker.getLatitude()), String.valueOf(gpsTracker.getLongitude()), Constants.NOTIFY_TYPE_FOLLOW_REQ,"0", true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_share_location:
                if (mSelectedContacts.size() > 0) {
                    sendFollowMeRequest();
                }else {
                    yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_select_friends), "Ok");
                }
                break;

            case R.id.txt_cancel:
                getDialog().dismiss();
                break;
        }
    }


    /**
     *  Method is used to set the listener...
     * @param listener
     */
    public void setListener(Listener listener) {
        mListener = listener;
    }



    /**
     *  INTERFACE is used to get the listener when dialog is closed...
     */
    public static interface Listener {
        void returnData(String data);
    }
}
