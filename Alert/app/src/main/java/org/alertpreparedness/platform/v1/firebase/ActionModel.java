package org.alertpreparedness.platform.v1.firebase;

import static org.alertpreparedness.platform.v2.utils.extensions.ActionExtensionsKt.canBeAssigned;

import com.google.firebase.database.Exclude;
import java.util.ArrayList;
import org.alertpreparedness.platform.v2.models.Action;
import org.alertpreparedness.platform.v2.models.ClockSetting;
import org.alertpreparedness.platform.v2.models.ClockSettingsSource;
import org.alertpreparedness.platform.v2.models.enums.ActionType;
import org.alertpreparedness.platform.v2.models.enums.HazardScenario;

public class ActionModel extends FirebaseModel {

    private Boolean isArchived;

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

    private ArrayList<Integer> assignHazard;

    private String department;

    private TimeTrackingModel timeTracking;

    @Exclude
    private boolean isChs;

    @Exclude
    private boolean hasEnoughChsInfo;

    public ActionModel() {
    }

    @Deprecated
    /*Constructor used to link v2 action class to v1 action class*/
    public ActionModel(Action action, String countryId) {
        isArchived = action.isArchived();
        asignee = action.getAssignee();
        task = action.getTask();
        isComplete = action.isComplete();
        isCompleteAt = action.isCompleteAt() == null ? null : action.isCompleteAt().getMillis();
        createdAt = action.getCreatedAt().getMillis();
        dueDate = action.getDueDate() == null ? null : action.getDueDate().getMillis();
        updatedAt = action.getUpdatedAt() == null ? null : action.getUpdatedAt().getMillis();
        type = action.getActionType().getValue();
        level = action.getActionLevel().getValue();
        budget = action.getBudget() == null ? null : action.getBudget().longValue();
        requireDoc = action.getRequireDoc();
        ClockSetting clockSettings = action.getMClockSetting();
        ClockSettingsSource source = clockSettings.getClockSettingsSource();
        frequencyBase = source == ClockSettingsSource.COUNTRY ? null : clockSettings.getType().getValue();
        frequencyValue = source == ClockSettingsSource.COUNTRY ? null : clockSettings.getValue();

        if (action.getAssignedHazards() != null) {
            assignHazard = new ArrayList<>();
            for (final HazardScenario assignedHazard : action.getAssignedHazards()) {
                assignHazard.add(assignedHazard.getValue());
            }
        }

        department = action.getDepartmentId();
        timeTracking = action.getTimeTracking() == null ? null : new TimeTrackingModel(action.getTimeTracking());
        isChs = action.getActionType() == ActionType.CHS;
        setParentId(countryId);
        setId(action.id);
        setHasEnoughChsInfo(canBeAssigned(action));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActionModel that = (ActionModel) o;

        if (!asignee.equals(that.asignee)) {
            return false;
        }
        if (!task.equals(that.task)) {
            return false;
        }
        if (!isComplete.equals(that.isComplete)) {
            return false;
        }
        if (!isCompleteAt.equals(that.isCompleteAt)) {
            return false;
        }
        if (!createdAt.equals(that.createdAt)) {
            return false;
        }
        if (!dueDate.equals(that.dueDate)) {
            return false;
        }
        if (!updatedAt.equals(that.updatedAt)) {
            return false;
        }
        if (!type.equals(that.type)) {
            return false;
        }
        if (!level.equals(that.level)) {
            return false;
        }
        if (!budget.equals(that.budget)) {
            return false;
        }
        if (!requireDoc.equals(that.requireDoc)) {
            return false;
        }
        if (!frequencyBase.equals(that.frequencyBase)) {
            return false;
        }
        if (frequencyValue != null ? !frequencyValue.equals(that.frequencyValue) : that.frequencyValue != null) {
            return false;
        }
        return assignHazard != null ? assignHazard.equals(that.assignHazard) : that.assignHazard == null;
    }

    @Exclude
    public boolean hasCustomClockSettings() {
        return frequencyValue != null && frequencyBase != null;
    }

    @Override
    public int hashCode() {
        int result = asignee.hashCode();
        result = 31 * result + task.hashCode();
        result = 31 * result + isComplete.hashCode();
        result = 31 * result + isCompleteAt.hashCode();
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + dueDate.hashCode();
        result = 31 * result + updatedAt.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + level.hashCode();
        result = 31 * result + budget.hashCode();
        result = 31 * result + requireDoc.hashCode();
        result = 31 * result + frequencyBase.hashCode();
        result = 31 * result + (frequencyValue != null ? frequencyValue.hashCode() : 0);
        result = 31 * result + (assignHazard != null ? assignHazard.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ActionModel{" +
                "asignee='" + asignee + '\'' +
                ", task='" + task + '\'' +
                ", getIsComplete=" + isComplete +
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
                ", assignHazard=" + assignHazard +
                '}';
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

    public boolean getIsComplete() {
        return (isComplete == null ? false : isComplete);
    }

    public void setIsComplete(Boolean complete) {
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

    public ArrayList<Integer> getAssignHazard() {
        return assignHazard;
    }

    public void setAssignHazard(ArrayList<Integer> assignHazard) {
        this.assignHazard = assignHazard;
    }

    public Boolean getIsArchived() {
        return (isArchived == null ? false : isArchived);
    }

    public void setIsArchived(Boolean archived) {
        isArchived = archived;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isChs() {
        return isChs;
    }

    public void setChs(boolean chs) {
        isChs = chs;
    }

    public TimeTrackingModel getTimeTracking() {
        if (timeTracking == null) {
            timeTracking = new TimeTrackingModel();
        }
        return timeTracking;
    }

    public void setTimeTracking(TimeTrackingModel timeTracking) {
        this.timeTracking = timeTracking;
    }

    @Exclude
    public void setHasEnoughChsInfo(boolean hasEnoughChsInfo) {
        this.hasEnoughChsInfo = hasEnoughChsInfo;
    }

    @Exclude
    public boolean hasEnoughChsInfo() {
        return hasEnoughChsInfo;
    }
}
