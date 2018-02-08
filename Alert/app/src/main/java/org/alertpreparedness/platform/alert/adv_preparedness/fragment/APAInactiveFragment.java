package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionMandatedRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAInactiveFragment extends Fragment implements APActionAdapter.ItemSelectedListener {

    private ArrayList<Integer> alertHazardTypes = new ArrayList<>();

    public APAInactiveFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgActionInactive;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionInactive;

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
    @AlertRef
    DatabaseReference dbAlertRef;

    @Inject
    @ActionCHSRef
    DatabaseReference dbCHSRef;

    @Inject
    @ActionMandatedRef
    DatabaseReference dbMandatedRef;

    @Inject
    @UserPublicRef
    DatabaseReference dbUserPublicRef;

    @Inject
    @BaseAlertRef
    DatabaseReference baseAlertRef;

    @Inject
    User user;

    @Inject
    @NetworkRef
    DatabaseReference dbNetworkRef;

    @Inject
    @NetworkRef
    DatabaseReference networkRef;

    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    @Inject
    @AlertRef
    DatabaseReference alertRef;

    @Inject
    @BaseActionRef
    public DatabaseReference dbActionBaseRef;

    private APActionAdapter mAPAdapter;
    private Boolean isCHS = false;
    private Boolean isCHSAssigned = false;
    private Boolean isMandated = false;
    private Boolean isMandatedAssigned = false;
    private Boolean isInProgress = false;
    private int freqBase = 0;
    private int freqValue = 0;
    private AgencyListener agencyListener = new AgencyListener();
    private AlertListener alertListener = new AlertListener();
    private NetworkListener networkListener = new NetworkListener();


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
        assert imgActionInactive != null;
        imgActionInactive.setImageResource(R.drawable.ic_in_progress_gray);
        assert tvActionInactive != null;
        tvActionInactive.setText("Inactive");
        tvActionInactive.setTextColor(getResources().getColor(R.color.alertGray));
        mAPAdapter = getAPAdapter();
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);
        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        networkRef.addValueEventListener(networkListener);
        AdvPreparednessFragment xFragment = null;
        for(Fragment fragment : getFragmentManager().getFragments()){
            if(fragment instanceof AdvPreparednessFragment){
                xFragment = (AdvPreparednessFragment) fragment;
                break;
            }
        }
        if(xFragment != null) {
            FloatingActionButton fab = xFragment.fabCreateAPA;
            fab.show();

            mAdvActionRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 && fab.isShown()) {
                        fab.hide();
                    }
