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
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseHazardRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardRef;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.dashboard.model.Task;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.firebase.IndicatorModel;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.interfaces.IHomeActivity;
import org.alertpreparedness.platform.alert.interfaces.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.risk_monitoring.view.ActiveRiskFragment;
import org.alertpreparedness.platform.alert.risk_monitoring.view.UpdateIndicatorActivity;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.NetworkFetcher;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

/**
 * Created by Tj on 13/12/2017.
 */

public class HomeFragment extends Fragment implements IHomeActivity, OnAlertItemClickedListener, FirebaseAuth.AuthStateListener, NestedScrollView.OnScrollChangeListener, NetworkFetcher.NetworkFetcherListener, TaskAdapter.TaskSelectListener {

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

    @BindView(R.id.network_tasks)
    RecyclerView networkTaskList;

    @BindView(R.id.taskTypeTitle)
    TextView taskTypeTitle;

    @BindView(R.id.noNetworkTasks)
    TextView noNetworkTasks;

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
    @HazardRef
    DatabaseReference hazardRef;

    @Inject
    @BaseHazardRef
    DatabaseReference baseHazardRef;

    @Inject
    @BaseIndicatorRef
    DatabaseReference baseIndicatorRef;

    @Inject
    @BaseActionRef
    DatabaseReference baseActionRef;

    @Inject
    User user;

    @BindView(R.id.networkTitle)
    TextView networkTitle;

    @BindView(R.id.countryTitle)
    TextView countryTitle;

    @BindView(R.id.countryTasks)
    TextView countryTastsTitle;

    @BindView(R.id.networkTasks)
    TextView networkTasksTitle;

    public TaskAdapter taskAdapter;
    public AlertAdapter alertAdapter;
    public AlertAdapter networkAlertAdapter;

    private AgencyListener agencyListener = new AgencyListener();
    private AlertListener alertListener = new AlertListener(false);
    private AlertListener networkAlertListener = new AlertListener(true);
    private TaskListener taskListener = new TaskListener();
    private TaskListener networkTaskListener = new NetworkTaskListener();
    private TaskListener indicatorListener = new TaskListener();
    private TaskListener hazardTaskListener = new TaskListener();
    private NetworkListener networkListener = new NetworkListener();
    private HazardListener hazardListener = new HazardListener();
    private String agencyAdminId;
    private String networkLeadId;
    private Task task;
    private TaskAdapter networkTaskAdapter;

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
        taskRef.addChildEventListener(taskListener);
        indicatorRef.addChildEventListener(new TaskListener(user.countryID));
        networkRef.addValueEventListener(networkListener);
        hazardRef.addChildEventListener(hazardListener);
        alertRecyclerView.setHasFixedSize(true);

        alertRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alertRecyclerView.setItemAnimator(new DefaultItemAnimator());

        networkAlertList.setLayoutManager(new LinearLayoutManager(getContext()));

        alertAdapter = new AlertAdapter(getContext(), this);
        alertRecyclerView.setAdapter(alertAdapter);

        networkAlertAdapter = new AlertAdapter(getContext(), this);
        networkAlertList.setAdapter(networkAlertAdapter);

        myTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myTaskRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        networkTaskList.setLayoutManager(new LinearLayoutManager(getContext()));
        networkTaskList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        taskAdapter = new TaskAdapter(this);
        myTaskRecyclerView.setAdapter(taskAdapter);

        networkTaskAdapter = new TaskAdapter(this);
        networkTaskList.setAdapter(networkTaskAdapter);

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
    public void addTask(String key, Task task) {
        taskAdapter.add(key, task);
    }

    private void addNetworkTask(String key, Task task) {
        networkTaskList.setVisibility(View.VISIBLE);
        noNetworkTasks.setVisibility(View.GONE);
        networkTasksTitle.setVisibility(View.VISIBLE);
        networkTaskAdapter.add(key, task);
    }

    @Override
    public void onAlertItemClicked(AlertModel alert) {
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
        }
        else {
            mPinnedHeader.setVisibility(View.GONE);
            ((MainDrawer) getActivity()).showActionbarElevation();
        }

