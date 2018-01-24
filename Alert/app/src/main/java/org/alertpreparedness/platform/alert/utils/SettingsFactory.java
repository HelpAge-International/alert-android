package org.alertpreparedness.platform.alert.utils;

import android.provider.Settings;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.realm.SettingsRealm;

import io.realm.Realm;

/**
 * Created by Tj on 23/01/2018.
 */

public class SettingsFactory {


    private final User user;

    public SettingsFactory(User user) {
        this.user = user;
    }

    public static void tryMakeBaseSettings(User user) {
        Realm realm = Realm.getDefaultInstance();

        if(getSettings(user) == null) {
            SettingsRealm settings = new SettingsRealm();
            settings.setUserId(user.getUserID());
            settings = customMPASettings(user.getUserType(), settings);
            settings = customAPASettings(user.getUserType(), settings);
            settings = chsSettings(user.getUserType(), settings);
            settings = mandatedMpaSettings(user.getUserType(), settings);
            settings = mandatedAPASettings(user.getUserType(), settings);
            settings = hazardSettings(user.getUserType(), settings);
            settings = hazardIndicatorSettings(user.getUserType(), settings);
            settings = viewAgenciesInCountrySettings(user.getUserType(), settings);
            settings = countryContactSettings(user.getUserType(), settings);
            settings = agencyCountrySettings(user.getUserType(), settings);
            settings = noteSettings(user.getUserType(), settings);

            settings.setCanViewCHS(true);
            settings.setCanViewMPA(true);
            settings.setCanViewCustomMPA(true);
            settings.setCanViewMandatedAPA(true);
            settings.setCanViewCustomAPA(true);
            settings.setCanViewHazard(true);
            settings.setCanViewHazardIndicators(true);
            settings.setCanViewNotes(true);
            settings.setCanDownloadDocuments(true);

            //not if agency admin
            settings.setCanViewAgencyCountryOffices(true);

            realm.beginTransaction();
            realm.copyToRealm(settings);
            realm.commitTransaction();
        }

    }

    @SuppressWarnings("ConstantConditions")
    public static void processCountryLevelSettings(DataSnapshot snapshot, User user) {
        SettingsRealm settings = getSettings(user);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if(snapshot.child("chsActions").child(String.valueOf(user.getUserType())).exists()) {
            settings.setCanAssignCHS(snapshot.child("chsActions").child(String.valueOf(user.getUserType())).getValue(boolean.class));
        }
        else if(snapshot.child("countryContacts").child(String.valueOf(user.getUserType())).exists()) {
            settings.setCanEditCountryContacts(snapshot.child("countryContacts").child(String.valueOf(user.getUserType())).child("edit").getValue(boolean.class));
            settings.setCanCreateCountryContacts(snapshot.child("countryContacts").child(String.valueOf(user.getUserType())).child("new").getValue(boolean.class));
            settings.setCanDeleteCountryContacts(snapshot.child("countryContacts").child(String.valueOf(user.getUserType())).child("new").getValue(boolean.class));
        }
//        if(snapshot.child("crossCountry").child(String.valueOf(user.getUserType())).exists()) {
//cant update
//        }
        if(snapshot.child("customApa").child(String.valueOf(user.getUserType())).exists()) {
            settings.setCanEditCustomAPA(snapshot.child("customApa").child(String.valueOf(user.getUserType())).child("edit").getValue(boolean.class));
            settings.setCanAssignCustomAPA(snapshot.child("customApa").child(String.valueOf(user.getUserType())).child("assign").getValue(boolean.class));
            settings.setCanDeleteCustomAPA(snapshot.child("customApa").child(String.valueOf(user.getUserType())).child("delete").getValue(boolean.class));
            settings.setCanCreateCustomAPA(snapshot.child("customApa").child(String.valueOf(user.getUserType())).child("new").getValue(boolean.class));
        }
        if(snapshot.child("customMpa").child(String.valueOf(user.getUserType())).exists()) {
            settings.setCanEditCustomMPA(snapshot.child("customMpa").child(String.valueOf(user.getUserType())).child("edit").getValue(boolean.class));
            settings.setCanAssignCustomMPA(snapshot.child("customMpa").child(String.valueOf(user.getUserType())).child("assign").getValue(boolean.class));
            settings.setCanDeleteCustomMPA(snapshot.child("customMpa").child(String.valueOf(user.getUserType())).child("delete").getValue(boolean.class));
            settings.setCanCreateCustomMPA(snapshot.child("customMpa").child(String.valueOf(user.getUserType())).child("new").getValue(boolean.class));
        }
//        if(snapshot.child("interAgency").child(String.valueOf(user.getUserType())).exists()) {
//cant update
//        }
        if(snapshot.child("mandatedApaAssign").child(String.valueOf(user.getUserType())).exists()) {
            settings.setCanAssignMandatedAPA(snapshot.child("mandatedApaAssign").child(String.valueOf(user.getUserType())).getValue(boolean.class));
        }
        if(snapshot.child("mandatedMpaAssign").child(String.valueOf(user.getUserType())).exists()) {
            settings.setCanAssignMPA(snapshot.child("mandatedMpaAssign").child(String.valueOf(user.getUserType())).getValue(boolean.class));
        }
        if(snapshot.child("notes").child(String.valueOf(user.getUserType())).exists()) {
            settings.setCanEditNotes(snapshot.child("notes").child(String.valueOf(user.getUserType())).child("edit").getValue(boolean.class));
            settings.setCanDeleteNotes(snapshot.child("notes").child(String.valueOf(user.getUserType())).child("delete").getValue(boolean.class));
            settings.setCanCreateNotes(snapshot.child("notes").child(String.valueOf(user.getUserType())).child("new").getValue(boolean.class));
        }
        if(snapshot.child("other").child(String.valueOf(user.getUserType())).exists()) {
            settings.setCanDownloadDocuments(snapshot.child("other").child(String.valueOf(user.getUserType())).child("downloadDoc").getValue(boolean.class));
        }
        realm.commitTransaction();
        realm.close();
    }

