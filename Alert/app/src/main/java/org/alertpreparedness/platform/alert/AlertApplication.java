package org.alertpreparedness.platform.alert;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.leakcanary.LeakCanary;

import net.danlew.android.joda.JodaTimeAndroid;

import org.acra.ACRA;
import org.acra.annotation.AcraMailSender;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.PermissionRef;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.notifications.ActionUpdateNotificationHandler;
import org.alertpreparedness.platform.alert.notifications.IndicatorUpdateNotificationHandler;
import org.alertpreparedness.platform.alert.notifications.NotificationIdHandler;
import org.alertpreparedness.platform.alert.notifications.ResponsePlanUpdateNotificationHandler;
import org.alertpreparedness.platform.alert.offline.OfflineSyncHandler;
import org.alertpreparedness.platform.alert.offline.SyncJobService;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.alertpreparedness.platform.alert.utils.SettingsFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.realm.Realm;
import shortbread.Shortbread;
import timber.log.Timber;

/**
 * Created by fei on 06/11/2017.
 */
@AcraMailSender(mailTo = "tj@rolleragency.co.uk")
public class AlertApplication extends MultiDexApplication implements ValueEventListener {

    public static final boolean IS_LIVE = false;
    private User user;

//    public static final String API_KEY = "";

    public enum APP_STATUS {
        LIVE,
        SAND,
        TESTING,
        UAT
    }

    public static final APP_STATUS CURRENT_STATUS = APP_STATUS.SAND;

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        DependencyInjector.initialize(this);

        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

//        FirebaseAuth.getInstance().signOut();
        Shortbread.create(this);

        Realm.init(this);
        // JODA
        JodaTimeAndroid.init(this);

        boolean loggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;

        System.out.println("FirebaseAuth.getInstance().getCurrentUser() = " + FirebaseAuth.getInstance().getCurrentUser());

//        ACRA.init(this);

        if(!PreferHelper.getBoolean(this, Constants.HAS_RUN_BEFORE)) {
            System.out.println("PreferHelper.getString(this, Constants.HAS_RUN_BEFORE) = " + PreferHelper.getString(this, Constants.HAS_RUN_BEFORE));
            FirebaseAuth.getInstance().signOut();
            PreferHelper.deleteString(this, Constants.UID);
            PreferHelper.putBoolean(this, Constants.HAS_RUN_BEFORE, true);
        }

        // Live-Only additions
        if (!IS_LIVE && CURRENT_STATUS == APP_STATUS.SAND) {
            // Leak Canary
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
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

        if(loggedIn) {
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
            Job myJob = dispatcher.newJobBuilder()
                    .setService(SyncJobService.class) // the JobService that will be called
                    .setTag("sync")        // uniquely identifies the job
                    .setRecurring(true)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setReplaceCurrent(true)
                    .setLifetime(Lifetime.FOREVER)
                    .setTrigger(Trigger.executionWindow((int) TimeUnit.MINUTES.toSeconds(60), (int) TimeUnit.MINUTES.toSeconds(90)))
                    .build();

            dispatcher.schedule(myJob);
            new IndicatorUpdateNotificationHandler(this).scheduleAllNotifications();
            new ActionUpdateNotificationHandler(this).scheduleAllNotifications();
            new ResponsePlanUpdateNotificationHandler(this).scheduleAllNotifications();

            if(FirebaseInstanceId.getInstance().getToken() != null) {
                new NotificationIdHandler().registerDeviceId(new UserInfo().getUser().getUserID(), FirebaseInstanceId.getInstance().getToken());
            }

        }
        else{
            //TODO: CANCEL ALL NOTIFICATIONS?
        }
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
