package pkg.android.skynet.yourcompanion.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import pkg.android.skynet.yourcompanion.activity.LoginActivity;
import pkg.android.skynet.yourcompanion.activity.MainActivity;
import pkg.android.skynet.yourcompanion.models.UserDetails;

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
     
    private Context _context;

    private static final String PREF_NAME = "your_companion";
     
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String USER_ID = "user_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String ALARM_DELAY = "alarm_delay";
    public static final String IS_BROADCAST = "is_broadcast";
    public static final String IS_EMERGENCY_SOUND = "is_emergency_sound";
    public static final String PRIMARY_NUMBER = "primary_number";
    public static final String PRIMARY_FRIEND_ID = "primary_friend_id";
    public static final String EMERGENCY_MESSAGE = "emergency_message";

    public static final String FOLLOW_ID = "follow_id";
    public static final String FOLLOWING_NAME = "following_name";
    public static final String FOLLOWING_DATE_TIME = "following_date_time";
    public static final String IS_EMERGENCY = "is_emergency";
    public static final String PHONEY_NAME = "phoney_name";
     

    public SessionManager(Context context){
        this._context = context;

        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
     

    public void createLoginSession(String userId, String firstName, String lastName, String email, String phone, String primaryFriendId, String emergencyMsg){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(USER_ID, userId);
        editor.putString(FIRST_NAME, firstName);
        editor.putString(LAST_NAME, lastName);
        editor.putString(EMAIL, email);
        editor.putString(PHONE, phone);
        editor.putString(PRIMARY_FRIEND_ID, primaryFriendId);
        editor.putString(EMERGENCY_MESSAGE, emergencyMsg);
        editor.commit();
    }


    /**
     * Used to update alaram delay data...
     * @param delaySeconds
     */
    public void updateAlarmDelay(String delaySeconds) {
        editor.putString(ALARM_DELAY, delaySeconds);
        editor.commit();
    }


    /**
     * Used to update broadcast data...
     * @param isBroadcast
     */
    public void updateBroadcastData(String isBroadcast) {
        editor.putString(IS_BROADCAST, isBroadcast);
        editor.commit();
    }


    /**
     * Used to update the emergencySound
     * @param emergencySound
     */
    public void updateEmergencySound(String emergencySound) {
        editor.putString(IS_EMERGENCY_SOUND, emergencySound);
        editor.commit();
    }


    /**
     * Used to update the primaryNumber
     * @param primaryNumber
     */
    public void setPrimaryNumber(String primaryNumber) {
        editor.putString(PRIMARY_NUMBER, primaryNumber);
        editor.commit();
    }


    public void setFollowMeData(UserDetails userDetails) {
        editor.putString(FOLLOW_ID, userDetails.getFollowId());
        editor.putString(FOLLOWING_NAME, userDetails.getFirstName());
        editor.putString(FOLLOWING_DATE_TIME, userDetails.getCreatedDate());
        editor.putString(IS_EMERGENCY, userDetails.getIsEmergency());

        editor.commit();
    }

    public void setPhoneyName(String phoneyName) {
        editor.putString(PHONEY_NAME, phoneyName);

        editor.commit();
    }


    public String getPhoneyName() {
        return pref.getString(PHONEY_NAME, null);
    }


    public void clearFollowMeData() {
        editor.putString(FOLLOW_ID, "");
        editor.putString(FOLLOWING_NAME, "");
        editor.commit();
    }
     
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        if(!this.isLoggedIn()){
                Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);

        }else {
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }
     
     
     
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(USER_ID, pref.getString(USER_ID, null));
        user.put(FIRST_NAME, pref.getString(FIRST_NAME, null));
        user.put(LAST_NAME, pref.getString(LAST_NAME, null));
        user.put(EMAIL, pref.getString(EMAIL, null));
        user.put(PHONE, pref.getString(PHONE, null));
        user.put(ALARM_DELAY, pref.getString(ALARM_DELAY, null));
        user.put(IS_BROADCAST, pref.getString(IS_BROADCAST, null));
        user.put(IS_EMERGENCY_SOUND, pref.getString(IS_EMERGENCY_SOUND, "1"));
        user.put(PRIMARY_NUMBER, pref.getString(PRIMARY_NUMBER, null));
        user.put(EMERGENCY_MESSAGE, pref.getString(EMERGENCY_MESSAGE, "Please help me"));
        user.put(PRIMARY_FRIEND_ID, pref.getString(PRIMARY_FRIEND_ID, null));
        user.put(FOLLOW_ID, pref.getString(FOLLOW_ID, null));
        user.put(FOLLOWING_NAME, pref.getString(FOLLOWING_NAME, null));
        user.put(FOLLOWING_DATE_TIME, pref.getString(FOLLOWING_DATE_TIME, null));

        return user;
    }
     
    /**
     * Clear session details
     * */
    public void logoutUser(){
        editor.clear();
        editor.commit();
         
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }
     
    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}