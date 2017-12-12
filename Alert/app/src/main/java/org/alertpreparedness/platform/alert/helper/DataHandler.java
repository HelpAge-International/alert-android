package org.alertpreparedness.platform.alert.helper;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.interfaces.IHomeActivity;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;
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
        //networkCountryID = UserInfo.getUser(context).networkCountryID;
        usersID = new String[]{countryID};

        for (String ids : usersID) {
            DatabaseReference db = database.child(mAppStatus).child("alert").child(ids);
            ChildEventListener childEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.child("alertLevel").getValue() != null) {
                        long alertLevel = (long) dataSnapshot.child("alertLevel").getValue();
                        Alert a = new Alert(db);

                        if (alertLevel != 0) {
                            long numberOfAreas = dataSnapshot.child("affectedAreas").getChildrenCount();
                            long country = (long) dataSnapshot.child("affectedAreas").getChildren().iterator().next().child("country").getValue();
                            long hazardScenario = (long) dataSnapshot.child("hazardScenario").getValue();
                            long population = (long) dataSnapshot.child("estimatedPopulation").getValue();
                            String info = (String) dataSnapshot.child("infoNotes").getValue();

                            if (dataSnapshot.child("affectedAreas").getChildren().iterator().next().child("level1").getValue() != null) {
                                if (country >= 0) {
                                    long level1 = (long) dataSnapshot.child("affectedAreas").getChildren().iterator().next().child("level1").getValue();

                                    if (level1 != -1) {
                                        long level2 = (long) dataSnapshot.child("affectedAreas").getChildren().iterator().next().child("level2").getValue();
                                        Alert alert = new Alert(country, level1, level2);
                                    }
                                }
                            } else {
                                Alert alert = new Alert(country);
                            }

                            if (dataSnapshot.child("timeUpdated").exists()) {
                                long updated = (long) dataSnapshot.child("timeUpdated").getValue();
                                date.setTimeInMillis(updated);
                                String updatedDay = format.format(date.getTime());

                                if (hazardScenario != -1) {
                                    Alert alert = new Alert(alertLevel, hazardScenario, population, numberOfAreas, info, updatedDay, null);

                                    iHome.updateTitle(R.string.amber_alert_level, R.drawable.alert_amber_main);

                                    alerts.add((int) alertLevel);
                                    setRedActionBar(iHome, alerts.contains(2));

                                    iHome.addAlert(alert);
                                } else if (dataSnapshot.child("otherName").exists()) {
                                    String nameId = (String) dataSnapshot.child("otherName").getValue();
                                    long level1 = alert.getLevel1();
                                    long level2 = alert.getLevel2();
                                    System.out.println("L1: " + level1 + " L2: " + level2 + " Country: " + country);
                                    setOtherName(iHome, nameId, alertLevel, hazardScenario, numberOfAreas, population, country, level1, level2, info, updatedDay);
                                }

                            } else if (dataSnapshot.child("timeCreated").exists()) {
                                long updated = (long) dataSnapshot.child("timeCreated").getValue();
                                date.setTimeInMillis(updated);
                                String updatedDay = format.format(date.getTime());

                                if (hazardScenario != -1) {
                                    Alert alert = new Alert(alertLevel, hazardScenario, population, numberOfAreas, info, updatedDay, null);

                                    iHome.updateTitle(R.string.amber_alert_level, R.drawable.alert_amber_main);

                                    alerts.add((int) alertLevel);
                                    setRedActionBar(iHome, alerts.contains(2));

                                    iHome.addAlert(alert);
                                } else if (dataSnapshot.child("otherName").exists()) {
                                    String nameId = (String) dataSnapshot.child("otherName").getValue();
                                    long level1 = alert.getLevel1();
                                    long level2 = alert.getLevel2();

                                    setOtherName(iHome, nameId, alertLevel, hazardScenario, numberOfAreas, population, country, level1, level2, info, updatedDay);
                                }
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
            };
            db.addChildEventListener(childEventListener);
            dbListener.add(db, childEventListener);

        }
    }

    private void setOtherName(IHomeActivity iHome, String nameId, long alertLevel, long hazardScenario, long numOfAreas, long population, long country, long level1, long level2, String info, String updatedDay) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                Alert alert = new Alert(alertLevel, hazardScenario, population, numOfAreas, updatedDay, info, name);
                Alert alert1 = new Alert(country, level1, level2);
                iHome.addAlert(alert);
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

    private void setRedActionBar(IHomeActivity iHome, boolean isRedExists) {
        if (isRedExists) {
            iHome.updateTitle(R.string.red_alert_level, R.drawable.alert_red_main);
        }
    }


}
