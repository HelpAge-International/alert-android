package org.alertpreparedness.platform.alert.firebase.wrappers;

import android.service.autofill.Dataset;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.ClockSetting;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.notifications.ActionFetcher;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.jetbrains.annotations.NotNull;

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

    public static ActionItemWrapper createCHS(DataSnapshot dataSnapshot, @NotNull DataSnapshot actionSnapshot, Group group) {
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
        return AppUtils.isActionInProgress(makeModel(), getClockSetting());
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

            if(model.getType() == null) {
                //handles the case when CHS actions dont have a type field in firebase
                model.setType(Constants.CHS);
            }

            model.setLevel(typeSnapshot.child("level").getValue(Integer.class));
            model.setCreatedAt(typeSnapshot.child("createdAt").getValue(Long.class));

            if(actionSnapshot.child("updatedAt").exists()) {
                model.setUpdatedAt(actionSnapshot.child("updatedAt").getValue(Long.class));
            }

            if(type == ActionType.MANDATED) {
                model.setDepartment(typeSnapshot.child("department").getValue(String.class));
            }

            model.setTask(typeSnapshot.child("task").getValue(String.class));
            model.setAsignee(actionSnapshot.child("asignee").getValue(String.class));

            return model;
        }
        else if(actionSnapshot != null) {
            return AppUtils.getFirebaseModelFromDataSnapshot(actionSnapshot, ActionModel.class);
        }
        else {
            if(type == ActionType.MANDATED) {
                return AppUtils.getFirebaseModelFromDataSnapshot(typeSnapshot, ActionModel.class);
            }
            else {
                ActionModel model = AppUtils.getFirebaseModelFromDataSnapshot(typeSnapshot, ActionModel.class);
                model.setHasEnoughChsInfo(false);
                return model;
            }
        }
    }

    public DataSnapshot getTypeSnapshot() {
        return typeSnapshot;
    }

    public void setTypeSnapshot(DataSnapshot typeSnapshot) {
        this.typeSnapshot = typeSnapshot;
    }

    public String getGroupId() {
        return actionSnapshot == null ? null : actionSnapshot.getRef().getParent().getKey();
    }

    public String getActionId(){
        if(actionSnapshot != null){
            return actionSnapshot.getKey();
        }
        else if(typeSnapshot != null){
            return typeSnapshot.getKey();
        }
        return null;
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
