package org.alertpreparedness.platform.alert.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.helper.DataHandler;
import org.alertpreparedness.platform.alert.interfaces.IHomeActivity;
import org.alertpreparedness.platform.alert.interfaces.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

/**
 * Created by Tj on 13/12/2017.
 */

public class HomeFragment extends Fragment implements IHomeActivity,OnAlertItemClickedListener, FirebaseAuth.AuthStateListener {

    @BindView(R.id.tasks_list_view)
    RecyclerView myTaskRecyclerView;

    @BindView(R.id.alert_list_view)
    RecyclerView alertRecyclerView;

    public TaskAdapter taskAdapter;
    public List<Tasks> tasksList;
    public AlertAdapter alertAdapter;
    public HashMap<String, Alert> alertList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<DataHandler> mHandlerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, v);

        initViews();

        FirebaseAuth.getInstance().addAuthStateListener(this);

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.ALERT, R.string.green_alert_level, R.color.alertGreen);

        return v;
    }

    private void initViews() {
        alertRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager alertlayoutManager = new LinearLayoutManager(getContext());
        alertRecyclerView.setLayoutManager(alertlayoutManager);
        alertRecyclerView.setItemAnimator(new DefaultItemAnimator());
        alertRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        alertList = new HashMap<>();
        alertAdapter = new AlertAdapter(alertList, getContext(), this);
        alertRecyclerView.setAdapter(alertAdapter);

        myTaskRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        myTaskRecyclerView.setLayoutManager(layoutManager);
        myTaskRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myTaskRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        tasksList = new ArrayList<>();
        taskAdapter = new TaskAdapter(tasksList);
        myTaskRecyclerView.setAdapter(taskAdapter);

        DataHandler obj = new DataHandler();
        obj.getAlertsFromFirebase(this, getContext());
        obj.getTasksFromFirebase(this, getContext());
        mHandlerList.add(obj);
    }

    @Override
    public void addTask(Tasks tasks) {
        taskAdapter.add(tasks);
    }

    @Override
    public void removeAlert(String id) {
        alertAdapter.remove(id);

        updateTitle();
    }

    @Override
    public void onAlertItemClicked(Alert alert) {
        Intent intent = new Intent(getActivity(), AlertDetailActivity.class);
        intent.putExtra(EXTRA_ALERT, alert);
        startActivity(intent);
    }

    @Override
    public void updateAlert(String id, Alert alert) {
        alertAdapter.update(id, alert);

        updateTitle();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            for (DataHandler dataHandler : mHandlerList) {
                dataHandler.detach();
            }
        }
    }


    @Override
    public void onDestroy() {
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        for (DataHandler dataHandler : mHandlerList) {
            dataHandler.detach();
        }

        super.onDestroy();
    }


    private void updateTitle() {
        boolean redPresent = false;
        for(Alert a: alertAdapter.getAlerts()){
            if (a.getAlertLevel() == 2){
                redPresent = true;
                break;
            }
        }
        if (redPresent){
            updateTitle(R.string.red_alert_level, R.drawable.alert_red_main);
        }
        else {
            updateTitle(R.string.amber_alert_level, R.drawable.alert_amber_main);
        }
    }

    @Override
    public void updateTitle(int stringResource, int backgroundResource) {
        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.ALERT, stringResource, backgroundResource);
    }
}
