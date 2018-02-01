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

public abstract class BaseCompletedFragment extends BaseInProgressFragment {
    protected void getCustom(DataModel model, DataSnapshot getChild, String id) {
        System.out.println("model = " + model);
        if (user.getUserID().equals(model.getAsignee()) //MPA CUSTOM assigned and COMPLETED for logged in user.
                && model.getLevel() != null
                && model.getLevel() == getType()
                && model.getIsCompleteAt() != null
                && model.getIsComplete() != null
                && model.getIsComplete()) {

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
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                    if (actionIDs.contains(getChild.getKey())) {
                        String CHSTaskName = (String) getChild.child("task").getValue();
                        Long CHSlevel = (Long) getChild.child("level").getValue();
                        Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();
                        isCHS = true;
                        isCHSAssigned = true;

                        if (isCHSAssigned && isCHS  //MPA CHS assigned and COMPLETED for logged in user.
                                && user.getUserID().equals(model.getAsignee())
                                && model.getLevel() != null
                                && model.getLevel() == getType()
                                && model.getIsCompleteAt() != null
                                && model.getIsComplete() != null
                                && model.getIsComplete()) {

                            getNoActionView().setVisibility(View.GONE);
                            getAdapter().addItems(getChild.getKey(), new Action(
                                    id,
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
                            getAdapter().removeItem(getChild.getKey());
                        }
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
                    if (actionIDs.contains(getChild.getKey())) {
                        String taskNameMandated = (String) getChild.child("task").getValue();
                        //String departmentMandated = (String) getChild.child("department").getValue(); //Department is also found under "action"
                        Long manCreatedAt = (Long) getChild.child("createdAt").getValue();
                        Long manLevel = (Long) getChild.child("level").getValue();

                        isMandated = true;
                        isMandatedAssigned = true;
                        isCHS = false;
                        isCHSAssigned = false;

                        if (isMandatedAssigned && isMandated  //MPA CHS assigned and COMPLETED for logged in user.
                                && user.getUserID().equals(model.getAsignee())
                                && model.getLevel() != null
                                && model.getLevel() == getType()
                                && model.getIsCompleteAt() != null
                                && model.getIsComplete() != null
                                && model.getIsComplete()) {

                            getNoActionView().setVisibility(View.GONE);
                            getAdapter().addItems(getChild.getKey(), new Action(
                                    id,
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
                            getAdapter().removeItem(getChild.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected class CompletedListener implements ChildEventListener {
        private String id;

        public CompletedListener(String id) {
            this.id = id;
        }

        @SuppressWarnings("ConstantConditions")
        public void process(DataSnapshot dataSnapshot) {

            String actionIDs = dataSnapshot.getKey();
            DataModel model = dataSnapshot.getValue(DataModel.class);

            if (dataSnapshot.child("frequencyBase").getValue() != null) {
                model.setFrequencyBase(dataSnapshot.child("frequencyBase").getValue().toString());
            }
            if (dataSnapshot.child("frequencyValue").getValue() != null) {
                model.setFrequencyValue(dataSnapshot.child("frequencyValue").getValue().toString());
            }

            if (model.getType() != null && model.getType() == 0) {
                getCHS(model, actionIDs, id);
            } else if (model.getType() != null && model.getType() == 1) {
                getMandated(model, actionIDs, id);
            } else if (model.getType() != null && model.getType() == 2){
                System.out.println("model = " + model);
                getCustom(model, dataSnapshot, id);
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
