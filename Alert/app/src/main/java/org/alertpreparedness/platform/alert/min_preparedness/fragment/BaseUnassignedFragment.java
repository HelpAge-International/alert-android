package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;

/**
 * Created by Tj on 01/02/2018.
 */

public abstract class BaseUnassignedFragment extends BaseInProgressFragment {
    private void getCustom(DataModel model, DataSnapshot getChild, String id) {
        System.out.println("getChild = " + getChild.getValue());
        if (model.getLevel() != null //MPA CUSTOM UNASSIGNED // NO USERS.
                && model.getLevel() == getType()
                && model.getAsignee() == null
                && model.getTask() != null) {

            getNoActionView().setVisibility(View.GONE);
            getAdapter().addItems(getChild.getKey(), new Action(
                    id,
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
            getAdapter().removeItem(getChild.getKey());
        }
    }

    protected void getCHS(DataModel model, String actionIDs, String id) {
        System.out.println("model chs = " + model);
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                    if (!actionIDs.equals(getChild.getKey())) {
                        System.out.println("getChild.getKey() = " + getChild.getKey());

                        String CHSTaskName = (String) getChild.child("task").getValue();
                        Long CHSlevel = (Long) getChild.child("level").getValue();
                        Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();

                        getNoActionView().setVisibility(View.GONE);
                        getAdapter().addItems(getChild.getKey(), new Action(
                                id,
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
                        getAdapter().removeItem(getChild.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void getMandated(DataModel model, String actionIDs, String id) {
        dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                    if (!actionIDs.contains(getChild.getKey())) {

                        try {
                            String taskNameMandated = (String) getChild.child("task").getValue();
                            String departmentMandated = (String) getChild.child("department").getValue();
                            Long manCreatedAt = (Long) getChild.child("createdAt").getValue();
                            Long manLevel = (Long) getChild.child("level").getValue();

                            getNoActionView().setVisibility(View.GONE);
                            getAdapter().addItems(getChild.getKey(), new Action(
                                    id,
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
                            getAdapter().removeItem(getChild.getKey());
                            System.out.println("exception = " + exception);
                        }
                    }
                    else {
                        getAdapter().removeItem(getChild.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void getMandatedForNewUser(String id) {
        dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                    try {
                        String taskNameMandated = (String) getChild.child("task").getValue();
                        String departmentMandated = (String) getChild.child("department").getValue();
                        Long manCreatedAt = (Long) getChild.child("createdAt").getValue();
                        Long manLevel = (Long) getChild.child("level").getValue();

                        getNoActionView().setVisibility(View.GONE);
                        getAdapter().addItems(getChild.getKey(), new Action(
                                id,
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
                        getAdapter().removeItem(getChild.getKey());
                        System.out.println("exception = " + exception);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void getCHSForNewUser(String id) {
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                    String CHSTaskName = (String) getChild.child("task").getValue();
                    Long CHSlevel = (Long) getChild.child("level").getValue();
                    Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();

                    getNoActionView().setVisibility(View.GONE);
                    getAdapter().addItems(getChild.getKey(), new Action(
                            id,
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

    protected class UnassignedChildListener implements ChildEventListener {

        private String id;

        public UnassignedChildListener(String id) {

            this.id = id;
        }

        @SuppressWarnings("ConstantConditions")
        public void process(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
//                for (DataSnapshot getChild : dataSnapshot.child(id).getChildren()) {
                String actionIDs = dataSnapshot.getKey();
                DataModel model = dataSnapshot.getValue(DataModel.class);

                if (dataSnapshot.child("frequencyBase").getValue() != null) {
                    model.setFrequencyBase(dataSnapshot.child("frequencyBase").getValue().toString());
                }
                if (dataSnapshot.child("frequencyValue").getValue() != null) {
                    model.setFrequencyValue(dataSnapshot.child("frequencyValue").getValue().toString());
                }

                if (model.getType() != null && model.getType() == 2) {
                    System.out.println("model = " + model);
                    getCustom(model, dataSnapshot, id);
                }

                //  if (model.getType() != null && model.getType() == 0) {
                getCHS(model, actionIDs, id);
                // } else if (model.getType() != null && model.getType() == 1) {
                System.out.println("model = " + model);
                getMandated(model, actionIDs, id);

//                }
            }

        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

}
