package org.alertpreparedness.platform.alert.dagger;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionStorageRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyBaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardOtherRef;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NoteRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ProgrammeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ResponsePlansRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserRef;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

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
    public DatabaseReference providesAlert(@BaseAlertRef DatabaseReference db, User user) {
        return db.child(user.countryID);
    }


    @Provides
    @Singleton
    @BaseAlertRef
    public DatabaseReference providesBaseAlert(@BaseDatabaseRef DatabaseReference db) {
        return db.child("alert");
    }

    @Provides
    @Singleton
    @AgencyRef
    public DatabaseReference provideAgencyRef(@BaseDatabaseRef DatabaseReference db, Context context) {
        return db.child("agency").child(PreferHelper.getString(context, Constants.AGENCY_ID));
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
    public DatabaseReference provideUserRef(@BaseDatabaseRef DatabaseReference db, Context context) {
        return db.child("userPublic").child(PreferHelper.getString(context, Constants.AGENCY_ID));
    }

    @Provides
    @Singleton
    @ProgrammeRef
    public DatabaseReference providePrgrammes(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("countryOfficeProfile").child("programme").child(user.countryID).child("4WMapping");
    }


    @Provides
    @Singleton
    @UserPublicRef
    public DatabaseReference provideUserPublicRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("userPublic");
    }

    @Provides
    @Singleton
    @HazardOtherRef
    public DatabaseReference provideHazardOtherRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("hazardOther");
    }

    @Provides
    @Singleton
    @NetworkRef
    public DatabaseReference provideNetworkRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("network");
    }

    @Provides
    @Singleton
    @AgencyBaseRef
    public DatabaseReference provideBaseAgencykRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("agency");
    }

    @Provides
    @Singleton
    @NoteRef
    public DatabaseReference provideNoteRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("note").child(user.countryID);
    }

    @Provides
    @Singleton
    @ActionCHSRef
    public DatabaseReference provideCHSRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("actionCHS").child(user.getSystemAdminID());
    }
//    @Provides
//    @Singleton
//    @TaskRef
//    public DatabaseReference provideTask(@BaseDatabaseRef DatabaseReference db) {
//        return db.child("task");
//    }
}
