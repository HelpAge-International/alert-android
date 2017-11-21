package org.alertpreparedness.platform.alert.helper;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faizmohideen on 20/11/2017.
 */

public class DataHandler extends HomeScreen{
    public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    public static void getAlertsFromFirebase(String ids){
        database.child("sand").child("alert").child(ids).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("alertLevel").getValue()!= null) {
                    long alertLevel = (long) dataSnapshot.child("alertLevel").getValue();
                    long numberOfAreas = dataSnapshot.child("affectedAreas").getChildrenCount();

                    if(alertLevel != 0) {
                        long hazardScenario = (long) dataSnapshot.child("hazardScenario").getValue();
                        long population = (long) dataSnapshot.child("estimatedPopulation").getValue();

                        if(hazardScenario != -1) {
                            Alert alert = new Alert(alertLevel, hazardScenario, population, numberOfAreas, null);

                            if(alert.getAlertLevel() == 2){
                                appBarTitle.setText(R.string.red_alert_level);
                                appBarTitle.setBackgroundResource(R.drawable.alert_red);
                            }else if(alert.getAlertLevel() != 2 && alert.getAlertLevel() == 1){
                                appBarTitle.setText(R.string.amber_alert_level);
                                appBarTitle.setBackgroundResource(R.drawable.alert_amber);
                            }

                            alertAdapter.add(alert);
                        }else if (dataSnapshot.child("otherName").exists()){
                            String nameId = (String) dataSnapshot.child("otherName").getValue();
                            setOtherName(nameId, alertLevel, hazardScenario, numberOfAreas, population);
                        }
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

    private static void setOtherName(String nameId, long alertLevel, long hazardScenario, long numOfAreas, long population) {
        database.child("sand").child("hazardOther").child(nameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                Alert alert = new Alert(alertLevel, hazardScenario, population, numOfAreas, name);
                alertAdapter.add(alert);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void getTasksFromFirebase(String node) {

        String types[] = {"action", "indicator"};

        for (String type : types) {
            if(type.equals("action")) {
                database.child("sand").child("action").child(node).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String asignee = (String) dataSnapshot.child("asignee").getValue();
                        String task = (String) dataSnapshot.child("task").getValue();

                        if (asignee != null && task != null && asignee.equals(UserInfo.userID)) {
                            if(dataSnapshot.hasChild("dueDate")) {
                                long dueDate = (long) dataSnapshot.child("dueDate").getValue();
                                Tasks tasks = new Tasks("red", "action", task, dueDate);

                                taskAdapter.add(tasks);
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
            }else if(type.equals("indicator")) {
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
                                //myTaskRecyclerView.setAdapter(taskAdapter);
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

}