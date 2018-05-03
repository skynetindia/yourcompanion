package pkg.android.skynet.yourcompanion.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;

import pkg.android.skynet.yourcompanion.R;

/**
 * Created by ST-3 on 02-10-2017.
 */

public class InCallActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer chronometer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_call);

        initControls();
    }


    /**
     * Used to initialize all the controls...
     */
    private void initControls() {
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.start();

        findViewById(R.id.img_decline).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_decline:
                finish();
                break;
        }
    }
}
