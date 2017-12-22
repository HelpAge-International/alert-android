package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.app.Fragment;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;

/**
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionArchivedFragment extends InProgressFragment {

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
            Long actionType = (Long) getChild.child("type").getValue();
            Long dueDate = (Long) getChild.child("dueDate").getValue();
            Long budget = (Long) getChild.child("budget").getValue();

            mAdapter.addItem(getChild.getKey(), new Action(
                    taskName,
                    department,
                    assignee,
                    isArchived,
                    actionType,
                    dueDate,
                    budget,
                    dbAgencyRef.getRef())
            );

        }

    }
}
