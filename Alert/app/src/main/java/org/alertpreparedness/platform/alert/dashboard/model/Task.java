package org.alertpreparedness.platform.alert.dashboard.model;

/**
 * Created by faizmohideen on 13/11/2017.
 */

public class Task {

    private String parentId;
    private int alertLevel;
    private int actionType;
    private String taskType;
    private String taskName;
    public long dueDate;
    private boolean requireDoc;
    private String hazardId;

    public static final String TASK_ACTION = "action";
    public static final String TASK_INDICATOR = "indicator";

    public Task(boolean isCompleteTask) {
        this.isCompleteTask = isCompleteTask;
    }

    public Task(String parentId, int alertLevel, String taskType, String taskName, long dueDate, boolean requireDoc, Integer level) {
        this.parentId = parentId;
        this.alertLevel = alertLevel;
        this.taskType = taskType;
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.requireDoc = requireDoc;
        this.actionType = level;
    }

    public boolean isCompleteTask() {
        return isCompleteTask;
    }

    public void setCompleteTask(boolean completeTask) {
        isCompleteTask = completeTask;
    }

    private boolean isCompleteTask;

    public Task(String parentId, int alertLevel, String taskType, String taskName, long dueDate) {
        this.parentId = parentId;
        this.alertLevel = alertLevel;
        this.taskType = taskType;
        this.taskName = taskName;
        this.dueDate = dueDate;
    }

    public Task(String parentId, int alertLevel, String taskType, String taskName, long dueDate, boolean requireDoc) {
        this.parentId = parentId;
        this.alertLevel = alertLevel;
        this.taskType = taskType;
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.requireDoc = requireDoc;
    }

    public Task(String parentId, int alertLevel, String taskType, String taskName, long dueDate, int actionType) {
        this.parentId = parentId;
        this.alertLevel = alertLevel;
        this.taskType = taskType;
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.actionType = actionType;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(int alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getHazardId() {
        return hazardId;
    }

    public void setHazardId(String hazardId) {
        this.hazardId = hazardId;
    }

    public boolean isRequireDoc() {
        return requireDoc;
    }

    public void setRequireDoc(boolean requireDoc) {
        this.requireDoc = requireDoc;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}