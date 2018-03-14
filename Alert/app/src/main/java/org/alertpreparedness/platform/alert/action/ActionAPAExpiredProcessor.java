package org.alertpreparedness.platform.alert.action;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.ActionModel;

import java.util.List;

/**
 * Created by Tj on 28/02/2018.
 */

public class ActionAPAExpiredProcessor extends ActionExpiredProcessor {

    private final List<Integer> alertHazardTypes;
    private List<Integer> networkHazardTypes;

    public ActionAPAExpiredProcessor(int type, DataSnapshot snapshot, ActionModel model, String id, String parentId, ActionProcessorListener listener, List<Integer> alertHazardTypes, List<Integer> networkHazardTypes) {
        super(type, snapshot, model, id, parentId, listener);
        this.alertHazardTypes = alertHazardTypes;
        this.networkHazardTypes = networkHazardTypes;
    }

    @Override
    protected void addObjects(String name, String department, Long createdAt, Long level,
                              ActionModel model, DataSnapshot childSnapshot, String id, String actionIDs, Boolean isCHS, Boolean isCHSAssigned, Boolean isMandated, Boolean isMandatedAssigned) {

        if (user.getUserID().equals(model.getAsignee()) //MPA CUSTOM assigned and EXPIRED for logged in user.
                && model.getLevel() != null
                && model.getLevel() == getType()
                && model.getDueDate() != null
                && model.getTask() != null
                || (user.getUserID().equals(model.getAsignee()) //MPA CHS assigned and EXPIRED for logged in user.
                && isCHSAssigned && isCHS
                && model.getLevel() != null
                && model.getLevel() == getType()
                && model.getDueDate() != null
                && model.getTask() != null)
                || (user.getUserID().equals(model.getAsignee()) //MPA Mandated assigned and EXPIRED for logged in user.
                && isMandatedAssigned && isMandated
                && model.getLevel() != null
                && model.getLevel() == getType()
                && model.getDueDate() != null
                && model.getTask() != null)) {

            if(model.getAssignHazard() != null
                    && ((networkHazardTypes.indexOf(model.getAssignHazard().get(0)) != -1 && model.isNetworkLevel())
                    || (alertHazardTypes.indexOf(model.getAssignHazard().get(0)) != -1 && !model.isNetworkLevel()))
                    && !model.getIsArchived()
                    ) {

                listener.onAddAction(childSnapshot, new Action(
                        id,
                        name,
                        department,
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
                        dbNetworkRef)

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
