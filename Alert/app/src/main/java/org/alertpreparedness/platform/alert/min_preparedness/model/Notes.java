package org.alertpreparedness.platform.alert.min_preparedness.model;

/**
 * Created by faizmohideen on 03/01/2018.
 */

public class Notes {

    private Long time;
    private String content;
    private String uploadBy;
    private String actionTaskID;

    public Notes() {
    }

    public Notes(String actionTaskID) {
        this.actionTaskID = actionTaskID;
    }

    public Notes(String content, Long time, String uploadBy) {
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
        return "Notes{" +
                "time=" + time +
                ", content='" + content + '\'' +
                ", uploadBy='" + uploadBy + '\'' +
                ", actionTaskID='" + actionTaskID + '\'' +
                '}';
    }
}
