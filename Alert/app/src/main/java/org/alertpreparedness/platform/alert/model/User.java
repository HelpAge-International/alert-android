package org.alertpreparedness.platform.alert.model;
import android.content.SharedPreferences;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by faizmohideen on 08/11/2017.
 */

@IgnoreExtraProperties
public class User implements Serializable {
    public String agencyAdminID;
    public String countryID;
    private String userID;
    private String systemAdminID;



    public List <String> hazardID;
    private String firstName;
    private String lastName;
    private String email;
    private int userType;
    private boolean isCountryDirector;
    public String networkCountryID;

    public User(String userID, int userType, String agencyAdminID, String countryID, String systemAdminID, String networkCountryID, List<String> hazardID,  boolean isCountryDirector) {
        this.userID = userID;
        this.userType = userType;
        this.agencyAdminID = agencyAdminID;
        this.countryID = countryID;
        this.systemAdminID = systemAdminID;
        this.hazardID = hazardID;
        this.isCountryDirector = isCountryDirector;
        this.networkCountryID = networkCountryID;
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

    public void setHazardID(List<String> hazardID) {
        this.hazardID = hazardID;
    }

    public List<String> getHazardID() {
        return hazardID;
    }

    public String getNetworkCountryID() {
        return networkCountryID;
    }

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
