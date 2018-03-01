package org.alertpreparedness.platform.alert.action;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;

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
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (!actionId.contains(childSnapshot.getKey())) {

                        try {
                            String taskNameMandated = (String) childSnapshot.child("task").getValue();
                            String departmentMandated = (String) childSnapshot.child("department").getValue();
                            Long manCreatedAt = (Long) childSnapshot.child("createdAt").getValue();
                            Long manLevel = (Long) childSnapshot.child("level").getValue();

                            addObject(childSnapshot, taskNameMandated, departmentMandated, manCreatedAt, manLevel, childSnapshot.getKey(),
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
                            listener.tryRemoveAction(childSnapshot);
                        }
                    }
                    else {
                       listener.tryRemoveAction(childSnapshot);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void addObject(
            DataSnapshot snapshot,
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
        listener.onAddAction(snapshot, new Action(
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
            DataSnapshot snapshot,
            String taskName,
            Long createdAt,
            Long level,
            String key,
            boolean chsHasInfo
    ) {
        Action action = new Action(
                parentId,
                taskName,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                createdAt,
                null,
                0L,
                null,
                null,
                level,
                null,
                null,
                user,
                dbAgencyRef.getRef(),
                dbUserPublicRef.getRef(),
                dbNetworkRef);

        action.setHasCHSInfo(chsHasInfo);

        action.setIsCHS(true);

        listener.onAddAction(snapshot, action);
    }

    @Override
    public void getCHS() {
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    dbActionRef.child(childSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (!actionId.equals(childSnapshot.getKey())) {

                                String CHSTaskName = (String) childSnapshot.child("task").getValue();
                                Long CHSlevel = (Long) childSnapshot.child("level").getValue();
                                Long CHSCreatedAt = (Long) childSnapshot.child("createdAt").getValue();

                                addObject(
                                        childSnapshot,
                                        CHSTaskName,
                                        CHSCreatedAt,
                                        CHSlevel,
                                        childSnapshot.getKey(),
                                        dataSnapshot.exists()
                                );

                            }
                            else {
                                listener.tryRemoveAction(childSnapshot);
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
                    snapshot,
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
            listener.tryRemoveAction(snapshot);
        }

    }

    protected void getMandatedForNewUser() {
        dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    try {
                        String taskNameMandated = (String) childSnapshot.child("task").getValue();
                        String departmentMandated = (String) childSnapshot.child("department").getValue();
                        Long manCreatedAt = (Long) childSnapshot.child("createdAt").getValue();
                        Long manLevel = (Long) childSnapshot.child("level").getValue();

                        addObject(
                            childSnapshot,
                            taskNameMandated,
                            departmentMandated,
                            manCreatedAt,
                            manLevel,
                            childSnapshot.getKey(),
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
                        listener.tryRemoveAction(childSnapshot);
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
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String CHSTaskName = (String) childSnapshot.child("task").getValue();
                    Long CHSlevel = (Long) childSnapshot.child("level").getValue();
                    Long CHSCreatedAt = (Long) childSnapshot.child("createdAt").getValue();

                    addObject(
                            childSnapshot,
                            CHSTaskName,
                            null,
                            CHSCreatedAt,
                            CHSlevel,
                            childSnapshot.getKey(),
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
