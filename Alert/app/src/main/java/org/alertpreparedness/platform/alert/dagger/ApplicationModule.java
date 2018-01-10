package org.alertpreparedness.platform.alert.dagger;

/**
 * Created by Tj on 13/12/2017.
 */

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.dagger.annotation.AppStatusConst;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserEmail;
import org.alertpreparedness.platform.alert.dagger.annotation.UserId;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

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

    @Provides
    @Singleton
    public AlertApplication provideApplication() {
        return application;
    }

    @Provides
    public User provideUser(UserInfo provideUserInfo) {
        return provideUserInfo.getUser();
    }

    @Provides
    public Realm providesRealm() {
        return Realm.getDefaultInstance();
    }

    @Provides
    @Singleton
    public UserInfo provideUserInfo(Context context) {
        return new UserInfo(context);
    }

    @Provides
    @Singleton
    @BaseDatabaseRef
    public DatabaseReference provideFirebaseRef(@AppStatusConst String appStatus) {
        return FirebaseDatabase.getInstance().getReference().child(appStatus);
    }

    @Provides
    @Singleton
    @AppStatusConst
    public String provideAppStatus() {
        return PreferHelper.getString(application, Constants.APP_STATUS);
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    public SimpleDateFormat provideDateFomatter() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Provides
    @UserId
    @Nullable
    public String provideUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            return null;
        }
    }

    @Provides
    @UserEmail
    @Nullable
    public String provideUserEmail() {
        try {
            return FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        catch (NullPointerException e) {
            return null;
        }
    }
}