package org.alertpreparedness.platform.alert.model;

/**
 * Created by faizmohideen on 13/11/2017.
 */

public class Tasks {
    private String redAlertLevel;
    private String amberAlertLevel;
    private String actionType;
    private String indicatorType;
    private String taskName;
    private long dueDate;

    public Tasks(String taskName, long dueDate) {
        this.redAlertLevel = "red";
        this.amberAlertLevel = "amber";
        this.actionType = "action";
        this.indicatorType = "indicator";
        this.taskName = taskName;
        this.dueDate = dueDate;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    public String getAmberAlertLevel() {
        return amberAlertLevel;
    }

    public void setAmberAlertLevel(String amberAlertLevel) {
        this.amberAlertLevel = amberAlertLevel;
    }
    public String getRedAlertLevel() {
        return redAlertLevel;
    }

    public void setRedAlertLevel(String redAlertLevel) {
        this.redAlertLevel = redAlertLevel;
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
