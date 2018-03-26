package org.alertpreparedness.platform.alert.firebase;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tj on 26/03/2018.
 */

public class TimeTrackingModel implements Serializable {
    private ArrayList<TimeTrackingItemModel> timeSpentInAmber;
    private ArrayList<TimeTrackingItemModel> timeSpentInRed;
    private ArrayList<TimeTrackingItemModel> timeSpentInGreen;

    public TimeTrackingModel() {
    }

    public ArrayList<TimeTrackingItemModel> getTimeSpentInAmber() {
        return timeSpentInAmber;
    }

    public ArrayList<TimeTrackingItemModel> getTimeSpentInRed() {
        return timeSpentInRed;
    }

    public ArrayList<TimeTrackingItemModel> getTimeSpentInGreen() {
        return timeSpentInGreen;
    }

    public void setTimeSpentInAmber(ArrayList<TimeTrackingItemModel> timeSpentInAmber) {
        this.timeSpentInAmber = timeSpentInAmber;
    }

    public void setTimeSpentInRed(ArrayList<TimeTrackingItemModel> timeSpentInRed) {
        this.timeSpentInRed = timeSpentInRed;
    }

    public void setTimeSpentInGreen(ArrayList<TimeTrackingItemModel> timeSpentInGreen) {
        this.timeSpentInGreen = timeSpentInGreen;
    }
}
