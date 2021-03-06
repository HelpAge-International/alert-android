package org.alertpreparedness.platform.v1.dagger;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.v1.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionMandatedRef;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.AgencyBaseRef;
import org.alertpreparedness.platform.v1.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.v1.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionCHSRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionMandatedRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseCountryOfficeRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseDocumentRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseHazardRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseLogRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseNetworkCountryRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseNetworkRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseNoteRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseResponsePlansRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseUserRef;
import org.alertpreparedness.platform.v1.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.v1.dagger.annotation.DocumentRef;
import org.alertpreparedness.platform.v1.dagger.annotation.HazardOtherRef;
import org.alertpreparedness.platform.v1.dagger.annotation.HazardRef;
import org.alertpreparedness.platform.v1.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.v1.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.v1.dagger.annotation.NotificationSettingsRef;
import org.alertpreparedness.platform.v1.dagger.annotation.NoteRef;
import org.alertpreparedness.platform.v1.dagger.annotation.PermissionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.ProgrammeRef;
import org.alertpreparedness.platform.v1.dagger.annotation.ResponsePlansRef;
import org.alertpreparedness.platform.v1.dagger.annotation.StaffRef;
import org.alertpreparedness.platform.v1.dagger.annotation.UserId;
import org.alertpreparedness.platform.v1.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.v1.dagger.annotation.UserRef;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.notifications.NotificationIdHandler;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PreferHelper;

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
    @BaseResponsePlansRef
    public DatabaseReference provideBaseResponsePlansRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("responsePlan");
    }

    @Provides
    @BaseLogRef
    public DatabaseReference provideBaseLogRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("log");
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
    @NotificationSettingsRef
    public DatabaseReference provideNotificationSettingsRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("staff").child(user.getCountryID()).child(user.getUserID()).child("notification");
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
    @BaseHazardRef
    public DatabaseReference provideBaseHazardRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("hazard");
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
    public DatabaseReference provideBaseActionRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("action");
    }

    @Provides
    @BaseActionMandatedRef
    public DatabaseReference provideBaseActionMandatedRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("actionMandated");
    }

    @Provides
    @BaseActionCHSRef
    public DatabaseReference provideBaseActionCHSRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("actionCHS");
    }

    @Provides
    @UserRef
    public DatabaseReference provideUserRef(@BaseDatabaseRef DatabaseReference db, @UserId String userId) {
        return db.child("userPublic").child(userId);
    }

    @Provides
    @ProgrammeRef
    public DatabaseReference providePrgrammes(@BaseDatabaseRef DatabaseReference db) {
        return db.child("countryOfficeProfile").child("programme");
    }

    @Provides
    @BaseCountryOfficeRef
    public DatabaseReference provideCountryOffice(@BaseDatabaseRef DatabaseReference db) {
        return db.child("countryOffice");
    }

    @Provides
    @CountryOfficeRef
    public DatabaseReference provideUsersCountryOffice(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("countryOffice").child(user.agencyAdminID).child(user.countryID);
    }

    @Provides
    @BaseNetworkRef
    public DatabaseReference providesLocalNetworkRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("network");
    }

    @Provides
    @BaseNetworkCountryRef
    public DatabaseReference providesNetworkCountryRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("networkCountry");
    }

    @Provides
    @UserPublicRef
    public DatabaseReference provideUserPublicRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("userPublic");
    }

    @Provides
    public NotificationIdHandler provideNotificationIdHandler() {
        return new NotificationIdHandler();
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
    @StaffRef
    public DatabaseReference provideStaffRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("staff").child(user.countryID);
    }


    @Provides
    @NoteRef
    public DatabaseReference provideNoteRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("note").child(user.countryID);
    }

    @Provides
    @BaseNoteRef
    public DatabaseReference provideBaseNoteRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("note");
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

    @Provides
    @BaseUserRef
    public DatabaseReference provideBaseUserRef(@BaseDatabaseRef DatabaseReference db, User user) {
        String nodeName;
        switch (user.getUserType()) {
            case Constants.ErtLeader:
                nodeName = "ertLeader";
                break;
            case Constants.CountryAdmin:
                nodeName = "administratorCountry";
                break;
            case Constants.CountryDirector:
                nodeName = "countryDirector";
                break;
            case Constants.PartnerUser:
                nodeName = "partner";
                break;
            default:
                nodeName = "ert";
                break;
        }
        return db.child(nodeName).child(user.getUserID());
    }


    @Provides
    @PermissionRef
    public DatabaseReference providePermissionRef(@BaseDatabaseRef DatabaseReference db, User user) {
        System.out.println("db = [" + db + "], providePermissionRef = [" + user + "]");
        switch (user.getUserType()) {
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
            case Constants.CountryDirector:
            case Constants.Ert:
                return db.child("countryOffice").child(user.agencyAdminID).child(user.countryID).child("permissionSettings");
            default: //Constants.PartnerUser:
                return db.child("partner").child(user.getUserID()).child("permissions");
        }
    }

    @Provides
    @DocumentRef
    public DatabaseReference provideDocRef(@BaseDatabaseRef DatabaseReference db, User user) {
        return db.child("document").child(user.countryID);
    }

    @Provides
    @BaseDocumentRef
    public DatabaseReference provideBaseDocRef(@BaseDatabaseRef DatabaseReference db) {
        return db.child("document");
    }

//    @Provides
//    @Singleton
//    @TaskRef
//    public DatabaseReference provideTask(@BaseDatabaseRef DatabaseReference db) {
//        return db.child("task");
//    }
}
