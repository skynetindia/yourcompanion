package pkg.android.skynet.yourcompanion.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.Constants;

/**
 * Created by ST-3 on 13-10-2017.
 */

public class VideoRecordingService extends Service implements SurfaceHolder.Callback {

    private static final String TAG = "RecorderService";

    private View mFloatingView;
    private SurfaceView mSurfaceView;
    private WindowManager mWindowManager;
    private SurfaceHolder mSurfaceHolder;
    private static Camera mServiceCamera;
    private boolean mRecordingStatus;
    private MediaRecorder mMediaRecorder;

    private String mFileName = "";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mFloatingView = inflater.inflate(R.layout.layout_surface_view, null);

        mRecordingStatus = false;

        mSurfaceView = (SurfaceView)mFloatingView.findViewById(R.id.surface_video_record);
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.setZOrderMediaOverlay(true);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //mSurfaceView = MainFragment.mVideoRecordSurface;
        //mSurfaceHolder = MainFragment.mVideoRecordSurfaceHolder;
        mServiceCamera = Camera.open(0);


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);



        //Specify the view position
        params.gravity = Gravity.BOTTOM | Gravity.RIGHT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVideoRecording();
        mWindowManager.removeView(mFloatingView);

        Intent uploadVdoServiceIntent = new Intent(getApplicationContext(), UploadVideoService.class);
        uploadVdoServiceIntent.putExtra("FILE_NAME", mFileName);
        startService(uploadVdoServiceIntent);

    }


    private boolean startVideoRecoring() {
        try {
            Toast.makeText(getBaseContext(), "Recording Started", Toast.LENGTH_SHORT).show();

            mFileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".mp4";

            //mServiceCamera = Camera.open();
            Camera.Parameters params = mServiceCamera.getParameters();
            mServiceCamera.setParameters(params);
            Camera.Parameters p = mServiceCamera.getParameters();

            final List<Size> listSize = p.getSupportedPreviewSizes();
            Size mPreviewSize = listSize.get(2);
            Log.v(TAG, "use: width = " + mPreviewSize.width
                    + " height = " + mPreviewSize.height);
            p.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            p.setPreviewFormat(PixelFormat.YCbCr_420_SP);
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mServiceCamera.setParameters(p);

            try {
                mServiceCamera.setPreviewDisplay(mSurfaceHolder);
                mServiceCamera.startPreview();
            }
            catch (IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

            Size size = listSize.get(2);
            int width = size.width;
            int heigt = size.height;

            mServiceCamera.unlock();

            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setCamera(mServiceCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            mMediaRecorder.setVideoEncodingBitRate(150000);
            mMediaRecorder.setVideoSize(1080, 720);
            mMediaRecorder.setVideoFrameRate(15);
            mMediaRecorder.setProfile(CamcorderProfile.get(1, CamcorderProfile.QUALITY_HIGH));
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + File.separator + Constants.VIDEO_PATH + mFileName);
            mMediaRecorder.setOrientationHint(90);

            mMediaRecorder.prepare();
            mMediaRecorder.start();


            mRecordingStatus = true;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     *
     */
    private void stopVideoRecording() {
        Toast.makeText(getBaseContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
        try {
            mServiceCamera.reconnect();
            mServiceCamera.stopPreview();
            mServiceCamera.release();
            mServiceCamera = null;

            //mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mRecordingStatus)
            startVideoRecoring();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
