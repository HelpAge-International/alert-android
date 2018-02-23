package org.alertpreparedness.platform.alert.firebase;

/**
 * Created by Elliot on 20/02/2018.
 */

public class ResponsePlanModel {

    private Long timeCreated;
    private Long timeUpdated;
    private String name;


    public ResponsePlanModel() {

    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Long getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(Long timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
