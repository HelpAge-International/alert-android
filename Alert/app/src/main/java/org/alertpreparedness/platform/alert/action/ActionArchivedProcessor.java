package org.alertpreparedness.platform.alert.action;

import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.utils.Constants;

/**
 * Created by Tj on 28/02/2018.
 */

public class ActionArchivedProcessor extends BaseActionProcessor {

    public ActionArchivedProcessor(int type, DataSnapshot snapshot, DataModel model, String id, String parentId, ActionProcessorListener listener) {
        super(type, snapshot, model, id, parentId, listener);
    }

    @Override
    public void getMandated() {
        dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                    if (actionId.contains(getChild.getKey())) {
                        String taskNameMandated = (String) getChild.child("task").getValue();
                        //String departmentMandated = (String) getChild.child("department").getValue();
                        Long manCreatedAt = (Long) getChild.child("createdAt").getValue();
                        Long manLevel = (Long) getChild.child("level").getValue();

                        isMandated = true;
                        isMandatedAssigned = true;
                        isCHS = false;
                        isCHSAssigned = false;

                        if (isMandated && isMandatedAssigned
                                && user.getUserID().equals(model.getAsignee()) //MPA Mandated assigned and ARCHIVED for logged in user.
                                && model.getLevel() != null
                                && model.getLevel() == getType()
                                && model.getIsArchived() != null
                                && model.getIsArchived()
                                && model.getDueDate() != null) {

                            listener.onAddAction(getChild.getKey(), new Action(
                                    parentId,
                                    taskNameMandated,
                                    model.getDepartment(),
                                    model.getAsignee(),
                                    model.getCreatedByAgencyId(),
                                    model.getCreatedByCountryId(),
                                    model.getNetworkId(),
                                    model.getIsArchived(),
                                    model.getIsComplete(),
                                    manCreatedAt,
                                    model.getUpdatedAt(),
                                    model.getType(),
                                    model.getDueDate(),
                                    model.getBudget(),
                                    manLevel,
                                    model.getFrequencyBase(),
                                    freqValue,
                                    user,
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef(),
                                    dbNetworkRef)
                            );
                        }
                        else {
                            listener.tryRemoveAction(getChild.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void getCHS() {
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                    if (actionId.contains(getChild.getKey())) {
                        String CHSTaskName = (String) getChild.child("task").getValue();
                        Long CHSlevel = (Long) getChild.child("level").getValue();
                        Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();
                        isCHS = true;
                        isCHSAssigned = true;

                        if (isCHSAssigned && isCHS
                                && user.getUserID().equals(model.getAsignee()) //MPA CHS assigned and ARCHIVED for logged in user.
                                && model.getLevel() != null
                                && model.getLevel() == getType()
                                && model.getIsArchived() != null
                                && model.getIsArchived()
                                && model.getDueDate() != null) {

                            listener.onAddAction(getChild.getKey(), new Action(
                                    parentId,
                                    CHSTaskName,
                                    model.getDepartment(),
                                    model.getAsignee(),
                                    model.getCreatedByAgencyId(),
                                    model.getCreatedByCountryId(),
                                    model.getNetworkId(),
                                    model.getIsArchived(),
                                    model.getIsComplete(),
                                    CHSCreatedAt,
                                    model.getUpdatedAt(),
                                    model.getType(),
                                    model.getDueDate(),
                                    model.getBudget(),
                                    CHSlevel,
                                    model.getFrequencyBase(),
                                    freqValue,
                                    user,
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef(),
                                    dbNetworkRef)
                            );
                        }
                        else {
                            listener.tryRemoveAction(getChild.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void getCustom() {
        if (user.getUserID().equals(model.getAsignee()) //MPA CUSTOM assigned and ARCHIVED for logged in user.
                && model.getLevel() != null
                && model.getLevel() == getType()
                && model.getIsArchived() != null
                && model.getIsArchived()
                && model.getDueDate() != null) {

            listener.onAddAction(snapshot.getKey(), new Action(
                    parentId,
                    model.getTask(),
                    model.getDepartment(),
                    model.getAsignee(),
                    model.getCreatedByAgencyId(),
                    model.getCreatedByCountryId(),
                    model.getNetworkId(),
                    model.getIsArchived(),
                    model.getIsComplete(),
                    model.getCreatedAt(),
                    model.getUpdatedAt(),
                    model.getType(),
                    model.getDueDate(),
                    model.getBudget(),
                    model.getLevel(),
                    model.getFrequencyBase(),
                    freqValue,
                    user,
                    dbAgencyRef.getRef(),
                    dbUserPublicRef.getRef(),
                    dbNetworkRef)
            );
        }
        else {
            listener.tryRemoveAction(snapshot.getKey());
        }
    }

    public int getType() {
        return type;
    }
}
