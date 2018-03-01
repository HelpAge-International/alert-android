package org.alertpreparedness.platform.alert.firebase;

import android.os.Parcel;
import android.os.Parcelable;

public class ProgrammeModel extends FirebaseModel implements Parcelable {

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

    private String agencyId;
    private int level1;
    private String level2;
    private int sector;
    private String toWho;
    private String what;
    private long when;

    public ProgrammeModel() {
    }

    protected ProgrammeModel(Parcel in) {
        agencyId = in.readString();
        level1 = in.readInt();
        level2 = in.readString();
        sector = in.readInt();
        toWho = in.readString();
        what = in.readString();
        when = in.readLong();
    }

    @Override
    public String toString() {
        return "ProgrammeModel{" +
                "agencyId='" + agencyId + '\'' +
                ", level1=" + level1 +
                ", level2='" + level2 + '\'' +
                ", sector=" + sector +
                ", toWho='" + toWho + '\'' +
                ", what='" + what + '\'' +
                ", when=" + when +
                '}' +
                super.toString();
    }

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
        parcel.writeLong(when);
    }
}