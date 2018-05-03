package pkg.android.skynet.yourcompanion.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;

/**
 * Created by ST-3 on 06-10-2017.
 */

public class FeedbackFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private ImageView mRate1Img, mRate2Img, mRate3Img, mRate4Img, mRate5Img;
    private EditText mYourThoughtEdt;

    private String mRating = "";

    private YourCompanion yourCompanion;
    private SessionManager sessionManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        yourCompanion = (YourCompanion)getActivity().getApplication();
        sessionManager = new SessionManager(getActivity());

        initControls(view);

        return view;
    }


    private void initControls(View view) {
        mRate1Img = (ImageView)view.findViewById(R.id.img_rate1);
        mRate1Img.setOnClickListener(this);

        mRate2Img = (ImageView)view.findViewById(R.id.img_rate2);
        mRate2Img.setOnClickListener(this);

        mRate3Img = (ImageView)view.findViewById(R.id.img_rate3);
        mRate3Img.setOnClickListener(this);

        mRate4Img = (ImageView)view.findViewById(R.id.img_rate4);
        mRate4Img.setOnClickListener(this);

        mRate5Img = (ImageView)view.findViewById(R.id.img_rate5);
        mRate5Img.setOnClickListener(this);

        mYourThoughtEdt = (EditText)view.findViewById(R.id.edt_feedback);
        mYourThoughtEdt.addTextChangedListener(this);

        view.findViewById(R.id.btn_submit).setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);
    }


    /**
     * Used to check the field validation..
     * @return
     */
    private boolean isValid() {
        if (mRating.equals("")) {
            yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_select_rate), "Ok");
            return false;

        }else if (yourCompanion.isEditTextEmpty(mYourThoughtEdt)) {
            yourCompanion.showAlertDialog(getActivity(), getString(R.string.err_enter_thoughts), "Ok");
            return false;

        }else return true;
    }


    /**
     * Used to send the feedback..
     */
    private void sendFeedback() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String userId = user.get(SessionManager.USER_ID);
        String email = user.get(SessionManager.EMAIL);

        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.submitFeedback(getActivity(), new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(getActivity(), resMessage, "Ok");
                    }else {
                        Toast.makeText(getActivity(), resMessage, Toast.LENGTH_LONG).show();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new MainFragment()).commit();
                    }
                }
            }, userId, email, yourCompanion.getEditTextValue(mYourThoughtEdt), mRating, true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_rate1:
                mRate1Img.setSelected(true);
                mRate2Img.setSelected(false);
                mRate3Img.setSelected(false);
                mRate4Img.setSelected(false);
                mRate5Img.setSelected(false);
                mRating = "1";
                break;

            case R.id.img_rate2:
                mRate1Img.setSelected(false);
                mRate2Img.setSelected(true);
                mRate3Img.setSelected(false);
                mRate4Img.setSelected(false);
                mRate5Img.setSelected(false);
                mRating = "2";
                break;

            case R.id.img_rate3:
                mRate1Img.setSelected(false);
                mRate2Img.setSelected(false);
                mRate3Img.setSelected(true);
                mRate4Img.setSelected(false);
                mRate5Img.setSelected(false);
                mRating = "3";
                break;

            case R.id.img_rate4:
                mRate1Img.setSelected(false);
                mRate2Img.setSelected(false);
                mRate3Img.setSelected(false);
                mRate4Img.setSelected(true);
                mRate5Img.setSelected(false);
                mRating = "4";
                break;

            case R.id.img_rate5:
                mRate1Img.setSelected(false);
                mRate2Img.setSelected(false);
                mRate3Img.setSelected(false);
                mRate4Img.setSelected(false);
                mRate5Img.setSelected(true);
                mRating = "5";
                break;

            case R.id.btn_submit:
                if (isValid()) {
                    sendFeedback();
                }
                break;

            case R.id.img_back:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new MainFragment()).commit();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0)
            mYourThoughtEdt.setTypeface(null, Typeface.NORMAL);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
