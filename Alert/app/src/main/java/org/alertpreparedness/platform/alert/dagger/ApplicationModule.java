package org.alertpreparedness.platform.alert.dagger;

/**
 * Created by Tj on 13/12/2017.
 */

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.dagger.annotation.AppStatusConst;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

/**
 * Currently, the only provider of injections, application-wide.
 * Eventually, it would probably be good to have separate Modules to group related items.
 */
@Module
public class ApplicationModule {

    private final AlertApplication application;

    public ApplicationModule(AlertApplication application) {
        this.application = application;
    }

    @Provides @Singleton
    public AlertApplication provideApplication() {
        return application;
    }

    @Provides @Singleton
    public User provideUser() {
        return UserInfo.getUser(application);
    }

    @Provides @Singleton @BaseDatabaseRef
    public DatabaseReference provideFirebaseRef(@AppStatusConst String appStatus) {
        return FirebaseDatabase.getInstance().getReference().child(appStatus);
    }

    @Provides @Singleton @AppStatusConst
    public String provideAppStatus() {
        return PreferHelper.getString(application, Constants.APP_STATUS);
    }

    @Provides @Singleton
    public Context provideContext() {
        return application;
    }
}
