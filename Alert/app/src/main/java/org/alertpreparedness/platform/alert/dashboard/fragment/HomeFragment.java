package org.alertpreparedness.platform.alert.dashboard.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.firebase.IndicatorModel;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.interfaces.IHomeActivity;
import org.alertpreparedness.platform.alert.interfaces.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.dashboard.model.Tasks;
import org.alertpreparedness.platform.alert.model.User;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

/**
 * Created by Tj on 13/12/2017.
 */

public class HomeFragment extends Fragment implements IHomeActivity, OnAlertItemClickedListener, FirebaseAuth.AuthStateListener, NestedScrollView.OnScrollChangeListener {

    @BindView(R.id.tasks_list_view)
    RecyclerView myTaskRecyclerView;

    @BindView(R.id.alert_list_view)
    RecyclerView alertRecyclerView;

    @BindView(R.id.nsScrollView)
    NestedScrollView scroller;

    @BindView(R.id.textView3)
    TextView mTaskText;

    @BindView(R.id.llPinned)
    CardView mPinnedHeader;

    @BindView(R.id.rvNetworkAlerts)
    RecyclerView networkAlertList;

    @Inject
    @BaseDatabaseRef
    DatabaseReference database;

    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    @Inject
    @AlertRef
    DatabaseReference alertRef;

    @Inject
    @ActionRef
    DatabaseReference taskRef;

    @Inject
    @IndicatorRef
    DatabaseReference indicatorRef;

    @Inject
    @BaseAlertRef
    DatabaseReference baseAlertRef;

    @Inject
    @NetworkRef
    DatabaseReference networkRef;

    @Inject
    @ActionCHSRef
    DatabaseReference dbCHSRef;

    @Inject
    User user;

    @BindView(R.id.networkTitle)
    TextView networkTitle;

    @BindView(R.id.countryTitle)
    TextView countryTitle;

    public TaskAdapter taskAdapter;
    public List<Tasks> tasksList;
    public AlertAdapter alertAdapter;
    public AlertAdapter networkAlertAdapter;

