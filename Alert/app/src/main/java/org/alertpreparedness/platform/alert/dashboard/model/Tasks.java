package org.alertpreparedness.platform.alert.dashboard.model;

/**
 * Created by faizmohideen on 13/11/2017.
 */

public class Tasks {

    private int alertLevel;
    private String taskType;
    private String taskName;
    public long dueDate;

    public Tasks(boolean isCompleteTask) {
        this.isCompleteTask = isCompleteTask;
    }

    public boolean isCompleteTask() {
        return isCompleteTask;
    }

    public void setCompleteTask(boolean completeTask) {
        isCompleteTask = completeTask;
    }

    private boolean isCompleteTask;

    public Tasks(int alertLevel, String taskType, String taskName, long dueDate) {
        this.alertLevel = alertLevel;
        this.taskType = taskType;
        this.taskName = taskName;
        this.dueDate = dueDate;
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

}