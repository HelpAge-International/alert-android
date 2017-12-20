package org.alertpreparedness.platform.alert.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.alertpreparedness.platform.alert.BaseActivity;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.utils.AppUtils;

public class SplashActivity extends BaseActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String uid = getUid();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                AppUtils.getDatabase();
                if (uid != null) {
                    startActivity(new Intent(SplashActivity.this, HomeScreen.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginScreen.class));
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
