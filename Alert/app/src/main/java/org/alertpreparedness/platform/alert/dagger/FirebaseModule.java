package org.alertpreparedness.platform.alert.dagger;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.model.User;

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
    @ResponsePlansRef
    public DatabaseReference provideResponsePlansRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("responsePlan").child(user.countryID);
    }

    @Provides
    @Singleton
    @AlertRef
    public DatabaseReference providesAlert(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("alert").child(user.countryID);
    }


    @Provides
    @Singleton
    @AgencyRef
    public DatabaseReference providesAgency(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("agency").child(user.agencyAdminID);
    }

}
