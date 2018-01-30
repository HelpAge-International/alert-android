package org.alertpreparedness.platform.alert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.leakcanary.LeakCanary;

import net.danlew.android.joda.JodaTimeAndroid;

import org.acra.ACRA;
import org.acra.annotation.AcraMailSender;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.MailSenderConfiguration;
import org.acra.data.StringFormat;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseUserRef;
import org.alertpreparedness.platform.alert.dagger.annotation.PermissionRef;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.alertpreparedness.platform.alert.utils.SettingsFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import shortbread.Shortbread;
import timber.log.Timber;

/**
 * Created by fei on 06/11/2017.
 */
@AcraMailSender(mailTo = "tj@rolleragency.co.uk")
public class AlertApplication extends Application implements ValueEventListener {

    public static final boolean IS_LIVE = true;
    private User user;

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
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        FirebaseAuth.getInstance().signOut();
        Shortbread.create(this);

        Realm.init(this);
        // JODA
        JodaTimeAndroid.init(this);

        ACRA.init(this);

        if(!PreferHelper.getBoolean(this, Constants.HAS_RUN_BEFORE)) {
            System.out.println("PreferHelper.getString(this, Constants.HAS_RUN_BEFORE) = " + PreferHelper.getString(this, Constants.HAS_RUN_BEFORE));
            FirebaseAuth.getInstance().signOut();
            PreferHelper.deleteString(this, Constants.UID);
            PreferHelper.putBoolean(this, Constants.HAS_RUN_BEFORE, true);
        }

        // Live-Only additions
        if (!IS_LIVE && CURRENT_STATUS != APP_STATUS.SAND) {
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
        }
        else if (CURRENT_STATUS == APP_STATUS.TESTING) {
            PreferHelper.putString(getApplicationContext(), Constants.APP_STATUS, Constants.APP_STATUS_TEST);
        }
        else if (CURRENT_STATUS == APP_STATUS.UAT) {
            PreferHelper.putString(getApplicationContext(), Constants.APP_STATUS, Constants.APP_STATUS_UAT);
        }
        else if (CURRENT_STATUS == APP_STATUS.SAND) {
            PreferHelper.putString(getApplicationContext(), Constants.APP_STATUS, Constants.APP_STATUS_SAND);
        }

        DependencyInjector.initialize(this);

    }

    public static Context getContext() {
        if (sContext == null) {
            throw new IllegalArgumentException("Context is null!!");
        }
        return sContext;
    }

    public void startPermissionListeners(@PermissionRef DatabaseReference permissionsRef, User user) {
        if(permissionsRef != null) {
            this.user = user;
            permissionsRef.addValueEventListener(this);
        }
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(dataSnapshot.getKey().equals("permissionSettings")) {
            SettingsFactory.processCountryLevelSettings(dataSnapshot, user);
        }
        else {
            SettingsFactory.proccessPartnerSettings(dataSnapshot, user);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
