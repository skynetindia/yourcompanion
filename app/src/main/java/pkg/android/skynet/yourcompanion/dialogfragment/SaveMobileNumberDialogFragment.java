package pkg.android.skynet.yourcompanion.dialogfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.activity.LoginActivity;
import pkg.android.skynet.yourcompanion.activity.MainActivity;
import pkg.android.skynet.yourcompanion.activity.SignUpActivity;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.models.UserDetails;

/**
 * Created by ST-3 on 17-01-2018.
 */

public class SaveMobileNumberDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText mPhoneCodeEdt, mPhoneNoEdt;

    private UserDetails userDetails;
    private YourCompanion mAppContext;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_save_mobile_number, container, false);

        mAppContext = (YourCompanion)getActivity().getApplication();
        sessionManager = new SessionManager(getActivity());

        initControls(view);

        if (getArguments() != null) {
            userDetails = (UserDetails) getArguments().get("USER_DETAILS");
        }

        return view;
    }


    /**
     * Used to initialize all the controls...
     * @param view
     */
    private void initControls(View view) {
        mPhoneCodeEdt = (EditText)view.findViewById(R.id.edt_phone_code);
        mPhoneCodeEdt.setText(mAppContext.getCountryPhoneCode());

        mPhoneNoEdt = (EditText)view.findViewById(R.id.edt_phone);
        mPhoneNoEdt.requestFocus();

        view.findViewById(R.id.txt_login).setOnClickListener(this);
        view.findViewById(R.id.txt_cancel).setOnClickListener(this);
    }


    /**
     * Used to check the field validation..
     * @return
     */
    private boolean isValid() {
        if (mAppContext.isEditTextEmpty(mPhoneCodeEdt)) {
            mAppContext.showAlertDialog(getActivity(), getString(R.string.err_enter_phone_code), "Ok");
            mPhoneCodeEdt.requestFocus();
            return false;

        }else if (mAppContext.isEditTextEmpty(mPhoneNoEdt)) {
            mAppContext.showAlertDialog(getActivity(), getString(R.string.err_enter_phone), "Ok");
            mPhoneNoEdt.requestFocus();
            return false;

        }else return true;
    }


    /**
     * Used to save the profile data..
     */
    private void updateProfile() {
        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.updateUser(getActivity(), new ReqRespCallBack() {
                        @Override
                        public void onResponse(Object resObj, String resMessage, String resCode) {
                            if (resCode.equals(Constants.SUCCESS)) {
                                List<UserDetails> userDetailses = (List)resObj;

                                sessionManager.createLoginSession(userDetailses.get(0).getUserId(), userDetailses.get(0).getFirstName(), userDetailses.get(0).getLastName(),
                                        userDetailses.get(0).getEmail(), userDetailses.get(0).getPhone(), userDetailses.get(0).getPrimaryFriendId(), userDetailses.get(0).getHelpMessage());

                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getDialog().dismiss();
                                getActivity().finish();
                            }
                        }

                        @Override
                        public void onResponse(String response, String resMessage, String resCode) {
                            if (resCode.equals(Constants.FAILURE)) {
                                mAppContext.showAlertDialog(getActivity(), resMessage, "Ok");
                            }
                        }
                    }, userDetails.getUserId(), userDetails.getFirstName(), "", userDetails.getEmail(), "+"+mAppContext.getEditTextValue(mPhoneCodeEdt) + mAppContext.getEditTextValue(mPhoneNoEdt),
                        userDetails.getGender(), "", "", userDetails.getFbUser(), userDetails.getHelpMessage(),"", userDetails.getUserImg(), true);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_cancel:
                getDialog().dismiss();
                break;

            case R.id.txt_login:
                if (isValid()) {
                    updateProfile();
                }
                break;
        }
    }
}
