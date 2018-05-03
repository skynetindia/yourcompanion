package pkg.android.skynet.yourcompanion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.List;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.models.UserDetails;

/**
 * Created by ST-3 on 28-09-2017.
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText mFullNameEdt, mEmailEdt, mPhoneEdt, mPasswordEdt, mConfirmPasswordEdt, mPhoneCodeEdt, mAddressEdt;
    private RadioButton mMaleRdo, mFemaleRdo, mOtherRdo;

    private String mGender = "Male";

    private YourCompanion yourCompanion;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        yourCompanion = (YourCompanion)getApplication();
        sessionManager = new SessionManager(getApplicationContext());

        initControls();
    }


    /**
     * Used to initialize all the controls..
     */
    private void initControls() {
        mFullNameEdt = (EditText)findViewById(R.id.edt_fullname);
        mEmailEdt = (EditText)findViewById(R.id.edt_email);
        mPhoneEdt = (EditText)findViewById(R.id.edt_phone);
        mPasswordEdt = (EditText)findViewById(R.id.edt_password);
        mConfirmPasswordEdt = (EditText)findViewById(R.id.edt_confirm_password);
        mAddressEdt = (EditText)findViewById(R.id.edt_address);

        mPhoneCodeEdt = (EditText)findViewById(R.id.edt_phone_code);
        mPhoneCodeEdt.setText(yourCompanion.getCountryPhoneCode());

        mMaleRdo = (RadioButton)findViewById(R.id.rdo_male);
        mMaleRdo.setOnCheckedChangeListener(this);

        mFemaleRdo = (RadioButton)findViewById(R.id.rdo_female);
        mFemaleRdo.setOnCheckedChangeListener(this);

        mOtherRdo = (RadioButton)findViewById(R.id.rdo_other);
        mOtherRdo.setOnCheckedChangeListener(this);

        findViewById(R.id.btn_continue).setOnClickListener(this);
        findViewById(R.id.txt_signin).setOnClickListener(this);
    }


    /**
     * Used to check the field validation..
     * @return
     */
    private boolean isValid() {
        String[] name = yourCompanion.getEditTextValue(mFullNameEdt).split(" ");

        if (yourCompanion.isEditTextEmpty(mFullNameEdt) || !yourCompanion.getEditTextValue(mFullNameEdt).contains(" ")) {
            mFullNameEdt.requestFocus();
            yourCompanion.showAlertDialog(SignUpActivity.this, getString(R.string.err_enter_fullname), "Ok");
            return false;

        }else if (name.length < 2) {
            mFullNameEdt.requestFocus();
            yourCompanion.showAlertDialog(SignUpActivity.this, getString(R.string.err_enter_email), "Ok");
            return false;

        }else if (yourCompanion.isEditTextEmpty(mEmailEdt)) {
            yourCompanion.showAlertDialog(SignUpActivity.this, getString(R.string.err_enter_email), "Ok");
            mEmailEdt.requestFocus();
            return false;

        }else if (!yourCompanion.isValidEmail(yourCompanion.getEditTextValue(mEmailEdt))) {
            yourCompanion.showAlertDialog(SignUpActivity.this, getString(R.string.err_enter_valid_email), "Ok");
            mEmailEdt.requestFocus();
            return false;

        }else if (yourCompanion.isEditTextEmpty(mPhoneCodeEdt)) {
            yourCompanion.showAlertDialog(SignUpActivity.this, getString(R.string.err_enter_phone_code), "Ok");
            mPhoneCodeEdt.requestFocus();
            return false;

        }else if (yourCompanion.isEditTextEmpty(mPhoneEdt)) {
            yourCompanion.showAlertDialog(SignUpActivity.this, getString(R.string.err_enter_phone), "Ok");
            mPhoneEdt.requestFocus();
            return false;

        }else if (yourCompanion.getEditTextValue(mPhoneEdt).length() < 6) {
            yourCompanion.showAlertDialog(SignUpActivity.this, getString(R.string.err_enter_valid_phone), "Ok");
            mPhoneEdt.requestFocus();
            return false;

        }else if (yourCompanion.isEditTextEmpty(mPasswordEdt)) {
            yourCompanion.showAlertDialog(SignUpActivity.this, getString(R.string.err_enter_password), "Ok");
            mPasswordEdt.requestFocus();
            return false;

        }else if (yourCompanion.isEditTextEmpty(mConfirmPasswordEdt)) {
            mConfirmPasswordEdt.requestFocus();
            yourCompanion.showAlertDialog(SignUpActivity.this, getString(R.string.err_enter_confirm_password), "Ok");
            return false;

        }else if (!yourCompanion.getEditTextValue(mConfirmPasswordEdt).equals(yourCompanion.getEditTextValue(mPasswordEdt))) {
            mConfirmPasswordEdt.requestFocus();
            yourCompanion.showAlertDialog(SignUpActivity.this, getString(R.string.err_diff_password), "Ok");
            return false;

        }else return true;
    }


    /**
     * Used to register user..
     */
    private void registerUser() {
        try{
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.registerUser(SignUpActivity.this, new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {
                    if (resCode.equals(Constants.SUCCESS)) {
                        List<UserDetails> userDetailses = (List)resObj;

                        /*sessionManager.createLoginSession(userDetailses.get(0).getUserId(), userDetailses.get(0).getFirstName(), userDetailses.get(0).getLastName(), userDetailses.get(0).getEmail(),
                                userDetailses.get(0).getPhone());*/

                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        finish();
                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(SignUpActivity.this, resMessage, "Ok");
                    }
                }
            }, yourCompanion.getEditTextValue(mFullNameEdt), "", yourCompanion.getEditTextValue(mEmailEdt), "+"+yourCompanion.getEditTextValue(mPhoneCodeEdt) + yourCompanion.getEditTextValue(mPhoneEdt), mGender,
                    yourCompanion.getEditTextValue(mConfirmPasswordEdt), "0", yourCompanion.getEditTextValue(mAddressEdt), true);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                if (isValid())
                    registerUser();
                break;

            case R.id.txt_signin:
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rdo_male:
                if (isChecked)
                    mGender = "Male";
                break;

            case R.id.rdo_female:
                if (isChecked)
                    mGender = "Female";
                break;

            case R.id.rdo_other:
                if (isChecked)
                    mGender = "Other";
                break;
        }
    }
}
