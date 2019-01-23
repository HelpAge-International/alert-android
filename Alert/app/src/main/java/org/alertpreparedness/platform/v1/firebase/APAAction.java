package org.alertpreparedness.platform.v1.firebase;

import java.util.ArrayList;

public class APAAction extends FirebaseModel {

    private ArrayList<Integer> assignHazard;

    private String asignee;

    private Integer budget;

    private String task;

    private Boolean requireDoc;

    private String department;

    private Long createdAt;

    private Long updatedAt;

    private int type;

    private int level;

    private Long dueDate;

    public APAAction() {
    }

    @Override
    public String toString() {
        return "APAAction{" +
                "assignHazard=" + assignHazard +
                ", asignee='" + asignee + '\'' +
                ", budget=" + budget +
                ", task='" + task + '\'' +
                ", requireDoc=" + requireDoc +
                ", department='" + department + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", type=" + type +
                ", level=" + level +
                ", dueDate=" + dueDate +
                '}' +
                super.toString();
    }

    public ArrayList<Integer> getAssignHazard() {
        return assignHazard;
    }

    public void setAssignHazard(ArrayList<Integer> assignHazard) {
        this.assignHazard = assignHazard;
    }

    public String getAsignee() {
        return asignee;
    }

    public void setAsignee(String asignee) {
        this.asignee = asignee;
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
        this.updatedAt = createdAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }
}
