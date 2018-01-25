package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionCompletedFragment extends InProgressFragment {

    @Nullable
    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionCompleted;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgCompleted;

    private ActionAdapter mAdapter;
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
        View v = inflater.inflate(R.layout.content_minimum, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {

        assert imgCompleted != null;
        imgCompleted.setImageResource(R.drawable.icon_status_complete);
        assert tvActionCompleted != null;
        tvActionCompleted.setText("Completed");
        tvActionCompleted.setTextColor(getResources().getColor(R.color.alertGreen));

        mAdapter = getmAdapter();
        assert mActionRV != null;
        mActionRV.setAdapter(mAdapter);

        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        dbActionRef.addChildEventListener(this);
    }

    @Override
    protected ActionAdapter getmAdapter() {
        return new ActionAdapter(getContext(), dbActionRef, this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void process(DataSnapshot dataSnapshot) {
        String actionIDs = dataSnapshot.getKey();
        System.out.println("getChild = " + dataSnapshot);
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

    }

    private void getCustom(DataModel model, DataSnapshot getChild) {
        System.out.println("model = " + model);
        if (user.getUserID().equals(model.getAsignee()) //MPA CUSTOM assigned and COMPLETED for logged in user.
                && model.getLevel() != null
                && model.getLevel() == Constants.MPA
                && model.getIsCompleteAt() != null
                && model.getIsComplete() != null
                && model.getIsComplete()) {

            txtNoAction.setVisibility(View.GONE);
            mAdapter.addItems(getChild.getKey(), new Action(
                    model.getTask(),
                    model.getDepartment(),
                    model.getAsignee(),
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
                    dbAgencyRef.getRef(),
                    dbUserPublicRef.getRef())
            );
        }
        else {
            mAdapter.removeItem(getChild.getKey());
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

                        if (isCHSAssigned && isCHS  //MPA CHS assigned and COMPLETED for logged in user.
                                && user.getUserID().equals(model.getAsignee())
                                && model.getLevel() != null
                                && model.getLevel() == Constants.MPA
                                && model.getIsCompleteAt() != null
                                && model.getIsComplete() != null
                                && model.getIsComplete()) {

                            txtNoAction.setVisibility(View.GONE);
                            mAdapter.addItems(getChild.getKey(), new Action(
                                    CHSTaskName,
                                    model.getDepartment(),
                                    model.getAsignee(),
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
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef())
                            );
                        }
                        else {
                            mAdapter.removeItem(getChild.getKey());
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

                        if (isMandatedAssigned && isMandated  //MPA CHS assigned and COMPLETED for logged in user.
                                && user.getUserID().equals(model.getAsignee())
                                && model.getLevel() != null
                                && model.getLevel() == Constants.MPA
                                && model.getIsCompleteAt() != null
                                && model.getIsComplete() != null
                                && model.getIsComplete()) {

                            txtNoAction.setVisibility(View.GONE);
                            mAdapter.addItems(getChild.getKey(), new Action(
                                    taskNameMandated,
                                    model.getDepartment(),
                                    model.getAsignee(),
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
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef())
                            );
                        }
                        else {
                            mAdapter.removeItem(getChild.getKey());
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
