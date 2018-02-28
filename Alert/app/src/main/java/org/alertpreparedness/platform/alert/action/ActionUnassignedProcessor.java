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

public class ActionUnassignedProcessor extends BaseActionProcessor {

    public ActionUnassignedProcessor(int type, DataSnapshot snapshot, DataModel model, String id, String parentId, ActionProcessorListener listener) {
        super(type, snapshot, model, id, parentId, listener);
    }

    @Override
    public void getMandated() {
        dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                    if (!actionId.contains(getChild.getKey())) {

                        try {
                            String taskNameMandated = (String) getChild.child("task").getValue();
                            String departmentMandated = (String) getChild.child("department").getValue();
                            Long manCreatedAt = (Long) getChild.child("createdAt").getValue();
                            Long manLevel = (Long) getChild.child("level").getValue();

                            listener.onAddAction(getChild.getKey(), new Action(
                                    parentId,
                                    taskNameMandated,
                                    departmentMandated,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    manCreatedAt,
                                    null,
                                    (long) 1, //Mandated always 1
                                    null,
                                    null,
                                    manLevel,
                                    null,
                                    null,
                                    user,
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef(),
                                    dbNetworkRef)
                            );

                        } catch (Exception exception) {
                            listener.tryRemoveAction(getChild.getKey());
                        }
                    }
                    else {
                       listener.tryRemoveAction(getChild.getKey());
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

                    if (!actionId.equals(getChild.getKey())) {
                        System.out.println("getChild.getKey() = " + getChild.getKey());

                        String CHSTaskName = (String) getChild.child("task").getValue();
                        Long CHSlevel = (Long) getChild.child("level").getValue();
                        Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();

                        listener.onAddAction(getChild.getKey(), new Action(
                                parentId,
                                CHSTaskName,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                CHSCreatedAt,
                                null,
                                (long) 0, //CHS always 0
                                null,
                                null,
                                CHSlevel,
                                null,
                                null,
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getCustom() {
        if (model.getLevel() != null //MPA CUSTOM UNASSIGNED // NO USERS.
                && model.getLevel() == getType()
                && model.getAsignee() == null
                && model.getTask() != null) {

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

    protected void getMandatedForNewUser() {
        dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                    try {
                        String taskNameMandated = (String) getChild.child("task").getValue();
                        String departmentMandated = (String) getChild.child("department").getValue();
                        Long manCreatedAt = (Long) getChild.child("createdAt").getValue();
                        Long manLevel = (Long) getChild.child("level").getValue();

                                listener.onAddAction(getChild.getKey(), new Action(
                                parentId,
                                taskNameMandated,
                                departmentMandated,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                manCreatedAt,
                                null,
                                (long) 1, //Mandated always 1
                                null,
                                null,
                                manLevel,
                                null,
                                null,
                                user,
                                dbAgencyRef.getRef(),
                                dbUserPublicRef.getRef(),
                                dbNetworkRef)
                        );

                    } catch (Exception exception) {
                        listener.tryRemoveAction(getChild.getKey());
                        System.out.println("exception = " + exception);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void getCHSForNewUser() {
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                    String CHSTaskName = (String) getChild.child("task").getValue();
                    Long CHSlevel = (Long) getChild.child("level").getValue();
                    Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();

                    listener.onAddAction(getChild.getKey(), new Action(
                            parentId,
                            CHSTaskName,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            CHSCreatedAt,
                            null,
                            (long) 0, //CHS always 0
                            null,
                            null,
                            CHSlevel,
                            null,
                            null,
                            user,
                            dbAgencyRef.getRef(),
                            dbUserPublicRef.getRef(),
                            dbNetworkRef)
                    );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public int getType() {
        return type;
    }
}
