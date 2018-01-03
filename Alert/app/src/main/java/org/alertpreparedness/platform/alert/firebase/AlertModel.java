package org.alertpreparedness.platform.alert.firebase;


import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tj on 19/12/2017.
 */

public class AlertModel implements Serializable {

    private String key;

    private List<AffectedAreaModel> affectedAreas = new ArrayList<>();

    private Integer alertLevel;

    private Integer hazardScenario;

    private String infoNotes;

    private Long timeCreated = new Date().getTime();

    private String createdBy;

    private Integer estimatedPopulation;

    private String reasonForRedAlert;

    private ApprovalModel approval;

    private Long timeUpdated;

    private String updatedBy;

    private String otherName;

    private String name;

//    private transient DataSnapshot snapshot;

    public AlertModel() {}

    public AlertModel(int alertLevel, int hazardScenario, int estimatedPopulation, String infoNotes, String createdBy, ArrayList<AffectedAreaModel> affectedAreas) {

        this.alertLevel = alertLevel;
        this.hazardScenario = hazardScenario;
        this.estimatedPopulation = estimatedPopulation;
        this.infoNotes = infoNotes;
        this.createdBy = createdBy;
        this.affectedAreas = affectedAreas;
    }

    public Integer getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(Integer alertLevel) {
        this.alertLevel = alertLevel;
    }

    public List<AffectedAreaModel> getAffectedAreas() {
        return affectedAreas;
    }

    public void setAffectedAreas(List<AffectedAreaModel> affectedAreas) {
        this.affectedAreas = affectedAreas;
    }

    public Integer getHazardScenario() {
        return hazardScenario;
    }

    public void setHazardScenario(Integer hazardScenario) {
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

    public Integer getEstimatedPopulation() {
        return estimatedPopulation;
    }

    public void setEstimatedPopulation(Integer estimatedPopulation) {
        this.estimatedPopulation = estimatedPopulation;
    }

    public String getReasonForRedAlert() {
        return reasonForRedAlert;
    }

    public void setReasonForRedAlert(String reasonForRedAlert) {
        this.reasonForRedAlert = reasonForRedAlert;
    }

    public boolean wasRedAlertRequested() {
        return approval == null || approval.getCountryDirector() == null || approval.getCountryDirector().size() == 0;
    }

    public ApprovalModel getApproval() {
        return approval;
    }

    public void setApproval(ApprovalModel approval) {
        this.approval = approval;
    }

    public Long getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(Long timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "AlertModel{" +
                "key='" + key + '\'' +
                ", affectedAreas=" + affectedAreas +
                ", alertLevel=" + alertLevel +
                ", hazardScenario=" + hazardScenario +
                ", infoNotes='" + infoNotes + '\'' +
                ", timeCreated=" + timeCreated +
                ", createdBy='" + createdBy + '\'' +
                ", estimatedPopulation=" + estimatedPopulation +
                ", reasonForRedAlert='" + reasonForRedAlert + '\'' +
                ", approval=" + approval +
                ", timeUpdated=" + timeUpdated +
                ", updatedBy='" + updatedBy + '\'' +
                ", otherName='" + otherName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
