package org.alertpreparedness.platform.alert.min_preparedness.model;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by faizmohideen on 13/12/2017.
 */

public class Action {
    private Boolean isArchived;
    private Boolean isComplete;
    private Boolean isInProgress;
    private String taskName;
    private String department;
    private String assignee;
    private Integer frequencyValue;
    private Long frequencyBase;
    private Long actionType;
    private Long dueDate;
    private Long budget;
    private Long level;
    private Long createdAt;
    private Long updatedAt;
    private Uri path;
    public DatabaseReference db;
    public DatabaseReference userRef;

    public Action() {
    }

    public Action(Uri path) {
        this.path = path;
    }

    public Action(Boolean isInProgress) {
        this.isInProgress = isInProgress;
    }

    public Action(String taskName, String department, String assignee, Boolean isArchived, Boolean isComplete, Long createdAt, Long updatedAt,
                  Long actionType, Long dueDate, Long budget, Long level, Long frequencyBase, Integer frequencyValue, DatabaseReference db, DatabaseReference userRef) {
        this.taskName = taskName;
        this.department = department;
        this.assignee = assignee;
        this.isArchived = isArchived;
        this.isComplete = isComplete;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.actionType = actionType;
        this.dueDate = dueDate;
        this.budget = budget;
        this.level = level;
        this.frequencyBase = frequencyBase;
        this.frequencyValue = frequencyValue;
        this.db = db;
        this.userRef = userRef;
    }

    public Boolean getInProgress() {
        return isInProgress;
    }

    public void setInProgress(Boolean inProgress) {
        isInProgress = inProgress;
    }

    public Integer getFrequencyValue() {
        return frequencyValue;
    }

    public void setFrequencyValue(Integer frequencyValue) {
        this.frequencyValue = frequencyValue;
    }

    public Long getFrequencyBase() {
        return frequencyBase;
    }

    public void setFrequencyBase(Long frequencyBase) {
        this.frequencyBase = frequencyBase;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public long getActionType() {
        return actionType;
    }

    public void setActionType(Long actionType) {
        this.actionType = actionType;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public Long getBudget() {
        return budget;
    }

    public void setBudget(Long budget) {
        this.budget = budget;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public Boolean getComplete() {
        return isComplete;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Action{" +
                "isArchived=" + isArchived +
                ", isComplete=" + isComplete +
                ", taskName='" + taskName + '\'' +
                ", department='" + department + '\'' +
                ", assignee='" + assignee + '\'' +
                ", actionType=" + actionType +
                ", dueDate=" + dueDate +
                ", budget=" + budget +
                ", level=" + level +
                ", db=" + db +
                '}';
    }
}
