package org.alertpreparedness.platform.v1.dashboard.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.alertpreparedness.platform.v1.MainDrawer;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.AdvPreparednessFragment;
import org.alertpreparedness.platform.v1.risk_monitoring.view.RiskFragment;
import org.alertpreparedness.platform.v2.dashboard.home.HomeFragment;
import org.alertpreparedness.platform.v2.preparedness.MinimumPreparednessFragment;
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
                setFragment(new MinimumPreparednessFragment());
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