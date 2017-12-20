package org.alertpreparedness.platform.alert.min_preparedness.model;

import java.util.Date;

/**
 * Created by faizmohideen on 13/12/2017.
 */

public class Action {
    private Boolean isArchived;
    private String taskName;
    private String department;
    private String assignee;
    private Long actionType;
    private Long budget;
    private Date dueDate;

    public Action(String taskName, String department, String assignee, Boolean isArchived, Long actionType, Date dueDate, Long budget) {
        this.taskName = taskName;
        this.department = department;
        this.assignee = assignee;
        this.isArchived = isArchived;
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

    public void setActionType(Long actionType) {
        this.actionType = actionType;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Long getBudget() {
        return budget;
    }

    public void setBudget(Long budget) {
        this.budget = budget;
    }

    public Boolean isArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }
}
