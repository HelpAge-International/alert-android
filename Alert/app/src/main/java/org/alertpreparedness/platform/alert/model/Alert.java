package org.alertpreparedness.platform.alert.model;

import java.io.Serializable;

/**
 * Created by faizmohideen on 20/11/2017.
 */

public class Alert implements Serializable{
    public long alertLevel;
    public long numOfAreas;
    public long hazardScenario;
    public long population;
    public String updated;
    public String otherName;
    public String reason;

    public Alert(long alertLevel, long hazardScenario, long population, long numOfAreas, String updated, String otherName) {
        this.alertLevel = alertLevel;
        this.hazardScenario = hazardScenario;
        this.population = population;
        this.numOfAreas = numOfAreas;
        this.updated = updated;
        this.otherName = otherName;
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


}
