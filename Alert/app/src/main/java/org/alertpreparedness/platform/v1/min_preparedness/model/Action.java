package org.alertpreparedness.platform.v1.min_preparedness.model;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import org.alertpreparedness.platform.v1.model.User;

/**
 * Created by faizmohideen on 13/12/2017.
 */

public class Action {
    private String id;
    private String assignee;
    private Long budget;
    private Long createdAt;
    private String department;
    private Long dueDate;
    private Boolean isComplete;
    private Boolean isArchived;
    private Boolean isInProgress;
    private String taskName;
    private String createdByAgencyId;
    private String createdByCountryId;
    private String networkId;
    @Exclude
    private Integer frequencyValue;
    private Long frequencyBase;
    private Long actionType;
    private Long level;
    private Long updatedAt;
    private boolean requireDoc;

    @Exclude
    private Uri path;

    @Exclude
    public User user;

    @Exclude
    public DatabaseReference db;

    @Exclude
    public DatabaseReference userRef;

    @Exclude
    public DatabaseReference networkRef;
    private boolean hasCHSInfo;
    private boolean isCHS;

    public Action() {
    }

    public Action(Uri path) {
        this.path = path;
    }

    public Action(Boolean isInProgress) {
        this.isInProgress = isInProgress;
    }

    public Action(String id, String taskName, String department, String assignee, String createdByAgencyId, String createdByCountryId, String networkId, Boolean isArchived, Boolean isComplete, Long createdAt, Long updatedAt,
                  Long actionType, Long dueDate, Long budget, Long level, Long frequencyBase, Integer frequencyValue, User user, DatabaseReference db, DatabaseReference userRef, DatabaseReference networkRef) {
        this.id = id;
        this.taskName = taskName;
        this.department = department;
        this.assignee = assignee;
        this.createdByAgencyId = createdByAgencyId;
        this.createdByCountryId = createdByCountryId;
        this.networkId = networkId;
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
        this.user = user;
        this.db = db;
        this.userRef = userRef;
        this.networkRef = networkRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedByAgencyId() {
        return createdByAgencyId;
    }

    public void setCreatedByAgencyId(String createdByAgencyId) {
        this.createdByAgencyId = createdByAgencyId;
    }

    public String getCreatedByCountryId() {
        return createdByCountryId;
    }

    public void setCreatedByCountryId(String createdByCountryId) {
        this.createdByCountryId = createdByCountryId;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public Boolean getInProgress() {
        return isInProgress;
    }

    public void setInProgress(Boolean inProgress) {
        isInProgress = inProgress;
    }

    @Exclude
    public Integer getFrequencyValue() {
        return frequencyValue;
    }

    @Exclude
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
                "id='" + id + '\'' +
                ", isArchived=" + isArchived +
                ", getIsComplete=" + isComplete +
                ", isInProgress=" + isInProgress +
                ", taskName='" + taskName + '\'' +
                ", department='" + department + '\'' +
                ", assignee='" + assignee + '\'' +
                ", createdByAgencyId='" + createdByAgencyId + '\'' +
                ", createdByCountryId='" + createdByCountryId + '\'' +
                ", networkId='" + networkId + '\'' +
                ", frequencyValue=" + frequencyValue +
                ", frequencyBase=" + frequencyBase +
                ", actionLevel=" + actionType +
                ", dueDate=" + dueDate +
                ", budget=" + budget +
                ", level=" + level +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", ref=" + path +
                ", db=" + db +
                ", userRef=" + userRef +
                ", networkRef=" + networkRef +
                '}';
    }

    public boolean getRequireDoc() {
        return requireDoc;
    }

    public void setRequireDoc(boolean requireDoc) {
        this.requireDoc = requireDoc;
    }

    public void setHasCHSInfo(boolean hasCHSInfo) {
        this.hasCHSInfo = hasCHSInfo;
    }

    public boolean hasCHSInfo() {
        return hasCHSInfo;
    }

    public void setIsCHS(boolean isCHS) {
        this.isCHS = isCHS;
    }

    public boolean isCHS() {
        return isCHS;
    }
}
