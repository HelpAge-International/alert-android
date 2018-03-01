package org.alertpreparedness.platform.alert.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.List;

/**
 * Created by Tj on 09/01/2018.
 */

public class ProgrammeModel implements Parcelable {

    private String key;
    private String agencyId;
    private int level1;
    private String level2;
    private int sector;
    private String toWho;
    private String what;
    private long toDate;
//    private String where;
    private long when;

    private String countryName;
    @Exclude
    private String level1Name;
    @Exclude
    private String level2Name;

    public ProgrammeModel(){}


    protected ProgrammeModel(Parcel in) {
        agencyId = in.readString();
        level1 = in.readInt();
        level2 = in.readString();
        sector = in.readInt();
        toWho = in.readString();
        what = in.readString();
//        where = in.readString();
        when = in.readLong();
    }

    public static final Creator<ProgrammeModel> CREATOR = new Creator<ProgrammeModel>() {
        @Override
        public ProgrammeModel createFromParcel(Parcel in) {
            return new ProgrammeModel(in);
        }

        @Override
        public ProgrammeModel[] newArray(int size) {
            return new ProgrammeModel[size];
        }
    };

    public int getLevel1() {
        return level1;
    }

    public void setLevel1(int level1) {
        this.level1 = level1;
    }

    public String getLevel2() {
        return level2;
    }

    public void setLevel2(String level2) {
        this.level2 = level2;
    }

    public int getSector() {
        return sector;
    }

    public void setSector(int sector) {
        this.sector = sector;
    }

    public String getToWho() {
        return toWho;
    }

    public void setToWho(String toWho) {
        this.toWho = toWho;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

//    public String getWhere() {
//        return where;
//    }
//
//    public void setWhere(String where) {
//        this.where = where;
//    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(agencyId);
        parcel.writeInt(level1);
        parcel.writeString(level2);
        parcel.writeInt(sector);
        parcel.writeString(toWho);
        parcel.writeString(what);
//        parcel.writeString(where);
        parcel.writeLong(when);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getToDate() {
        return toDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    public void setLevel1Name(String level1Name) {
        this.level1Name = level1Name;
    }

    public void setLevel2Name(String level2Name) {
        this.level2Name = level2Name;
    }

    public String getLevel1Name() {
        return level1Name;
    }

    public String getLevel2Name() {
        return level2Name;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
