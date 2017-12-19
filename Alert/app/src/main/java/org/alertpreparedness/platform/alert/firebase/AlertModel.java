package org.alertpreparedness.platform.alert.firebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tj on 19/12/2017.
 */

public class AlertModel {

    private List<AffectedAreaModel> affectedAreas = new ArrayList<>();

    private int alertLevel;

    private int hazardScenario;

    private String infoNotes;

    private Long timeCreated = new Date().getTime();

    private String createdBy;

    private int estimatedPopulation;

    private String reasonForRedAlert;

    public AlertModel() {}

    public AlertModel(int alertLevel, int hazardScenario, int estimatedPopulation, String infoNotes, String createdBy, ArrayList<AffectedAreaModel> affectedAreas) {

        this.alertLevel = alertLevel;
        this.hazardScenario = hazardScenario;
        this.estimatedPopulation = estimatedPopulation;
        this.infoNotes = infoNotes;
        this.createdBy = createdBy;
        this.affectedAreas = affectedAreas;
    }

    public int getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(int alertLevel) {
        this.alertLevel = alertLevel;
    }

    public List<AffectedAreaModel> getAffectedAreas() {
        return affectedAreas;
    }

    public void setAffectedAreas(List<AffectedAreaModel> affectedAreas) {
        this.affectedAreas = affectedAreas;
    }

    public int getHazardScenario() {
        return hazardScenario;
    }

    public void setHazardScenario(int hazardScenario) {
        this.hazardScenario = hazardScenario;
    }

    public String getInfoNotes() {
        return infoNotes;
    }

    public void setInfoNotes(String infoNotes) {
        this.infoNotes = infoNotes;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getEstimatedPopulation() {
        return estimatedPopulation;
    }

    public void setEstimatedPopulation(int estimatedPopulation) {
        this.estimatedPopulation = estimatedPopulation;
    }

    public String getReasonForRedAlert() {
        return reasonForRedAlert;
    }

    public void setReasonForRedAlert(String reasonForRedAlert) {
        this.reasonForRedAlert = reasonForRedAlert;
    }

    @Override
    public String toString() {
        return "AlertModel{" +
                "affectedAreas=" + affectedAreas +
                ", alertLevel=" + alertLevel +
                ", hazardScenario=" + hazardScenario +
                ", infoNotes='" + infoNotes + '\'' +
                ", timeCreated=" + timeCreated +
                ", createdBy='" + createdBy + '\'' +
                ", estimatedPopulation=" + estimatedPopulation +
                ", reasonForRedAlert='" + reasonForRedAlert + '\'' +
                '}';
    }
}
