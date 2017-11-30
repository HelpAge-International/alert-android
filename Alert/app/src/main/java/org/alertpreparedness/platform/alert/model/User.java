package org.alertpreparedness.platform.alert.model;
import android.content.SharedPreferences;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by faizmohideen on 08/11/2017.
 */

@IgnoreExtraProperties
public class User implements Serializable {

    public String userID;
    public int userType;
    public String agencyAdminID;
    public String countryID;
    public String systemAdminID;
    //public String networkCountryID;

    //public static User sharedInstance = new User();

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User(String userID, int userType, String agencyAdminID, String countryID, String systemAdminID) {
        this.userID = userID;
        this.userType = userType;
        this.agencyAdminID = agencyAdminID;
        this.countryID = countryID;
        this.systemAdminID = systemAdminID;
        //this.networkCountryID = networkCountryID;
    }

    public User() {

    }

    public String getUserID() {
        return userID;
    }

    public int getUserType() {
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

  //  public String getNetworkCountryID() {
    //    return networkCountryID;
   // }

    public void saveToPreferences(SharedPreferences prefs){

    }
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "userID='" + userID + '\'' +
//                ", userType=" + userType +
//                ", agencyAdminID='" + agencyAdminID + '\'' +
//                ", countryID='" + countryID + '\'' +
//                ", systemAdminID='" + systemAdminID + '\'' +
//                ", networkCountryID='" + networkCountryID + '\'' +
//                '}';
//    }
}
