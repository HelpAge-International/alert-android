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

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.model.Alert;

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
                //System.out.println("USER: "+userID);
                System.out.println("DATA: "+dataSnapshot.child("alertLevel").getValue());
               // if(dataSnapshot.child("alertLevel").getValue()!=null) {
                    long alertLevel = dataSnapshot.child("alertLevel").getValue(long.class);
                    long hazardScenario = dataSnapshot.child("hazardScenario").getValue(long.class);
                    long population = dataSnapshot.child("estimatedPopulation").getValue(long.class);

                Alert alert = new Alert(alertLevel, hazardScenario, population);
                alertAdapter.add(alert);
                //}
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
