package org.alertpreparedness.platform.alert.model;

/**
 * Created by faizmohideen on 20/11/2017.
 */

public class Alert {
    public long alertLevel;
    public long numOfAreas;
    public long hazardScenario;
    public long population;
    public String otherName;
    public String reason;

    public Alert(long alertLevel, long hazardScenario, long population, long numOfAreas, String otherName) {
        this.alertLevel = alertLevel;
        this.hazardScenario = hazardScenario;
        this.population = population;
        this.numOfAreas = numOfAreas;
        this.otherName = otherName;
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