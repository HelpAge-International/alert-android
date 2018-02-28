package org.alertpreparedness.platform.alert.firebase;

/**
 * Created by Tj on 27/12/2017.
 */

public class IndicatorModel {

    private String assignee;
    private String name;
    private Long triggerSelected;
    private Long dueDate;
    private String hazardId;

    public IndicatorModel() {

    }

    @Override
    public String toString() {
        return "IndicatorModel{" +
                "assignee='" + assignee + '\'' +
                ", name='" + name + '\'' +
                ", triggerSelected=" + triggerSelected +
                ", dueDate=" + dueDate +
                '}';
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTriggerSelected() {
        return triggerSelected;
    }

    public void setTriggerSelected(Long triggerSelected) {
        this.triggerSelected = triggerSelected;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public void setHazardId(String hazardId) {
        this.hazardId = hazardId;
    }

    public String getHazardId() {
        return hazardId;
    }
}
