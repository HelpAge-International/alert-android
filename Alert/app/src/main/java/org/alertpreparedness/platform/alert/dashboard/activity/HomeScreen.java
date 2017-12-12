package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.helper.DataHandler;
import org.alertpreparedness.platform.alert.interfaces.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.interfaces.IHomeActivity;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;


public class HomeScreen extends MainDrawer implements View.OnClickListener, OnAlertItemClickedListener, IHomeActivity, FirebaseAuth.AuthStateListener {
    private static final int STORAGE_RC = 0x0013;
    private RecyclerView myTaskRecyclerView;
    private Toolbar toolbar;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<DataHandler> mHandlerList = new ArrayList<>();

    public TaskAdapter taskAdapter;
    public List<Tasks> tasksList;
    public TextView appBarTitle;
    public AlertAdapter alertAdapter;
    public List<Alert> alertList;
    public RecyclerView alertRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateDrawer(R.layout.activity_home_screen);

        FirebaseAuth.getInstance().addAuthStateListener(this);
        AlertAdapter.updateActivity(this);

        toolbar = (Toolbar) findViewById(R.id.alert_appbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        boolean cdornot = UserInfo.getUser(this).isCountryDirector();
        System.out.println("CD or Not: " + cdornot);

//        appBarTitle = (TextView) findViewById(R.id.custom_bar_title);
//        appBarTitle.setOnClickListener(this);

        alertRecyclerView = (RecyclerView) findViewById(R.id.alert_list_view);
        alertRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager alertlayoutManager = new LinearLayoutManager(getApplicationContext());
        alertRecyclerView.setLayoutManager(alertlayoutManager);
        alertRecyclerView.setItemAnimator(new DefaultItemAnimator());
        alertRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        alertList = new ArrayList<>();
        alertAdapter = new AlertAdapter(alertList);
        alertRecyclerView.setAdapter(alertAdapter);

        myTaskRecyclerView = (RecyclerView) findViewById(R.id.tasks_list_view);
        myTaskRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        myTaskRecyclerView.setLayoutManager(layoutManager);
        myTaskRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myTaskRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        tasksList = new ArrayList<>();
        taskAdapter = new TaskAdapter(tasksList);
        myTaskRecyclerView.setAdapter(taskAdapter);

        DataHandler obj = new DataHandler();
        obj.getAlertsFromFirebase(this, HomeScreen.this);
        obj.getTasksFromFirebase(this, HomeScreen.this);
        mHandlerList.add(obj);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Check for Storage permissions
        int permCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permCheck != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_RC);
            }
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        compositeDisposable.dispose();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        for (DataHandler dataHandler : mHandlerList) {
            dataHandler.detach();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == appBarTitle) {
            startActivity(new Intent(getApplicationContext(), CreateAlertActivity.class));
        }
    }

    @Override
    public void onAlertItemClicked(Alert alert) {
        Intent intent = new Intent(HomeScreen.this, AlertDetailActivity.class);
        intent.putExtra(EXTRA_ALERT, alert);
        startActivity(intent);
    }

    @Override
    public void addAlert(Alert alert) {
        alertAdapter.add(alert);
    }

    @Override
    public void addTask(Tasks tasks) {
        taskAdapter.add(tasks);
    }

    @Override
    public void updateTitle(int stringResource, int backgroundResource) {
//        appBarTitle.setText(stringResource);
//        appBarTitle.setBackgroundResource(backgroundResource);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            compositeDisposable.clear();
            for (DataHandler dataHandler : mHandlerList) {
                dataHandler.detach();
            }
        }
    }

}