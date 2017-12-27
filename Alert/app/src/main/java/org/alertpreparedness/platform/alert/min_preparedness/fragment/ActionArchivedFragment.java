package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;

/**
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionArchivedFragment extends InProgressFragment {

    private ActionAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_action_archived, container, false);
        return v;
    }

    @Override
    protected ActionAdapter getmAdapter() {
        return new ActionAdapter(getContext(), dbActionRef, this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot getChild : dataSnapshot.getChildren()) {

            String taskName = (String) getChild.child("task").getValue();
            String department = (String) getChild.child("departments").getValue();
            String assignee = (String) getChild.child("asignee").getValue();
            Boolean isArchived = (Boolean) getChild.child("isArchived").getValue();
            Boolean isComplete = (Boolean) getChild.child("isComplete").getValue();
            Long actionType = (Long) getChild.child("type").getValue();
            Long dueDate = (Long) getChild.child("dueDate").getValue();
            Long budget = (Long) getChild.child("budget").getValue();

            mAdapter.addArchivedItem(getChild.getKey(), new Action(
                    taskName,
                    department,
                    assignee,
                    isArchived,
                    isComplete,
                    actionType,
                    dueDate,
                    budget,
                    dbAgencyRef.getRef())
            );

        }

    }
}
