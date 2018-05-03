package pkg.android.skynet.yourcompanion.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.activity.MainActivity;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.asynctask.CustomMultiPartEntity;

/**
 * Created by ST-3 on 17-10-2017.
 */

public class UploadVideoService extends Service {

    private String mFileName = "";
    private SessionManager sessionManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sessionManager = new SessionManager(getApplicationContext());

        if (intent.getExtras() != null) {
            mFileName = intent.getStringExtra("FILE_NAME");
            new UploadTaskAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Used to upload video in background..
     */
    private class UploadTaskAsync extends AsyncTask<Void, Integer, String> {

        HashMap<String, String> user = sessionManager.getUserDetails();
        float totalSize ;

        NotificationCompat.Builder notificationBuilder;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //pb.setProgress(progress[0]);
            Intent intent = null;
            intent = new Intent(getApplicationContext(), MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), new Random().nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            notificationBuilder = new
                    NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(android.R.drawable.stat_sys_upload)
                    .setLargeIcon(icon)
                    .setContentTitle("Your Companion")
                    .setAutoCancel(false)
                    .setProgress(100, progress[0], false)
                    .setContentText("Uploading Video")
                    .setContentIntent(pendingIntent);
                    //.setStyle(new NotificationCompat.BigTextStyle().bigText(description));

            startForeground(Constants.UPLOAD_VIDEO_NOTIFICATION_ID, notificationBuilder.build());

        }

        @Override
        protected String doInBackground(Void... params) {
            return upload();
        }

        private String upload() {
            String responseString = "no";

            File sourceFile = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.VIDEO_PATH + mFileName);
            if (!sourceFile.isFile()) {
                return "not a file";
            }
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.BASE_URL + "uploadrecordvideo");


            try {
                CustomMultiPartEntity entity=new CustomMultiPartEntity(new CustomMultiPartEntity.ProgressListener() {

                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });

                entity.addPart("user_id", new StringBody(user.get(SessionManager.USER_ID)));
                entity.addPart("userfile", new FileBody(sourceFile));
                entity.addPart("device_id", new StringBody(FirebaseInstanceId.getInstance().getToken()));
                entity.addPart("device_type", new StringBody("1"));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                responseString = EntityUtils.toString(r_entity);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseString;

        }


        @Override
        protected void onPostExecute(String result) {
            //tv.setText(result);
            super.onPostExecute(result);
            stopSelf();

            Intent intent = null;
            intent = new Intent(getApplicationContext(), MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), new Random().nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            notificationBuilder = new
                    NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                    .setLargeIcon(icon)
                    .setContentTitle("Your Companion")
                    .setAutoCancel(false)
                    .setContentText("Video Successfully Uploaded")
                    .setContentIntent(pendingIntent);
            //.setStyle(new NotificationCompat.BigTextStyle().bigText(description));
            NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constants.UPLOAD_VIDEO_NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}
