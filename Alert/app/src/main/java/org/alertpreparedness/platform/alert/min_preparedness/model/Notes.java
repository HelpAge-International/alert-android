package org.alertpreparedness.platform.alert.min_preparedness.model;

/**
 * Created by faizmohideen on 03/01/2018.
 */

public class Notes {

    Long timeStamp;
    String content;
    String addedBy;

    public Notes(String addedBy, Long timeStamp, String content) {
        this.addedBy = addedBy;
        this.timeStamp = timeStamp;
        this.content = content;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
