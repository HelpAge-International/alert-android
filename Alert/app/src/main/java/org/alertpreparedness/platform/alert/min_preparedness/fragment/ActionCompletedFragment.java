package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;

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
        mActionRV.setAdapter(mAdapter);

        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        dbActionRef.addValueEventListener(this);
    }

    @Override
    protected ActionAdapter getmAdapter() {
        return new ActionAdapter(getContext(), dbActionRef, this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
//        for (DataSnapshot getChild : dataSnapshot.getChildren()) {
//            String actionIDs = getChild.getKey();
//            String taskName = (String) getChild.child("task").getValue();
//            String department = (String) getChild.child("department").getValue();
//            String assignee = (String) getChild.child("asignee").getValue();
//            Boolean isArchived = (Boolean) getChild.child("isArchived").getValue();
//            Boolean isComplete = (Boolean) getChild.child("isComplete").getValue();
//            Long actionType = (Long) getChild.child("type").getValue();
//            Long dueDate = (Long) getChild.child("dueDate").getValue();
//            Long budget = (Long) getChild.child("budget").getValue();
//            Long level = (Long) getChild.child("level").getValue();
//            Long createdAt = (Long) getChild.child("createdAt").getValue();
//            Long updatedAt = (Long) getChild.child("updatedAt").getValue();
//            Long frequencyBase = (Long) getChild.child("frequencyBase").getValue();
//            String freqValue = (String) getChild.child("frequencyValue").getValue();
//            Long frequencyValue = Long.parseLong(freqValue);
//
//
//            if (actionType == 0) {
//                //CHS
//                dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot getChild : dataSnapshot.getChildren()) {
//                            if (actionIDs.contains(getChild.getKey())) {
//                                String CHSTaskName = (String) getChild.child("task").getValue();
//                                Long CHSlevel = (Long) getChild.child("level").getValue();
//                                Long createdAt = (Long) getChild.child("createdAt").getValue();
//
//                                isCHS = true;
//                                isCHSAssigned = true;
//                                mAdapter.addCompletedItem(getChild.getKey(), new Action(
//                                                CHSTaskName,
//                                                department,
//                                                assignee,
//                                                isArchived,
//                                                isComplete,
//                                                createdAt,
//                                                updatedAt,
//                                                actionType,
//                                                dueDate,
//                                                budget,
//                                                CHSlevel,
//                                                frequencyBase,
//                                                frequencyValue,
//                                                dbAgencyRef.getRef(),
//                                                dbUserPublicRef.getRef()),
//                                        isCHS,
//                                        isMandated,
//                                        isCHSAssigned,
//                                        isMandatedAssigned
//                                );
//                            } else {
//                                isCHSAssigned = false;
//                                String CHSTaskName = (String) getChild.child("task").getValue();
//                                Long CHSlevel = (Long) getChild.child("level").getValue();
//                                Long createdAt = (Long) getChild.child("createdAt").getValue();
//
//                                mAdapter.addCompletedItem(getChild.getKey(), new Action(
//                                                CHSTaskName,
//                                                null,
//                                                null,
//                                                null,
//                                                null,
//                                                createdAt,
//                                                null,
//                                                (long) 0,
//                                                null,
//                                                null,
//                                                CHSlevel,
//                                                null,
//                                                null,
//                                                dbAgencyRef.getRef(),
//                                                dbUserPublicRef.getRef()),
//                                        isCHS,
//                                        isMandated,
//                                        isCHSAssigned,
//                                        isMandatedAssigned
//                                );
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            } else if (actionType == 1) {
//
//                //Mandated
//                dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot getChild : dataSnapshot.getChildren()) {
//                            if (actionIDs.contains(getChild.getKey())) {
//                                String taskNameMandated = (String) getChild.child("task").getValue();
//                                String departmentMandated = (String) getChild.child("department").getValue();
//                                Long createdAt = (Long) getChild.child("createdAt").getValue();
//
//                                isCHS = false;
//                                isMandated = true;
//                                mAdapter.addCompletedItem(getChild.getKey(), new Action(
//                                                taskNameMandated,
//                                                departmentMandated,
//                                                assignee,
//                                                isArchived,
//                                                isComplete,
//                                                createdAt,
//                                                updatedAt,
//                                                actionType,
//                                                dueDate,
//                                                budget,
//                                                level,
//                                                frequencyBase,
//                                                frequencyValue,
//                                                dbAgencyRef.getRef(),
//                                                dbUserPublicRef.getRef()),
//                                        isCHS,
//                                        isMandated,
//                                        isCHSAssigned,
//                                        isMandatedAssigned
//                                );
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//            } else {
//                mAdapter.addCompletedItem(getChild.getKey(), new Action(
//                                taskName,
//                                department,
//                                assignee,
//                                isArchived,
//                                isComplete,
//                                createdAt,
//                                updatedAt,
//                                actionType,
//                                dueDate,
//                                budget,
//                                level,
//                                frequencyBase,
//                                frequencyValue,
//                                dbAgencyRef.getRef(),
//                                dbUserPublicRef.getRef()),
//                        isCHS,
//                        isMandated,
//                        isCHSAssigned,
//                        isMandatedAssigned
//                );
//            }
//        }
    }
}
