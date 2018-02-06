package org.alertpreparedness.platform.alert.firebase;

import java.util.ArrayList;

/**
 * Created by Tj on 06/02/2018.
 */

public class APAAction {

    private ArrayList<Integer> assignHazard;

    private String assignee;

    private Integer budget;

    private String task;

    private Boolean requireDoc;

    private String department;

    private Long createdAt;

    public APAAction() {}

    public ArrayList<Integer> getAssignHazard() {
        return assignHazard;
    }

    public void setAssignHazard(ArrayList<Integer> assignHazard) {
        this.assignHazard = assignHazard;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
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

    public Boolean getRequireDoc() {
        return requireDoc;
    }

    public void setRequireDoc(Boolean requireDoc) {
        this.requireDoc = requireDoc;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
