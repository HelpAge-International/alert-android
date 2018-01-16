package org.alertpreparedness.platform.alert.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import org.alertpreparedness.platform.alert.BaseActivity;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.UserId;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            // Start your app main activity
            AppUtils.getDatabase();
            System.out.println("FirebaseAuth.getInstance().getCurrentUser() = " + FirebaseAuth.getInstance().getCurrentUser());
            System.out.println("new UserInfo().getUser() = " + new UserInfo().getUser());
            System.out.println("PreferHelper.getString(this, Constants.UID) = " + PreferHelper.getString(this, Constants.UID));

            if (FirebaseAuth.getInstance().getCurrentUser() != null && new UserInfo().getUser() != null && !PreferHelper.getString(this, Constants.UID).equals("")) {
                startActivity(new Intent(this, HomeScreen.class));
            } else {
                startActivity(new Intent(this, LoginScreen.class));
            }
            finish();
        }, SPLASH_TIME_OUT);
    }
}
