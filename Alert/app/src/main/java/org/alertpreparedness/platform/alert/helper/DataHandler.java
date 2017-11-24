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
import org.alertpreparedness.platform.alert.utils.DBListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by faizmohideen on 20/11/2017.
 */

public class DataHandler extends HomeScreen {
    public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    static List<Integer> alerts = new ArrayList<Integer>();
    private static DBListener dbListener = new DBListener();
    private static ChildEventListener childEventListener;
    private static ValueEventListener valueEventListener;

    public static void getAlertsFromFirebase(String ids) {
        database.child("sand").child("alert").child(ids).addChildEventListener(childEventListener =  new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("alertLevel").getValue() != null) {
                    long alertLevel = (long) dataSnapshot.child("alertLevel").getValue();
                    long numberOfAreas = dataSnapshot.child("affectedAreas").getChildrenCount();

                    if (alertLevel != 0) {
                        long hazardScenario = (long) dataSnapshot.child("hazardScenario").getValue();
                        long population = (long) dataSnapshot.child("estimatedPopulation").getValue();

                        if (hazardScenario != -1) {
                            Alert alert = new Alert(alertLevel, hazardScenario, population, numberOfAreas, null);

                            appBarTitle.setText(R.string.amber_alert_level);
                            appBarTitle.setBackgroundResource(R.drawable.alert_amber);

                            alerts.add((int) alertLevel);
                            setRedActionBar(alerts.contains(2));

                            alertAdapter.add(alert);
                        } else if (dataSnapshot.child("otherName").exists()) {
                            String nameId = (String) dataSnapshot.child("otherName").getValue();
                            setOtherName(nameId, alertLevel, hazardScenario, numberOfAreas, population);
                        }
                    }
                }
                //dbListener.add(database, (ChildEventListener) dataSnapshot);
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

        dbListener.add(database, childEventListener);

    }

    private static void setOtherName(String nameId, long alertLevel, long hazardScenario, long numOfAreas, long population) {
        database.child("sand").child("hazardOther").child(nameId).addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                Alert alert = new Alert(alertLevel, hazardScenario, population, numOfAreas, name);
                alertAdapter.add(alert);

               // dbListener.add(database, (ValueEventListener) dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dbListener.add(database, valueEventListener);

    }

    public static void getTasksFromFirebase(String node) {

        String types[] = {"action", "indicator"};

        for (String type : types) {
            if (type.equals("action")) {
                database.child("sand").child("action").child(node).addChildEventListener(childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String asignee = (String) dataSnapshot.child("asignee").getValue();
                        String task = (String) dataSnapshot.child("task").getValue();

                        if (asignee != null && task != null && asignee.equals(UserInfo.userID)) {
                            if (dataSnapshot.hasChild("dueDate")) {
                                long dueDate = (long) dataSnapshot.child("dueDate").getValue();
                                Tasks tasks = new Tasks("red", "action", task, dueDate);

                                taskAdapter.add(tasks);
                            }
                        }

                      //  dbListener.add(database, (ChildEventListener) dataSnapshot);
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
                dbListener.add(database, childEventListener);
            } else if (type.equals("indicator")) {
                database.child("sand").child("indicator").child(node).addChildEventListener(childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String asignee = (String) dataSnapshot.child("assignee").getValue();
                        String taskName = (String) dataSnapshot.child("name").getValue();
                        // long dueDate = (long) dataSnapshot.child("dueDate").getValue();

                        if (asignee != null && taskName != null && asignee.equals(UserInfo.userID)) {
                            if (dataSnapshot.hasChild("dueDate")) {
                                long dueDate = (long) dataSnapshot.child("dueDate").getValue();
                                Tasks tasks = new Tasks("red", "indicator", taskName, dueDate);

                                tasksList.add(tasks);
                                //myTaskRecyclerView.setAdapter(taskAdapter);
                            }
                        }

                      //  dbListener.add(database, (ChildEventListener) dataSnapshot);
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
                dbListener.add(database, childEventListener);
            }
        }
    }

    public static void setRedActionBar(boolean isRedExists){
        if(isRedExists){
            appBarTitle.setText(R.string.red_alert_level);
            appBarTitle.setBackgroundResource(R.drawable.alert_red);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dbListener.detatch();

    }
}
