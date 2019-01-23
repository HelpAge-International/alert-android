package org.alertpreparedness.platform.v1.responseplan;

import android.os.Parcel;
import android.os.Parcelable;

import org.alertpreparedness.platform.v1.min_preparedness.model.Note;

import java.util.HashMap;

/**
 * Created by Tj on 12/12/2017.
 */

public class ApprovalStatusObj implements Parcelable {


    public String permisionLevel;
    public int status;
    public HashMap<String, Note> notes;

    public ApprovalStatusObj(String permisionLevel, int status, HashMap<String, Note> notes) {
        this.permisionLevel = permisionLevel;
        this.status = status;
        this.notes = notes;
    }

    protected ApprovalStatusObj(Parcel in) {
        status = in.readInt();
        permisionLevel = in.readString();
        notes = (HashMap<String, Note>)in.readSerializable();
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
        parcel.writeSerializable(this.notes);
    }
}
