package pkg.android.skynet.yourcompanion.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.dialogfragment.SaveMobileNumberDialogFragment;
import pkg.android.skynet.yourcompanion.models.UserDetails;

/**
 * Created by ST-3 on 28-09-2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private EditText mEmailEdt, mPasswordEdt;

    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private TwitterAuthClient authClient;

    private static final int RC_SIGN_IN = 007;
    private boolean isApiCall = false;
    private static final String TWITTER_KEY = "IIZcbFrxodvoPKD7EegERDN0G";
    private static final String TWITTER_SECRET = "uJm9QJm9yT4bdcpmoSiRf1MwsD5LbU8MEcM5VGQtrDg6ntRrHz";

    private YourCompanion yourCompanion;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        yourCompanion = (YourCompanion)getApplication();
        sessionManager = new SessionManager(LoginActivity.this);

        initControls();

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        authClient = new TwitterAuthClient();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    /**
     * Used to initialize all the controls..
     */
    private void initControls() {
        mEmailEdt = (EditText) findViewById(R.id.edt_email);
        mPasswordEdt = (EditText)findViewById(R.id.edt_password);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.img_facebook).setOnClickListener(this);
        findViewById(R.id.img_google_plus).setOnClickListener(this);
        findViewById(R.id.img_linkedin).setOnClickListener(this);
        findViewById(R.id.img_twitter).setOnClickListener(this);
        findViewById(R.id.txt_forget_password).setOnClickListener(this);
        findViewById(R.id.txt_signup).setOnClickListener(this);
    }


    /**
     * Used to check the field validation..
     * @return
     */
    private boolean isValid() {
        if (yourCompanion.isEditTextEmpty(mEmailEdt)) {
            mEmailEdt.requestFocus();
            yourCompanion.showAlertDialog(LoginActivity.this, getString(R.string.err_enter_email), "Ok");
            return false;

        }else if (!yourCompanion.isValidEmail(yourCompanion.getEditTextValue(mEmailEdt))) {
            mEmailEdt.requestFocus();
            yourCompanion.showAlertDialog(LoginActivity.this, getString(R.string.err_enter_valid_email), "Ok");
            return false;

        }else if (yourCompanion.isEditTextEmpty(mPasswordEdt)) {
            mPasswordEdt.requestFocus();
            yourCompanion.showAlertDialog(LoginActivity.this, getString(R.string.err_enter_password), "Ok");
            return false;

        }else return true;
    }


    /**
     * Used to login with facebook...
     */
    private void facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email"));
        //LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("onSuccess", "loginResult==>" + loginResult.getRecentlyGrantedPermissions());

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,last_name,email,picture.type(large)");

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                try {
                                    Log.d("onCompleted", "JSON Result :" + json.toString());
                                    Log.d("onCompleted", "response Result :" + response.toString());

                                    if (!isApiCall) {
                                        socialLogin(json.getString("name") + json.getString("last_name"), json.getString("email"), json.getString("gender"), "1");
                                        isApiCall = true;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("onCancel", "facebook");
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("onError", "facebook:" + e.toString());
                if (e instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }


    /**
     * Used to login with google plus..
     */
    private void googlePlusLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /**
     * Used to login with twitter...
     */
    private void twitterLogin() {
        authClient.authorize(LoginActivity.this, new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> twitterSessionResult) {
                Log.e("TAG", "userid" + twitterSessionResult.data.getUserId());

                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

                authClient.requestEmail(session, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        Log.d("success", "result:" + result.data);

                        socialLogin(twitterSessionResult.data.getUserName(), result.data, "Male", "1");
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        exception.printStackTrace();
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * Used to login with linkedin...
     */
    private void linkedInLogin() {
        LISessionManager.getInstance(getApplicationContext()).init(LoginActivity.this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                Log.d("onAuthSuccess", "success");
                //String url = "https://api.linkedin.com/v1/people/~";
                String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))";

                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.getRequest(LoginActivity.this, url, new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse) {
                        Log.d("onApiSuccess", apiResponse.getResponseDataAsJson().toString());

                        try {
                            socialLogin(apiResponse.getResponseDataAsJson().getString("firstName") + " " + apiResponse.getResponseDataAsJson().getString("lastName"),
                                    apiResponse.getResponseDataAsJson().getString("emailAddress"), "Male", "1");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onApiError(LIApiError liApiError) {
                        Log.d("onApiError", "liApiError:" + liApiError);
                    }
                });
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Log.d("onAuthError", "error" + error);
            }
        }, true);
    }

    private Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }


    /**
     * Used to authanticate user..
     */
    private void authanticateUser() {
        try{
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.authanticateUser(LoginActivity.this, new ReqRespCallBack() {
                @Override
                public void onResponse(Object resObj, String resMessage, String resCode) {

                    if (resCode.equals(Constants.SUCCESS)) {
                        UserDetails userDetails = (UserDetails) resObj;

                        sessionManager.createLoginSession(userDetails.getUserId(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getEmail(),
                                userDetails.getPhone(), userDetails.getPrimaryFriendId(), userDetails.getHelpMessage());

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }

                @Override
                public void onResponse(String response, String resMessage, String resCode) {
                    if (resCode.equals(Constants.FAILURE)) {
                        yourCompanion.showAlertDialog(LoginActivity.this, resMessage, "Ok");
                    }
                }

            }, yourCompanion.getEditTextValue(mEmailEdt), yourCompanion.getEditTextValue(mPasswordEdt), true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to register user..
     */
    private void socialLogin(String fullname, String email, String gender, String socialLoginType) {
        try{
            ReqResAPICallBacks reqResAPICallBacks = new ReqResAPICallBacks();
            reqResAPICallBacks.registerUser(LoginActivity.this, new ReqRespCallBack() {
                        @Override
                        public void onResponse(Object resObj, String resMessage, String resCode) {
                            if (resCode.equals(Constants.SUCCESS2)) {
                                List<UserDetails> userDetailses = (List) resObj;

                                LoginManager.getInstance().logOut();
                                isApiCall = false;

                                if (userDetailses.get(0).getPhone().equals("")) {
                                    SaveMobileNumberDialogFragment mobileNumberDialogFragment = new SaveMobileNumberDialogFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("USER_DETAILS", userDetailses.get(0));
                                    mobileNumberDialogFragment.setArguments(bundle);
                                    mobileNumberDialogFragment.show(getSupportFragmentManager(), "Add Mobile Number");

                                }else {
                                    sessionManager.createLoginSession(userDetailses.get(0).getUserId(), userDetailses.get(0).getFirstName(), userDetailses.get(0).getLastName(), userDetailses.get(0).getEmail(),
                                            userDetailses.get(0).getPhone(), userDetailses.get(0).getPrimaryFriendId(), userDetailses.get(0).getHelpMessage());

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onResponse(String response, String resMessage, String resCode) {
                            if (resCode.equals(Constants.FAILURE)) {
                                yourCompanion.showAlertDialog(LoginActivity.this, resMessage, "Ok");
                                LoginManager.getInstance().logOut();
                                isApiCall = false;
                            }
                        }
                    }, fullname, "", email, "", gender, "", socialLoginType, "", true);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

        //pdkClient.onOauthResponse(requestCode, resultCode, data);

        authClient.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            System.out.println("G+ login " + result.getStatus());
            if (result.isSuccess()) {

                GoogleSignInAccount acct = result.getSignInAccount();

                /*if (ApplicationClass.checkInternetConnection(Login.this)) {
                    new socialLoginRequest(acct.getEmail(),
                            acct.getDisplayName(),
                            "",
                            "",
                            "",
                            "2").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    applicationClass.showAlertDialog(Login.this, getResources().getString(R.string.nonetwork));
                }*/

                socialLogin(acct.getDisplayName(), acct.getEmail(), "Other", "1");

            }else {
                System.out.println("G+ login failed");
            }
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_login:
                if (isValid())
                    authanticateUser();
                break;

            case R.id.img_facebook:
                facebookLogin();
                break;

            case R.id.img_google_plus:
                googlePlusLogin();
                break;

            case R.id.img_linkedin:
                linkedInLogin();
                break;

            case R.id.img_twitter:
                twitterLogin();
                break;

            case R.id.txt_forget_password:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                finish();
                break;

            case R.id.txt_signup:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