        checkCountryTasks(v,scrollX, scrollY, oldScrollX, oldScrollY);

    }

    private void checkCountryTasks(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        Rect rectf = new Rect();
        countryTitle.getGlobalVisibleRect(rectf);

        Rect rectf2 = new Rect();
        mPinnedHeader.getGlobalVisibleRect(rectf2);

        Rect rectf3 = new Rect();
        networkTitle.getGlobalVisibleRect(rectf3);

    }

    @Override
    public void onNetworkFetcherResult(NetworkFetcher.NetworkFetcherResult networkFetcherResult) {
        System.out.println("networkFetcherResult = " + networkFetcherResult);
        if (networkFetcherResult != null) {
            for (String id : networkFetcherResult.getNetworksCountries()) {
               addListenerForNetworkData(id);
            }
            for (String id : networkFetcherResult.getGlobalNetworks()) {
                addListenerForNetworkData(id);
            }
            for (String id : networkFetcherResult.getLocalNetworks()) {
                addListenerForNetworkData(id);
            }
        }
    }

    private void addListenerForNetworkData(String id) {
        baseAlertRef.child(id).addChildEventListener(networkAlertListener);
        baseActionRef.child(id).addChildEventListener(networkTaskListener);
        baseIndicatorRef.child(id).addChildEventListener(new TaskListener(id));
        baseHazardRef.child(id).addChildEventListener(hazardListener);
    }

    @Override
    public void onTaskSelected(String id, Task task) {
        if(task.getTaskType().equals("indicator")) {
            Intent intent = new Intent(getActivity(), UpdateIndicatorActivity.class);
            intent.putExtra("hazard_id", task.getHazardId());
            intent.putExtra("indicator_id", id);

            startActivity(intent);
        }
        else {//action
            Intent intent = new Intent(getActivity(), CompleteActionActivity.class);
            intent.putExtra(CompleteActionActivity.REQUIRE_DOC, task.isRequireDoc());
            intent.putExtra(CompleteActionActivity.ACTION_KEY, id);
            intent.putExtra(CompleteActionActivity.PARENT_KEY, task.getParentId());
            startActivity(intent);
        }
    }

    private class HazardListener implements ChildEventListener {

        private void process(DataSnapshot datasnapshot) {
            baseActionRef.child(datasnapshot.getKey()).addChildEventListener(hazardListener);
            baseIndicatorRef.child(datasnapshot.getKey()).addChildEventListener(new TaskListener(datasnapshot.getKey()));
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

    private class AlertListener implements ChildEventListener {

        private boolean isNetworkAlert;

        public AlertListener(boolean isNetworkAlert) {

            this.isNetworkAlert = isNetworkAlert;
        }

        private void process(DataSnapshot dataSnapshot, String s) {
            AlertModel model = AppUtils.getValueFromDataSnapshot(dataSnapshot, AlertModel.class);

            assert model != null;
            model.setKey(dataSnapshot.getKey());
            model.setParentKey(dataSnapshot.getRef().getParent().getKey());

            if (!isNetworkAlert) {
                if (model.getAlertLevel() != 0 && model.getHazardScenario() != null) {
                    updateAlert(dataSnapshot.getKey(), model);
                }
            }
            else {
//            else if (!model.hasNetworkApproval()) {
                System.out.println("AlertModelmodel = " + model);

                model.setLeadAgencyId(networkLeadId);
                model.setAgencyAdminId(agencyAdminId);
                if (model.getAlertLevel() != 0 && model.getHazardScenario() != null) {
                    updateNetworkAlert(model.getKey(), model);
                }
            }
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot, s);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot, s);
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

        private String hazardId;

        public TaskListener() {}

        public TaskListener(String hazardId) {
            this.hazardId = hazardId;
        }

        protected void process(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.getRef().getParent().getParent().getKey().equals("action")) {
                ActionModel model = AppUtils.getValueFromDataSnapshot(dataSnapshot, ActionModel.class);

                assert model != null;
                boolean shouldAdd = model.getAsignee() != null && !model.isComplete() && model.getAsignee().equals(user.getUserID()) && model.getDueDate() != null;

                if (shouldAdd) {
                    if (DateHelper.isDueInWeek(model.getDueDate()) || DateHelper.itWasDue(model.getDueDate())) {
                        addTask(dataSnapshot.getKey(), new Task(dataSnapshot.getRef().getParent().getKey(), 0, "action", model.getTask(), model.getDueDate(), model.getRequireDoc()));
                    }
                    else {
                        taskAdapter.tryRemove(dataSnapshot.getKey());
                    }
                }

            } else if (dataSnapshot.getRef().getParent().getParent().getKey().equals("indicator")) {

                IndicatorModel model = dataSnapshot.getValue(IndicatorModel.class);

                assert model != null;

                boolean shouldAdd = model.getAssignee() != null && model.getAssignee().equals(user.getUserID()) && model.getDueDate() != null;
                System.out.println("shouldAdd = " + shouldAdd);
                if (model.getAssignee() != null && model.getAssignee().equals(user.getUserID()) && model.getDueDate() != null) {
                    Task task = new Task(dataSnapshot.getRef().getParent().getKey(), model.getTriggerSelected().intValue(), "indicator", model.getName(), model.getDueDate());
                    task.setHazardId(hazardId);
                    if (DateHelper.isDueInWeek(task.dueDate) || DateHelper.itWasDue(task.dueDate)) {
                        addTask(dataSnapshot.getKey(), task);
                    }
                    else {
                        taskAdapter.tryRemove(dataSnapshot.getKey());
                    }
                }
            }
        }

        private void chsProcess(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.getRef().getParent().getParent().getKey().equals("action")) {
                ActionModel model = AppUtils.getValueFromDataSnapshot(dataSnapshot, ActionModel.class);
                String actionIDs = dataSnapshot.getKey();

                if (model.getType() == 0) {
                    dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot getChild : dataSnapshot.getChildren()) {

                                if (actionIDs.contains(getChild.getKey())) {
                                    // Task task;
                                    String taskNameCHS = (String) getChild.child("task").getValue();
                                    System.out.println("taskNameCHS = " + taskNameCHS);
                                    addTask(dataSnapshot.getKey(), new Task(dataSnapshot.getRef().getParent().getKey(), 0, "action", taskNameCHS, model.getDueDate(), model.getType()));
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
//            HashMap<String, Boolean> networks = (HashMap<String, Boolean>) dataSnapshot.child("networks").getValue();
            agencyAdminId = dataSnapshot.child("adminId").getValue(String.class);

            new NetworkFetcher(HomeFragment.this).fetch();
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

    private class NetworkTaskListener extends TaskListener {
        @Override
        protected void process(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.getRef().getParent().getParent().getKey().equals("action")) {
                ActionModel model = AppUtils.getValueFromDataSnapshot(dataSnapshot, ActionModel.class);

                assert model != null;
                boolean shouldAdd = model.getAsignee() != null && !model.isComplete() && model.getAsignee().equals(user.getUserID()) && model.getDueDate() != null;
                if (shouldAdd) {
                    if (DateHelper.isDueInWeek(model.getDueDate()) || DateHelper.itWasDue(model.getDueDate())) {
                        addNetworkTask(dataSnapshot.getKey(), new Task(dataSnapshot.getRef().getParent().getKey(), 0, "action", model.getTask(), model.getDueDate()));
                    }
                    else {
                        networkTaskAdapter.tryRemove(dataSnapshot.getKey());
                    }
                }

            } else if (dataSnapshot.getRef().getParent().getParent().getKey().equals("indicator")) {

                IndicatorModel model = dataSnapshot.getValue(IndicatorModel.class);
                assert model != null;
                boolean shouldAdd = model.getAssignee() != null && model.getAssignee().equals(user.getUserID()) && model.getDueDate() != null;
                if (shouldAdd) {
                    Task task = new Task(dataSnapshot.getRef().getParent().getKey(), model.getTriggerSelected().intValue(), "indicator", model.getName(), model.getDueDate());
                    if (DateHelper.isDueInWeek(task.dueDate) || DateHelper.itWasDue(task.dueDate)) {
                        addNetworkTask(dataSnapshot.getKey(), task);
                    }
                    else {
                        networkTaskAdapter.tryRemove(dataSnapshot.getKey());
                    }
                }
            }
        }
    }

    private void onNetworkRetrieved(DataSnapshot snapshot) {
        networkLeadId = snapshot.child("leadAgencyId").getValue(String.class);

        agencyRef.addListenerForSingleValueEvent(agencyListener);
        alertRef.addChildEventListener(alertListener);
    }

}
