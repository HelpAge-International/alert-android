package org.alertpreparedness.platform.alert.responseplan;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tj on 12/12/2017.
 */

public class ApprovalStatusObj implements Parcelable {


    public String permisionLevel;
    public int status;

    public ApprovalStatusObj(String permisionLevel, int status) {

        this.permisionLevel = permisionLevel;
        this.status = status;
    }

    protected ApprovalStatusObj(Parcel in) {
        status = in.readInt();
        permisionLevel = in.readString();
    }

    public static final Creator<ApprovalStatusObj> CREATOR = new Creator<ApprovalStatusObj>() {
        @Override
        public ApprovalStatusObj createFromParcel(Parcel in) {
            return new ApprovalStatusObj(in);
        }

        @Override
        public ApprovalStatusObj[] newArray(int size) {
            return new ApprovalStatusObj[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.permisionLevel);
        parcel.writeInt(this.status);
    }
}
