package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.google.firebase.database.ChildEventListener;
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
import org.alertpreparedness.platform.alert.dagger.annotation.BaseCountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
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

public class APACompletedFragment extends Fragment implements APActionAdapter.ItemSelectedListener, ChildEventListener {

    public APACompletedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgActionCompleted;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionCompleted;

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
    @ActionCHSRef
    DatabaseReference dbCHSRef;

    @Inject
    @ActionMandatedRef
    DatabaseReference dbMandatedRef;

    @Inject
    @UserPublicRef
    DatabaseReference dbUserPublicRef;

    @Inject
    @BaseCountryOfficeRef
    DatabaseReference countryOffice;

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
        assert imgActionCompleted != null;
        imgActionCompleted.setImageResource(R.drawable.icon_status_complete);
        assert tvActionCompleted != null;
        tvActionCompleted.setText("Completed");
        tvActionCompleted.setTextColor(getResources().getColor(R.color.alertGreen));
        mAPAdapter = getAPAdapter();
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);

        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        dbActionRef.addChildEventListener(this);
    }

    protected APActionAdapter getAPAdapter() {
        return new APActionAdapter(getContext(), dbActionRef, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void process(DataSnapshot dataSnapshot) {
//        for (DataSnapshot getChild : dataSnapshot.getChildren()) {
            String actionIDs = dataSnapshot.getKey();
            DataModel model = dataSnapshot.getValue(DataModel.class);

            if (dataSnapshot.child("frequencyBase").getValue() != null) {
                model.setFrequencyBase(dataSnapshot.child("frequencyBase").getValue().toString());
            }
            if (dataSnapshot.child("frequencyValue").getValue() != null) {
                model.setFrequencyValue(dataSnapshot.child("frequencyValue").getValue().toString());
            }

            if (model.getType() == 0) {
                getCHS(model, actionIDs);
            } else if (model.getType() == 1) {
                getMandated(model, actionIDs);
            } else {
                getCustom(model, dataSnapshot);
            }
//        }
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

    private void getCustom(DataModel model, DataSnapshot getChild) {
        System.out.println("model = " + model);
        if (user.getUserID().equals(model.getAsignee()) //APA CUSTOM assigned and COMPLETED for logged in user.
                && model.getLevel() != null
                && model.getLevel() == Constants.APA
                && model.getIsCompleteAt() != null
                && model.getIsComplete() != null
                && model.getIsComplete()) {

            txtNoAction.setVisibility(View.GONE);
//            mAPAdapter.addItems(getChild.getKey(), new Action(
//                    model.getTask(),
//                    model.getDepartment(),
//                    model.getAsignee(),
//                    model.getIsArchived(),
//                    model.getIsComplete(),
//                    model.getCreatedAt(),
//                    model.getUpdatedAt(),
//                    model.getType(),
//                    model.getDueDate(),
//                    model.getBudget(),
//                    model.getLevel(),
//                    model.getFrequencyBase(),
//                    freqValue,
//                    dbAgencyRef.getRef(),
//                    dbUserPublicRef.getRef())
//            );
        }
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

                        if (isCHSAssigned && isCHS  //APA CHS assigned and COMPLETED for logged in user.
                                && user.getUserID().equals(model.getAsignee())
                                && model.getLevel() != null
                                && model.getLevel() == Constants.APA
                                && model.getIsCompleteAt() != null
                                && model.getIsComplete() != null
                                && model.getIsComplete()) {

                            txtNoAction.setVisibility(View.GONE);
//                            mAPAdapter.addItems(getChild.getKey(), new Action(
//                                    CHSTaskName,
//                                    model.getDepartment(),
//                                    model.getAsignee(),
//                                    model.getIsArchived(),
//                                    model.getIsComplete(),
//                                    CHSCreatedAt,
//                                    model.getUpdatedAt(),
//                                    model.getType(),
//                                    model.getDueDate(),
//                                    model.getBudget(),
//                                    CHSlevel,
//                                    model.getFrequencyBase(),
//                                    freqValue,
//                                    dbAgencyRef.getRef(),
//                                    dbUserPublicRef.getRef())
//                            );
                        }
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
                        //String departmentMandated = (String) getChild.child("department").getValue(); //Department is also found under "action"
                        Long manCreatedAt = (Long) getChild.child("createdAt").getValue();
                        Long manLevel = (Long) getChild.child("level").getValue();

                        isMandated = true;
                        isMandatedAssigned = true;
                        isCHS = false;
                        isCHSAssigned = false;

                        if (isMandatedAssigned && isMandated  //APA CHS assigned and COMPLETED for logged in user.
                                && user.getUserID().equals(model.getAsignee())
                                && model.getLevel() != null
                                && model.getLevel() == Constants.APA
                                && model.getIsCompleteAt() != null
                                && model.getIsComplete() != null
                                && model.getIsComplete()) {

                            txtNoAction.setVisibility(View.GONE);
//                            mAPAdapter.addItems(getChild.getKey(), new Action(
//                                    taskNameMandated,
//                                    model.getDepartment(),
//                                    model.getAsignee(),
//                                    model.getIsArchived(),
//                                    model.getIsComplete(),
//                                    manCreatedAt,
//                                    model.getUpdatedAt(),
//                                    model.getType(),
//                                    model.getDueDate(),
//                                    model.getBudget(),
//                                    manLevel,
//                                    model.getFrequencyBase(),
//                                    freqValue,
//                                    dbAgencyRef.getRef(),
//                                    dbUserPublicRef.getRef())
//                            );
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
    public void onActionItemSelected(int pos, String key) {
        Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Currently under development!", Snackbar.LENGTH_LONG).show();
    }


}

