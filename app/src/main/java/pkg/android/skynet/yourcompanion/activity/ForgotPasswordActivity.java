package pkg.android.skynet.yourcompanion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;

/**
 * Created by ST-3 on 28-09-2017.
 */

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailEdt;

    private YourCompanion yourCompanion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        yourCompanion = (YourCompanion)getApplication();

        initControls();
    }


    /**
     * Used to initialize all the controls..
     */
    private void initControls() {
        mEmailEdt = (EditText)findViewById(R.id.edt_email);

        findViewById(R.id.btn_reset_password).setOnClickListener(this);
        findViewById(R.id.txt_signin).setOnClickListener(this);
    }


    /**
     * Used to check the field validation...
     * @return
     */
    private boolean isValid() {
        if (yourCompanion.isEditTextEmpty(mEmailEdt)) {
            mEmailEdt.requestFocus();
            yourCompanion.showAlertDialog(ForgotPasswordActivity.this, getString(R.string.err_enter_email), "Ok");
            return false;

        }else if (!yourCompanion.isValidEmail(yourCompanion.getEditTextValue(mEmailEdt))) {
            mEmailEdt.requestFocus();
            yourCompanion.showAlertDialog(ForgotPasswordActivity.this, getString(R.string.err_enter_valid_email), "Ok");
            return false;

        }else return true;
    }


    /**
     * Used to forgot password..
     */
    private void forgotPassword() {
        try {
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.forgetPassword(ForgotPasswordActivity.this, new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(ForgotPasswordActivity.this, resMessage, "Ok");
                    } else {
                        yourCompanion.showAlertDialog(ForgotPasswordActivity.this, resMessage, "Ok");
                    }
                }
            }, yourCompanion.getEditTextValue(mEmailEdt), true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_password:
                if (isValid()) {
                    forgotPassword();
                }
                break;

            case R.id.txt_signin:
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
