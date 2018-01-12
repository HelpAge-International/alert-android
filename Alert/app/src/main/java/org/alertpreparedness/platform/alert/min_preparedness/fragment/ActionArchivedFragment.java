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

public class ActionArchivedFragment extends InProgressFragment {

    @Nullable
    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionArchived;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgArchived;

    private ActionAdapter mAdapter;

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
        assert imgArchived != null;
        imgArchived.setImageResource(R.drawable.ic_close_round_gray);
        assert tvActionArchived != null;
        tvActionArchived.setText("Archived");
        tvActionArchived.setTextColor(getResources().getColor(R.color.alertGray));

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
        for (DataSnapshot getChild : dataSnapshot.getChildren()) {

            String taskName = (String) getChild.child("task").getValue();
            String department = (String) getChild.child("department").getValue();
            String assignee = (String) getChild.child("asignee").getValue();
            Boolean isArchived = (Boolean) getChild.child("isArchived").getValue();
            Boolean isComplete = (Boolean) getChild.child("isComplete").getValue();
            Long actionType = (Long) getChild.child("type").getValue();
            Long dueDate = (Long) getChild.child("dueDate").getValue();
            Long budget = (Long) getChild.child("budget").getValue();
            Long level =  (Long) getChild.child("level").getValue();

//            mAdapter.addArchivedItem(getChild.getKey(), new Action(
//                    taskName,
//                    department,
//                    assignee,
//                    isArchived,
//                    isComplete,
//                    actionType,
//                    dueDate,
//                    budget,
//                    level,
//                    dbAgencyRef.getRef(),
//                    dbUserPublicRef.getRef())
//            );

        }

    }
}
