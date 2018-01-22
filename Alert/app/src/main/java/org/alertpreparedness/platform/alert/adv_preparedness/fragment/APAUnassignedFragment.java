package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionMandatedRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAUnassignedFragment extends Fragment implements APActionAdapter.ItemSelectedListener, ValueEventListener, UsersListDialogFragment.ItemSelectedListener {

    public APAUnassignedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgActionUnassigned;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionUnassigned;

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

    private APActionAdapter mAPAdapter;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();
    private String actionID;
    private int freqValue = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_advanced, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        dialog.setListener(this);

        return v;
    }

    private void initViews() {
        assert imgActionUnassigned != null;
        imgActionUnassigned.setImageResource(R.drawable.ic_close_round);
        assert tvActionUnassigned != null;
        tvActionUnassigned.setText("Unassigned");
        tvActionUnassigned.setTextColor(getResources().getColor(R.color.alertRed));
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
        this.actionID = key;
        SheetMenu.with(getContext()).setMenu(R.menu.menu_unassigned_apa).setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.assign_action:
                        dialog.show(getActivity().getFragmentManager(), "users_list");
                        break;
                    case R.id.edit_action:
                        Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "EDIT ACTION", Snackbar.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        }).show();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                String actionIDs = getChild.getKey();
                DataModel model = getChild.getValue(DataModel.class);

                if (getChild.child("frequencyBase").getValue() != null) {
                    model.setFrequencyBase(getChild.child("frequencyBase").getValue().toString());
                }
                if (getChild.child("frequencyValue").getValue() != null) {
                    model.setFrequencyValue(getChild.child("frequencyValue").getValue().toString());
                }

                //if (model.getType() == 0) {
                getCHS(model, actionIDs);
                //  } else if (model.getType() == 1) {
                getMandated(model, actionIDs);
                //  } else {
                getCustom(model, getChild);
                // }
            }
        } else {
            getCHSForNewUser();
            getMandatedForNewUser();
        }
    }

    private void getCustom(DataModel model, DataSnapshot getChild) {

        if (model.getLevel() != null //MPA CUSTOM UNASSIGNED // NO USERS.
                && model.getLevel() == Constants.MPA
                && model.getAsignee() == null
                && model.getTask() != null) {

            txtNoAction.setVisibility(View.GONE);
            mAPAdapter.addItems(getChild.getKey(), new Action(
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
    }

    private void getCHS(DataModel model, String actionIDs) {
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                    if (!actionIDs.equals(getChild.getKey())) {

                        String CHSTaskName = (String) getChild.child("task").getValue();
                        Long CHSlevel = (Long) getChild.child("level").getValue();
                        Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();

                            txtNoAction.setVisibility(View.GONE);
                            mAPAdapter.addItems(getChild.getKey(), new Action(
                                    CHSTaskName,
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

    private void getMandated(DataModel model, String actionIDs) {
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
                            mAPAdapter.addItems(getChild.getKey(), new Action(
                                    taskNameMandated,
                                    departmentMandated,
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
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef())
                            );

                        } catch (Exception exception) {
                            System.out.println("exception = " + exception);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getMandatedForNewUser() {
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
                        mAPAdapter.addItems(getChild.getKey(), new Action(
                                taskNameMandated,
                                departmentMandated,
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
                                dbAgencyRef.getRef(),
                                dbUserPublicRef.getRef())
                        );

                    } catch (Exception exception) {
                        System.out.println("exception = " + exception);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCHSForNewUser() {
        dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                    String CHSTaskName = (String) getChild.child("task").getValue();
                    Long CHSlevel = (Long) getChild.child("level").getValue();
                    Long CHSCreatedAt = (Long) getChild.child("createdAt").getValue();

                    txtNoAction.setVisibility(View.GONE);
                    mAPAdapter.addItems(getChild.getKey(), new Action(
                            CHSTaskName,
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
                            dbAgencyRef.getRef(),
                            dbUserPublicRef.getRef())
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
    public void onItemSelected(UserModel userModel) {
        long millis = System.currentTimeMillis();
        dbActionRef.child(actionID).child("asignee").setValue(userModel.getUserID());
        dbActionRef.child(actionID).child("updatedAt").setValue(millis);
        mAPAdapter.removeItem(actionID);
        mAPAdapter.notifyDataSetChanged();
    }

}