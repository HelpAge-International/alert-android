package org.alertpreparedness.platform.alert.firebase;

import com.google.firebase.database.Exclude;

import org.alertpreparedness.platform.alert.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tj on 26/03/2018.
 */

public class TimeTrackingModel implements Serializable {
    private ArrayList<TimeTrackingItemModel> timeSpentInAmber;
    private ArrayList<TimeTrackingItemModel> timeSpentInRed;
    private ArrayList<TimeTrackingItemModel> timeSpentInGreen;
    private ArrayList<TimeTrackingItemModel> timeSpentInGrey;

    public enum LEVEL {
        RED,
        GREY,
        GREEN,
        AMBER
    }

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

    public ArrayList<TimeTrackingItemModel> getTimeSpentInGrey() {
        return timeSpentInGrey;
    }

    public void setTimeSpentInGrey(ArrayList<TimeTrackingItemModel> timeSpentInGrey) {
        this.timeSpentInGrey = timeSpentInGrey;
    }

    public void updateAlertTimeTracking(int oldLevel, int newLevel, boolean approval) {
        LEVEL oldState = establishLevel(oldLevel, approval);
        LEVEL newState = establishLevel(newLevel, approval);
        updateTimeTracking(oldState, newState);
    }

    public void updateTimeTracking(LEVEL oldState, LEVEL newState) {
        if(oldState != newState) {
            switch (newState) {
                case RED:
                    timeSpentInRed = timeSpentInRed == null ? new ArrayList<>() : timeSpentInRed;
                    updateTimeList(timeSpentInRed);
                    break;
                case GREY:
                    timeSpentInGrey = timeSpentInGrey == null ? new ArrayList<>() : timeSpentInGrey;
                    updateTimeList(timeSpentInGrey);
                    break;
                case GREEN:
                    timeSpentInGreen = timeSpentInGreen == null ? new ArrayList<>() : timeSpentInGreen;
                    updateTimeList(timeSpentInGreen);
                    break;
                case AMBER:
                    timeSpentInAmber = timeSpentInAmber == null ? new ArrayList<>() : timeSpentInAmber;
                    updateTimeList(timeSpentInAmber);
                    break;
            }
            switch (oldState) {
                case RED:
                    timeSpentInRed = timeSpentInRed == null ? new ArrayList<>() : timeSpentInRed;
                    finishState(timeSpentInRed);
                    break;
                case GREY:
                    timeSpentInGrey = timeSpentInGrey == null ? new ArrayList<>() : timeSpentInGrey;
                    finishState(timeSpentInGrey);
                    break;
                case GREEN:
                    timeSpentInGreen = timeSpentInGreen == null ? new ArrayList<>() : timeSpentInGreen;
                    finishState(timeSpentInGreen);
                    break;
                case AMBER:
                    timeSpentInAmber = timeSpentInAmber == null ? new ArrayList<>() : timeSpentInAmber;
                    finishState(timeSpentInAmber);
                    break;
            }
        }
    }

    private void finishState(ArrayList<TimeTrackingItemModel> list) {
        if(list.size() == 0) {
            list.add(new TimeTrackingItemModel(new Date().getTime(), new Date().getTime()));
        }
        else {
            list.get(list.size() - 1).setFinish(new Date().getTime());
        }
    }

    private void updateTimeList(ArrayList<TimeTrackingItemModel> list) {
        list.add(new TimeTrackingItemModel(-1L, new Date().getTime()));
    }

    @Exclude
    private LEVEL establishLevel(int level, boolean approval) {
        switch (level) {//old level
            case Constants.TRIGGER_RED:
                if(approval) {//red
                    return LEVEL.RED;
                }
                else {//grey
                    return LEVEL.GREY;
                }
            case Constants.TRIGGER_AMBER:
                return LEVEL.AMBER;
            case Constants.TRIGGER_GREEN:
                return LEVEL.GREEN;
            default:
                return LEVEL.GREEN;
        }
    }

}
