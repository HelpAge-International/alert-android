package org.alertpreparedness.platform.alert.action;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.ActionModel;
import org.alertpreparedness.platform.alert.utils.Constants;

/**
 * Created by Tj on 28/02/2018.
 */

public class ActionExpiredProcessor extends BaseActionProcessor {

    public ActionExpiredProcessor(int type, DataSnapshot snapshot, ActionModel model, String id, String parentId, ActionProcessorListener listener) {
        super(type, snapshot, model, id, parentId, listener);
    }

    @Override
    public void getMandated() {
        dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (actionId.contains(childSnapshot.getKey())) {
                        String taskNameMandated = (String) childSnapshot.child("task").getValue();
                        String departmentMandated = (String) childSnapshot.child("department").getValue();
                        Long manCreatedAt = (Long) childSnapshot.child("createdAt").getValue();
                        Long manLevel = (Long) childSnapshot.child("level").getValue();

                        isMandated = true;
                        isMandatedAssigned = true;
                        isCHS = false;
                        isCHSAssigned = false;

                        if (!isInProgress) {
                            addObjects(taskNameMandated,
                                    departmentMandated,
                                    manCreatedAt,
                                    manLevel,
                                    model,
                                    childSnapshot,
                                    parentId,
                                    actionId,
                                    isCHS,
                                    isCHSAssigned,
                                    isMandated,
                                    isMandatedAssigned);
                        }
                        else {
                            listener.tryRemoveAction(childSnapshot);
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

                        countryOffice.child(user.agencyAdminID).child(user.countryID).child("clockSettings").child("preparedness").addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long durationType = (Long) dataSnapshot.child("durationType").getValue();
                                Long value = (Long) dataSnapshot.child("value").getValue();

                                if (value != null) {
                                    if (model.getCreatedAt() != null && durationType != null && durationType == Constants.DUE_WEEK) {
                                        isInProgress = DateHelper.isInProgressWeek(model.getCreatedAt(), value.intValue());
                                    } else if (model.getCreatedAt() != null && durationType != null && durationType == Constants.DUE_MONTH) {
                                        isInProgress = DateHelper.isInProgressMonth(model.getCreatedAt(), value.intValue());
                                    } else if (model.getCreatedAt() != null && durationType != null && durationType == Constants.DUE_YEAR) {
                                        isInProgress = DateHelper.isInProgressYear(model.getCreatedAt(), value.intValue());
                                    }
                                }

                                if (model.getFrequencyValue() != null && model.getFrequencyBase() != null) {
                                    freqValue = model.getFrequencyValue().intValue();
                                    freqBase = model.getFrequencyBase().intValue();

                                    if (model.getCreatedAt() != null && freqBase == Constants.DUE_WEEK) {
                                        isInProgress = DateHelper.isInProgressWeek(model.getCreatedAt(), freqValue);
                                    } else if (model.getCreatedAt() != null && freqBase == Constants.DUE_MONTH) {
                                        isInProgress = DateHelper.isInProgressMonth(model.getCreatedAt(), freqValue);
                                    } else if (model.getCreatedAt() != null && freqBase == Constants.DUE_YEAR) {
                                        isInProgress = DateHelper.isInProgressYear(model.getCreatedAt(), freqValue);
                                    }
                                }

                                if (!isInProgress) {
                                    addObjects(CHSTaskName,
                                            model.getDepartment(),
                                            CHSCreatedAt,
                                            CHSlevel,
                                            model,
                                            getChild,
                                            parentId,
                                            actionId,
                                            isCHS,
                                            isCHSAssigned,
                                            isMandated,
                                            isMandatedAssigned);
                                }
                                else {
                                   listener.tryRemoveAction(getChild);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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

        countryOffice.child(user.agencyAdminID).child(user.countryID).child("clockSettings").child("preparedness").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long durationType = (Long) dataSnapshot.child("durationType").getValue();
                Long value = (Long) dataSnapshot.child("value").getValue();

                if (value != null) {
                    if (model.getCreatedAt() != null && model.getUpdatedAt() == null && durationType != null && durationType == Constants.DUE_WEEK) {
                        isInProgress = DateHelper.isInProgressWeek(model.getCreatedAt(), value.intValue());
                    }
                    else if (model.getUpdatedAt() != null && durationType != null && durationType == Constants.DUE_WEEK) {
                        isInProgress = DateHelper.isInProgressWeek(model.getUpdatedAt(), value.intValue());
                    }
                    else if (model.getCreatedAt() != null && model.getUpdatedAt() == null && durationType != null && durationType == Constants.DUE_MONTH) {
                        isInProgress = DateHelper.isInProgressMonth(model.getCreatedAt(), value.intValue());
                    }
                    else if (model.getUpdatedAt() != null && durationType != null && durationType == Constants.DUE_MONTH) {
                        isInProgress = DateHelper.isInProgressMonth(model.getUpdatedAt(), value.intValue());
                    }
                    else if (model.getCreatedAt() != null && model.getUpdatedAt() == null && durationType != null && durationType == Constants.DUE_YEAR) {
                        isInProgress = DateHelper.isInProgressYear(model.getCreatedAt(), value.intValue());
                    }
                    else if (model.getUpdatedAt() != null && durationType != null && durationType == Constants.DUE_YEAR) {
                        System.out.println("FAILED2");
                        isInProgress = DateHelper.isInProgressYear(model.getUpdatedAt(), value.intValue());
                        System.out.println("isInProgress = " + isInProgress);
                    }
                }

                if (model.getFrequencyValue() != null && model.getFrequencyBase() != null) {
                    freqValue = model.getFrequencyValue().intValue();
                    freqBase = model.getFrequencyBase().intValue();

                    if (model.getCreatedAt() != null && model.getUpdatedAt() == null && freqBase == Constants.DUE_WEEK) {
                        isInProgress = DateHelper.isInProgressWeek(model.getCreatedAt(), freqValue);
                    } else if (model.getUpdatedAt() != null && freqBase == Constants.DUE_WEEK) {
                        isInProgress = DateHelper.isInProgressWeek(model.getCreatedAt(), freqValue);
                    } else if (model.getCreatedAt() != null && model.getUpdatedAt() == null && freqBase == Constants.DUE_MONTH) {
                        isInProgress = DateHelper.isInProgressMonth(model.getCreatedAt(), freqValue);
                    } else if (model.getUpdatedAt() != null && freqBase == Constants.DUE_MONTH) {
                        isInProgress = DateHelper.isInProgressMonth(model.getCreatedAt(), freqValue);
                    } else if (model.getCreatedAt() != null && model.getUpdatedAt() == null && freqBase == Constants.DUE_YEAR) {
                        isInProgress = DateHelper.isInProgressYear(model.getCreatedAt(), freqValue);
                    } else if (model.getUpdatedAt() != null && freqBase == Constants.DUE_YEAR) {
                        isInProgress = DateHelper.isInProgressYear(model.getCreatedAt(), freqValue);
                    }
                }

                if (!isInProgress) {

                    addObjects(model.getTask(),
                            model.getDepartment(),
                            model.getCreatedAt(),
                            model.getLevel(),
                            model,
                            snapshot,
                            actionId,
                            parentId,
                            isCHS,
                            isCHSAssigned,
                            isMandated,
                            isMandatedAssigned);
                }
                else {
                    listener.tryRemoveAction(snapshot);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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

    public int getType() {
        return type;
    }
}
