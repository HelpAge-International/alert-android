package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.responseplan.ResponsePlanObj;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 13/12/2017.
 */

public class MinPreparednessFragment extends Fragment implements ActionAdapter.ItemSelectedListner{

    @BindView(R.id.rvActionsAssigned)
    RecyclerView actionsRecyclerView;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_min_preparedness, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.title_min_preparedness);

        return v;
    }

    private void initViews() {

        ArrayList<Action> items = new ArrayList<>();

        actionsRecyclerView.setAdapter(new ActionAdapter(getContext(), items, this));
        actionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        actionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        actionsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        dbActionRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot getChild : dataSnapshot.getChildren()){

//                    String taskName = (String) getChild.child("task").getValue();
//                    String department = (String) getChild.child("department").getValue();
//                    String assignee = (String) getChild.child("asignee").getValue();
//                    Boolean isArchived = (Boolean) getChild.child("isArchived").getValue();
//                    Long actionType = (Long) getChild.child("type").getValue();
//                    Long dueDate = (Long) getChild.child("dueDate").getValue();
//                    Long budget = (Long) getChild.child("budget").getValue();
//
//                    items.add(new Action(
//                            taskName,
//                            department,
//                            assignee,
//                            isArchived,
//                            actionType,
//                            new Date (dueDate),
//                            budget)
//                    );

                    //String assignee = (String) getChild.child("asignee").getValue();
//                    System.out.println(" Name: "+ taskName+
//                                        " Dep: "+ department+
//                                        " Assignee: " +assignee+
//                                        " isArch: "+isArchived+
//                                        " Actype: "+actionType+
//                                        " DueDate: "+dueDate+
//                                        " Budget: "+budget);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onActionItemSelected(int pos) {

    }
}
