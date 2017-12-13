package org.alertpreparedness.platform.alert.min_preparedness.model;

/**
 * Created by faizmohideen on 13/12/2017.
 */

public class Action {
    private String taskName;
    private String department;
    private String assignee;
    private long actionType;
    private long dueDate;
    private int budget;

    public Action(String taskName, String department, String assignee, long actionType, long dueDate, int budget) {
        this.taskName = taskName;
        this.department = department;
        this.assignee = assignee;
        this.actionType = actionType;
        this.dueDate = dueDate;
        this.budget = budget;
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

    public void setActionType(long actionType) {
        this.actionType = actionType;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }
}
