package org.alertpreparedness.platform.alert.firebase;

import com.google.firebase.auth.FirebaseAuth;

import org.alertpreparedness.platform.alert.dashboard.model.Tasks;
import org.alertpreparedness.platform.alert.responseplan.ActiveFragment;

/**
 * Created by Tj on 27/12/2017.
 */

public class ActionModel {

    private String asignee;
    private String task;
    private Boolean isComplete;
    private Long dueDate;
    private Long updatedAt;
    private Integer type;
    private Long budget;
    private Boolean requireDoc;

    public ActionModel() {

    }

//    if(isCompleteExist){
//        boolean isComplete = (boolean) dataSnapshot.child("isComplete").getValue();
//        Tasks tasks = new Tasks(isComplete);
//
//    }
//
//    if (asignee != null && task != null && !isCompleteExist && asignee.equals(uid)) {
//        if (dataSnapshot.hasChild("dueDate")) {
//
//            long dueDate = (long) dataSnapshot.child("dueDate").getValue();
//            Tasks tasks = new Tasks(0, "action", task, dueDate);
//            iHome.addTask(tasks);
//        }
//    }

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

    public boolean isComplete() {
        return (isComplete == null ? false : isComplete);
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
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
}