    @SuppressWarnings("ConstantConditions")
    public static void proccessPartnerSettings(DataSnapshot snapshot, User user) {
        SettingsRealm settings = getSettings(user);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if(snapshot.child("assignCHS").exists()) {
            settings.setCanAssignCHS(snapshot.child("assignCHS").getValue(boolean.class));
        }
        if(snapshot.child("assignMandatedApa").exists()) {
            settings.setCanAssignMandatedAPA(snapshot.child("assignMandatedApa").getValue(boolean.class));
        }
        if(snapshot.child("assignMandatedMpa").exists()) {
            settings.setCanAssignMPA(snapshot.child("assignMandatedMpa").getValue(boolean.class));
        }
        if(snapshot.child("contacts").exists()) {
            settings.setCanDeleteCountryContacts(snapshot.child("contacts").child("delete").getValue(boolean.class));
            settings.setCanEditCountryContacts(snapshot.child("contacts").child("edit").getValue(boolean.class));
            settings.setCanCreateCountryContacts(snapshot.child("contacts").child("new").getValue(boolean.class));
        }
        if(snapshot.child("customApa").exists()) {
            settings.setCanEditCustomAPA(snapshot.child("customApa").child("edit").getValue(boolean.class));
            settings.setCanCreateCustomAPA(snapshot.child("customApa").child("new").getValue(boolean.class));
            settings.setCanAssignCustomAPA(snapshot.child("customApa").child("assign").getValue(boolean.class));
            settings.setCanDeleteCustomAPA(snapshot.child("customApa").child("delete").getValue(boolean.class));
        }
        if(snapshot.child("customMpa").exists()) {
            settings.setCanEditCustomMPA(snapshot.child("customMpa").child("edit").getValue(boolean.class));
            settings.setCanCreateCustomMPA(snapshot.child("customMpa").child("new").getValue(boolean.class));
            settings.setCanAssignCustomMPA(snapshot.child("customMpa").child("assign").getValue(boolean.class));
            settings.setCanDeleteCustomMPA(snapshot.child("customMpa").child("delete").getValue(boolean.class));
        }
        if(snapshot.child("notes").exists()) {
            settings.setCanEditNotes(snapshot.child("notes").child("edit").getValue(boolean.class));
            settings.setCanCreateNotes(snapshot.child("notes").child("new").getValue(boolean.class));
            settings.setCanDeleteCustomMPA(snapshot.child("notes").child("delete").getValue(boolean.class));
        }
    }

