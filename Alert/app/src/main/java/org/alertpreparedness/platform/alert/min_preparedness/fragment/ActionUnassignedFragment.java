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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionUnassignedFragment extends InProgressFragment {

    @Nullable
    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionUnassigned;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgUnassigned;

    @Inject
    @BaseActionRef
    DatabaseReference baseActionRef;

    private ActionAdapter mUnassignedAdapter;
    private int freqValue = 0;
    private String ids[] = {};

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
        assert imgUnassigned != null;
        imgUnassigned.setImageResource(R.drawable.ic_close_round);
        assert tvActionUnassigned != null;
        tvActionUnassigned.setText("Unassigned");
        tvActionUnassigned.setTextColor(getResources().getColor(R.color.alertRed));

        mUnassignedAdapter = getmAdapter();
        mActionRV.setAdapter(mUnassignedAdapter);
        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        dbActionBaseRef.addValueEventListener(this);
    }

    @Override
    protected ActionAdapter getmAdapter() {
        return new ActionAdapter(getContext(), dbActionBaseRef, this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void process(DataSnapshot dataSnapshot) {
        ids = new String[]{user.getCountryID(), user.getNetworkID(), user.getLocalNetworkID(), user.getNetworkCountryID()};
        System.out.println("user.getNetworkCountryID() = " + user.getNetworkCountryID());
        System.out.println("user.getLocalNetworkID() = " + user.getLocalNetworkID());
        System.out.println("user.getNetworkID() = " + user.getNetworkID());
        System.out.println("user.getCountryID() = " + user.getCountryID());
        for (String id : ids) {
            if (dataSnapshot.getValue() != null && id != null) {
                for (DataSnapshot getChild : dataSnapshot.child(id).getChildren()) {
                    String actionIDs = getChild.getKey();
                    DataModel model = getChild.getValue(DataModel.class);

                    if (getChild.child("frequencyBase").getValue() != null) {
                        model.setFrequencyBase(getChild.child("frequencyBase").getValue().toString());
                    }
                    if (getChild.child("frequencyValue").getValue() != null) {
                        model.setFrequencyValue(getChild.child("frequencyValue").getValue().toString());
                    }

                    if (model.getType() != null && model.getType() == 2) {
                        System.out.println("model = " + model);
                        getCustom(model, getChild, id);
                    }

                    //  if (model.getType() != null && model.getType() == 0) {
                    getCHS(model, actionIDs, id);
                    // } else if (model.getType() != null && model.getType() == 1) {
                    System.out.println("model = " + model);
                    getMandated(model, actionIDs, id);

                }
            }else {
                getCHSForNewUser(id);
                getMandatedForNewUser(id);
            }
        }
    }

    private void getCustom(DataModel model, DataSnapshot getChild, String id) {
        System.out.println("getChild = " + getChild.getValue());
        if (model.getLevel() != null //MPA CUSTOM UNASSIGNED // NO USERS.
                && model.getLevel() == Constants.MPA
                && model.getAsignee() == null
                && model.getTask() != null) {


            txtNoAction.setVisibility(View.GONE);
            mUnassignedAdapter.addItems(getChild.getKey(), new Action(
                    id,
                    model.getTask(),
                    model.getDepartment(),
                    model.getAsignee(),
                    model.getCreatedByAgencyId(),
                    model.getCreatedByCountryId(),
                    model.getNetworkId(),
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
                    user,
                    dbAgencyRef.getRef(),
                    dbUserPublicRef.getRef(),
                    dbNetworkRef)
            );
        }
        else {
            mUnassignedAdapter.removeItem(getChild.getKey());
        }
    }

    private void getCHS(DataModel model, String actionIDs, String id) {
        System.out.println("model chs = " + model);
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                    if (!actionIDs.equals(getChild.getKey())) {
                        System.out.println("getChild.getKey() = " + getChild.getKey());

                        String CHSTaskName = (String) getChild.child("task").getValue();
                        Long CHSlevel = (Long) getChild.child("level").getValue();
                        Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();

                            txtNoAction.setVisibility(View.GONE);
                            mUnassignedAdapter.addItems(getChild.getKey(), new Action(
                                    id,
                                    CHSTaskName,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    CHSCreatedAt,
                                    null,
                                    (long) 0, //CHS always 0
                                    null,
                                    null,
                                    CHSlevel,
                                    null,
                                    null,
                                    user,
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef(),
                                    dbNetworkRef)
                            );

                    }
                    else {
                        mUnassignedAdapter.removeItem(getChild.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMandated(DataModel model, String actionIDs, String id) {
        dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                    if (!actionIDs.contains(getChild.getKey())) {

                        try {
                            String taskNameMandated = (String) getChild.child("task").getValue();
                            String departmentMandated = (String) getChild.child("department").getValue();
                            Long manCreatedAt = (Long) getChild.child("createdAt").getValue();
                            Long manLevel = (Long) getChild.child("level").getValue();

                            txtNoAction.setVisibility(View.GONE);
                            mUnassignedAdapter.addItems(getChild.getKey(), new Action(
                                    id,
                                    taskNameMandated,
                                    departmentMandated,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    manCreatedAt,
                                    null,
                                    (long) 1, //Mandated always 1
                                    null,
                                    null,
                                    manLevel,
                                    null,
                                    null,
                                    user,
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef(),
                                    dbNetworkRef)
                            );

                        } catch (Exception exception) {
                                mUnassignedAdapter.removeItem(getChild.getKey());
                            System.out.println("exception = " + exception);
                        }
                    }
                    else {
                        mUnassignedAdapter.removeItem(getChild.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getMandatedForNewUser(String id) {
        dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                    try {
                        String taskNameMandated = (String) getChild.child("task").getValue();
                        String departmentMandated = (String) getChild.child("department").getValue();
                        Long manCreatedAt = (Long) getChild.child("createdAt").getValue();
                        Long manLevel = (Long) getChild.child("level").getValue();

                        txtNoAction.setVisibility(View.GONE);
                        mUnassignedAdapter.addItems(getChild.getKey(), new Action(
                                id,
                                taskNameMandated,
                                departmentMandated,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                manCreatedAt,
                                null,
                                (long) 1, //Mandated always 1
                                null,
                                null,
                                manLevel,
                                null,
                                null,
                                user,
                                dbAgencyRef.getRef(),
                                dbUserPublicRef.getRef(),
                                dbNetworkRef)
                        );

                    } catch (Exception exception) {
                        mUnassignedAdapter.removeItem(getChild.getKey());
                        System.out.println("exception = " + exception);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCHSForNewUser(String id) {
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                    String CHSTaskName = (String) getChild.child("task").getValue();
                    Long CHSlevel = (Long) getChild.child("level").getValue();
                    Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();

                    txtNoAction.setVisibility(View.GONE);
                    mUnassignedAdapter.addItems(getChild.getKey(), new Action(
                            id,
                            CHSTaskName,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            CHSCreatedAt,
                            null,
                            (long) 0, //CHS always 0
                            null,
                            null,
                            CHSlevel,
                            null,
                            null,
                            user,
                            dbAgencyRef.getRef(),
                            dbUserPublicRef.getRef(),
                            dbNetworkRef)
                    );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onActionItemSelected(int pos, String key, String userTypeID) {
        Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Currently under development!", Snackbar.LENGTH_LONG).show();
    }
}
