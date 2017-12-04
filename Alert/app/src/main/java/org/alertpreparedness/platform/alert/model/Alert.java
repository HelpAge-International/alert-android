package org.alertpreparedness.platform.alert.model;

import java.io.Serializable;

/**
 * Created by faizmohideen on 20/11/2017.
 */

public class Alert implements Serializable{
    public long alertLevel;
    private long numOfAreas;
    private long hazardScenario;
    private long country;
    private long level1;
    private long level2;
    private long population;
    private String info;
    private String updated;
    private String otherName;
    private String reason;

    public Alert(long alertLevel, long hazardScenario, long population, long numOfAreas, String info, String updated, String otherName) {
        this.alertLevel = alertLevel;
        this.hazardScenario = hazardScenario;
        this.population = population;
        this.numOfAreas = numOfAreas;
        this.info = info;
        this.updated = updated;
        this.otherName = otherName;
    }

    public Alert(long country, long level1, long level2) {
        this.country = country;
        this.level1 = level1;
        this.level2 = level2;
    }

    public Alert(long country) {
        this.country = country;
    }

    public Alert() {
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public long getNumOfAreas() {
        return numOfAreas;
    }

    public void setNumOfAreas(long numOfAreas) {
        this.numOfAreas = numOfAreas;
    }

    public long getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(long alertLevel) {
        this.alertLevel = alertLevel;
    }

    public long getHazardScenario() {
        return hazardScenario;
    }

    public void setHazardScenario(long hazardScenario) {
        this.hazardScenario = hazardScenario;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public long getCountry() {
        return country;
    }

    public void setCountry(long country) {
        this.country = country;
    }

    public long getLevel1() {
        return level1;
    }

    public void setLevel1(long level1) {
        this.level1 = level1;
    }

    public long getLevel2() {
        return level2;
    }

    public void setLevel2(long level2) {
        this.level2 = level2;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


}
