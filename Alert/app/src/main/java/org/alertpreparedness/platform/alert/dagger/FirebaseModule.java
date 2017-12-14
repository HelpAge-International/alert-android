package org.alertpreparedness.platform.alert.dagger;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tj on 13/12/2017.
 */

@Module(
        includes = ApplicationModule.class
)
public class FirebaseModule {

    @Provides
    @Singleton
    @BaseRef
    public DatabaseReference provideBaseRef(@BaseDatabaseRef DatabaseReference db) {
        return db;
    }

    @Provides
    @Singleton
    @ResponsePlansRef
    public DatabaseReference provideResponsePlansRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("responsePlan").child(user.countryID);
    }

    @Provides
    @Singleton
    @AlertRef
    public DatabaseReference provideAlertRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("alert").child(user.countryID);
    }

    @Provides
    @Singleton
    @UserRef
    public DatabaseReference provideUserRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("userPublic");
    }



}
