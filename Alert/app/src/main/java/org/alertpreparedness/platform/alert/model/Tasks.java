package org.alertpreparedness.platform.alert.model;

/**
 * Created by faizmohideen on 13/11/2017.
 */

public class Tasks {

    public static String alertLevel;
    public static String taskType;
    public static String taskName;
    public static long dueDate;

    public Tasks(String alertLevel, String taskType, String taskName, long dueDate) {
        this.alertLevel = alertLevel;
        this.taskType = taskType;
        this.taskName = taskName;
        this.dueDate = dueDate;
    }


    public static String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public static String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public static String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public static long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

}
