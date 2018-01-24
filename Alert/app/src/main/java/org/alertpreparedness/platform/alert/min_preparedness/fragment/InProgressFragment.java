package org.alertpreparedness.platform.alert.min_preparedness.fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionMandatedRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseCountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.interfaces.OnItemsChangedListener;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProgressFragment extends Fragment implements ActionAdapter.ItemSelectedListener, OnItemsChangedListener, ValueEventListener {

    private DataModel model;

    public InProgressFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @Nullable
    @BindView(R.id.tvNoAction)
    TextView txtNoAction;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    @Inject
    @AgencyRef
    DatabaseReference dbAgencyRef;

    @Inject
    @ActionCHSRef
    DatabaseReference dbCHSRef;

    @Inject
    @ActionMandatedRef
    DatabaseReference dbMandatedRef;

    @Inject
    @BaseActionRef
    DatabaseReference dbActionBaseRef;

    @Inject
    @UserPublicRef
    DatabaseReference dbUserPublicRef;

    @Inject
    @BaseCountryOfficeRef
    DatabaseReference countryOffice;

    @Inject
    User user;

    private ActionAdapter mAdapter;
    private Boolean isCHS = false;
    private Boolean isCHSAssigned = false;
    private Boolean isMandated = false;
    private Boolean isMandatedAssigned = false;
    private Boolean isInProgress = false;
    private int freqBase = 0;
    private int freqValue = 0;
    String ids[] = {};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_minimum, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {
        mAdapter = getmAdapter();
        assert mActionRV != null;
        mActionRV.setAdapter(mAdapter);

        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        dbActionRef.addValueEventListener(this);
    }

    protected ActionAdapter getmAdapter() {
        return new ActionAdapter(getContext(), dbActionRef, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActionItemSelected(int pos, String key) {
        SheetMenu.with(getContext()).setMenu(R.menu.menu_in_progress).setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.complete_action:
                        Intent intent = new Intent(getActivity(), CompleteActionActivity.class);
                        intent.putExtra("ACTION_KEY", key);
                        startActivity(intent);
                        break;
                    case R.id.reassign_action:
                        Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Reassigned Clicked", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.action_notes:
                        intent = new Intent(getActivity(), AddNotesActivity.class);
                        intent.putExtra("ACTION_KEY", key);
                        startActivity(intent);
                        break;
                    case R.id.attachments:
                        Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Attached Clicked", Snackbar.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        }).show();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
//        ids = new String[]{user.getCountryID(), user.getNetworkID(), user.getLocalNetworkID(), user.getNetworkCountryID()};
//
//        for(String id: ids) {

          //  System.out.println("GET NETWORK ACTION " + dataSnapshot.child(id).getValue());
            for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                String actionIDs = getChild.getKey();

                // System.out.println("user.getCountryID() = " + user.getCountryID());
                System.out.println("user.getNetworkCountryID() = " + user.getNetworkCountryID());
                System.out.println("user.getLocalNetworkID() = " + user.getLocalNetworkID());
                System.out.println("user.getNetworkID() = " + user.getNetworkID());
                DataModel model = getChild.getValue(DataModel.class);

            if (getChild.child("frequencyBase").getValue() != null) {
                model.setFrequencyBase(getChild.child("frequencyBase").getValue().toString());
            }
            if (getChild.child("frequencyValue").getValue() != null) {
                model.setFrequencyValue(getChild.child("frequencyValue").getValue().toString());
            }

            if (model.getType() == 0) {
                getCHS(model, actionIDs);
            } else if (model.getType() == 1) {
                getMandated(model, actionIDs);
            } else {
                System.out.println("model = " + model);
                getCustom(model, getChild);
            }
          //  }
        }
    }

    private void getCustom(DataModel model, DataSnapshot getChild) {

        countryOffice.child(user.agencyAdminID).child(user.countryID).child("clockSettings").child("preparedness").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
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
                    if (isInProgress) {
                        addObjects(model.getTask(),
                                model.getCreatedAt(),
                                model.getLevel(),
                                model,
                                getChild,
                                isCHS,
                                isCHSAssigned,
                                isMandated,
                                isMandatedAssigned);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCHS(DataModel model, String actionIDs) {
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

                        countryOffice.child(user.agencyAdminID).child(user.countryID).child("clockSettings").child("preparedness").addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
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
                                            isCHS,
                                            isCHSAssigned,
                                            isMandated,
                                            isMandatedAssigned);
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

    private void getMandated(DataModel model, String actionIDs) {
        dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                    if (actionIDs.contains(getChild.getKey())) {
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
                                    isCHS,
                                    isCHSAssigned,
                                    isMandated,
                                    isMandatedAssigned);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addObjects(String name, Long createdAt, Long level,
                            DataModel model, DataSnapshot getChild, Boolean isCHS, Boolean isCHSAssigned, Boolean isMandated, Boolean isMandatedAssigned) {
        System.out.println("model.getTask() = " + model.getTask());
        if (user.getUserID().equals(model.getAsignee()) //MPA Custom assigned and in-progress for logged in user.
                && model.getAsignee() != null
                && level != null
                && level == Constants.MPA
                && model.getDueDate() != null
                && (model.getIsCompleteAt() == null && model.getIsComplete() == null || model.getIsCompleteAt() == null && !model.getIsComplete()) // isComplete can be set to false :D, and when it's false, isCreatedAt will disappear.
                && name != null
                || (isCHS && isCHSAssigned //MPA CHS assigned and in-progress for logged in user.
                && user.getUserID().equals(model.getAsignee())
                && model.getAsignee() != null
                && level != null
                && level == Constants.MPA
                && model.getDueDate() != null
                && (model.getIsCompleteAt() == null && model.getIsComplete() == null || model.getIsCompleteAt() == null && !model.getIsComplete())
                && name != null)
                || (isMandated && isMandatedAssigned //MPA Mandated assigned and in-progress for logged in user.
                && user.getUserID().equals(model.getAsignee())
                && model.getAsignee() != null
                && level != null
                && level == Constants.MPA
                && model.getDueDate() != null
                && (model.getIsCompleteAt() == null && model.getIsComplete() == null || model.getIsCompleteAt() == null && !model.getIsComplete())
                && name != null)) {

            txtNoAction.setVisibility(View.GONE);

            mAdapter.addItems(getChild.getKey(), new Action(
                    name,
                    model.getDepartment(),
                    model.getAsignee(),
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
                    dbAgencyRef.getRef(),
                    dbUserPublicRef.getRef())
            );
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onItemChanged(DataSnapshot getChild) {
//        String actionIDs = getChild.getKey();
//        System.out.println("CHANGED");
//        DataModel model = getChild.getValue(DataModel.class);
//
//        if (getChild.child("frequencyBase").getValue() != null) {
//            model.setFrequencyBase(getChild.child("frequencyBase").getValue().toString());
//        }
//        if (getChild.child("frequencyValue").getValue() != null) {
//            model.setFrequencyValue(getChild.child("frequencyValue").getValue().toString());
//        }
//
//        if (model.getType() == 0) {
//            getCHS(model, actionIDs);
//        } else if (model.getType() == 1) {
//            getMandated(model, actionIDs);
//        } else {
//            getCustom(model, getChild);
//        }

    }
}
