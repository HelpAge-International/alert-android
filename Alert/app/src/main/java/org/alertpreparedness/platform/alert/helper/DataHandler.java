package org.alertpreparedness.platform.alert.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.BaseActivity;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.interfaces.IHomeActivity;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.DBListener;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by faizmohideen on 20/11/2017.
 */

public class DataHandler {
    public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    static List<Integer> alerts = new ArrayList<Integer>();
    private DBListener dbListener = new DBListener();
    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;
    public static String mAppStatus = PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS);
    private static Calendar date = Calendar.getInstance();
    public static String dateFormat = "dd/MM/yyyy";
    private static SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());


    public void getAlertsFromFirebase(IHomeActivity iHome, String ids) {
        database.child(mAppStatus).child("alert").child(ids)
                .addChildEventListener(childEventListener =  new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.child("alertLevel").getValue() != null) {
                            long alertLevel = (long) dataSnapshot.child("alertLevel").getValue();
                            long numberOfAreas = dataSnapshot.child("affectedAreas").getChildrenCount();

                            if (alertLevel != 0) {
                                long hazardScenario = (long) dataSnapshot.child("hazardScenario").getValue();
                                long population = (long) dataSnapshot.child("estimatedPopulation").getValue();

                                if(dataSnapshot.child("timeUpdated").exists()) {
                                    long updated = (long) dataSnapshot.child("timeUpdated").getValue();
                                    date.setTimeInMillis(updated);
                                    String updatedDay = format.format(date.getTime());

                                    if (hazardScenario != -1) {
                                        Alert alert = new Alert(alertLevel, hazardScenario, population, numberOfAreas, updatedDay, null);

                                        iHome.updateTitle(R.string.amber_alert_level,R.drawable.alert_amber_main);

                                        alerts.add((int) alertLevel);
                                        setRedActionBar(iHome, alerts.contains(2));

                                        iHome.addAlert(alert);
                                    } else if (dataSnapshot.child("otherName").exists()) {
                                        String nameId = (String) dataSnapshot.child("otherName").getValue();
                                        setOtherName(iHome ,nameId, alertLevel, hazardScenario, numberOfAreas, population, updatedDay);
                                    }
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

    private void setOtherName(IHomeActivity iHome, String nameId, long alertLevel, long hazardScenario, long numOfAreas, long population, String updatedDay) {
        database.child(mAppStatus).child("hazardOther").child(nameId).addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                Alert alert = new Alert(alertLevel, hazardScenario, population, numOfAreas, updatedDay, name);
                iHome.addAlert(alert);

                // dbListener.add(database, (ValueEventListener) dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dbListener.add(database, valueEventListener);

    }

    public void detach(){
        dbListener.detatch();
    }

    public void getTasksFromFirebase(IHomeActivity iHome, String node) {

        String types[] = {"action", "indicator"};

        for (String type : types) {
            if (type.equals("action")) {
                database.child(mAppStatus).child("action").child(node).addChildEventListener(childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String asignee = (String) dataSnapshot.child("asignee").getValue();
                        String task = (String) dataSnapshot.child("task").getValue();
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if (asignee != null && task != null && asignee.equals(uid)) {
                            if (dataSnapshot.hasChild("dueDate")) {
                                long dueDate = (long) dataSnapshot.child("dueDate").getValue();
                                Tasks tasks = new Tasks("red", "action", task, dueDate);
                                iHome.addTask(tasks);
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
                database.child(mAppStatus).child("indicator").child(node).addChildEventListener(childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String asignee = (String) dataSnapshot.child("assignee").getValue();
                        String taskName = (String) dataSnapshot.child("name").getValue();
                        // long dueDate = (long) dataSnapshot.child("dueDate").getValue();

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if (asignee != null && taskName != null && asignee.equals(uid)) {
                            if (dataSnapshot.hasChild("dueDate")) {
                                long dueDate = (long) dataSnapshot.child("dueDate").getValue();
                                Tasks tasks = new Tasks("red", "indicator", taskName, dueDate);

                                iHome.addTask(tasks);
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

    public static void setRedActionBar(IHomeActivity iHome, boolean isRedExists){
        if(isRedExists){
            iHome.updateTitle(R.string.red_alert_level, R.drawable.alert_red_main);
        }
    }
}