    private static SettingsRealm hazardSettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.Ert:
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
            case Constants.CountryDirector:
            case Constants.PartnerUser:
                settings.setCanEditHazard(true);
                settings.setCanArchiveHazard(true);
                settings.setCanCreateHazard(true);
                settings.setCanDeleteHazard(true);
                break;
        }
        return settings;
    }

    private static SettingsRealm hazardIndicatorSettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.Ert:
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
            case Constants.CountryDirector:
            case Constants.PartnerUser:
                settings.setCanEditHazardIndicators(true);
                settings.setCanArchiveHazardIndicators(true);
                settings.setCanCreateHazardIndicators(true);
                settings.setCanDeleteHazardIndicators(true);
                break;
        }
        return settings;
    }

    private static SettingsRealm noteSettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.Ert:
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
            case Constants.CountryDirector:
                settings.setCanEditNotes(true);
                settings.setCanCreateNotes(true);
                settings.setCanDeleteNotes(true);
                break;
            case Constants.PartnerUser:
                break;
        }
        return settings;
    }

    private static SettingsRealm agencyCountrySettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.Ert:
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
            case Constants.CountryDirector:
            case Constants.PartnerUser:
                settings.setCanCopyAgencyCountryOffices(true);
                settings.setCanViewAgencyCountryOffices(true);
                break;
        }
        return settings;
    }

    private static SettingsRealm customAPASettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.Ert:
            case Constants.CountryDirector:
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
                settings.setCanAssignCustomMPA(true);
                settings.setCanEditCustomMPA(true);
                settings.setCanCreateCustomMPA(true);
                settings.setCanDeleteCustomMPA(true);
                settings.setCanCompleteCustomMPA(true);
                break;
            case Constants.PartnerUser:
                settings.setCanCompleteCustomMPA(true);
                break;
        }
        return settings;
    }

    private static SettingsRealm chsSettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.Ert:
            case Constants.CountryDirector:
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
                settings.setCanCompleteCHS(true);
                settings.setCanAssignCHS(true);
                break;
            case Constants.PartnerUser:
                settings.setCanCompleteCHS(true);
                break;
        }
        return settings;
    }

    private static SettingsRealm mandatedAPASettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.Ert:
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
            case Constants.CountryDirector:
                settings.setCanAssignMandatedAPA(true);
                settings.setCanCompleteCustomAPA(true);
                break;
            case Constants.PartnerUser:
                settings.setCanCompleteCustomAPA(true);
                break;
        }
        return settings;
    }

    private static SettingsRealm mandatedMpaSettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.Ert:
            case Constants.ErtLeader:
            case Constants.CountryDirector:
            case Constants.CountryAdmin:
                settings.setCanAssignMPA(true);
                settings.setCanCompleteMPA(true);
                break;
            case Constants.PartnerUser:
                settings.setCanCompleteMPA(true);
                break;
        }
        return settings;
    }

    private static SettingsRealm customMPASettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.Ert:
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
            case Constants.CountryDirector:
                settings.setCanAssignCustomMPA(true);
                settings.setCanEditCustomMPA(true);
                settings.setCanCreateCustomMPA(true);
                settings.setCanDeleteCustomMPA(true);
                settings.setCanCompleteCustomMPA(true);
                break;
            case Constants.PartnerUser:
                settings.setCanCompleteMPA(true);
                settings.setCanCompleteCHS(true);
                settings.setCanCompleteCustomMPA(true);
                break;
        }
        return settings;
    }

    private static SettingsRealm viewAgenciesInCountrySettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
            case Constants.CountryDirector:
                settings.setCanCopyOtherAgencies(true);
                settings.setCanViewOtherAgencies(true);
                break;
        }
        return settings;
    }

    private static SettingsRealm countryContactSettings(int type, SettingsRealm settings) {
        switch (type) {
            case Constants.Ert:
            case Constants.ErtLeader:
            case Constants.CountryAdmin:
            case Constants.CountryDirector:
                settings.setCanCreateCountryContacts(true);
                settings.setCanEditCountryContacts(true);
                settings.setCanDeleteCountryContacts(true);
                break;
        }
        return settings;
    }

    public static SettingsRealm getSettings(User user) {
        Realm realm = Realm.getDefaultInstance();

        return realm.where(SettingsRealm.class).equalTo("userId", user.getUserID()).findFirst();

    }
}
