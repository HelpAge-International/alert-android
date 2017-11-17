package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.Tasks;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.risk_monitoring.NetworkService;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import io.reactivex.disposables.Disposable;
import timber.log.Timber;


public class HomeScreen extends MainDrawer implements View.OnClickListener {
    private User user;
    private RecyclerView alertRecyclerView;
    private RecyclerView myTaskRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Tasks> tasksList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private TextView appBarTitle;
    private Toolbar toolbar;
    public static String countryID, agencyAdminID, systemAdminID, networkCountryID;

    private String[] usersID;
    public static final String mypreference = "mypref";
    public static final String userKey = "UserType";
    public static final PreferHelper sharedPreferences = new PreferHelper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateDrawer(R.layout.activity_home_screen);

        toolbar = (Toolbar) findViewById(R.id.alert_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        countryID = UserInfo.getUser(this).countryID;
        agencyAdminID = UserInfo.getUser(this).agencyAdminID;
        systemAdminID = UserInfo.getUser(this).systemAdminID;
        networkCountryID = UserInfo.getUser(this).networkCountryID;

        usersID = new String[]{networkCountryID, countryID, agencyAdminID, systemAdminID};

        appBarTitle = (TextView) findViewById(R.id.alert_bar_title);
        appBarTitle.setOnClickListener(this);

        myTaskRecyclerView = (RecyclerView) findViewById(R.id.tasks_list_view);
        myTaskRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        myTaskRecyclerView.setLayoutManager(layoutManager);
        myTaskRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myTaskRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        tasksList = new ArrayList<>();
        taskAdapter = new TaskAdapter(tasksList);
        firebaseDatabase = FirebaseDatabase.getInstance();

        for (int i = 0; i < usersID.length; i++) {
            getTasksFromFirebase(usersID[i]);
        }
    }

    public void getTasksFromFirebase(String node) {

        String type[] = {"action", "indicator"};

        for (int i = 0; i < type.length; i++) {
            if(type[i].equals("action")) {
                database.child("sand").child("action").child(node).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String asignee = (String) dataSnapshot.child("asignee").getValue();
                        String task = (String) dataSnapshot.child("task").getValue();

                        if (asignee != null && task != null && asignee.equals(UserInfo.userID)) {
                            if(dataSnapshot.hasChild("dueDate")) {
                                long dueDate = (long) dataSnapshot.child("dueDate").getValue();
                                Tasks tasks = new Tasks("red", "action", task, dueDate);

                                tasksList.add(tasks);
                                myTaskRecyclerView.setAdapter(taskAdapter);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
                });
            }else if(type[i].equals("indicator")) {
                database.child("sand").child("indicator").child(node).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String asignee = (String) dataSnapshot.child("assignee").getValue();
                        String taskName = (String) dataSnapshot.child("name").getValue();
                       // long dueDate = (long) dataSnapshot.child("dueDate").getValue();

                        if (asignee != null && taskName != null && asignee.equals(UserInfo.userID)) {
                            if(dataSnapshot.hasChild("dueDate")) {
                                long dueDate = (long) dataSnapshot.child("dueDate").getValue();
                                Tasks tasks = new Tasks("red", "indicator", taskName, dueDate);

                                tasksList.add(tasks);
                                myTaskRecyclerView.setAdapter(taskAdapter);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
                });
            }
        }
    }


    @Override
    public void onClick(View view) {
        if(view==appBarTitle){
            startActivity(new Intent(getApplicationContext(), CreateAlertActivity.class));
        }
    }
}
