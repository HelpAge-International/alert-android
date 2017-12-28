package org.alertpreparedness.platform.alert.firebase;

import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicatorLocation;

import java.io.Serializable;

/**
 * Created by Tj on 19/12/2017.
 */

public class AffectedAreaModel implements Serializable {

    private Integer country;

    private Integer level1;

    private Integer level2;

    //not set through firebase
    private String level1Name;

    //not set through firebase
    private String level2Name;

    public AffectedAreaModel() {}

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

    @Override
    public String toString() {
        return "AffectedAreaModel{" +
                "country=" + country +
                ", level1=" + level1 +
                ", level2=" + level2 +
                ", level1Name='" + level1Name + '\'' +
                ", level2Name='" + level2Name + '\'' +
                '}';
    }

    public String getLevel2Name() {
        return level2Name;
    }

    public void setLevel2Name(String level2Name) {
        this.level2Name = level2Name;
    }

    public String getLevel1Name() {
        return level1Name;
    }

    public void setLevel1Name(String level1Name) {
        this.level1Name = level1Name;
    }

}
