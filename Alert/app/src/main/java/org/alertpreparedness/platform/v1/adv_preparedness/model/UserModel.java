package org.alertpreparedness.platform.v1.adv_preparedness.model;

import java.io.Serializable;

/**
 * Created by faizmohideen on 09/01/2018.
 */

public class UserModel implements Serializable {

    private String userID;
    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userID='" + userID + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    public UserModel(String userID, String fullName) {
        this.userID = userID;
        this.fullName = fullName;
    }

}
