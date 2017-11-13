package org.alertpreparedness.platform.alert.model;
import android.content.SharedPreferences;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by faizmohideen on 08/11/2017.
 */

@IgnoreExtraProperties
public class User implements Serializable {

    public String userType;
    public String agencyAdminID;
    public String countryID;
    public String systemAdminID;

    //public static User sharedInstance = new User();

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User(String userType, String agencyAdminID, String countryID, String systemAdminID) {
        this.userType = userType;
        this.agencyAdminID = agencyAdminID;
        this.countryID = countryID;
        this.systemAdminID = systemAdminID;

    }

    public String getUserType() {
        return userType;
    }

    public String getAgencyAdminID() {
        return agencyAdminID;
    }

    public String getCountryID() {
        return countryID;
    }

    public String getSystemAdminID() {
        return systemAdminID;
    }

    public void saveToPreferences(SharedPreferences prefs){

    }

}
