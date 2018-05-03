package pkg.android.skynet.yourcompanion.callbacks;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.asynctask.YourCompanionAsyncTask;
import pkg.android.skynet.yourcompanion.models.FollowMeData;
import pkg.android.skynet.yourcompanion.models.FriendsData;
import pkg.android.skynet.yourcompanion.models.HelplineNumbersData;
import pkg.android.skynet.yourcompanion.models.HistoryData;
import pkg.android.skynet.yourcompanion.models.RequestReceivedData;
import pkg.android.skynet.yourcompanion.models.RequestSentData;
import pkg.android.skynet.yourcompanion.models.UpdateFollowLocationData;
import pkg.android.skynet.yourcompanion.models.UserDetails;


/**
 * Created by as on 5/6/2016.
 */
public class ReqResAPICallBacks implements OnTaskComplete {

    private ReqRespCallBack reqRespCallBack;
    private Context context;
    private YourCompanion appContext;


    public void registerUser(Context context, ReqRespCallBack reqRespCallBack, String firstName, String lastName, String email, String phone, String gender, String password,
                             String isSocial, String address, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("fname", firstName);
            requestParam.put("lname", lastName);
            requestParam.put("email", email);
            requestParam.put("phone", phone);
            requestParam.put("gender", gender);
            requestParam.put("fb_user", isSocial);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");
            requestParam.put("password", password);
            requestParam.put("address", address);


            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "adduser", requestParam, Constants.MEHTOD_POST, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"adduser");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUser(Context context, ReqRespCallBack reqRespCallBack, String userId, String firstName, String lastName, String email, String phone, String gender, String password,
                             String confirmPasswrod, String isSocial, String helpMessage, String alaramDelay, String picture, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("user_id", userId);
            requestParam.put("fname", firstName);
            requestParam.put("lname", lastName);
            requestParam.put("email", email);
            requestParam.put("phone", phone);
            requestParam.put("gender", gender);
            requestParam.put("fb_user", isSocial);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");
            requestParam.put("password", password);
            requestParam.put("confirm_password", confirmPasswrod);
            requestParam.put("help_message", helpMessage);
            requestParam.put("alarm_delay", alaramDelay);
            requestParam.put("userfile", picture);

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "updateUser", requestParam, Constants.MEHTOD_POST, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"updateUser");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUser(Context context, ReqRespCallBack reqRespCallBack, String id, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("id", id);

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "getUser", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"getUser");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void authanticateUser(Context context, ReqRespCallBack reqRespCallBack, String email, String password, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("email", email);
            requestParam.put("pwd", password);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "login", requestParam, Constants.MEHTOD_POST, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"login");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forgetPassword(Context context, ReqRespCallBack reqRespCallBack, String email, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("email", email);

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "forgotPassword", requestParam, Constants.MEHTOD_POST, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"forgotPassword");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inviteFriends(Context context, ReqRespCallBack reqRespCallBack, String senderNumber, ArrayList<String> inviteNumber, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            String requestNo = TextUtils.join(",", inviteNumber);

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("sender_no", senderNumber);
            requestParam.put("request_no", requestNo);

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "inviteFriend", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"inviteFriend");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFriends(Context context, ReqRespCallBack reqRespCallBack, String id, String phoneNumber, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("id", id);
            requestParam.put("phone_number", phoneNumber);

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "getFriendsList", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"getFriendsList");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void helplineNumbers(Context context, ReqRespCallBack reqRespCallBack, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "helplinenumbers", requestParam, Constants.MEHTOD_GET, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"helplinenumbers");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitFeedback(Context context, ReqRespCallBack reqRespCallBack, String userId, String email, String text, String rate, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("user_id", userId);
            requestParam.put("email", email);
            requestParam.put("message", text);
            requestParam.put("rate", rate);

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "sendfeedback", requestParam, Constants.MEHTOD_POST, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"sendfeedback");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEmergencyNotifications(Context context, ReqRespCallBack reqRespCallBack, String userId, String isEmailSend, String notifyType, String friendIds, String msgTitle,
                                           String msgText, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("id", userId);
            requestParam.put("is_email", isEmailSend);
            requestParam.put("notify_type", notifyType);
            requestParam.put("friend_ids", friendIds);
            requestParam.put("msg_title", msgTitle);
            requestParam.put("msg_text", msgText);

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "EmergencyNotification", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"EmergencyNotification");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLocation(Context context, ReqRespCallBack reqRespCallBack, String userId, String latitude, String longitude, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("user_id", userId);
            requestParam.put("latitude", latitude);
            requestParam.put("longitude", longitude);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "updatelatlong", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"updatelatlong");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void declineRequest(Context context, ReqRespCallBack reqRespCallBack, String userId, String phone, String invId, boolean isDeclineBySender, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("user_id", userId);
            requestParam.put("phone", phone);
            requestParam.put("inv_id", invId);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            if (isDeclineBySender) {
                requestParam.put("is_cancel_by_sender", "1");
            }

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "declinedRequest", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"declinedRequest");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptRequest(Context context, ReqRespCallBack reqRespCallBack, String userId, String invId, String notifyType, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("user_id", userId);
            requestParam.put("inv_id", invId);
            requestParam.put("notify_type", notifyType);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "acceptRequest", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"acceptRequest");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFriends(Context context, ReqRespCallBack reqRespCallBack, String userId, String id, String notifyType, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("user_id", userId);
            requestParam.put("friend_list_id", id);
            requestParam.put("notify_type", notifyType);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "deleteFriend", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"deleteFriend");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFollowMeReq(Context context, ReqRespCallBack reqRespCallBack, String userId, String friendIds, String startLat, String startLong, String notificationType,
                                String isEmergency, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("sender_id", userId);
            requestParam.put("follow_users_ids", friendIds);
            requestParam.put("start_lat", startLat);
            requestParam.put("start_long", startLong);
            requestParam.put("notify_type", notificationType);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");
            requestParam.put("is_emergency", isEmergency);

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "sendfollowfriends", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"sendfollowfriends");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFollowMeList (Context context, ReqRespCallBack reqRespCallBack, String userId, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("user_id", userId);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "getFollowList", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"getFollowList");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopFollowing (Context context, ReqRespCallBack reqRespCallBack, String userId, String notifiyType, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("sender_id", userId);
            requestParam.put("notify_type", notifiyType);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "stopFollow", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"stopFollow");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptFollowMe (Context context, ReqRespCallBack reqRespCallBack, String followId, String status, String notifiyType, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("follow_id", followId);
            requestParam.put("status", status);
            requestParam.put("notify_type", notifiyType);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "acceptCancelFollow", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"acceptCancelFollow");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFollowMeLatLong(Context context, ReqRespCallBack reqRespCallBack, String senderId, String latitude, String longitude, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("sender_id", senderId);
            requestParam.put("latitude", latitude);
            requestParam.put("longitude", longitude);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "updatefollowlatlong", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"updatefollowlatlong");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFollowMeDetails(Context context, ReqRespCallBack reqRespCallBack, String followId, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("follow_id", followId);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "getFollowById", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"getFollowById");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePrimaryFriend(Context context, ReqRespCallBack reqRespCallBack, String userId, String friendId, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("user_id", userId);
            requestParam.put("friend_id", friendId);
            requestParam.put("device_id", FirebaseInstanceId.getInstance().getToken());
            requestParam.put("device_type", "1");

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "updateprimaryfriend", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"updateprimaryfriend");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getHistory(Context context, ReqRespCallBack reqRespCallBack, String userId, boolean isProgressShow) {
        try {
            this.context = context;
            this.reqRespCallBack = reqRespCallBack;
            appContext = (YourCompanion)context.getApplicationContext();

            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("user_id", userId);

            YourCompanionAsyncTask asyncTask;

            asyncTask = new YourCompanionAsyncTask(context, this, "getUserVideos", requestParam, Constants.MEHTOD_JSON, isProgressShow);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.BASE_URL+"getUserVideos");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onTaskComplete(int res_code, String res_message, String res_result, String webService) {
        try {
            JSONObject jsonObject = new JSONObject(res_result);

            String error = jsonObject.get("msg").toString();;
            String status = jsonObject.get("status").toString();

            switch (webService) {
                case "adduser":
                    if ((status.equals(Constants.SUCCESS) || status.equals(Constants.SUCCESS2)) && !jsonObject.get("user_details").toString().equals("")) {
                        Gson gson = new Gson();
                        System.out.println("Response ->" + jsonObject.toString());

                        List<UserDetails> userDetailses = (List<UserDetails>) gson.fromJson(jsonObject.get("user_details").toString(), new TypeToken<List<UserDetails>>() {}.getType());
                        reqRespCallBack.onResponse(userDetailses, error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "getUser":
                    if (status.equals(Constants.SUCCESS) && !jsonObject.get("getUser").toString().equals("")) {
                        Gson gson = new Gson();

                        List<UserDetails> userDetailses = (List<UserDetails>) gson.fromJson(jsonObject.get("getUser").toString(), new TypeToken<List<UserDetails>>() {}.getType());
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse(userDetailses, error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "updateUser":
                    if (status.equals(Constants.SUCCESS)) {
                        Gson gson = new Gson();

                        List<UserDetails> userDetailses = (List<UserDetails>) gson.fromJson(jsonObject.get("user_details").toString(), new TypeToken<List<UserDetails>>() {}.getType());
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse(userDetailses, error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "login":
                    if (status.equals(Constants.SUCCESS) && !jsonObject.get("user_details").toString().equals("")) {
                        Gson gson = new Gson();

                        UserDetails userDetailses = (UserDetails) gson.fromJson(jsonObject.get("user_details").toString(), new TypeToken<UserDetails>() {}.getType());
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse(userDetailses, error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "forgotPassword":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "inviteFriend":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "getFriendsList":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());

                        Gson gson = new Gson();

                        List<UserDetails> userDetails = new ArrayList<>();
                        UserDetails details = new UserDetails();
                        details.setPrimaryFriendId(jsonObject.get("primary_friend_id").toString());
                        userDetails.add(details);
                        reqRespCallBack.onResponse(userDetails, error, status);

                        List<FriendsData> friendsDatas = (List<FriendsData>) gson.fromJson(jsonObject.get("friendslist").toString(), new TypeToken<List<FriendsData>>() {}.getType());
                        reqRespCallBack.onResponse(friendsDatas, error, status);

                        List<RequestReceivedData> requestReceivedDatas = (List<RequestReceivedData>) gson.fromJson(jsonObject.get("recievelist").toString(), new TypeToken<List<RequestReceivedData>>() {}.getType());
                        reqRespCallBack.onResponse(requestReceivedDatas, error, status);

                        List<RequestSentData> requestSentDatas = (List<RequestSentData>) gson.fromJson(jsonObject.get("sendlist").toString(), new TypeToken<List<RequestSentData>>() {}.getType());
                        reqRespCallBack.onResponse(requestSentDatas, error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "helplinenumbers":
                    if (status.equals(Constants.SUCCESS)) {
                        Gson gson = new Gson();

                        List<HelplineNumbersData> friendsDatas = (List<HelplineNumbersData>) gson.fromJson(jsonObject.get("result").toString(), new TypeToken<List<HelplineNumbersData>>() {}.getType());
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse(friendsDatas, error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "getFollowList":
                    if (status.equals(Constants.SUCCESS)) {
                        Gson gson = new Gson();

                        List<FollowMeData> followMeData = (List<FollowMeData>) gson.fromJson(jsonObject.get("list").toString(), new TypeToken<List<FollowMeData>>() {}.getType());
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse(followMeData, error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "sendfeedback":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "EmergencyNotification":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "declinedRequest":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "acceptRequest":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "deleteFriend":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "sendfollowfriends":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "stopFollow":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "acceptCancelFollow":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "updatelatlong":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "updatefollowlatlong":
                    if (status.equals(Constants.SUCCESS)) {
                        Gson gson = new Gson();

                        List<UpdateFollowLocationData> followMeData = (List<UpdateFollowLocationData>) gson.fromJson(jsonObject.get("details").toString(), new TypeToken<List<UpdateFollowLocationData>>() {}.getType());
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse(followMeData, error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "getFollowById":
                    if (status.equals(Constants.SUCCESS)) {
                        Gson gson = new Gson();

                        List<FollowMeData> followMeData = (List<FollowMeData>) gson.fromJson(jsonObject.get("list").toString(), new TypeToken<List<FollowMeData>>() {}.getType());
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse(followMeData, error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "getUserVideos":
                    if (status.equals(Constants.SUCCESS)) {
                        Gson gson = new Gson();

                        List<HistoryData> historyData = (List<HistoryData>) gson.fromJson(jsonObject.get("getUser").toString(), new TypeToken<List<HistoryData>>() {}.getType());
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse(historyData, error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

                case "updateprimaryfriend":
                    if (status.equals(Constants.SUCCESS)) {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);

                    } else {
                        System.out.println("Response ->" + jsonObject.toString());
                        reqRespCallBack.onResponse("", error, status);
                    }
                    break;

            }
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Result -> "+res_result);
        }
    }
}
