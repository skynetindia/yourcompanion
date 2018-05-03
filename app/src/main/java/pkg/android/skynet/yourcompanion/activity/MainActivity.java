package pkg.android.skynet.yourcompanion.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import pkg.android.skynet.yourcompanion.R;
import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.fragment.DrawerFragment;
import pkg.android.skynet.yourcompanion.fragment.MainFragment;
import pkg.android.skynet.yourcompanion.fragment.MyFriendsFragment;
import pkg.android.skynet.yourcompanion.models.UserDetails;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerFragment drawerFragment;
    private ImageView mDrawerImg;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;

    private YourCompanion yourCompanion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yourCompanion = (YourCompanion)getApplication();

        initConrols();
        openGpsDialog();

        drawerFragment = (DrawerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.initDrawerViews(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        if (getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FRIEND_ADD) || getIntent().getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FRIEND_DELETE) ||
                    getIntent().getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FRIEND_ACCEPT)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new MyFriendsFragment()).commit();

            }else if (getIntent().getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_SHARE_LOCATION)) {
                MainFragment mainFragment = new MainFragment();
                Bundle bundle = new Bundle();
                bundle.putString("NOTIFY_TYPE", Constants.NOTIFY_TYPE_SHARE_LOCATION);
                bundle.putSerializable("DATA", (UserDetails)getIntent().getExtras().get("DATA"));
                mainFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_body, mainFragment).commit();

            }else if (getIntent().getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FOLLOW_REQ)) {
                MainFragment mainFragment = new MainFragment();
                Bundle bundle = new Bundle();
                bundle.putString("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FOLLOW_REQ);
                bundle.putSerializable("DATA", (UserDetails)getIntent().getExtras().get("DATA"));
                mainFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_body, mainFragment).commit();

            }else if (getIntent().getStringExtra("NOTIFY_TYPE").equals(Constants.NOTIFY_TYPE_FOLLOW_STOPPED)) {
                MainFragment mainFragment = new MainFragment();
                Bundle bundle = new Bundle();
                bundle.putString("NOTIFY_TYPE", Constants.NOTIFY_TYPE_FOLLOW_STOPPED);
                bundle.putSerializable("DATA", (UserDetails)getIntent().getExtras().get("DATA"));
                mainFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_body, mainFragment).commit();

            }
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_body, new MainFragment()).commit();
        }

    }


    /**
     * Used to initialize all the controls..
     */
    private void initConrols() {
        findViewById(R.id.img_drawer).setOnClickListener(this);
    }


    /**
     * Used to ask user for enable GPS..
     */
    public void openGpsDialog() {
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_confirm_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();

        alertDialog.setCancelable(false);
        alertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        final TextView titleTxt, messageTxt, positiveTxt, negativeTxt;
        ImageView alertTypeImg = null;

        messageTxt = (TextView)dialogView.findViewById(R.id.txt_message);
        titleTxt = (TextView)dialogView.findViewById(R.id.txt_title);
        positiveTxt = (TextView)dialogView.findViewById(R.id.txt_positive);
        negativeTxt = (TextView)dialogView.findViewById(R.id.txt_negative);
        negativeTxt.setVisibility(View.GONE);

        titleTxt.setText(getString(R.string.title_location_seting));
        positiveTxt.setText("Turn on location");
        messageTxt.setText(R.string.msg_location_disable);

        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                alertDialog.dismiss();
            }
        });

        negativeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_drawer:
                drawerFragment.openDrawer();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    BroadcastReceiver locationReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getExtras() != null) {
                boolean isLocationEnable = intent.getExtras().getBoolean(Constants.LOCATION_STATUS);

                if (!isLocationEnable)
                    alertDialog.show();
                else
                    alertDialog.dismiss();
            }else {
                Toast.makeText(getApplicationContext(), "Null intent", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(locationReceiver, new IntentFilter("PROVIDERS_CHANGED"));
    }
}
