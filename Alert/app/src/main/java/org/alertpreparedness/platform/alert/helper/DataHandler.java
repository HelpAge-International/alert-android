package org.alertpreparedness.platform.alert.helper;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.interfaces.AuthCallback;
import org.alertpreparedness.platform.alert.interfaces.IHomeActivity;
import org.alertpreparedness.platform.alert.dashboard.model.Alert;
import org.alertpreparedness.platform.alert.dashboard.model.Tasks;
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
    private List<Integer> alerts = new ArrayList<Integer>();
    private DBListener dbListener = new DBListener();
    private String mAppStatus = PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS);
    private Calendar date = Calendar.getInstance();
    private String dateFormat = "dd/MM/yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
    private String countryID, agencyAdminID, systemAdminID, networkCountryID;
    private String[] usersID;
    private Alert alert = new Alert();

    public DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public void getAlertsFromFirebase(IHomeActivity iHome, Context context) {
        countryID = UserInfo.getUser(context).countryID;
        networkCountryID = UserInfo.getUser(context).networkCountryID;
        usersID = new String[]{countryID};

        for (String ids : usersID) {
            DatabaseReference db = database.child(mAppStatus).child("alert").child(ids);
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    getAlert(dataSnapshot, ids);
                    //iHome.updateTitle(R.string.amber_alert_level, R.drawable.alert_amber_main);   // alerts.add((int) alertLevel);
                    //setRedActionBar(iHome, alerts.contains(2));
                }

                private void getAlert(DataSnapshot dataSnapshot, String ids) {
                    if (dataSnapshot.child("alertLevel").getValue() != null) {
                        long alertLevel = (long) dataSnapshot.child("alertLevel").getValue();
                        String id = dataSnapshot.getKey();

                        if (alertLevel != 0) {
                            long numberOfAreas = dataSnapshot.child("affectedAreas").getChildrenCount();
                            Log.e("f",id+" "+numberOfAreas);
                            long country = (long) dataSnapshot.child("affectedAreas").getChildren().iterator().next().child("country").getValue();
                            long hazardScenario = (long) dataSnapshot.child("hazardScenario").getValue();
                            long population = (long) dataSnapshot.child("estimatedPopulation").getValue();
                            long redStatus = (long) dataSnapshot.child("approval").child("countryDirector").child(ids).getValue();
                            String info = (String) dataSnapshot.child("infoNotes").getValue();

                            if (dataSnapshot.child("timeUpdated").exists()) {
                                long updated = (long) dataSnapshot.child("timeUpdated").getValue();
                                String updatedBy = (String) dataSnapshot.child("updatedBy").getValue();
                                date.setTimeInMillis(updated);
                                String updatedDay = format.format(date.getTime());

                                if (hazardScenario != -1) {
                                    Alert alert = new Alert(alertLevel, hazardScenario, population,
                                            numberOfAreas, redStatus, info, updatedDay, updatedBy, null);
                                    alert.setId(id);

                                    iHome.updateAlert(dataSnapshot.getKey(), alert);
                                } else if (dataSnapshot.child("otherName").exists()) {
                                    String nameId = (String) dataSnapshot.child("otherName").getValue();
                                    long level1 = alert.getLevel1();
                                    long level2 = alert.getLevel2();
                                    setOtherName(iHome, nameId, alertLevel, hazardScenario, numberOfAreas,
                                            redStatus, population, country, level1, level2, info, updatedDay, updatedBy);
                                }

                            } else if (dataSnapshot.child("timeCreated").exists()) {
                                long updated = (long) dataSnapshot.child("timeCreated").getValue();
                                date.setTimeInMillis(updated);
                                String updatedDay = format.format(date.getTime());

                                if (hazardScenario != -1) {
                                    Alert alert = new Alert(alertLevel, hazardScenario, population, numberOfAreas,
                                            redStatus, info, updatedDay, null, null);
                                    alert.setId(id);

                                    iHome.updateAlert(dataSnapshot.getKey(), alert);
                                } else if (dataSnapshot.child("otherName").exists()) {
                                    String nameId = (String) dataSnapshot.child("otherName").getValue();
                                    long level1 = alert.getLevel1();
                                    long level2 = alert.getLevel2();

                                    setOtherName(iHome, nameId, alertLevel, hazardScenario, numberOfAreas,
                                            redStatus, population, country, level1, level2, info, updatedDay, null);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.e("CHANGED", dataSnapshot.getKey());
                    getAlert(dataSnapshot, ids);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    iHome.removeAlert(dataSnapshot.getKey());
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            db.addChildEventListener(childEventListener);
            dbListener.add(db, childEventListener);

        }
    }

    private void setOtherName(IHomeActivity iHome, String nameId, long alertLevel, long hazardScenario, long numOfAreas,
                              long redStatus, long population, long country, long level1, long level2, String info, String updatedDay, String updatedBy) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //TODO signle event listener
                String name = (String) dataSnapshot.child("name").getValue();
                Alert alert = new Alert(alertLevel, hazardScenario, population, numOfAreas, redStatus, info, updatedDay, updatedBy, name);
                Alert alert1 = new Alert(country, level1, level2);
                alert.setId(dataSnapshot.getKey());
                iHome.updateAlert(dataSnapshot.getKey(), alert);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        DatabaseReference ref = database.child(mAppStatus).child("hazardOther").child(nameId);
        ref.addValueEventListener(valueEventListener);
        dbListener.add(ref, valueEventListener);

    }

    public void detach() {
        dbListener.detatch();
    }

    public void getTasksFromFirebase(IHomeActivity iHome, Context context) {
        countryID = UserInfo.getUser(context).countryID;
        agencyAdminID = UserInfo.getUser(context).agencyAdminID;
        usersID = new String[]{countryID};
        String types[] = {"action", "indicator"};

        for (String ids : usersID) {
            for (String type : types) {
                if (type.equals("action")) {
                    ChildEventListener childEventListener;
                    DatabaseReference db = database.child(mAppStatus).child("action").child(ids);
                    db.addChildEventListener(childEventListener = new ChildEventListener() {

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
                    dbListener.add(db, childEventListener);
                } else if (type.equals("indicator")) {
                    ChildEventListener childEventListener;
                    DatabaseReference db = database.child(mAppStatus).child("indicator").child(ids);
                    db.addChildEventListener(childEventListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            String asignee = (String) dataSnapshot.child("assignee").getValue();
                            String taskName = (String) dataSnapshot.child("name").getValue();

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            if (asignee != null && taskName != null && asignee.equals(uid)) {
                                if (dataSnapshot.hasChild("dueDate")) {
                                    long dueDate = (long) dataSnapshot.child("dueDate").getValue();
                                    Tasks tasks = new Tasks("red", "indicator", taskName, dueDate);

                                    iHome.addTask(tasks);
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
                    dbListener.add(db, childEventListener);
                }
            }
        }
    }
}
