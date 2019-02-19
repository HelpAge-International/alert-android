package org.alertpreparedness.platform.v1.firebase;

import com.google.firebase.database.Exclude;
import java.io.Serializable;
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelIndicatorLocation;
import org.alertpreparedness.platform.v2.models.Area;

public class AffectedAreaModel extends FirebaseModel implements Serializable {

    private Integer country;

    private Integer level1;

    private Integer level2;

    //not set through firebase
    @Exclude
    private String level1Name;

    //not set through firebase
    @Exclude
    private String level2Name;

    public AffectedAreaModel() {
    }

    public AffectedAreaModel(ModelIndicatorLocation area) {
        level2 = area.getLevel2();
        country = area.getCountry();
        level1 = area.getLevel1();
    }

    public AffectedAreaModel(int country, int level1, int level2) {
        this.country = country;
        this.level1 = level1;
        this.level2 = level2;
    }

    //DEV NOTE: This links v1 to v2, this model needs to be completely replaced with v2 eventually
    public AffectedAreaModel(Area area) {
        country = area.getCountry().getValue();
        level1 = area.getLevel1();
        level2 = area.getLevel2();
    }

    @Override
    public String toString() {
        return "AffectedAreaModel{" +
                "country=" + country +
                ", level1=" + level1 +
                ", level2=" + level2 +
                ", level1Name='" + level1Name + '\'' +
                ", level2Name='" + level2Name + '\'' +
                '}' +
                super.toString();
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public Integer getLevel1() {
        return level1;
    }

    public void setLevel1(Integer level1) {
        this.level1 = level1;
    }

    public Integer getLevel2() {
        return level2;
    }

    public void setLevel2(Integer level2) {
        this.level2 = level2;
    }

    @Exclude
    public String getLevel2Name() {
        return level2Name;
    }

    @Exclude
    public void setLevel2Name(String level2Name) {
        this.level2Name = level2Name;
    }

    @Exclude
    public String getLevel1Name() {
        return level1Name;
    }

    @Exclude
    public void setLevel1Name(String level1Name) {
        this.level1Name = level1Name;
    }

}
