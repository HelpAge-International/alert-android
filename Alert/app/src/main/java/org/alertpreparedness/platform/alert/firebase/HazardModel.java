package org.alertpreparedness.platform.alert.firebase;

public class HazardModel {

    private String id;
    private int hazardScenario;
    private boolean isActive;
    private boolean isSeasonal;
    private boolean risk;

    public HazardModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHazardScenario() {
        return hazardScenario;
    }

    public void setHazardScenario(int hazardScenario) {
        this.hazardScenario = hazardScenario;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isSeasonal() {
        return isSeasonal;
    }

    public void setSeasonal(boolean seasonal) {
        isSeasonal = seasonal;
    }

    public boolean isRisk() {
        return risk;
    }

    public void setRisk(boolean risk) {
        this.risk = risk;
    }
}
