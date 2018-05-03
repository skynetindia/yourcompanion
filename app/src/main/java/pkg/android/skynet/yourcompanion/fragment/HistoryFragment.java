package pkg.android.skynet.yourcompanion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.adapter.HelplineNumberAdapter;
import pkg.android.skynet.yourcompanion.adapter.HistoryAdapter;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.models.HistoryData;

/**
 * Created by ST-3 on 16-03-2018.
 */

public class HistoryFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mHistoryRec;

    private YourCompanion mAppContext;
    private SessionManager sessionManager;
    private HashMap<String, String> mUserDetails;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mAppContext = (YourCompanion)getActivity().getApplication();
        sessionManager = new SessionManager(getActivity());
        mUserDetails = sessionManager.getUserDetails();

        initControls(view);
        getHistory();

        return view;
    }


    /**
     * Used to initialize all the controls..
     * @param view
     */
    private void initControls(View view) {
        mHistoryRec = (RecyclerView)view.findViewById(R.id.rec_history);

        view.findViewById(R.id.img_back).setOnClickListener(this);
    }


    /**
     * Used to set the history adaptor...
     * @param data
     */
    private void setAdapter(List<HistoryData> data) {
        HistoryAdapter adapter = new HistoryAdapter(HistoryFragment.this, data);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 1);
        mHistoryRec.setLayoutManager(linearLayoutManager);
        mHistoryRec.setItemAnimator(new DefaultItemAnimator());
        mHistoryRec.setAdapter(adapter);
    }


    /**
     * Used to get all the history...
     */
    private void getHistory() {
        try{
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.getHistory(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {
                        List<HistoryData> historyData = (List)resObj;
                        setAdapter(historyData);
                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        mAppContext.showAlertDialog(getActivity(), resMessage, "Ok");
                    }
                }
            }, mUserDetails.get(SessionManager.USER_ID), true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:

                break;
        }
    }
}
