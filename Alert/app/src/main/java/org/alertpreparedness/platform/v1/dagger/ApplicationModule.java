package org.alertpreparedness.platform.v1.dagger;

/**
 * Created by Tj on 13/12/2017.
 */

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.alertpreparedness.platform.v1.AlertApplication;
import org.alertpreparedness.platform.v1.dagger.annotation.AppStatusConst;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.v1.dagger.annotation.UserEmail;
import org.alertpreparedness.platform.v1.dagger.annotation.UserId;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseStorageRef;
import org.alertpreparedness.platform.v1.helper.UserInfo;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.realm.SettingsRealm;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PermissionsHelper;
import org.alertpreparedness.platform.v1.utils.PreferHelper;
import org.alertpreparedness.platform.v1.utils.SettingsFactory;

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
    public User provideUser(UserInfo info) {
        return info.getUser();
    }

    @Provides
    public Realm providesRealm() {
        return Realm.getDefaultInstance();
    }

    @Provides
    public UserInfo provideUserInfo() {
        return new UserInfo();
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
    @BaseStorageRef
    public StorageReference provideFirebaseStorageRef() {
        return FirebaseStorage.getInstance().getReference();
    }

    @Provides @Singleton
    public SimpleDateFormat provideDateFomatter() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Provides
    @UserId
    public String provideUserId(Context context) {
        return PreferHelper.getString(context, Constants.UID);
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

    @Provides
    public SettingsRealm providePermissions(User user) {
        return SettingsFactory.getSettings(user);
    }

    @Provides
    public PermissionsHelper providerPermissionsHelper(SettingsRealm realm) {
        return new PermissionsHelper(realm);
    }
}