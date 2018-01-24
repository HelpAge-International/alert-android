package org.alertpreparedness.platform.alert.min_preparedness.model;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

/**
 * Created by faizmohideen on 17/01/2018.
 */

public class DataModel {

    private Boolean isArchived;
    private Boolean isComplete;
    private Boolean requireDoc;
    private String task;
    private String department;
    private String asignee;
    private String createdByAgencyId;
    private String createdByCountryId;
    private String networkId;
    @Exclude
    private Long frequencyValue;
    @Exclude
    private Long frequencyBase;
    private Long isCompleteAt;
    private Long type;
    private Long dueDate;
    private Long budget;
    private Long level;
    private Long createdAt;
    private Long updatedAt;
    private Uri path;
    public DatabaseReference db;
    public DatabaseReference userRef;

    @Override
    public String toString() {
        return "DataModel{" +
                "isArchived=" + isArchived +
                ", isComplete=" + isComplete +
                ", requireDoc=" + requireDoc +
                ", task='" + task + '\'' +
                ", department='" + department + '\'' +
                ", asignee='" + asignee + '\'' +
                ", createdByAgencyId='" + createdByAgencyId + '\'' +
                ", createdByCountryId='" + createdByCountryId + '\'' +
                ", networkId='" + networkId + '\'' +
                ", frequencyValue=" + frequencyValue +
                ", frequencyBase=" + frequencyBase +
                ", isCompleteAt=" + isCompleteAt +
                ", type=" + type +
                ", dueDate=" + dueDate +
                ", budget=" + budget +
                ", level=" + level +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", path=" + path +
                ", db=" + db +
                ", userRef=" + userRef +
                '}';
    }

    public DataModel() {
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

    public Long getIsCompleteAt() {
        return isCompleteAt;
    }

    public void setIsCompleteAt(Long isCompleteAt) {
        this.isCompleteAt = isCompleteAt;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean archived) {
        isArchived = archived;
    }

    public Boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Boolean complete) {
        isComplete = complete;
    }

    public Boolean getRequireDoc() {
        return requireDoc;
    }

    public void setRequireDoc(Boolean requireDoc) {
        this.requireDoc = requireDoc;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAsignee() {
        return asignee;
    }

    public void setAsignee(String asignee) {
        this.asignee = asignee;
    }

    @Exclude
    public Long getFrequencyValue() {
        return frequencyValue;
    }

    @Exclude
    public void setFrequencyValue(Long frequencyValue) {
        this.frequencyValue = frequencyValue;
    }

    @Exclude
    public void setFrequencyValue(String frequencyValue) {
        this.frequencyValue = Long.valueOf(frequencyValue);
    }

    @Exclude
    public Long getFrequencyBase() {
        return frequencyBase;
    }


    @Exclude
    public void setFrequencyBase(Long frequencyBase) {
        this.frequencyBase = frequencyBase;
    }

    @Exclude
    public void setFrequencyBase(String frequencyBase) {
        this.frequencyBase = Long.valueOf(frequencyBase);
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
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

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
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

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    public DatabaseReference getDb() {
        return db;
    }

    public void setDb(DatabaseReference db) {
        this.db = db;
    }

    public DatabaseReference getUserRef() {
        return userRef;
    }

    public void setUserRef(DatabaseReference userRef) {
        this.userRef = userRef;
    }


}
