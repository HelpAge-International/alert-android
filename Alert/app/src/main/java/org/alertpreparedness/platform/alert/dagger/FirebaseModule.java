package org.alertpreparedness.platform.alert.dagger;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionMandatedRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyBaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseCountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardOtherRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardRef;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ProgrammeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ResponsePlansRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserId;
import org.alertpreparedness.platform.alert.dagger.annotation.NoteRef;
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
    @ResponsePlansRef
    public DatabaseReference provideResponsePlansRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("responsePlan").child(user.countryID);
    }

    @Provides
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
    @AgencyRef
    public DatabaseReference provideAgencyRef(@BaseDatabaseRef DatabaseReference db, Context context) {
        return db.child("agency").child(PreferHelper.getString(context, Constants.AGENCY_ID));
    }

    @Provides
    @ActionRef
    public DatabaseReference provideActionRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("action").child(user.countryID);
    }


    @Provides
    @HazardRef
    public DatabaseReference provideHazardRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("hazard").child(user.countryID);
    }


    @Provides
    @IndicatorRef
    public DatabaseReference provideIndicatorRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("indicator").child(user.countryID);
    }

    @Provides
    @BaseIndicatorRef
    public DatabaseReference provideBaseIndicatorRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("indicator");
    }


    @Provides
    @BaseActionRef
    public DatabaseReference provideBaseActionRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("action");
    }

    @Provides
    @UserRef
    public DatabaseReference provideUserRef(@BaseDatabaseRef DatabaseReference db, @UserId String userId) {
        return db.child("userPublic").child(userId);
    }

    @Provides
    @ProgrammeRef
    public DatabaseReference providePrgrammes(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("countryOfficeProfile").child("programme").child(user.countryID).child("4WMapping");
    }

    @Provides
    @BaseCountryOfficeRef
    public DatabaseReference provideCountryOffice(@BaseDatabaseRef DatabaseReference db) {
        return db.child("countryOffice");
    }


    @Provides
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

    @Provides
    @Singleton
    @ActionMandatedRef
    public DatabaseReference provideMandatedRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("actionMandated").child(user.getAgencyAdminID());
    }

//    @Provides
//    @Singleton
//    @TaskRef
//    public DatabaseReference provideTask(@BaseDatabaseRef DatabaseReference db) {
//        return db.child("task");
//    }
}
