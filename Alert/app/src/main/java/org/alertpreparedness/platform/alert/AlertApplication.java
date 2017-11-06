package org.alertpreparedness.platform.alert;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import net.danlew.android.joda.JodaTimeAndroid;

import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import timber.log.Timber;

/**
 * Created by fei on 06/11/2017.
 */

public class AlertApplication extends Application {

    public static final boolean IS_LIVE = false;

//    public static final String API_KEY = "";

    public enum APP_STATUS {
        LIVE,
        SAND,
        TESTING,
        UAT
    }

    public static final APP_STATUS CURRENT_STATUS = APP_STATUS.SAND;

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        // JODA
        JodaTimeAndroid.init(this);

        // FCM
//        FCMNotifications.setup(CustomProcessor.class, getApplicationContext(), API_KEY);

        // Live-Only additions
        if (!IS_LIVE) {
            // Leak Canary
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
        }

        // Debug-Only builds
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Check APP Status
        if (CURRENT_STATUS == APP_STATUS.LIVE) {
            PreferHelper.putString(getApplicationContext(), Constants.APP_STATUS, Constants.APP_STATUS_LIVE);
        } else if (CURRENT_STATUS == APP_STATUS.TESTING) {
            PreferHelper.putString(getApplicationContext(), Constants.APP_STATUS, Constants.APP_STATUS_TEST);
        } else if (CURRENT_STATUS == APP_STATUS.UAT) {
            PreferHelper.putString(getApplicationContext(), Constants.APP_STATUS, Constants.APP_STATUS_UAT);
        } else if (CURRENT_STATUS == APP_STATUS.SAND) {
            PreferHelper.putString(getApplicationContext(), Constants.APP_STATUS, Constants.APP_STATUS_SAND);
        }

    }

    public static Context getContext() {
        if (sContext == null) {
            throw new IllegalArgumentException("Context is null!!");
        }
        return sContext;
    }
}
