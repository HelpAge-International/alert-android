package org.alertpreparedness.platform.alert.action;

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

public class ActionInProgressProcessor extends BaseActionProcessor {

    public ActionInProgressProcessor(int type, DataSnapshot snapshot, ActionModel model, String id, String parentId, ActionProcessorListener listener) {
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

                        if (isInProgress) {
                            addObjects(taskNameMandated,
                                    manCreatedAt,
                                    manLevel,
                                    model,
                                    getChild,
                                    parentId,
                                    isCHS,
                                    isCHSAssigned,
                                    isMandated,
                                    isMandatedAssigned);
                        } else {
                            listener.tryRemoveAction(dataSnapshot);
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
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long durationType = (Long) dataSnapshot.child("durationType").getValue();
                                Long value = (Long) dataSnapshot.child("value").getValue();

                                if (value != null) {
                                    if (model.getCreatedAt() != null && model.getUpdatedAt() == null && durationType != null && durationType == Constants.DUE_WEEK) {
                                        isInProgress = DateHelper.isInProgressWeek(model.getCreatedAt(), value.intValue());
                                    } else if (model.getUpdatedAt() != null && durationType != null && durationType == Constants.DUE_WEEK) {
                                        isInProgress = DateHelper.isInProgressWeek(model.getUpdatedAt(), value.intValue());
                                    } else if (model.getCreatedAt() != null && model.getUpdatedAt() == null && durationType != null && durationType == Constants.DUE_MONTH) {
                                        isInProgress = DateHelper.isInProgressMonth(model.getCreatedAt(), value.intValue());
                                    } else if (model.getUpdatedAt() != null && durationType != null && durationType == Constants.DUE_MONTH) {
                                        isInProgress = DateHelper.isInProgressMonth(model.getUpdatedAt(), value.intValue());
                                    } else if (model.getCreatedAt() != null && model.getUpdatedAt() == null && durationType != null && durationType == Constants.DUE_YEAR) {
                                        isInProgress = DateHelper.isInProgressYear(model.getCreatedAt(), value.intValue());
                                    } else if (model.getUpdatedAt() != null && durationType != null && durationType == Constants.DUE_YEAR) {
                                        isInProgress = DateHelper.isInProgressYear(model.getUpdatedAt(), value.intValue());
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

                                if (isInProgress) {
                                    addObjects(CHSTaskName,
                                            CHSCreatedAt,
                                            CHSlevel,
                                            model,
                                            getChild,
                                            parentId,
                                            isCHS,
                                            isCHSAssigned,
                                            isMandated,
                                            isMandatedAssigned);
                                } else {
                                    listener.tryRemoveAction(dataSnapshot);
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
                    } else if (model.getUpdatedAt() != null && durationType != null && durationType == Constants.DUE_WEEK) {
                        isInProgress = DateHelper.isInProgressWeek(model.getUpdatedAt(), value.intValue());
                    } else if (model.getCreatedAt() != null && model.getUpdatedAt() == null && durationType != null && durationType == Constants.DUE_MONTH) {
                        isInProgress = DateHelper.isInProgressMonth(model.getCreatedAt(), value.intValue());
                    } else if (model.getUpdatedAt() != null && durationType != null && durationType == Constants.DUE_MONTH) {
                        isInProgress = DateHelper.isInProgressMonth(model.getUpdatedAt(), value.intValue());
                    } else if (model.getCreatedAt() != null && model.getUpdatedAt() == null && durationType != null && durationType == Constants.DUE_YEAR) {
                        isInProgress = DateHelper.isInProgressYear(model.getCreatedAt(), value.intValue());
                    } else if (model.getUpdatedAt() != null && durationType != null && durationType == Constants.DUE_YEAR) {
                        isInProgress = DateHelper.isInProgressYear(model.getUpdatedAt(), value.intValue());
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
                if (isInProgress) {
                    addObjects(model.getTask(),
                            model.getCreatedAt(),
                            model.getLevel(),
                            model,
                            snapshot,
                            parentId,
                            isCHS,
                            isCHSAssigned,
                            isMandated,
                            isMandatedAssigned);
                } else {
                    listener.tryRemoveAction(dataSnapshot);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

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

            if(!model.getIsArchived()) {
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
        }
        else {
            listener.tryRemoveAction(childSnapshot);
        }
    }

    public int getType() {
        return type;
    }
}
