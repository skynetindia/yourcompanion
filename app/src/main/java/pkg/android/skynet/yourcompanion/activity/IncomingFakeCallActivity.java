package pkg.android.skynet.yourcompanion.activity;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.SessionManager;

/**
 * Created by ST-3 on 29-09-2017.
 */

public class IncomingFakeCallActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mCallFromTxt;

    private Ringtone ringtone;
    private Vibrator vibrator;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_fake_call);

        sessionManager = new SessionManager(getApplicationContext());

        initControls();
        playRingVibrate();
        screenTimeOut();
    }


    /**
     * Used to initialize all the controls...
     */
    private void initControls() {
        mCallFromTxt = (TextView)findViewById(R.id.txt_call_from);
        mCallFromTxt.setText(sessionManager.getPhoneyName());

        findViewById(R.id.txt_decline).setOnClickListener(this);
        findViewById(R.id.txt_accept).setOnClickListener(this);
    }


    /**
     * Used to play ringtone and vibrate...
     */
    private void playRingVibrate() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ringtone.play();

        long[] pattern = {1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
    }


    /**
     * Used to set screen time out...
     */
    private void screenTimeOut() {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                finish();
            }
        }, 25000);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_decline:
                finish();
                break;

            case R.id.txt_accept:
                startActivity(new Intent(this, InCallActivity.class));
                finish();
                break;
        }
    }


    @Override
    protected void onPause() {
        ringtone.stop();
        vibrator.cancel();

        super.onPause();
    }


    @Override
    protected void onDestroy() {
        ringtone.stop();
        vibrator.cancel();

        super.onDestroy();
    }
}
