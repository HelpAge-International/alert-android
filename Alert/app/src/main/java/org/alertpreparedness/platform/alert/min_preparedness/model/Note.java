package org.alertpreparedness.platform.alert.min_preparedness.model;


import com.google.firebase.database.Exclude;

import org.alertpreparedness.platform.alert.firebase.FirebaseModel;

/**
 * Created by faizmohideen on 03/01/2018.
 */

public class Note extends FirebaseModel {

    private Long time;
    private String content;
    private String uploadBy;
    private String actionTaskID;

    @Exclude
    private String fullName;

    public Note() {
    }

    public Note(String actionTaskID) {
        this.actionTaskID = actionTaskID;
    }

    public Note(String content, Long time, String uploadBy) {
        this.content = content;
        this.time = time;
        this.uploadBy = uploadBy;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getUploadBy() {
        return uploadBy;
    }

    public void setUploadBy(String uploadBy) {
        this.uploadBy = uploadBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getActionTaskID() {
        return actionTaskID;
    }

    public void setActionTaskID(String actionTaskID) {
        this.actionTaskID = actionTaskID;
    }

    @Override
    public String toString() {
        return "Note{" +
                "time=" + time +
                ", content='" + content + '\'' +
                ", uploadBy='" + uploadBy + '\'' +
                ", actionTaskID='" + actionTaskID + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    @Exclude
    public String getFullName() {
        return fullName;
    }

    @Exclude
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
