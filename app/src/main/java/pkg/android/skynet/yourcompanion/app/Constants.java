package pkg.android.skynet.yourcompanion.app;

/**
 * Created by as on 5/6/2016.
 */
public class Constants {
    public static int MEHTOD_POST = 1;
    public static int MEHTOD_GET = 2;
    public static int MEHTOD_JSON = 3;

    public static final String FAILURE = "0";
    public static final String SUCCESS="1";
    public static final String SUCCESS2 = "2";

    public static final String BROADCAST_ON = "1";
    public static final String BROADCAST_OFF = "0";
    public static final String SOUND_ON = "1";
    public static final String SOUND_OFF = "0";

    public  static final String IMAGE_PATH = "YourCompanion/Media/Images";
    public  static final String VIDEO_PATH = "YourCompanion/Media/Videos/";

    public  static final String BASE_URL = "http://yourcomp.nobat.webfactional.com/api/";
    public  static final String BASE_IMAGE_URL = "http://yourcomp.nobat.webfactional.com/images/";

    public static final String PHONEY_PREF = "pref_phoney";
    public static final String SELECTED_TIME = "selected_time";
    public static final String LOCATION_STATUS = "location_status";
    public static final String OPEN_FROM = "open_from";
    public static final String EMERGENCY_SERVICE = "emergency_service";
    public static final String TIMER_SERVICE = "timer_changes";
    public static final String FOLLOW_ME_SERVICE = "follow_me_service";

    public static final String ACTION_EMERGENCY_SERVICE_START = "emergency_service_start";
    public static final String ACTION_TIMER_SERVICE = "timer_service";

    // Notifications ids
    public static final int TIMER_NOTIFICATION_ID = 1000;
    public static final int ALARM_NOTIFICATION_ID = 1001;
    public static final int UPLOAD_VIDEO_NOTIFICATION_ID = 1002;
    public static final int SHARE_LOCATION_NOTIFICATION_ID = 1003;
    public static final int FRIEND_REQ_NOTIFICATION_ID = 1004;
    public static final int FOLLOW_ME_NOTIFICATION_ID = 1005;
    public static final int FRIEND_REQ_ACCEPT_NOTIFICATION_ID = 1005;
    public static final int FRIEND_REQ_DELETE_NOTIFICATION_ID = 1006;

    public static final String NOTIFY_TYPE_SHARE_LOCATION = "10";
    public static final String NOTIFY_TYPE_EMERGENCY = "11";
    public static final String NOTIFY_TYPE_FOLLOW_REQ = "12";
    public static final String NOTIFY_TYPE_FOLLOW_APPROVED = "13";
    public static final String NOTIFY_TYPE_FOLLOW_DECLINE = "14";
    public static final String NOTIFY_TYPE_FOLLOW_STOPPED = "15";
    public static final String NOTIFY_TYPE_FRIEND_ADD = "1";
    public static final String NOTIFY_TYPE_FRIEND_ACCEPT = "2";
    public static final String NOTIFY_TYPE_FRIEND_DELETE = "6";

    public static boolean isAlertDialogShown = false;
}
