package pkg.android.skynet.yourcompanion;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Random;

import pkg.android.skynet.yourcompanion.activity.MainActivity;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.models.UserDetails;
import pkg.android.skynet.yourcompanion.services.EmergencyHelpService;
import pkg.android.skynet.yourcompanion.services.FollowingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

	private Intent intent = null;
	private Intent broadcastIntent;

	private YourCompanion mAppContext;

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		mAppContext = (YourCompanion)getApplication();

		System.out.println("NOTIFICATION RECEIVED -> " + remoteMessage.getData());

		if (remoteMessage.getData().get("notify_type").equals(Constants.NOTIFY_TYPE_FRIEND_ADD)) {
			intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FRIEND_ADD);
			sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("text"), Constants.FRIEND_REQ_NOTIFICATION_ID, "");

			broadcastIntent = new Intent("RECEIVER_NOTIFICATION");
			broadcastIntent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FRIEND_ADD);
			sendBroadcast(broadcastIntent);

		}else if (remoteMessage.getData().get("notify_type").equals(Constants.NOTIFY_TYPE_FRIEND_DELETE)) {
			intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FRIEND_DELETE);
			sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("text"), Constants.FRIEND_REQ_NOTIFICATION_ID, "");

			broadcastIntent = new Intent("RECEIVER_NOTIFICATION");
			broadcastIntent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FRIEND_DELETE);
			sendBroadcast(broadcastIntent);

		}else if (remoteMessage.getData().get("notify_type").equals(Constants.NOTIFY_TYPE_SHARE_LOCATION)) {
			UserDetails userDetails = new UserDetails();

			try {
				JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("userdetail"));

				userDetails.setFirstName(jsonObject.getString("fname"));
				userDetails.setUserId(jsonObject.getString("user_id"));
				userDetails.setLatitude(jsonObject.getString("latitude"));
				userDetails.setLongitude(jsonObject.getString("longitude"));
				userDetails.setMapLink(remoteMessage.getData().get("map_link"));
				userDetails.setHelpMessage(jsonObject.getString("help_message"));

				System.out.println("JSON -> " + jsonObject.toString());
			}catch (Exception e) {
				e.printStackTrace();
			}

			intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_SHARE_LOCATION);
			intent.putExtra("DATA", userDetails);
			sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("text"), Constants.SHARE_LOCATION_NOTIFICATION_ID, "");

			broadcastIntent = new Intent("RECEIVER_NOTIFICATION");
			broadcastIntent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_SHARE_LOCATION);
			broadcastIntent.putExtra("DATA", userDetails);
			sendBroadcast(broadcastIntent);

		}else if (remoteMessage.getData().get("notify_type").equals(Constants.NOTIFY_TYPE_FOLLOW_REQ)) {
				UserDetails userDetails = new UserDetails();

			try {
				JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("object"));
				JSONObject senderObjet = jsonObject.getJSONObject("sender");

				userDetails.setFirstName(senderObjet.getString("fname"));
				userDetails.setUserId(senderObjet.getString("user_id"));
				userDetails.setPhone(senderObjet.getString("phone"));
				userDetails.setFollowId(remoteMessage.getData().get("follow_id"));
				userDetails.setCreatedDate(remoteMessage.getData().get("create_date"));
				userDetails.setIsEmergency(remoteMessage.getData().get("is_emergency"));
				System.out.println("JSON -> " + senderObjet.toString());

			}catch (Exception e) {
				e.printStackTrace();
			}

			intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FOLLOW_REQ);
			intent.putExtra("DATA", userDetails);
			sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("text"), Constants.FOLLOW_ME_NOTIFICATION_ID, remoteMessage.getData().get("is_emergency"));

			broadcastIntent = new Intent("RECEIVER_NOTIFICATION");
			broadcastIntent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FOLLOW_REQ);
			broadcastIntent.putExtra("DATA", userDetails);
			sendBroadcast(broadcastIntent);

		}else if (remoteMessage.getData().get("notify_type").equals(Constants.NOTIFY_TYPE_FOLLOW_STOPPED)) {
			UserDetails userDetails = new UserDetails();

			try {
				JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("object"));
				JSONObject senderObjet = jsonObject.getJSONObject("sender");

				userDetails.setFirstName(senderObjet.getString("fname"));
				userDetails.setUserId(senderObjet.getString("user_id"));
				userDetails.setPhone(senderObjet.getString("phone"));

				System.out.println("JSON -> " + senderObjet.toString());
			}catch (Exception e) {
				e.printStackTrace();
			}

			intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FOLLOW_STOPPED);
			intent.putExtra("DATA", userDetails);
			sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("text"), Constants.FOLLOW_ME_NOTIFICATION_ID, "");

			broadcastIntent = new Intent("RECEIVER_NOTIFICATION");
			broadcastIntent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FOLLOW_STOPPED);
			broadcastIntent.putExtra("DATA", userDetails);
			sendBroadcast(broadcastIntent);

			// Stop setvice when user stop follow me.
			if(mAppContext.isServiceRunning(FollowingService.class)) {
				stopService(new Intent(getApplicationContext(), FollowingService.class));
			}

		}else if (remoteMessage.getData().get("notify_type").equals(Constants.NOTIFY_TYPE_FOLLOW_APPROVED)) {
			UserDetails userDetails = new UserDetails();

			try {
				JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("object"));
				JSONObject senderObjet = jsonObject.getJSONObject("sender");

				userDetails.setFirstName(senderObjet.getString("fname"));
				userDetails.setUserId(senderObjet.getString("user_id"));
				userDetails.setPhone(senderObjet.getString("phone"));

				System.out.println("JSON -> " + senderObjet.toString());
			}catch (Exception e) {
				e.printStackTrace();
			}

			broadcastIntent = new Intent("RECEIVER_NOTIFICATION");
			broadcastIntent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FOLLOW_APPROVED);
			broadcastIntent.putExtra("DATA", userDetails);
			sendBroadcast(broadcastIntent);

		}else if (remoteMessage.getData().get("notify_type").equals(Constants.NOTIFY_TYPE_FRIEND_ACCEPT)) {
			intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FRIEND_ACCEPT);
			sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("text"), Constants.FRIEND_REQ_ACCEPT_NOTIFICATION_ID, "");

		}else if (remoteMessage.getData().get("notify_type").equals(Constants.NOTIFY_TYPE_FRIEND_DELETE)) {
			intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.putExtra("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FRIEND_DELETE);
			sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("text"), Constants.FRIEND_REQ_DELETE_NOTIFICATION_ID, "");

		}
	}


	/**
	 * Used to send notification to user..
	 * @param title
	 * @param description
	 * @param notificationId
	 */
	private void sendNotification(String title, String description, int notificationId, String isEmergency) {
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

		NotificationCompat.Builder notificationBuilder = new
				NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_notification)
				.setLargeIcon(icon)
				.setContentTitle(title)
				.setAutoCancel(true)
				.setContentText(description)
				.setContentIntent(pendingIntent)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(description));

		if (isEmergency.equals("1")) {
			final MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.siren);
			mPlayer.start();

			notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
		}else {
			notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
		}

		//int notificationId = new Random().nextInt();
		android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationId, notificationBuilder.build());
	}
}
