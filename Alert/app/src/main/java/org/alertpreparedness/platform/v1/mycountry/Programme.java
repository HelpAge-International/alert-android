package org.alertpreparedness.platform.v1.mycountry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tj on 02/01/2018.
 */
public class Programme implements Parcelable{

    public Programme() {

    }

    protected Programme(Parcel in) {
    }

    public static final Creator<Programme> CREATOR = new Creator<Programme>() {
        @Override
        public Programme createFromParcel(Parcel in) {
            return new Programme(in);
        }

        @Override
        public Programme[] newArray(int size) {
            return new Programme[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
