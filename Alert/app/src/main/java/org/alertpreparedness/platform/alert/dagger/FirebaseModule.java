package org.alertpreparedness.platform.alert.dagger;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardOtherRef;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ResponsePlansRef;
import org.alertpreparedness.platform.alert.dagger.annotation.TaskRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserRef;
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
    public DatabaseReference provideAgencyRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("agency").child(user.agencyAdminID);
    }

    @Provides
    @Singleton
    @ActionRef
    public DatabaseReference provideActionRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("action").child(user.countryID);
    }

    @Provides
    @Singleton
    @IndicatorRef
    public DatabaseReference provideIndicatorRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("indicator").child(user.countryID);
    }

    @Provides
    @Singleton
    @UserRef
    public DatabaseReference provideUserRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("userPublic").child(user.getUserID());
    }

    @Provides
    @Singleton
    @HazardOtherRef
    public DatabaseReference provideHazardOtherRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("hazardOther");
    }

//    @Provides
//    @Singleton
//    @TaskRef
//    public DatabaseReference provideTask(@BaseDatabaseRef DatabaseReference db) {
//        return db.child("task");
//    }
}
