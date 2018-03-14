package org.alertpreparedness.platform.alert.action;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.ActionModel;

import java.util.List;

/**
 * Created by Tj on 28/02/2018.
 */

public class ActionAPAInProgressProcessor extends ActionInProgressProcessor {

    private final List<Integer> alertHazardTypes;
    private final List<Integer> networkHazardTypes;

    public ActionAPAInProgressProcessor(int type, DataSnapshot snapshot, ActionModel model, String id, String parentId, ActionProcessorListener listener, List<Integer> alertHazardTypes, List<Integer> networkHazardTypes) {
        super(type, snapshot, model, id, parentId, listener);
        this.alertHazardTypes = alertHazardTypes;
        this.networkHazardTypes = networkHazardTypes;
    }
    @Override
    protected void addObjects(String name, Long createdAt, Long level,
                              ActionModel model, DataSnapshot childSnapshot, String id, Boolean isCHS, Boolean isCHSAssigned, Boolean isMandated, Boolean isMandatedAssigned) {
        if (user.getUserID().equals(model.getAsignee()) //MPA Custom assigned and in-progress for logged in user.
                && model.getAsignee() != null
                && level != null
                && level == getType()
                && model.getDueDate() != null
                && (model.getIsCompleteAt() == null && model.getIsComplete() == null || model.getIsCompleteAt() == null && !model.getIsComplete()) // isComplete can be set to false :D, and when it's false, isCreatedAt will disappear.
                && name != null
                || (isCHS && isCHSAssigned //MPA CHS assigned and in-progress for logged in user.
                && user.getUserID().equals(model.getAsignee())
                && model.getAsignee() != null
                && level != null
                && level == getType()
                && model.getDueDate() != null
                && (model.getIsCompleteAt() == null && model.getIsComplete() == null || model.getIsCompleteAt() == null && !model.getIsComplete())
                && name != null)
                || (isMandated && isMandatedAssigned //MPA Mandated assigned and in-progress for logged in user.
                && user.getUserID().equals(model.getAsignee())
                && model.getAsignee() != null
                && level != null
                && level == getType()
                && model.getDueDate() != null
                && (model.getIsCompleteAt() == null && model.getIsComplete() == null || model.getIsCompleteAt() == null && !model.getIsComplete())
                && name != null)) {

            if(model.getAssignHazard() != null
                    && ((networkHazardTypes.indexOf(model.getAssignHazard().get(0)) != -1 && model.isNetworkLevel())
                    || (alertHazardTypes.indexOf(model.getAssignHazard().get(0)) != -1 && !model.isNetworkLevel())) && !model.getIsArchived() && model.getAsignee() != null) {

                listener.onAddAction(childSnapshot, new Action(
                        id,
                        name,
                        model.getDepartment(),
                        model.getAsignee(),
                        model.getCreatedByAgencyId(),
                        model.getCreatedByCountryId(),
                        model.getNetworkId(),
                        model.getIsArchived(),
                        model.getIsComplete(),
                        createdAt,
                        model.getUpdatedAt(),
                        model.getType(),
                        model.getDueDate(),
                        model.getBudget(),
                        level,
                        model.getFrequencyBase(),
                        freqValue,
                        user,
                        dbAgencyRef.getRef(),
                        dbUserPublicRef.getRef(),
                        dbNetworkRef.getRef())
                );
            }
            else {
                listener.tryRemoveAction(childSnapshot);
            }
        }
        else {
            listener.tryRemoveAction(childSnapshot);
        }
    }

    public int getType() {
        return type;
    }
}
