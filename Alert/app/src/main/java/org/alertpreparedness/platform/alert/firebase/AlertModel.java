package org.alertpreparedness.platform.alert.firebase;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlertModel extends FirebaseModel implements Serializable {

    private boolean isNetwork;

    private String leadAgencyId;

    private String agencyAdminId;

    private String parentKey;

    private List<AffectedAreaModel> affectedAreas = new ArrayList<>();

    private Integer alertLevel;

    private Integer hazardScenario;

    private String infoNotes;

    private Long timeCreated = new Date().getTime();

    private String createdBy;

    private Long estimatedPopulation;

    private String reasonForRedAlert;

    private ApprovalModel approval;

    private Long timeUpdated;

    private String updatedBy;

    private String otherName;

    private String name;

    private String key;

    private TimeTrackingModel timeTracking;

    private Boolean redAlertApproved;

    public AlertModel() {
    }

    public AlertModel(int alertLevel, int hazardScenario, Long estimatedPopulation, String infoNotes, String
            createdBy, ArrayList<AffectedAreaModel> affectedAreas) {

        this.alertLevel = alertLevel;
        this.hazardScenario = hazardScenario;
        this.estimatedPopulation = estimatedPopulation;
        this.infoNotes = infoNotes;
        this.createdBy = createdBy;
        this.affectedAreas = affectedAreas;
    }

    @Override
    public String toString() {
        return "AlertModel{" +
                "isNetwork=" + isNetwork +
                ", leadAgencyId='" + leadAgencyId + '\'' +
                ", agencyAdminId='" + agencyAdminId + '\'' +
                ", parentKey='" + parentKey + '\'' +
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
                '}' +
                super.toString();
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

    public Long getEstimatedPopulation() {
        return estimatedPopulation;
    }

    public void setEstimatedPopulation(Long estimatedPopulation) {
        this.estimatedPopulation = estimatedPopulation;
    }

    public String getReasonForRedAlert() {
        return reasonForRedAlert;
    }

    public void setReasonForRedAlert(String reasonForRedAlert) {
        this.reasonForRedAlert = reasonForRedAlert;
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

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public boolean isNetwork() {
        return isNetwork;
    }

    public void setNetwork(boolean network) {
        isNetwork = network;
    }

    public boolean hasNetworkApproval() {
        return approval != null &&
                approval.getCountryDirector() != null &&
                approval.getCountryDirector().size() > 0 &&
                approval.getCountryDirector().entrySet().iterator().next().getValue() >= 1;
    }

    public String getLeadAgencyId() {
        return leadAgencyId;
    }

    public void setLeadAgencyId(String leadAgencyId) {
        this.leadAgencyId = leadAgencyId;
    }

    public String getAgencyAdminId() {
        return agencyAdminId;
    }

    public void setAgencyAdminId(String agencyAdminId) {
        this.agencyAdminId = agencyAdminId;
    }

    public boolean getRedAlertApproved() {
        if (redAlertApproved != null) {
            return redAlertApproved;
        }
        else {
            boolean res = false;
            for(Integer value : approval.getCountryDirector().values()) {
                if(value >= 1) {
                    res = true;
                }
            }
            return res;
        }
    }

    public void setRedAlertApproved(boolean redAlertApproved) {
        this.redAlertApproved = redAlertApproved;
    }

    public TimeTrackingModel getTimeTracking() {
        return timeTracking;
    }

    public void setTimeTracking(TimeTrackingModel timeTracking) {
        this.timeTracking = timeTracking;
    }
}
