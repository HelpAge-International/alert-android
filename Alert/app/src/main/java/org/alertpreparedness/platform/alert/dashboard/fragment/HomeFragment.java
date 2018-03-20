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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActiveActionObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseHazardRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardRef;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.dashboard.model.Task;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.firebase.IndicatorModel;
import org.alertpreparedness.platform.alert.firebase.consumers.ItemConsumer;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.FetcherResultItem;
import org.alertpreparedness.platform.alert.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.alert.firebase.wrappers.AlertResultWrapper;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.interfaces.IHomeActivity;
import org.alertpreparedness.platform.alert.interfaces.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.risk_monitoring.view.UpdateIndicatorActivity;
import org.alertpreparedness.platform.alert.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

/**
 * Created by Tj on 13/12/2017.
 */

public class HomeFragment extends Fragment implements IHomeActivity, OnAlertItemClickedListener, FirebaseAuth.AuthStateListener, NestedScrollView.OnScrollChangeListener, TaskAdapter.TaskSelectListener {

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
    @AgencyObservable
    Flowable<FetcherResultItem<DataSnapshot>> agencyObservable;

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

    @Inject
    Flowable<FetcherResultItem<AlertResultWrapper>> alertFlowable;

    @Inject
    @IndicatorObservable
    Flowable<FetcherResultItem<DataSnapshot>> indicatorFlowable;

    @Inject
    @ActiveActionObservable
    Flowable<FetcherResultItem<Collection<ActionItemWrapper>>> actionFlowable;

    public TaskAdapter taskAdapter;
    public AlertAdapter alertAdapter;
    public AlertAdapter networkAlertAdapter;
    private TaskAdapter networkTaskAdapter;
    private CompositeDisposable disposable;

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

        alertFlowable.subscribe(new ItemConsumer<>(
                this::processAlert,
                alertResultWrapper -> removeAlert(alertResultWrapper.getAlertSnapshot().getKey())
            )
        );

        indicatorFlowable.subscribe(new ItemConsumer<>(this::processTask, dataSnapshot -> {
            //handled by the processTask method
        }));

        actionFlowable.subscribe(new ItemConsumer<>(actionItemWrappers -> {
            ArrayList<String> networkRes = new ArrayList<>();
            ArrayList<String> countryRes = new ArrayList<>();

            for (ActionItemWrapper wrapper : actionItemWrappers) {
                ActionModel model = wrapper.makeModel();

                if(model.getParentId().equals(user.countryID)) {
                    countryRes.add(model.getId());
                }
                else {
                    networkRes.add(model.getId());
                }

                processAction(model);
            }

            taskAdapter.updateKeys(countryRes);
            networkTaskAdapter.updateKeys(networkRes);

        }, actionItemWrappers -> {
            //not used
        }));

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

        updateTitle(R.string.green_alert_level, R.drawable.alert_green);
        for (String a : alertAdapter.getAlerts()) {
            AlertModel model = alertAdapter.getModel(a);
            switch (model.getAlertLevel()) {
                case 2:
                    if(model.getRedAlertApproved()) {//means is has approval
                        redPresent = true;
                    }
                    break;
                case 1:
                    amberPresent = true;
                    break;
            }
        }

        if (!redPresent && !amberPresent) {
            updateTitle(R.string.green_alert_level, R.drawable.alert_green);
        }
        else if (redPresent) {
            updateTitle(R.string.red_alert_level, R.drawable.alert_red_main);
        }
        else {
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
    public void onTaskSelected(String id, Task task) {
        if(task.getTaskType().equals("indicator")) {
            Intent intent = new Intent(getActivity(), UpdateIndicatorActivity.class);
            intent.putExtra("hazard_id", task.getParentId());
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

    private void processAlert(AlertResultWrapper alertResult) {
        AlertModel model = AppUtils.getValueFromDataSnapshot(alertResult.getAlertSnapshot(), AlertModel.class);

        assert model != null;

        model.setId(alertResult.getAlertSnapshot().getKey());
        model.setParentKey(alertResult.getAlertSnapshot().getRef().getParent().getKey());

        if (!alertResult.isNetwork()) {
            if (model.getAlertLevel() != 0 && model.getHazardScenario() != null) {
                updateAlert(alertResult.getAlertSnapshot().getKey(), model);
            }
        }
        else {
            model.setLeadAgencyId(alertResult.getNetworkLeadId());
            model.setAgencyAdminId(user.agencyAdminID);
            if (model.getAlertLevel() != 0 && model.getHazardScenario() != null) {
                updateNetworkAlert(model.getId(), model);
            }
        }
    }

    private void processAction(ActionModel model) {
        assert model != null;
        boolean shouldAdd = model.getAsignee() != null && !model.getIsComplete() && model.getAsignee().equals(user.getUserID()) && model.getDueDate() != null;

        if (shouldAdd) {
            if (DateHelper.isDueInWeek(model.getDueDate()) || DateHelper.itWasDue(model.getDueDate())) {
                if(model.getParentId().equals(user.countryID)) {
                    addTask(model.getId(), new Task(model.getParentId(), 0, Task.TASK_ACTION, model.getTask(), model.getDueDate(), model.getRequireDoc(), model.getLevel()));
                }
                else {
                    addNetworkTask(model.getId(), new Task(model.getParentId(), 0, Task.TASK_ACTION, model.getTask(), model.getDueDate(), model.getRequireDoc(), model.getLevel()));
                }
            }
            else {
                taskAdapter.tryRemove(model.getId());
            }
        }
    }

    protected void processTask(DataSnapshot dataSnapshot) {
        if(dataSnapshot == null) return;
        if (dataSnapshot.getRef().getParent().getParent().getKey().equals(Task.TASK_INDICATOR)) {

            IndicatorModel model = dataSnapshot.getValue(IndicatorModel.class);

            assert model != null;
            boolean shouldAdd = model.getAssignee() != null && model.getAssignee().equals(user.getUserID()) && model.getDueDate() != null;

            if (shouldAdd) {
                Task task = new Task(dataSnapshot.getRef().getParent().getKey(), model.getTriggerSelected().intValue(), Task.TASK_INDICATOR, model.getName(), model.getDueDate());
                task.setParentId(dataSnapshot.getRef().getParent().getKey());
                if (DateHelper.isDueInWeek(task.dueDate) || DateHelper.itWasDue(task.dueDate)) {
                    if(dataSnapshot.getRef().getParent().getKey().equals(user.countryID)) {
                        addTask(dataSnapshot.getKey(), task);
                    }
                    else {
                        addNetworkTask(dataSnapshot.getKey(), task);
                    }
                }
                else {
                    taskAdapter.tryRemove(dataSnapshot.getKey());
                }
            }
        }
    }
}
