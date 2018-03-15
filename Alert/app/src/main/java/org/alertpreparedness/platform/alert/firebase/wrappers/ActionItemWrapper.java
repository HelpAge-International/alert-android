package org.alertpreparedness.platform.alert.firebase.wrappers;

import android.service.autofill.Dataset;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.ClockSetting;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;

/**
 * Created by Tj on 08/03/2018.
 */

public class ActionItemWrapper {

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    private ClockSetting clockSetting;

    public ClockSetting getClockSetting() {
        return clockSetting;
    }

    public void setClockSetting(ClockSetting clockSetting) {
        this.clockSetting = clockSetting;
    }

    public boolean checkActionInProgress() {
        boolean res = false;
        ActionModel actionModel = AppUtils.getFirebaseModelFromDataSnapshot(getPrimarySnapshot(), ActionModel.class);

        if(actionModel.hasCustomClockSettings()) {

            if (actionModel.getCreatedAt() != null && actionModel.getFrequencyBase() == Constants.DUE_WEEK) {
                res = DateHelper.isInProgressWeek(actionModel.getCreatedAt(), actionModel.getFrequencyValue());
            }
            else if (actionModel.getCreatedAt() != null && actionModel.getFrequencyBase() == Constants.DUE_MONTH) {
                res = DateHelper.isInProgressMonth(actionModel.getCreatedAt(), actionModel.getFrequencyValue());
            }
            else if (actionModel.getCreatedAt() != null && actionModel.getFrequencyBase() == Constants.DUE_YEAR) {
                res = DateHelper.isInProgressYear(actionModel.getCreatedAt(), actionModel.getFrequencyValue());
            }

        }
        else {
            ClockSetting clockSetting = getClockSetting();
            if (actionModel.getCreatedAt() != null && clockSetting.getDurationType() == Constants.DUE_WEEK) {
                res = DateHelper.isInProgressWeek(actionModel.getCreatedAt(), clockSetting.getValue());
            }
            else if (actionModel.getCreatedAt() != null && clockSetting.getDurationType() == Constants.DUE_MONTH) {
                res = DateHelper.isInProgressMonth(actionModel.getCreatedAt(), clockSetting.getValue());
            }
            else if (actionModel.getCreatedAt() != null && clockSetting.getDurationType() == Constants.DUE_YEAR) {
                res = DateHelper.isInProgressYear(actionModel.getCreatedAt(), clockSetting.getValue());
            }
        }

        return res;
    }

    public enum ActionType {
        CHS,
        MANDATED,
        ACTION
    }

    private ActionType type;

    private DataSnapshot typeSnapshot;
    private DataSnapshot actionSnapshot;

    public static ActionItemWrapper createCHS(DataSnapshot dataSnapshot) {
        return new ActionItemWrapper(ActionType.CHS, dataSnapshot, null);
    }

    public static ActionItemWrapper createCHS(DataSnapshot dataSnapshot, DataSnapshot actionSnapshot) {
        return new ActionItemWrapper(ActionType.CHS, dataSnapshot, actionSnapshot);
    }

    @Override
    public String toString() {
        return "ActionItemWrapper{" +
                "type=" + type +
                ", typeSnapshot=" + typeSnapshot +
                ", actionSnapshot=" + actionSnapshot +
                '}';
    }

    public static ActionItemWrapper createMandated(DataSnapshot dataSnapshot) {
        return new ActionItemWrapper(ActionType.MANDATED, dataSnapshot, null);
    }

    public static ActionItemWrapper createMandated(DataSnapshot dataSnapshot, DataSnapshot actionSnapshot) {
        return new ActionItemWrapper(ActionType.MANDATED, dataSnapshot, actionSnapshot);
    }

    public static ActionItemWrapper createAction(DataSnapshot dataSnapshot, DataSnapshot actionSnapshot) {
        return new ActionItemWrapper(ActionType.ACTION, dataSnapshot, actionSnapshot);
    }

    public static ActionItemWrapper createAction(DataSnapshot dataSnapshot) {
        return new ActionItemWrapper(ActionType.ACTION, dataSnapshot, null);
    }

    private ActionItemWrapper(ActionType type, DataSnapshot typeSnapshot, DataSnapshot actionSnapshot) {
        this.type = type;
        this.typeSnapshot = typeSnapshot;
        this.actionSnapshot = actionSnapshot;
    }

    public DataSnapshot getActionSnapshot() {
        return actionSnapshot;
    }

    public DataSnapshot getPrimarySnapshot() {
        return (actionSnapshot == null ? typeSnapshot : actionSnapshot);
    }


    public void setActionSnapshot(DataSnapshot actionSnapshot) {
        this.actionSnapshot = actionSnapshot;
    }

    public DataSnapshot getTypeSnapshot() {
        return typeSnapshot;
    }

    public void setTypeSnapshot(DataSnapshot typeSnapshot) {
        this.typeSnapshot = typeSnapshot;
    }
}
