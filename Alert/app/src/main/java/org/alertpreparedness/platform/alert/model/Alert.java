package org.alertpreparedness.platform.alert.model;

/**
 * Created by faizmohideen on 20/11/2017.
 */

public class Alert {
    public long alertLevel;
    public int numOfAreas;
    public long hazardScenario;
    public long population;
    public String otherName;
    public String reason;

    public Alert(long alertLevel, long hazardScenario, long population, String otherName) {
        this.alertLevel = alertLevel;
        this.hazardScenario = hazardScenario;
        this.population = population;
        this.otherName = otherName;
    }

    public int getNumOfAreas() {
        return numOfAreas;
    }

    public void setNumOfAreas(int numOfAreas) {
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
