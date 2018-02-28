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

                            addObject(taskNameMandated, departmentMandated, manCreatedAt, manLevel, getChild.getKey(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    1L,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);


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

    protected void addObject(
            String taskName,
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
        listener.onAddAction(key, new Action(
                parentId,
                taskName,
                department,
                assignee,
                agencyId,
                countryId,
                networkId,
                isArchived,
                isComplete,
                createdAt,
                updatedAt,
                actionType,
                dueDate,
                budget,
                level,
                frequencyBase,
                frequencyValue,
                user,
                dbAgencyRef.getRef(),
                dbUserPublicRef.getRef(),
                dbNetworkRef)
        );
    }

    protected void addObject(
            String taskName,
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
            Integer frequencyValue,
            boolean chsHasInfo
    ) {
        Action action = new Action(
                parentId,
                taskName,
                department,
                assignee,
                agencyId,
                countryId,
                networkId,
                isArchived,
                isComplete,
                createdAt,
                updatedAt,
                actionType,
                dueDate,
                budget,
                level,
                frequencyBase,
                frequencyValue,
                user,
                dbAgencyRef.getRef(),
                dbUserPublicRef.getRef(),
                dbNetworkRef);

        action.setHasCHSInfo(chsHasInfo);

        action.setIsCHS(true);

        listener.onAddAction(key, action);
    }

    @Override
    public void getCHS() {
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                    dbActionRef.child(getChild.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (!actionId.equals(getChild.getKey())) {

                                String CHSTaskName = (String) getChild.child("task").getValue();
                                Long CHSlevel = (Long) getChild.child("level").getValue();
                                Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();

                                System.out.println("getCHSdataSnapshot = " + dataSnapshot);

                                addObject(
                                        CHSTaskName,
                                        null,
                                        CHSCreatedAt,
                                        CHSlevel,
                                        getChild.getKey(),
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        0L,
                                        null,
                                        null,
                                        null,
                                        null,
                                        dataSnapshot.exists()
                                );

                            }
                            else {
                                listener.tryRemoveAction(getChild.getKey());
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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


            addObject(
                    model.getTask(),
                    model.getDepartment(),
                    model.getCreatedAt(),
                    model.getLevel(),
                    snapshot.getKey(),
                    model.getAsignee(),
                    model.getCreatedByAgencyId(),
                    model.getCreatedByCountryId(),
                    model.getNetworkId(),
                    model.getIsArchived(),
                    model.getIsComplete(),
                    model.getUpdatedAt(),
                    model.getType(),
                    model.getDueDate(),
                    model.getBudget(),
                    model.getFrequencyBase(),
                    model.getFrequencyValue().intValue()
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

                        addObject(taskNameMandated, departmentMandated, manCreatedAt, manLevel, getChild.getKey(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            1L,
                            null,
                            null,
                            null,
                            null,
                         null);

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

                    addObject(
                            CHSTaskName,
                            null,
                            CHSCreatedAt,
                            CHSlevel,
                            getChild.getKey(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            0L,
                            null,
                            null,
                            null,
                            null
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
