package org.alertpreparedness.platform.alert.firebase.wrappers;

import android.service.autofill.Dataset;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.ClockSetting;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;

import butterknife.internal.ListenerClass;
import durdinapps.rxfirebase2.RxFirebaseChildEvent;

/**
 * Created by Tj on 08/03/2018.
 */

public class ActionItemWrapper {

    private Group group;
    private ClockSetting clockSetting;
    private ActionType type;
    private DataSnapshot typeSnapshot;
    private DataSnapshot actionSnapshot;

    private ActionItemWrapper(ActionType type, DataSnapshot typeSnapshot, DataSnapshot actionSnapshot, Group group) {
        this.type = type;
        this.typeSnapshot = typeSnapshot;
        this.actionSnapshot = actionSnapshot;
        this.group = group;
    }

    public static ActionItemWrapper createCHS(DataSnapshot dataSnapshot) {
        return new ActionItemWrapper(ActionType.CHS, dataSnapshot, null, Group.NONE);
    }

    public static ActionItemWrapper createCHS(DataSnapshot dataSnapshot, @Nullable DataSnapshot actionSnapshot, Group group) {
        return new ActionItemWrapper(ActionType.CHS, dataSnapshot, actionSnapshot, group);
    }

    public static ActionItemWrapper createMandated(DataSnapshot dataSnapshot) {
        return new ActionItemWrapper(ActionType.MANDATED, dataSnapshot, null, Group.NONE);
    }

    public static ActionItemWrapper createMandated(DataSnapshot dataSnapshot, DataSnapshot actionSnapshot, Group group) {
        return new ActionItemWrapper(ActionType.MANDATED, dataSnapshot, actionSnapshot, group);
    }

    public static ActionItemWrapper createAction(DataSnapshot dataSnapshot, DataSnapshot actionSnapshot, Group group) {
        return new ActionItemWrapper(ActionType.ACTION, dataSnapshot, actionSnapshot, group);
    }

    public static ActionItemWrapper createAction(DataSnapshot actionSnapshot, Group group) {
        return new ActionItemWrapper(ActionType.ACTION, null, actionSnapshot, group);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public ClockSetting getClockSetting() {
        return clockSetting;
    }

    public void setClockSetting(ClockSetting clockSetting) {
        this.clockSetting = clockSetting;
    }

    public boolean checkActionInProgress() {
        boolean res = false;
        ActionModel actionModel = makeModel();

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

    @Override
    public String toString() {
        return "ActionItemWrapper{" +
                "type=" + type +
                ", typeSnapshot=" + typeSnapshot +
                ", actionSnapshot=" + actionSnapshot +
                '}';
    }

    public DataSnapshot getActionSnapshot() {
        return actionSnapshot;
    }

    public void setActionSnapshot(DataSnapshot actionSnapshot) {
        this.actionSnapshot = actionSnapshot;
    }

    public DataSnapshot getPrimarySnapshot() {
        return (actionSnapshot == null ? typeSnapshot : actionSnapshot);
    }

    public ActionModel makeModel() {
        if((type == ActionType.MANDATED || type == ActionType.CHS) && actionSnapshot != null) {
            ActionModel model = AppUtils.getFirebaseModelFromDataSnapshot(actionSnapshot, ActionModel.class);
            model.setLevel(typeSnapshot.child("level").getValue(Integer.class));
            model.setCreatedAt(typeSnapshot.child("createdAt").getValue(Long.class));
            if(type == ActionType.MANDATED) {
                model.setDepartment(typeSnapshot.child("department").getValue(String.class));
            }
            model.setTask(typeSnapshot.child("task").getValue(String.class));

            return model;
        }
        else if(actionSnapshot != null) {
            return AppUtils.getFirebaseModelFromDataSnapshot(actionSnapshot, ActionModel.class);
        }
        else {
            return AppUtils.getFirebaseModelFromDataSnapshot(typeSnapshot, ActionModel.class);
        }
    }

    public DataSnapshot getTypeSnapshot() {
        return typeSnapshot;
    }

    public void setTypeSnapshot(DataSnapshot typeSnapshot) {
        this.typeSnapshot = typeSnapshot;
    }

    public enum Group {
        NONE,
        LOCAL_NETWORK,
        COUNTRY,
        NETWORK_COUNTRY
    }

    public enum ActionType {
        CHS,
        MANDATED,
        ACTION
    }
}
