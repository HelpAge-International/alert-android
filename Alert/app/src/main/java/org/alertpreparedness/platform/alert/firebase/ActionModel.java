package org.alertpreparedness.platform.alert.firebase;

/**
 * Created by Tj on 27/12/2017.
 */

public class ActionModel {

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

//    if(isCompleteExist){
//        boolean isComplete = (boolean) dataSnapshot.child("isComplete").getValue();
//        Task tasks = new Task(isComplete);
//
//    }
//
//    if (asignee != null && task != null && !isCompleteExist && asignee.equals(uid)) {
//        if (dataSnapshot.hasChild("dueDate")) {
//
//            long dueDate = (long) dataSnapshot.child("dueDate").getValue();
//            Task tasks = new Task(0, "action", task, dueDate);
//            iHome.addTask(tasks);
//        }
//    }

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

    public Long getDueDate() {
        return dueDate;
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

    public void setComplete(Boolean complete) {
        isComplete = complete;
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

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
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
