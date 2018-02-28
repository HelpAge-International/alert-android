package org.alertpreparedness.platform.alert.action;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;

import java.util.List;

/**
 * Created by Tj on 28/02/2018.
 */

public class ActionAPAUnassignedProcessor extends ActionUnassignedProcessor {

    private final List<Integer> alertHazardTypes;
    private final List<Integer> networkHazardTypes;

    public ActionAPAUnassignedProcessor(int type, DataSnapshot snapshot, DataModel model, String id, String parentId, ActionProcessorListener listener, List<Integer> alertHazardTypes, List<Integer> networkHazardTypes) {
        super(type, snapshot, model, id, parentId, listener);
        this.alertHazardTypes = alertHazardTypes;
        this.networkHazardTypes = networkHazardTypes;
    }


    @Override
    protected void addObject( String taskName,
                              String department,
                              Long createdAt,
                              Long level,
                              String key,
                              String assignee,
                              String agencyId,
                              String countryId,
                              String networkId,
                              Boolean isArchived,
                              Boolean isComplete,
                              Long updatedAt,
                              Long actionType,
                              Long dueDate,
                              Long budget,
                              Long frequencyBase,
                              Integer frequencyValue
    ) {


        if(model.getAssignHazard() != null
                && ((networkHazardTypes.indexOf(model.getAssignHazard().get(0)) != -1 && model.isNetworkLevel())
                || (alertHazardTypes.indexOf(model.getAssignHazard().get(0)) != -1 && !model.isNetworkLevel())) && !model.getIsArchived() && model.getAsignee() == null) {
            super.addObject(taskName, department, createdAt, level, key, assignee, agencyId, countryId, networkId, isArchived, isComplete, updatedAt, actionType, dueDate, budget, frequencyBase, frequencyValue);
        }

    }


    public int getType() {
        return type;
    }
}
