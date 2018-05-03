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

import java.util.List;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.adapter.HelplineNumberAdapter;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.models.HelplineNumbersData;

/**
 * Created by ST-3 on 06-10-2017.
 */

public class HelplineNumberFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mHelplineNumRec;

    private YourCompanion yourCompanion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_helpline_number, container, false);

        yourCompanion = (YourCompanion)getActivity().getApplication();

        initControls(view);
        getHelplineNumber();

        return view;
    }


    /**
     * Used to initialize all the controls..
     * @param view
     */
    private void initControls(View view) {
        mHelplineNumRec = (RecyclerView)view.findViewById(R.id.rec_helpline_num);

        view.findViewById(R.id.img_back).setOnClickListener(this);
    }


    /**
     * Used to set helpline number adapter..
     * @param numbersDatas
     */
    private void setHelplineNumAdaptor(List<HelplineNumbersData> numbersDatas) {
        HelplineNumberAdapter adapter = new HelplineNumberAdapter(HelplineNumberFragment.this, numbersDatas);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 2);
        mHelplineNumRec.setLayoutManager(linearLayoutManager);
        mHelplineNumRec.setItemAnimator(new DefaultItemAnimator());
        mHelplineNumRec.setAdapter(adapter);
    }


    /**
     * Used to get all the helpline number list...
     */
    private void getHelplineNumber() {
        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.helplineNumbers(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {
                        List<HelplineNumbersData> numbersDatas = (List)resObj;
                        setHelplineNumAdaptor(numbersDatas);
                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {

                    }
                }
            }, true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new MainFragment()).commit();
                break;
        }
    }
}
