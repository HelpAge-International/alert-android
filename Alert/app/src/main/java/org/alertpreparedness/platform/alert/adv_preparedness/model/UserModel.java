package org.alertpreparedness.platform.alert.adv_preparedness.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by faizmohideen on 09/01/2018.
 */

public class UserModel {
   private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserModel(String fullName) {
        this.fullName = fullName;
    }
}
