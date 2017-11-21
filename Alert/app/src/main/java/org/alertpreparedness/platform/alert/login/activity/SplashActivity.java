package org.alertpreparedness.platform.alert.login.activity;

import android.content.Intent;
import android.os.Bundle;

import org.alertpreparedness.platform.alert.BaseActivity;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.utils.AppUtils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String uid = getUid();
        AppUtils.getDatabase();
        if (uid != null) {
            startActivity(new Intent(SplashActivity.this, HomeScreen.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginScreen.class));
        }
        finish();
    }
}
