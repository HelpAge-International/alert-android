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
    private String localNetworkID;
    private String networkID;
    private String countryName;
    private int countryListId;
    public List <String> hazardID;
    private String firstName;
    private String lastName;
    private String email;
    private int userType;
    private boolean isCountryDirector;
    public String networkCountryID;

    enum UserLevel {
        STANDARD,
        COUNTRY_DIRECTOR,
        COUNTRY_OFFICE_ADMIN
    }

    @Override
    public String toString() {
        return "User{" +
                "agencyAdminID='" + agencyAdminID + '\'' +
                ", countryID='" + countryID + '\'' +
                ", userID='" + userID + '\'' +
                ", systemAdminID='" + systemAdminID + '\'' +
                ", localNetworkID='" + localNetworkID + '\'' +
                ", networkID='" + networkID + '\'' +
                ", countryName='" + countryName + '\'' +
                ", countryListId=" + countryListId +
                ", hazardID=" + hazardID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                ", isCountryDirector=" + isCountryDirector +
                ", networkCountryID='" + networkCountryID + '\'' +
                '}';
    }

    public User(String userID, int userType, String agencyAdminID, String countryID, String systemAdminID, String networkCountryID, String localNetworkID, String networkID, List<String> hazardID,  boolean isCountryDirector) {
        this.userID = userID;
        this.userType = userType;
        this.agencyAdminID = agencyAdminID;
        this.countryID = countryID;
        this.systemAdminID = systemAdminID;
        this.hazardID = hazardID;
        this.isCountryDirector = isCountryDirector;
        this.networkCountryID = networkCountryID;
        this.localNetworkID = localNetworkID;
        this.networkID = networkID;
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

    @Deprecated
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

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryListId(int countryListId) {
        this.countryListId = countryListId;
    }

    public int getCountryListId() {
        return countryListId;
    }

    @Deprecated
    public String getLocalNetworkID() {
        return localNetworkID;
    }

    public void setLocalNetworkID(String localNetworkID) {
        this.localNetworkID = localNetworkID;
    }

    @Deprecated
    public String getNetworkID() {
        return networkID;
    }

    public void setNetworkID(String networkID) {
        this.networkID = networkID;
    }
}
