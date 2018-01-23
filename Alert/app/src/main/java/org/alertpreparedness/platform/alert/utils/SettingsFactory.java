package org.alertpreparedness.platform.alert.utils;

import android.provider.Settings;

import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.realm.SettingsRealm;

import io.realm.Realm;

/**
 * Created by Tj on 23/01/2018.
 */

public class SettingsFactory {

    public static void makeSettings(User user) {
        Realm realm = Realm.getDefaultInstance();
        SettingsRealm settings = new SettingsRealm();

        switch (user.getUserType()) {
            case Constants.Ert:
                break;
            case Constants.ErtLeader:
                break;
            case Constants.CountryAdmin:
                break;
            case Constants.CountryDirector:
                break;
            case Constants.PartnerUser:
                break;
        }

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
    }

    public static SettingsRealm getSettings(User user) {
        Realm realm = Realm.getDefaultInstance();

        return realm.where(SettingsRealm.class).equalTo("userId", user.getUserID()).findFirst();

    }
}
