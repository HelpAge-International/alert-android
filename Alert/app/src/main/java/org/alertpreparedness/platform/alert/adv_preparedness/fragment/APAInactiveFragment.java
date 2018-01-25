package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionMandatedRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAInactiveFragment extends Fragment implements APActionAdapter.ItemSelectedListener, ValueEventListener {

    public APAInactiveFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgActionInactive;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionInactive;

    @Nullable
    @BindView(R.id.tvAPANoAction)
    TextView txtNoAction;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    @Inject
    @AgencyRef
    DatabaseReference dbAgencyRef;

    @Inject
    @AlertRef
    DatabaseReference dbAlertRef;

    @Inject
    @ActionCHSRef
    DatabaseReference dbCHSRef;

    @Inject
    @ActionMandatedRef
    DatabaseReference dbMandatedRef;

    @Inject
    @UserPublicRef
    DatabaseReference dbUserPublicRef;

    @Inject
    User user;

    private APActionAdapter mAPAdapter;
    private Boolean isCHS = false;
    private Boolean isCHSAssigned = false;
    private Boolean isMandated = false;
    private Boolean isMandatedAssigned = false;
    private Boolean isInProgress = false;
    private int freqBase = 0;
    private int freqValue = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_advanced, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {
        assert imgActionInactive != null;
        imgActionInactive.setImageResource(R.drawable.ic_in_progress_gray);
        assert tvActionInactive != null;
        tvActionInactive.setText("Inactive");
        tvActionInactive.setTextColor(getResources().getColor(R.color.alertGray));
        mAPAdapter = getAPAdapter();
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);

        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        dbActionRef.addValueEventListener(this);
    }

    protected APActionAdapter getAPAdapter() {
        return new APActionAdapter(getContext(), dbActionRef, this);
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
        for (DataSnapshot getChild : dataSnapshot.getChildren()) {
            String actionIDs = getChild.getKey();

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

        }
    }

    private void getCustom(DataModel model, DataSnapshot getChild) {
        dbAlertRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long alertLevel = (Long) dataSnapshot.child("alertLevel").getValue();

                if (isInProgress) {
                    addObjects(model.getTask(),
                            model.getCreatedAt(),
                            model.getLevel(),
                            model,
                            alertLevel,
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

                        dbAlertRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long alertLevel = (Long) dataSnapshot.child("alertLevel").getValue();

                                if (isInProgress) {
                                    addObjects(CHSTaskName,
                                            CHSCreatedAt,
                                            CHSlevel,
                                            model,
                                            alertLevel,
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

                        dbAlertRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long alertLevel = (Long) dataSnapshot.child("alertLevel").getValue();

                                if (isInProgress) {
                                    addObjects(taskNameMandated,
                                            manCreatedAt,
                                            manLevel,
                                            model,
                                            alertLevel,
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

    private void addObjects(String name, Long createdAt, Long level,
                            DataModel model, Long alertLevel, DataSnapshot getChild, Boolean isCHS, Boolean isCHSAssigned, Boolean isMandated, Boolean isMandatedAssigned) {
        System.out.println("model.getTask() = " + model.getTask());
        if (user.getUserID().equals(model.getAsignee()) //APA Custom inactive for logged in user.
                && model.getLevel() != null
                && alertLevel != null
                && model.getLevel() == Constants.APA
                && alertLevel == Constants.TRIGGER_RED
                || (isCHS && isCHSAssigned //APA CHS inactive for logged in user.
                && user.getUserID().equals(model.getAsignee())
                && model.getLevel() != null
                && alertLevel != null
                && model.getLevel() == Constants.APA
                && alertLevel == Constants.TRIGGER_RED)
                || (isMandated && isMandatedAssigned //APA Mandated inactive for logged in user.
                && user.getUserID().equals(model.getAsignee())
                && model.getLevel() != null
                && alertLevel != null
                && model.getLevel() == Constants.APA
                && alertLevel == Constants.TRIGGER_RED)) {

            txtNoAction.setVisibility(View.GONE);

//            mAPAdapter.addItems(getChild.getKey(), new Action(
//                    name,
//                    model.getDepartment(),
//                    model.getAsignee(),
//                    model.getIsArchived(),
//                    model.getIsComplete(),
//                    createdAt,
//                    model.getUpdatedAt(),
//                    model.getType(),
//                    model.getDueDate(),
//                    model.getBudget(),
//                    level,
//                    model.getFrequencyBase(),
//                    freqValue,
//                    dbAgencyRef.getRef(),
//                    dbUserPublicRef.getRef())
//            );
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}


