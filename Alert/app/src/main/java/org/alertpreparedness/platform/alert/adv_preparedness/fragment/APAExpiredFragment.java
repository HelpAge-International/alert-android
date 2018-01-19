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
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAExpiredFragment extends Fragment implements APActionAdapter.ItemSelectedListener, ValueEventListener {

    public APAExpiredFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgActionExpired;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionExpired;

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

    private APActionAdapter mAPAdapter;
    private Boolean isCHS = false;
    private Boolean isCHSAssigned = false;
    private Boolean isMandated = false;
    private Boolean isMandatedAssigned = false;

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
        assert imgActionExpired != null;
        imgActionExpired.setImageResource(R.drawable.ic_close_round);
        assert tvActionExpired != null;
        tvActionExpired.setText("Expired");
        tvActionExpired.setTextColor(getResources().getColor(R.color.alertRed));
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
//                                mAPAdapter.addExpiredItem(getChild.getKey(), new Action(
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
//                                                null,
//                                                null,
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
//                                mAPAdapter.addExpiredItem(getChild.getKey(), new Action(
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
//                                mAPAdapter.addExpiredItem(getChild.getKey(), new Action(
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
//
//            } else {
//                mAPAdapter.addExpiredItem(getChild.getKey(), new Action(
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
//                                null,
//                                null,
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

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


}

