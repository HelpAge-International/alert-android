package org.alertpreparedness.platform.v1.firebase;

public class IndicatorModel extends FirebaseModel {

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
                ", triggerLevel=" + triggerSelected +
                ", dueDate=" + dueDate +
                ", hazardId='" + hazardId + '\'' +
                '}' +
                super.toString();
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

    public String getHazardId() {
        return hazardId;
    }

    public void setHazardId(String hazardId) {
        this.hazardId = hazardId;
    }
}
