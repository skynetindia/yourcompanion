package pkg.android.skynet.yourcompanion.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.SessionManager;
import pkg.android.skynet.yourcompanion.app.YourCompanion;

public class DrawerFragment extends Fragment implements View.OnClickListener {
 
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;

    private SessionManager sessionManager;
    private YourCompanion yourCompanion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        sessionManager = new SessionManager(getActivity());
        yourCompanion = (YourCompanion)getActivity().getApplication();

        initControls(view);

        return view;
    }


    /**
     * Used to initialize all the controls..
     * @param view
     */
    private void initControls(View view) {
        view.findViewById(R.id.txt_home).setOnClickListener(this);
        view.findViewById(R.id.img_close_drawer).setOnClickListener(this);
        view.findViewById(R.id.txt_setting).setOnClickListener(this);
        view.findViewById(R.id.txt_feedback).setOnClickListener(this);
        view.findViewById(R.id.txt_logout).setOnClickListener(this);
        view.findViewById(R.id.txt_my_friends).setOnClickListener(this);
        view.findViewById(R.id.txt_helpline_num).setOnClickListener(this);
        view.findViewById(R.id.txt_history).setOnClickListener(this);
    }


    public void initDrawerViews(int fragmentId, DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
        containerView = getActivity().findViewById(fragmentId);
    }


    @Override
    public void onClick(View v) {
        Fragment fragment = null;

        switch (v.getId()) {
            case R.id.img_close_drawer:
                closeDrawer();
                break;

            case R.id.txt_home:
                fragment = new MainFragment();
                break;

            case R.id.txt_setting:
                fragment = new SettingFragment();
                break;

            case R.id.txt_logout:
                yourCompanion.askForLogout(getActivity());
                break;

            case R.id.txt_my_friends:
                fragment = new MyFriendsFragment();
                break;

            case R.id.txt_helpline_num:
                fragment = new HelplineNumberFragment();
                break;

            case R.id.txt_feedback:
                fragment = new FeedbackFragment();
                break;

            case R.id.txt_history:
                fragment = new HistoryFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        }
        closeDrawer();
    }


    /**
     * Used to open a drawer..
     */
    public void openDrawer () {
        mDrawerLayout.openDrawer(containerView);
    }


    /**
     * Used to Close a drawer..
     */
    public void closeDrawer () {
        mDrawerLayout.closeDrawer(containerView);
    }
}