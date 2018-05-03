package pkg.android.skynet.yourcompanion.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import libs.expandableheightgridview.ExpandableHeightGridView;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.adapter.RequestReceivedListAdapter;
import pkg.android.skynet.yourcompanion.adapter.MyFriendsListAdapter;
import pkg.android.skynet.yourcompanion.adapter.RequestSendListAdapter;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.models.FriendsData;
import pkg.android.skynet.yourcompanion.models.RequestReceivedData;
import pkg.android.skynet.yourcompanion.models.RequestSentData;
import pkg.android.skynet.yourcompanion.models.UserDetails;

/**
 * Created by ST-3 on 06-10-2017.
 */

public class MyFriendsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mMyFriendsRec;
    private SwipeRefreshLayout mSwRefresh;
    private ExpandableHeightGridView mMyFriendList, mFriendRequestList, mRequestSendList;
    private TextView mNoNewRequestTxt, mNoFriendTxt, mNoInvitedTxt;

    private String primaryContactId = "";

    private SessionManager sessionManager;
    private YourCompanion yourCompanion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);

        yourCompanion = (YourCompanion)getActivity().getApplication();
        sessionManager = new SessionManager(getActivity());

        initControls(view);
        getFriends(true);

        return view;
    }


    /**
     * Used to initialize all the contorls..
     * @param view
     */
    private void initControls(View view) {
        mMyFriendsRec = (RecyclerView)view.findViewById(R.id.rec_my_friends);

        mSwRefresh = (SwipeRefreshLayout)view.findViewById(R.id.sw_refresh);
        mSwRefresh.setOnRefreshListener(this);

        mMyFriendList = (ExpandableHeightGridView)view.findViewById(R.id.list_my_friends);
        mMyFriendList.setExpanded(true);

        mFriendRequestList = (ExpandableHeightGridView)view.findViewById(R.id.list_friend_requests);
        mFriendRequestList.setExpanded(true);

        mRequestSendList = (ExpandableHeightGridView)view.findViewById(R.id.list_request_sent);
        mRequestSendList.setExpanded(true);

        mNoNewRequestTxt = (TextView)view.findViewById(R.id.txt_no_new_request);
        mNoFriendTxt = (TextView)view.findViewById(R.id.txt_no_friends);
        mNoInvitedTxt = (TextView)view.findViewById(R.id.txt_no_invited_friends);

        view.findViewById(R.id.txt_add_friends).setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);
    }


    /**
     * used to set friend list adapter..
     * @param friendsDatas
     */
    private void setFriendAdapter(List<FriendsData> friendsDatas) {
        /*MyFriendsAdapter myFriendsAdapter = new MyFriendsAdapter(MyFriendsFragment.this, friendsDatas);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mMyFriendsRec.setLayoutManager(linearLayoutManager);
        mMyFriendsRec.setItemAnimator(new DefaultItemAnimator());
        mMyFriendsRec.setAdapter(myFriendsAdapter);*/

        MyFriendsListAdapter adapter = new MyFriendsListAdapter(MyFriendsFragment.this, friendsDatas, primaryContactId);
        mMyFriendList.setAdapter(adapter);
    }


    /**
     * Used to set the request list adapter..
     * @param requestReceivedDatas
     */
    private void requestReceivedAdapter(List<RequestReceivedData> requestReceivedDatas) {
        RequestReceivedListAdapter adapter = new RequestReceivedListAdapter(MyFriendsFragment.this, requestReceivedDatas);
        mFriendRequestList.setAdapter(adapter);
    }


    private void requestSentAdapter(List<RequestSentData> requestSentDatas) {
        RequestSendListAdapter adapter = new RequestSendListAdapter(MyFriendsFragment.this, requestSentDatas);
        mRequestSendList.setAdapter(adapter);
    }


    /**
     * Used to get all the friends..
     */
    public void getFriends(boolean isProgressShow) {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);
        String phoneNumber = user.get(SessionManager.PHONE);

        mNoFriendTxt.setVisibility(View.VISIBLE);
        mNoNewRequestTxt.setVisibility(View.VISIBLE);
        mNoInvitedTxt.setVisibility(View.VISIBLE);

        ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
        reqResAPICallBacks.getFriends(getActivity(), new ReqRespCallBack() {
            @Override
            public void onResponse(Object resObj, String resMessage, String resCode) {
                if (resCode.equals(Constants.SUCCESS)) {

                    if (!((List)resObj).isEmpty()) {
                        if (((List) resObj).get(0).getClass().equals(UserDetails.class)) {
                            List<UserDetails> userDetails = (List) resObj;
                            primaryContactId = userDetails.get(0).getPrimaryFriendId();

                        }else if (((List) resObj).get(0).getClass().equals(FriendsData.class)) {
                            List<FriendsData> friendsDatas = (List) resObj;
                            setFriendAdapter(friendsDatas);

                            mNoFriendTxt.setVisibility(View.GONE);

                        }else if (((List) resObj).get(0).getClass().equals(RequestReceivedData.class)) {
                            List<RequestReceivedData> requestReceivedDatas = (List) resObj;
                            requestReceivedAdapter(requestReceivedDatas);

                            mNoNewRequestTxt.setVisibility(View.GONE);

                        }else if (((List) resObj).get(0).getClass().equals(RequestSentData.class)) {
                            List<RequestSentData> requestReceivedDatas = (List) resObj;
                            requestSentAdapter(requestReceivedDatas);

                            mNoInvitedTxt.setVisibility(View.GONE);
                        }
                    }

                    if (mSwRefresh.isRefreshing())
                        mSwRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onResponse(String response, String resMessage, String resCode) {
                if (resCode.equals(Constants.FAILURE)) {
                    yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                }
            }
        }, userId, phoneNumber, isProgressShow);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new MainFragment()).commit();
                break;

            case R.id.txt_add_friends:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new AddNewFriendFragment()).addToBackStack(null).commit();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(friendReceiver, new IntentFilter("RECEIVER_FRIEND"));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(friendReceiver);
    }

    /**
     * Used to handle broadcast intent..
     */
    private BroadcastReceiver friendReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FRIEND_ADD) || intent.getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FRIEND_DELETE)) {
                getFriends(false);
            }
        }
    };

    @Override
    public void onRefresh() {
        mSwRefresh.setRefreshing(true);
        getFriends(false);
    }
}