//                    else if (!fab.isShown() && dy <= 0) {
//                        fab.show();
//                    }
                    System.out.println("dy = " + dy);

                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        fab.show();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }

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
        SheetMenu.with(getContext()).setMenu(R.menu.menu_in_progress).setClick(menuItem -> {
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
        }).show();
    }

    private void getCustom(DataModel model, DataSnapshot getChild) {
        dbAlertRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long alertLevel = (Long) dataSnapshot.child("alertLevel").getValue();

//                if (isInProgress) {
                addObjects(model.getTask(),
                        model.getCreatedAt(),
                        model.getLevel(),
                        model,
                        alertLevel,
                        getChild,
                        isCHS,
                        isCHSAssigned,
                        isMandated,
                        isMandatedAssigned);
//                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

                        dbAlertRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long alertLevel = (Long) dataSnapshot.child("alertLevel").getValue();

//                                if (isInProgress) {
                                addObjects(CHSTaskName,
                                        CHSCreatedAt,
                                        CHSlevel,
                                        model,
                                        alertLevel,
                                        getChild,
                                        isCHS,
                                        isCHSAssigned,
                                        isMandated,
                                        isMandatedAssigned);

//                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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
                        Long manCreatedAt = (Long) getChild.child("createdAt").getValue();
                        Long manLevel = (Long) getChild.child("level").getValue();

                        isMandated = true;
                        isMandatedAssigned = true;
                        isCHS = false;
                        isCHSAssigned = false;

                        dbAlertRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long alertLevel = (Long) dataSnapshot.child("alertLevel").getValue();

//                                if (isInProgress) {
                                addObjects(taskNameMandated,
                                        manCreatedAt,
                                        manLevel,
                                        model,
                                        alertLevel,
                                        getChild,
                                        isCHS,
                                        isCHSAssigned,
                                        isMandated,
                                        isMandatedAssigned);

//                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addObjects(String name, Long createdAt, Long level,
                            DataModel model, Long alertLevel, DataSnapshot getChild, Boolean isCHS, Boolean isCHSAssigned, Boolean isMandated, Boolean isMandatedAssigned) {


        if (model.getLevel() != null
                && model.getLevel() == Constants.APA
                || (isCHS && isCHSAssigned //APA CHS inactive for logged in user.
                && user.getUserID().equals(model.getAsignee())
                && model.getLevel() != null
                && model.getLevel() == Constants.APA)
                || (isMandated && isMandatedAssigned //APA Mandated inactive for logged in user.
                && user.getUserID().equals(model.getAsignee())
                && model.getLevel() != null
                && model.getLevel() == Constants.APA)) {

            if(model.getAssignHazard() != null
                    && alertHazardTypes.indexOf(model.getAssignHazard().get(0)) == -1) {
                txtNoAction.setVisibility(View.GONE);
                mAPAdapter.addItems(getChild.getKey(), new Action(
                        model.getId(),
                        model.getTask(),
                        model.getDepartment(),
                        model.getAsignee(),
                        model.getCreatedByAgencyId(),
                        model.getCreatedByCountryId(),
                        model.getNetworkId(),
                        model.getIsArchived(),
                        model.getIsComplete(),
                        createdAt,
                        model.getUpdatedAt(),
                        model.getType(),
                        model.getDueDate(),
                        model.getBudget(),
                        level,
                        model.getFrequencyBase(),
                        freqValue,
                        user,
                        dbAgencyRef.getRef(),
                        dbUserPublicRef.getRef(),
                        dbNetworkRef.getRef())
                );
            }
            else {
                mAPAdapter.removeItem(getChild.getKey());
            }
        }
        else {
            mAPAdapter.removeItem(getChild.getKey());
        }
    }

    private class InactiveAPAListener implements ChildEventListener {

        private String id;

        public InactiveAPAListener(String id) {

            this.id = id;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private void process(DataSnapshot dataSnapshot) {
        String actionIDs = dataSnapshot.getKey();
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
            System.out.println("model = " + model);
            getCustom(model, dataSnapshot);
        }

    }

    private class NetworkListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            onNetworkRetrieved(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private void onNetworkRetrieved(DataSnapshot snapshot) {
        agencyRef.addListenerForSingleValueEvent(agencyListener);
        alertRef.addValueEventListener(alertListener);
    }

    private class AgencyListener implements ValueEventListener {
        @SuppressWarnings("unchecked")
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            HashMap<String, Boolean> networks = (HashMap<String, Boolean>) dataSnapshot.child("networks").getValue();

            if (networks != null) {
                for (String id : networks.keySet()) {
                    System.out.println("networkidid = " + id);
                    DatabaseReference ref = baseAlertRef.child(id);

                    ref.addValueEventListener(alertListener);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class AlertListener implements ValueEventListener {

        private void process(DataSnapshot dataSnapshot) {

            final GsonBuilder gsonBuilder = new GsonBuilder();
            final Gson gson = gsonBuilder.create();

            JsonReader reader = new JsonReader(new StringReader(gson.toJson(dataSnapshot.getValue()).trim()));
            reader.setLenient(true);
            AlertModel model = gson.fromJson(reader, AlertModel.class);

            assert model != null;
            model.setKey(dataSnapshot.getKey());
            model.setParentKey(dataSnapshot.getRef().getParent().getKey());

            if (model.getAlertLevel() == Constants.TRIGGER_RED && model.getHazardScenario() != null) {
                update(model);
            }

        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot child : dataSnapshot.getChildren()) {
                process(child);
            }
            try {
                dbActionRef.removeEventListener(this);
            }
            catch (Exception e) {}
            String[] ids = new String[]{user.getCountryID(), user.getNetworkID(), user.getLocalNetworkID(), user.getNetworkCountryID()};

            for (String id : ids) {
                if(id != null) {
                    dbActionBaseRef.child(id).addChildEventListener(new InactiveAPAListener(id));
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private void update(AlertModel model) {
        alertHazardTypes.add(model.getHazardScenario());
    }

}


