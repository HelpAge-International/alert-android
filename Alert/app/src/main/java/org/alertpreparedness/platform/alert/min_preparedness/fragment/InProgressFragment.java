package org.alertpreparedness.platform.alert.min_preparedness.fragment;


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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.dashboard.activity.CreateAlertActivity;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProgressFragment extends Fragment implements ActionAdapter.ItemSelectedListener, ValueEventListener {

    public InProgressFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;


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
    @UserPublicRef
    DatabaseReference dbUserPublicRef;

    private ActionAdapter mAdapter;
    private Boolean isCHS = false;

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
        for (DataSnapshot getChild : dataSnapshot.getChildren()) {
            String actionIDs = getChild.getKey();
            String taskName = (String) getChild.child("task").getValue();
            String department = (String) getChild.child("department").getValue();
            String assignee = (String) getChild.child("asignee").getValue();
            Boolean isArchived = (Boolean) getChild.child("isArchived").getValue();
            Boolean isComplete = (Boolean) getChild.child("isComplete").getValue();
            Long actionType = (Long) getChild.child("type").getValue();
            Long dueDate = (Long) getChild.child("dueDate").getValue();
            Long budget = (Long) getChild.child("budget").getValue();
            Long level = (Long) getChild.child("level").getValue();

            mAdapter.addInProgressItem(getChild.getKey(), new Action(
                    taskName,
                    department,
                    assignee,
                    isArchived,
                    isComplete,
                    isCHS,
                    actionType,
                    dueDate,
                    budget,
                    level,
                    dbAgencyRef.getRef(),
                    dbUserPublicRef.getRef())
            );

            dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                        if (actionIDs.contains(getChild.getKey())) {
                            String taskNameCHS = (String) getChild.child("task").getValue();
                            isCHS = true;
                            mAdapter.addInProgressItem(getChild.getKey(), new Action(
                                    taskNameCHS,
                                    department,
                                    assignee,
                                    isArchived,
                                    isComplete,
                                    isCHS,
                                    actionType,
                                    dueDate,
                                    budget,
                                    level,
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef())
                            );
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


}
