package org.alertpreparedness.platform.v1.firebase;

import com.google.firebase.database.Exclude;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v2.models.TimeTracking;
import org.alertpreparedness.platform.v2.models.TimeTrackingItem;
import org.jetbrains.annotations.NotNull;

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
        AMBER,
        NEW//only used when creating new items
    }

    public TimeTrackingModel() {
    }

    public TimeTrackingModel(final TimeTracking timeTracking) {
        if (timeTracking.getTimeSpentInGreen() != null) {
            timeSpentInGreen = new ArrayList<>();
            for (final TimeTrackingItem timeTrackingItem : timeTracking.getTimeSpentInGreen()) {
                timeSpentInGreen.add(new TimeTrackingItemModel(timeTrackingItem));
            }
        }
        if (timeTracking.getTimeSpentInAmber() != null) {
            timeSpentInAmber = new ArrayList<>();
            for (final TimeTrackingItem timeTrackingItem : timeTracking.getTimeSpentInAmber()) {
                timeSpentInAmber.add(new TimeTrackingItemModel(timeTrackingItem));
            }
        }
        if (timeTracking.getTimeSpentInRed() != null) {
            timeSpentInRed = new ArrayList<>();
            for (final TimeTrackingItem timeTrackingItem : timeTracking.getTimeSpentInRed()) {
                timeSpentInRed.add(new TimeTrackingItemModel(timeTrackingItem));
            }
        }
        if (timeTracking.getTimeSpentInGrey() != null) {
            timeSpentInGrey = new ArrayList<>();
            for (final TimeTrackingItem timeTrackingItem : timeTracking.getTimeSpentInGrey()) {
                timeSpentInGrey.add(new TimeTrackingItemModel(timeTrackingItem));
            }
        }
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

    public void updateAlertTimeTracking(int oldLevel, int newLevel) {
        LEVEL oldState = establishLevel(oldLevel);
        LEVEL newState = establishLevel(newLevel);
        updateTimeTracking(oldState, newState);
    }

    public void updateActionTimeTracking(LEVEL currentState, boolean isComplete, boolean isArchived, boolean isAssigned, boolean isInProgress) {

        LEVEL newState;
        if(!isAssigned) {
            newState = LEVEL.RED;
        }
        else {
            if (isComplete) {
                newState = LEVEL.GREEN;
            }
            else if (isArchived) {
                newState = LEVEL.GREY;
            }
            else if(isInProgress) {
                newState = LEVEL.AMBER;
            }
            else {//!isInProgress
                newState = LEVEL.GREY;
            }
        }

        System.out.println("newState = " + newState);
        System.out.println("currentState = " + currentState);
        updateTimeTracking(currentState, newState);
        System.out.println("timeSpentInGrey = " + timeSpentInGrey);
        System.out.println("timeSpentInAmber = " + timeSpentInAmber);
        System.out.println("timeSpentInGreen = " + timeSpentInGreen);
        System.out.println("timeSpentInRed = " + timeSpentInRed);
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

    public void updateIndicatorTracking(@NotNull LEVEL level, int triggerSelected) {
        LEVEL state = establishLevel(triggerSelected);
        if(state != level) {
            updateTimeTracking(level, state);
        }
    }

    public void updateIndicatorTracking(int oldSelection, int triggerSelected) {
        LEVEL state = establishLevel(triggerSelected);
        LEVEL oldState = establishLevel(oldSelection);
        if(state != oldState) {
            updateTimeTracking(oldState, state);
        }
    }

    @Exclude
    private LEVEL establishLevel(int level) {
        switch (level) {//old level
            case Constants.TRIGGER_RED:
                return LEVEL.RED;
            case Constants.TRIGGER_AMBER:
                return LEVEL.AMBER;
            case Constants.TRIGGER_GREEN:
                return LEVEL.GREEN;
            default:
                return LEVEL.GREEN;
        }
    }

}
