package org.alertpreparedness.platform.alert.adv_preparedness.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by faizmohideen on 09/01/2018.
 */

public class UserModel implements Serializable{
   private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "fullName='" + fullName + '\'' +
                '}';
    }

    public UserModel(String fullName) {
        this.fullName = fullName;
    }

}
