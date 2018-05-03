package pkg.android.skynet.yourcompanion.dialogfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.ReqResAPICallBacks;
import pkg.android.skynet.yourcompanion.callbacks.ReqRespCallBack;
import pkg.android.skynet.yourcompanion.fragment.DrawerFragment;
import pkg.android.skynet.yourcompanion.fragment.MainFragment;
import pkg.android.skynet.yourcompanion.services.EmergencyHelpService;
import pkg.android.skynet.yourcompanion.services.FollowMeService;
import pkg.android.skynet.yourcompanion.services.TimerService;

/**
 * Created by ST-3 on 07-10-2017.
 */

public class AlarmActivatedDialog extends DialogFragment implements View.OnClickListener {

    private String mOpenFrom = "";

    private SessionManager sessionManager;
    private YourCompanion yourCompanion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_alaram_activated, container, false);

        sessionManager = new SessionManager(getActivity());
        yourCompanion = (YourCompanion)getActivity().getApplication();

        initControls(view);

        if (getArguments() != null) {
            mOpenFrom = getArguments().getString(Constants.OPEN_FROM);
        }

        return view;
    }


    /**
     * Used to initialize all the controls..
     * @param view
     */
    private void initControls(View view) {
        view.findViewById(R.id.txt_turn_off).setOnClickListener(this);
        view.findViewById(R.id.txt_map).setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_map:
                getDialog().dismiss();
                break;

            case R.id.txt_turn_off:
                /*if (mOpenFrom.equals(Constants.TIMER_SERVICE)){
                    getActivity().stopService(new Intent(getActivity(), TimerService.class));

                }else if (mOpenFrom.equals(Constants.EMERGENCY_SERVICE)) {
                    getActivity().stopService(new Intent(getActivity(), EmergencyHelpService.class));
                }*/
                Fragment fragment =  (MainFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.container_body);
                ((MainFragment)fragment).stopFollowMe();
                getDialog().dismiss();
                break;
        }
    }
}
