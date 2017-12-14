package org.alertpreparedness.platform.alert.model;
import android.content.SharedPreferences;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by faizmohideen on 08/11/2017.
 */

@IgnoreExtraProperties
public class User implements Serializable {

    private String userID;
    private int userType;
    public String agencyAdminID;
    public String countryID;
    private String systemAdminID;
    private boolean isCountryDirector;

    private String firstName;
    private String lastName;
    private String email;

//public String networkCountryID;

    public User(String userID, int userType, String agencyAdminID, String countryID, String systemAdminID, boolean isCountryDirector) {
        this.userID = userID;
        this.userType = userType;
        this.agencyAdminID = agencyAdminID;
        this.countryID = countryID;
        this.systemAdminID = systemAdminID;
        this.isCountryDirector = isCountryDirector;
        //this.networkCountryID = networkCountryID;
    }

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

  //  public String getNetworkCountryID() {
    //    return networkCountryID;
   // }

    public void saveToPreferences(SharedPreferences prefs){

    }

    public boolean isCountryDirector() {
        return isCountryDirector;
    }

    public void setCountryDirector(boolean countryDirector) {
        isCountryDirector = countryDirector;
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
