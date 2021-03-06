package org.alertpreparedness.platform.v1;

import android.os.StrictMode;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import net.danlew.android.joda.JodaTimeAndroid;
import org.alertpreparedness.platform.BuildConfig;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.PermissionRef;
import org.alertpreparedness.platform.v1.helper.UserInfo;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.notifications.ActionUpdateNotificationHandler;
import org.alertpreparedness.platform.v1.notifications.IndicatorUpdateNotificationHandler;
import org.alertpreparedness.platform.v1.notifications.NotificationIdHandler;
import org.alertpreparedness.platform.v1.notifications.ResponsePlanUpdateNotificationHandler;
import org.alertpreparedness.platform.v1.offline.OfflineSyncJob;
import org.alertpreparedness.platform.v1.offline.OfflineSyncJobCreator;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PreferHelper;
import org.alertpreparedness.platform.v1.utils.SettingsFactory;
import org.alertpreparedness.platform.v2.utils.AreaJsonManager;
import shortbread.Shortbread;
import timber.log.Timber;

/**
 * Created by fei on 06/11/2017.
 */
public class AlertApplication extends MultiDexApplication implements ValueEventListener {

    private User user;

    public enum APP_STATUS {
        LIVE,
        SAND,
        TEST,
        UAT
    }

    public static final APP_STATUS CURRENT_STATUS = BuildConfig.FLAVOR.equals("envSand") ? APP_STATUS.SAND :
                                                    BuildConfig.FLAVOR.equals("envUat") ? APP_STATUS.UAT :
                                                    BuildConfig.FLAVOR.equals("envTest") ? APP_STATUS.TEST :
                                                    APP_STATUS.LIVE;

    @Override
    public void onCreate() {
        super.onCreate();

        //Precache Area JSON
        AreaJsonManager.INSTANCE.preCache(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        DependencyInjector.initialize(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(false)           // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);

//        FirebaseAuthExtensions.getInstance().signOut();
//        Shortbread.create(this);

        Realm.init(this);
        // JODA
        JodaTimeAndroid.init(this);

        boolean loggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;

        if (!PreferHelper.getBoolean(this, Constants.HAS_RUN_BEFORE)) {
            FirebaseAuth.getInstance().signOut();
            PreferHelper.deleteString(this, Constants.UID);
            PreferHelper.putBoolean(this, Constants.HAS_RUN_BEFORE, true);
        }

        // Debug-Only builds
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        if (loggedIn) {

            DependencyInjector.initialize(this);
            DependencyInjector.initializeUserScope();

            JobManager.create(this).addJobCreator(new OfflineSyncJobCreator());

            OfflineSyncJob.scheduleJob();

            new IndicatorUpdateNotificationHandler(this).scheduleAllNotifications();
            new ActionUpdateNotificationHandler(this).scheduleAllNotifications();
            new ResponsePlanUpdateNotificationHandler(this).scheduleAllNotifications();

            if (FirebaseInstanceId.getInstance().getToken() != null) {
                new NotificationIdHandler().registerDeviceId(new UserInfo().getUser().getUserID(),
                        FirebaseInstanceId.getInstance().getToken());
            }

        } else {
            //TODO: CANCEL ALL NOTIFICATIONS?
        }
    }

    public void startPermissionListeners(@PermissionRef DatabaseReference permissionsRef, User user) {
        if (permissionsRef != null) {
            this.user = user;
            permissionsRef.addValueEventListener(this);
        }
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getKey().equals("permissionSettings")) {
            SettingsFactory.processCountryLevelSettings(dataSnapshot, user);
        } else {
            SettingsFactory.proccessPartnerSettings(dataSnapshot, user);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
