package pkg.android.skynet.yourcompanion.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Calendar;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.activity.IncomingFakeCallActivity;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.receiver.FakeCallReceiver;

/**
 * Created by ST-3 on 30-09-2017.
 */

public class PhoneyFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private EditText mCallFromEdt;
    private YourCompanion yourCompanion;

    private SharedPreferences mPhoneyPref;
    private SharedPreferences.Editor editor;

    private Calendar calendar = Calendar.getInstance();

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phoney, container, false);

        yourCompanion = (YourCompanion)getActivity().getApplication();
        sessionManager = new SessionManager(getActivity());

        initControls(view);
        setData();

        mPhoneyPref = getActivity().getSharedPreferences(Constants.PHONEY_PREF, 0);
        editor = mPhoneyPref.edit();

        return view;
    }


    /**
     * Used to initialize all the controls..
     * @param view
     */
    private void initControls(View view) {
        mCallFromEdt = (EditText)view.findViewById(R.id.edt_call_from);
        mCallFromEdt.addTextChangedListener(this);

        view.findViewById(R.id.img_call_now).setOnClickListener(this);
        view.findViewById(R.id.ll_30sec).setOnClickListener(this);
        view.findViewById(R.id.ll_5min).setOnClickListener(this);
        view.findViewById(R.id.ll_30min).setOnClickListener(this);
        view.findViewById(R.id.ll_1hour).setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);
    }


    /**
     * Used to set saved data..
     */
    private void setData() {
        mCallFromEdt.setText(sessionManager.getPhoneyName());
    }


    /**
     * Used to setup a fake call...
     * @param time
     */
    public void setupFakeCall(String time){
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), FakeCallReceiver.class);

        PendingIntent fakePendingIntent = PendingIntent.getBroadcast(getActivity(), 12345,  intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), fakePendingIntent);

        yourCompanion.showAlertDialog(getActivity(), getString(R.string.msg_call_set) + " "+ time, "Ok");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_call_now:
                startActivity(new Intent(getActivity(), IncomingFakeCallActivity.class));
                break;

            case R.id.ll_30sec:
                calendar.add(Calendar.SECOND, 30);
                setupFakeCall("30 Sec");
                break;

            case R.id.ll_5min:
                calendar.add(Calendar.MINUTE, 5);
                setupFakeCall("5 Mins");
                break;

            case R.id.ll_30min:
                calendar.add(Calendar.MINUTE, 30);
                setupFakeCall("30 Mins");
                break;

            case R.id.ll_1hour:
                calendar.add(Calendar.HOUR, 1);
                setupFakeCall("1 Hour");
                break;

            case R.id.img_back:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new MainFragment()).commit();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCallFromEdt.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);

        sessionManager.setPhoneyName(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
