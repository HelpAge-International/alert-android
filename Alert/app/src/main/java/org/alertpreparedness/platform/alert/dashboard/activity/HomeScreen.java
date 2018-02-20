package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.AdvPreparednessFragment;
import org.alertpreparedness.platform.alert.dashboard.fragment.HomeFragment;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.MinPreparednessFragment;
import org.alertpreparedness.platform.alert.risk_monitoring.view.RiskFragment;

import timber.log.Timber;


public class HomeScreen extends MainDrawer {

    private static final int STORAGE_RC = 0x0013;

    public static final int SCREEN_HOME = 0;
    public static final int SCREEN_INDICATOR = 1;
    public static final int SCREEN_APA = 2;
    public static final int SCREEN_MPA = 3;

    public static final String START_SCREEN = "START_SCREEN";

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int startScreen = getIntent().getIntExtra(START_SCREEN, SCREEN_HOME);

        Timber.d("StartScreen: " + startScreen + " - " + getIntent().hasExtra(START_SCREEN));

        switch (startScreen){
            case SCREEN_INDICATOR:
                setFragment(new RiskFragment());
                break;
            case SCREEN_APA:
                setFragment(new AdvPreparednessFragment());
                break;
            case SCREEN_MPA:
                setFragment(new MinPreparednessFragment());
                break;
            case SCREEN_HOME:
            default:
                setFragment(new HomeFragment());


        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Check for Storage permissions
        int permCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permCheck != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_RC);
            }
        }
    }

}