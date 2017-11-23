package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.helper.DataHandler;
import org.alertpreparedness.platform.alert.helper.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.ArrayList;
import java.util.List;


public class HomeScreen extends MainDrawer implements View.OnClickListener, OnAlertItemClickedListener {
    private User user;
    private RecyclerView myTaskRecyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private Toolbar toolbar;
    private String[] usersID;
    private Alert alert;

    public static TaskAdapter taskAdapter;
    public static List<Tasks> tasksList;
    public static TextView appBarTitle;
    public static AlertAdapter alertAdapter;
    public static List<Alert> alertList;
    public static RecyclerView alertRecyclerView;
    public static String countryID, agencyAdminID, systemAdminID, networkCountryID;

    public static final String mypreference = "mypref";
    public static final String userKey = "UserType";
    public static final PreferHelper sharedPreferences = new PreferHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateDrawer(R.layout.activity_home_screen);
        AlertAdapter.updateActivity(this);

        toolbar = (Toolbar) findViewById(R.id.alert_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        countryID = UserInfo.getUser(this).countryID;
        agencyAdminID = UserInfo.getUser(this).agencyAdminID;
        systemAdminID = UserInfo.getUser(this).systemAdminID;
        networkCountryID = UserInfo.getUser(this).networkCountryID;

        usersID = new String[]{networkCountryID, countryID};

        appBarTitle = (TextView) findViewById(R.id.custom_bar_title);
        appBarTitle.setOnClickListener(this);

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

        firebaseDatabase = FirebaseDatabase.getInstance();

        for (String ids : usersID) {
            DataHandler.getAlertsFromFirebase(ids);
        }

        for (String ids : usersID) {
            DataHandler.getTasksFromFirebase(ids);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == appBarTitle) {
            startActivity(new Intent(getApplicationContext(), CreateAlertActivity.class));
        }
    }

    @Override
    public void onAlertItemClicked(int position) {
        Intent intent = new Intent(HomeScreen.this, AlertDetailActivity.class);
        intent.putExtra("ITEM_ID", position);
        startActivity(intent);
    }
}