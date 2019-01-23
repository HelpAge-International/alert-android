package org.alertpreparedness.platform.v1.firebase;

public class ResponsePlanModel extends FirebaseModel {

    private Long timeCreated;
    private Long timeUpdated;
    private String name;

    public ResponsePlanModel() {
    }

    @Override
    public String toString() {
        return "ResponsePlanModel{" +
                "timeCreated=" + timeCreated +
                ", timeUpdated=" + timeUpdated +
                ", name='" + name + '\'' +
                '}' +
                super.toString();
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
