package pkg.android.skynet.yourcompanion.dialogfragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.services.EmergencyHelpService;

/**
 * Created by ST-3 on 07-10-2017.
 */

public class AlarmTriggerDialog extends DialogFragment implements View.OnClickListener {

    private TextView mTriggerTimerTxt;

    private MyCountDownTimer myCountDownTimer;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_alarm_trigger, container, false);

        sessionManager = new SessionManager(getActivity());

        initControls(view);

        HashMap<String, String> user = sessionManager.getUserDetails();
        String[] alarmDelay = user.get(SessionManager.ALARM_DELAY).split(" ");
        long timer = TimeUnit.SECONDS.toMillis(Long.parseLong(alarmDelay[0]) + 1);
        System.out.println("Alarm Delay");

        myCountDownTimer = new MyCountDownTimer(timer, 1000);
        myCountDownTimer.start();

        return view;
    }


    /**
     * Used to initialize all the controls..
     * @param view
     */
    private void initControls(View view) {
        mTriggerTimerTxt = (TextView)view.findViewById(R.id.txt_trigger_timer);

        view.findViewById(R.id.txt_dismiss).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_dismiss:
                myCountDownTimer.cancel();
                getDialog().dismiss();
                break;
        }
    }


    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long interval) {
            super(millisInFuture, interval);
        }

        @Override
        public void onFinish() {
            getActivity().startService(new Intent(getActivity(), EmergencyHelpService.class));
            getDialog().dismiss();

            AlarmActivatedDialog alarmActivatedDialog = new AlarmActivatedDialog();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.OPEN_FROM, Constants.EMERGENCY_SERVICE);
            alarmActivatedDialog.setArguments(bundle);
            alarmActivatedDialog.show(getActivity().getSupportFragmentManager(), "Alarm Activated");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //System.out.println("CountDownTimer -> " + millisUntilFinished / 1000);
            long second = millisUntilFinished / 1000;

            long sec = second % 60;
            long min = (second / 60)%60;
            long hours = (second/60)/60;

            mTriggerTimerTxt.setText("" + sec);
            //System.out.println(hours + ":" + min + ":" + sec);
        }
    }
}