    private AgencyListener agencyListener = new AgencyListener();
    private AlertListener alertListener = new AlertListener(false);
    private AlertListener networkAlertListener = new AlertListener(true);
    private TaskListener taskListener = new TaskListener();
    private TaskListener indicatorListener = new TaskListener();
    private NetworkListener networkListener = new NetworkListener();
    private String agencyAdminId;
    private String networkLeadId;
    private Tasks task;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, v);

        DependencyInjector.applicationComponent().inject(this);

        ((MainDrawer) getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.ALERT, R.string.green_alert_level, R.drawable.alert_green);

        initViews();

        FirebaseAuth.getInstance().addAuthStateListener(this);

        return v;
    }

    private void initViews() {
        System.out.println("user = " + user);
        taskRef.addChildEventListener(taskListener);
        indicatorRef.addChildEventListener(indicatorListener);
        networkRef.addValueEventListener(networkListener);

        alertRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager alertlayoutManager = new LinearLayoutManager(getContext());
        alertRecyclerView.setLayoutManager(alertlayoutManager);
        alertRecyclerView.setItemAnimator(new DefaultItemAnimator());

        networkAlertList.setLayoutManager(new LinearLayoutManager(getContext()));

        alertAdapter = new AlertAdapter(getContext(), this);
        alertRecyclerView.setAdapter(alertAdapter);

        networkAlertAdapter = new AlertAdapter(getContext(), this);
        networkAlertList.setAdapter(networkAlertAdapter);

        myTaskRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        myTaskRecyclerView.setLayoutManager(layoutManager);
        myTaskRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myTaskRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        tasksList = new ArrayList<>();
        taskAdapter = new TaskAdapter(tasksList);
        myTaskRecyclerView.setAdapter(taskAdapter);

        scroller.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(alertRecyclerView, false);
        ViewCompat.setNestedScrollingEnabled(myTaskRecyclerView, false);
        scroller.setOnScrollChangeListener(this);
    }

    @Override
    public void removeAlert(String id) {
        alertAdapter.remove(id);
        updateTitle();
    }

    @Override
    public void addTask(Tasks task) {
        taskAdapter.add(task);
    }

    @Override
    public void onAlertItemClicked(AlertModel alert) {
        System.out.println("alert = [" + alert + "]");
        Intent intent = new Intent(getActivity(), AlertDetailActivity.class);
        intent.putExtra(EXTRA_ALERT, alert);
        startActivity(intent);
    }

    @Override
    public void updateAlert(String id, AlertModel alert) {
        countryTitle.setVisibility(View.VISIBLE);
        alertRecyclerView.setVisibility(View.VISIBLE);
        alertAdapter.update(id, alert);
        updateTitle();
    }

    public void updateNetworkAlert(String id, AlertModel alert) {
        networkTitle.setVisibility(View.VISIBLE);
        networkAlertList.setVisibility(View.VISIBLE);
        networkAlertAdapter.update(id, alert);
    }

    @Override
    public void onStop() {
        super.onStop();
        alertRef.removeEventListener(alertListener);
        agencyRef.removeEventListener(agencyListener);
        taskRef.removeEventListener(taskListener);
        indicatorRef.removeEventListener(taskListener);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {

        }
    }

    private void updateTitle() {
        boolean redPresent = false;
        boolean amberPresent = false;
        boolean noAlerts = false;

        updateTitle(R.string.green_alert_level, R.drawable.alert_green);
        for (String a : alertAdapter.getAlerts()) {
            AlertModel model = alertAdapter.getModel(a);
            switch (model.getAlertLevel()) {
                case 2:
                    redPresent = true;
                    break;
                case 1:
                    amberPresent = true;
                    break;
                case 0:
                    noAlerts = true;
                    break;
            }
        }

        if (!redPresent && !amberPresent && noAlerts) {
            updateTitle(R.string.green_alert_level, R.drawable.alert_green);
        }

        if (redPresent) {
            updateTitle(R.string.red_alert_level, R.drawable.alert_red_main);
        } else {
            updateTitle(R.string.amber_alert_level, R.drawable.alert_amber_main);
        }
    }

    @Override
    public void updateTitle(int stringResource, int backgroundResource) {
        try {
            ((MainDrawer) getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.ALERT, stringResource, backgroundResource);
        } catch (Exception e) {
        }
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        Rect rectf = new Rect();
        mTaskText.getGlobalVisibleRect(rectf);
        Rect rectf2 = new Rect();
        ((MainDrawer) getActivity()).alertToolbar.getGlobalVisibleRect(rectf2);

        if (rectf.top <= rectf2.bottom) {
            mPinnedHeader.setVisibility(View.VISIBLE);
            ((MainDrawer) getActivity()).removeActionbarElevation();
        } else {
            mPinnedHeader.setVisibility(View.GONE);
            ((MainDrawer) getActivity()).showActionbarElevation();
        }

    }

    private class AlertListener implements ChildEventListener {

        private boolean isNetworkAlert;

        public AlertListener(boolean isNetworkAlert) {

            this.isNetworkAlert = isNetworkAlert;
        }

        private void proccess(DataSnapshot dataSnapshot, String s) {
            AlertModel model = dataSnapshot.getValue(AlertModel.class);

            assert model != null;
            model.setKey(dataSnapshot.getKey());
            model.setParentKey(dataSnapshot.getRef().getParent().getKey());

            if (!isNetworkAlert) {
                if (model.getAlertLevel() != 0 && model.getHazardScenario() != null) {
                    updateAlert(dataSnapshot.getKey(), model);
                }
            } else if (!model.hasNetworkApproval()) {

                model.setLeadAgencyId(networkLeadId);
                model.setAgencyAdminId(agencyAdminId);
                if (model.getAlertLevel() != 0 && model.getHazardScenario() != null) {
                    System.out.println("updatednetworkalert");
                    updateNetworkAlert(model.getKey(), model);
                }

            }
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            System.out.println("onChildAdded = [" + dataSnapshot + "], s = [" + s + "]");
            proccess(dataSnapshot, s);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            System.out.println("onChildChanged = [" + dataSnapshot + "], s = [" + s + "]");
            proccess(dataSnapshot, s);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            removeAlert(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class TaskListener implements ChildEventListener {

        private void process(DataSnapshot dataSnapshot, String s) {

            if (dataSnapshot.getRef().getParent().getParent().getKey().equals("action")) {
                ActionModel model = dataSnapshot.getValue(ActionModel.class);
                assert model != null;
                if (model.getAsignee() != null && !model.isComplete() && model.getAsignee().equals(user.getUserID()) && model.getDueDate() != null) {
                    if (DateHelper.isDueInWeek(model.getDueDate()) || DateHelper.itWasDue(model.getDueDate())) {
                        addTask(new Tasks(0, "action", model.getTask(), model.getDueDate()));
                    }
                }

            } else if (dataSnapshot.getRef().getParent().getParent().getKey().equals("indicator")) {
                IndicatorModel model = dataSnapshot.getValue(IndicatorModel.class);
                assert model != null;
                if (model.getAssignee() != null && model.getAssignee().equals(user.getUserID()) && model.getDueDate() != null) {
                    Tasks tasks = new Tasks(model.getTriggerSelected().intValue(), "indicator", model.getName(), model.getDueDate());
                    if (DateHelper.isDueInWeek(tasks.dueDate) || DateHelper.itWasDue(tasks.dueDate)) {
                        addTask(tasks);
                    }
                }
            }
        }

        private void chsProcess(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.getRef().getParent().getParent().getKey().equals("action")) {
                ActionModel model = dataSnapshot.getValue(ActionModel.class);
                String actionIDs = dataSnapshot.getKey();

                if (model.getType() == 0) {
                    dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                                if (actionIDs.contains(getChild.getKey())) {
                                    // Tasks task;
                                    String taskNameCHS = (String) getChild.child("task").getValue();
                                    System.out.println("taskNameCHS = " + taskNameCHS);
                                    addTask(new Tasks(0, "action", taskNameCHS, model.getDueDate(), model.getType()));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }


        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot, s);
            //chsProcess(dataSnapshot, s);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot, s);
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

    private class AgencyListener implements ValueEventListener {
        @SuppressWarnings("unchecked")
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            HashMap<String, Boolean> networks = (HashMap<String, Boolean>) dataSnapshot.child("networks").getValue();
            agencyAdminId = dataSnapshot.child("adminId").getValue(String.class);

            if (networks != null) {
                for (String id : networks.keySet()) {
                    DatabaseReference ref = baseAlertRef.child(id);
                    ref.addChildEventListener(networkAlertListener);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

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
        networkLeadId = snapshot.child("leadAgencyId").getValue(String.class);

        agencyRef.addListenerForSingleValueEvent(agencyListener);
        alertRef.addChildEventListener(alertListener);
    }

}
