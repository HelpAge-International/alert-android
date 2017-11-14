package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Adapter;
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
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class HomeScreen extends  MainDrawer {
    private User user;
    private RecyclerView alertRecyclerView;
    private RecyclerView myTaskRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Tasks> tasksList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();;

    public String countryID;
    public String agencyAdminID;
    public String systemAdminID;

    private String[] usersID;
    private String[] users = {"administratorCountry", "countryDirector", "ert", "ertLeader", "partner"};

    public static final String mypreference = "mypref";
    public static final String userKey = "UserType";
    public static final PreferHelper sharedPreferences = new PreferHelper();
    public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;

    public String dateFormat = "dd/MM/yyyy hh:mm:ss.SSS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateDrawer(R.layout.activity_home_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.alert_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        for(int i = 0; i < users.length; i++) {
            UserInfo.getUserType(this, users[i]);
        }

        countryID = UserInfo.getUser(this).countryID;
        agencyAdminID = UserInfo.getUser(this).agencyAdminID;
        systemAdminID = UserInfo.getUser(this).systemAdminID;

        usersID = new String[]{countryID, agencyAdminID, systemAdminID};

        myTaskRecyclerView = (RecyclerView) findViewById(R.id.tasks_list_view);
        myTaskRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        myTaskRecyclerView.setLayoutManager(layoutManager);
        myTaskRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myTaskRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        tasksList = new ArrayList<>();
        taskAdapter = new TaskAdapter(tasksList);
        firebaseDatabase = FirebaseDatabase.getInstance();

        for(int i = 0; i < usersID.length; i++) {
            getActionsFromFirebase(usersID[i]);
        }
        for(int i = 0; i < usersID.length; i++) {
            getIndicatorsFromFirebase(usersID[i]);
        }
    }

    public String getActionsFromFirebase(String node){

            database.child("sand").child("action").child(node).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String asignee = (String) dataSnapshot.child("asignee").getValue();
                    // System.out.println("Node " + asignee+" = "+ UserInfo.userID);

                    if (asignee != null && asignee.equals(UserInfo.userID)) {
                        //System.out.println("Actions "+asignee);
                        String taskName = (String) dataSnapshot.child("task").getValue();
                        long dueDate = (long) dataSnapshot.child("dueDate").getValue();

                        //System.out.println(taskName);
                        Tasks tasks = new Tasks(taskName, dueDate);

                        tasksList.add(tasks);
                        myTaskRecyclerView.setAdapter(taskAdapter);
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
        return "action";
    }

    public String getIndicatorsFromFirebase(String node){

        database.child("sand").child("indicator").child(node).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("Indicators "+dataSnapshot.getValue());
                String asignee = (String) dataSnapshot.child("assignee").getValue();
                // System.out.println("Node " + asignee+" = "+ UserInfo.userID);

                if (asignee != null && asignee.equals(UserInfo.userID)) {
                    System.out.println("Indicators "+asignee);
                    String taskName = (String) dataSnapshot.child("name").getValue();
                    long dueDate = (long) dataSnapshot.child("dueDate").getValue();

                    //System.out.println(taskName);
                    Tasks tasks = new Tasks(taskName, dueDate);

                    tasksList.add(tasks);
                    myTaskRecyclerView.setAdapter(taskAdapter);
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
        return "indicator";
    }


    public static boolean isDueToday(long milliSeconds){
        System.out.println("Is "+ milliSeconds+" > "+MILLIS_PER_DAY);
        boolean isDueToday = milliSeconds > MILLIS_PER_DAY;
        return isDueToday;
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
