package org.alertpreparedness.platform.alert.firebase;

public class ActionModel extends FirebaseModel {

    private String asignee;
    private String task;
    private Boolean isComplete;
    private Long isCompleteAt;
    private Long createdAt;
    private Long dueDate;
    private Long updatedAt;
    private Integer type;
    private Integer level;
    private Long budget;
    private Boolean requireDoc;
    private Integer frequencyBase;
    private Integer frequencyValue;

    public ActionModel() {
    }

    @Override
    public String toString() {
        return "ActionModel{" +
                "asignee='" + asignee + '\'' +
                ", task='" + task + '\'' +
                ", isComplete=" + isComplete +
                ", isCompleteAt=" + isCompleteAt +
                ", createdAt=" + createdAt +
                ", dueDate=" + dueDate +
                ", updatedAt=" + updatedAt +
                ", type=" + type +
                ", level=" + level +
                ", budget=" + budget +
                ", requireDoc=" + requireDoc +
                ", frequencyBase=" + frequencyBase +
                ", frequencyValue=" + frequencyValue +
                '}' +
                super.toString();
    }

    public String getAsignee() {
        return asignee;
    }

    public void setAsignee(String asignee) {
        this.asignee = asignee;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isComplete() {
        return (isComplete == null ? false : isComplete);
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getBudget() {
        return budget;
    }

    public void setBudget(Long budget) {
        this.budget = budget;
    }

    public Boolean getRequireDoc() {
        return requireDoc;
    }

    public void setRequireDoc(Boolean requireDoc) {
        this.requireDoc = requireDoc;
    }

    public Long getIsCompleteAt() {
        return isCompleteAt;
    }

    public void setIsCompleteAt(Long isCompleteAt) {
        this.isCompleteAt = isCompleteAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getFrequencyBase() {
        return frequencyBase;
    }

    public void setFrequencyBase(Integer frequencyBase) {
        this.frequencyBase = frequencyBase;
    }

    public Integer getFrequencyValue() {
        return frequencyValue;
    }

    public void setFrequencyValue(Integer frequencyValue) {
        this.frequencyValue = frequencyValue;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
