package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.firebase.AffectedAreaModel;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicatorLocation;
import org.alertpreparedness.platform.alert.risk_monitoring.view.SelectAreaActivity;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.DBListener;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;

import java.util.ArrayList;
import java.util.List;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

public class UpdateAlertActivity extends CreateAlertActivity  {

    private DBListener dbListener = new DBListener();
    private List<AffectedAreaModel> affectedAreas = new ArrayList<>();
    private String countryID;
    protected int levelNew = -1;
    int hazardNew = -1;

    protected Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ALERT)){
            alert = (Alert) intent.getSerializableExtra(EXTRA_ALERT);
        }

        mToolbar.setTitle(R.string.update_alert);
        countryID = UserInfo.getUser(this).countryID;

        fetchDetails();
        setUpActionBarColour();

    }

    @Override
    public void onItemClicked(int position) {
        switch (position) {
            case 0:
                SnackbarHelper.show(this, getString(R.string.txt_cannot_change_hazard));
                break;
            case 1:
                mAlertLevelFragment.show(getSupportFragmentManager(), "alert_level");
                break;
            case 3:
                startActivityForResult(new Intent(this, SelectAreaActivity.class), EFFECTED_AREA_REQUEST);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EFFECTED_AREA_REQUEST:
                if (resultCode == RESULT_OK) {
                    ModelIndicatorLocation area = data.getParcelableExtra("selected_area");
                    String displayable = data.getStringExtra("selected_area_text");

                    mFieldsAdapter.addSubListValue(
                            3,
                            displayable
                    );
                    addArea(area);

                }
                break;
            case HAZARD_RESULT:
                if (resultCode == RESULT_OK) {
                    hazardNew = data.getIntExtra(HazardSelectionActivity.HAZARD_TYPE, 0);
                }
                break;
        }
    }


    protected void addArea(ModelIndicatorLocation location){
        if (alert == null || location == null) return;

        DatabaseReference db = alertRef.child(alert.getId());

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int level1 = location.getLevel1() == null ? -1 : location.getLevel1();
                    int level2 =location.getLevel2() == null ? -1 : location.getLevel2();
                    AffectedAreaModel affectedArea = new AffectedAreaModel(location.getCountry(),
                            level1, level2);
                    db.child("affectedAreas")
                            .child(String.valueOf(mFieldsAdapter.getSubListCapacity(3)))
                            .setValue(affectedArea)
                            .addOnCompleteListener(aVoid ->{});
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchDetails() {

        if(alert.getOtherName() != null){
            mFieldsAdapter.setTextFieldValue(0, alert.getOtherName());
        }else {

            for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
                if (i == alert.getHazardScenario()) {
                    mFieldsAdapter.setTextFieldValue(0, Constants.HAZARD_SCENARIO_NAME[i]);
                }
            }
        }

        int alertLevel = levelNew == -1 ? (int) alert.getAlertLevel() : levelNew;

        if (alertLevel == 1) {
            mFieldsAdapter.setTextFieldValue(1, R.drawable.amber_dot_26dp, "Amber Alert");
        } else if (alertLevel == 2) {
            mFieldsAdapter.setTextFieldValue(1, R.drawable.red_dot_26dp, "Red Alert");
        }

        mFieldsAdapter.setTextFieldValue(2, alert.getPopulation() + "");

        for (int i = 0; i < Constants.COUNTRIES.length; i++) {
            if (alert.getCountry() == i) {
                mFieldsAdapter.addSubListValue(3, Constants.COUNTRIES[i]);
            }
        }

        mFieldsAdapter.setTextFieldValue(4, alert.getInfo());
    }

    private void setUpActionBarColour() {
        Window window = getWindow();
        if (alert.getAlertLevel() == 1) {
            mToolbar.setBackgroundResource(R.color.alertAmber);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Amber));
            }

        } else if (alert.getAlertLevel() == 2) {
            mToolbar.setBackgroundResource(R.color.alertRed);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Red));
            }
        }
    }

    @Override
    public void saveData(boolean isRedAlert) {

        int alertLevel = levelNew == -1? (int) alert.getAlertLevel() : levelNew;
        long population = Long.parseLong(mFieldsAdapter.getModel(mFieldsAdapter.isRedAlert() ? 3 : 2).resultTitle);
        String info = mFieldsAdapter.getModel(mFieldsAdapter.isRedAlert() ?  5 : 4).resultTitle;

        if(mFieldsAdapter.isRedAlert()) {
            String reason = mFieldsAdapter.getModel(2).resultTitle;
            System.out.println("Level: "+ alertLevel +" Reason: "+ reason+ " Population: "+ population + " Info: "+ info);
            update(alertLevel, reason, population, affectedAreas, info);
        }
        else if (alert.getAlertLevel() == 1){
            update(alertLevel, null, population, affectedAreas, info);
            System.out.println("Level: "+ alertLevel );
        }

    }

    @Override
    public void onTypeSelected(int type) {
        levelNew = -1;
        switch (type) {
            case 1:
                mFieldsAdapter.removeRedReason();
                alert.setAlertLevel(1);
                break;
//            case 2:
//                alert.setRedAlertRequested(2);
//            default:
//                mFieldsAdapter.removeRedReason();
//                alert.setAlertLevel(0);
//                break;
        }
        super.onTypeSelected(type);
    }

    private void update(int alertLevel, String reason, long population, List<AffectedAreaModel> areas, String info) {
        String countryID = UserInfo.getUser(UpdateAlertActivity.this).countryID;
        String mAppStatus = PreferHelper.getString(getApplicationContext(), Constants.APP_STATUS);
        DatabaseReference db = database.child(mAppStatus).child("alert").child(countryID)
                .child(alert.getId());

           db.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {

                   db.child("alertLevel").setValue(alertLevel).addOnCompleteListener(task -> backToDetailView());

                   for (int i = 0; i < areas.size(); i++) {
                       db.child("affectedAreas").child(String.valueOf(i))
                               .setValue(areas.get(i));
                   }

                   if (reason != null) {
                       alert.setAlertLevel(1);
                       db.child("reasonForRedAlert").setValue(reason);
                       db.child("approval").child("countryDirector").child(countryID).setValue(Constants.REQ_PENDING);
                   }else{
                       db.child("approval").child("countryDirector").child(countryID).setValue(Constants.REQ_REJECTED);
                   }

                   long time = System.currentTimeMillis();
                   db.child("timeUpdated").setValue(time);

                   db.child("infoNotes").setValue(info);
                   db.child("estimatedPopulation").setValue(population);

//        db.child("affectedAreas").removeValue((databaseError, databaseReference) -> {
//            for (int i = 0; i < areas.size(); i++) {
//                db.child("affectedAreas").child(String.valueOf(i))
//                        .setValue(areas.get(i));
//            }
//        });
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
    }


    public void backToDetailView() {

        Intent intent = new Intent(UpdateAlertActivity.this, AlertDetailActivity.class);
        intent.putExtra(EXTRA_ALERT, alert);

        if (mFieldsAdapter.isRedAlert()) {
            if (mFieldsAdapter.getModel(2).resultTitle != null) {
                intent.putExtra("IS_RED_REQUEST", "true");
                startActivity(intent);
            } else {
                SnackbarHelper.show(this, getString(R.string.txt_reason_for_red));
            }
        } else {
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        dbListener.detatch();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (alert == null) {
            Intent intent = getIntent();
            alert = (Alert) intent.getSerializableExtra(EXTRA_ALERT);
        }
    }

}
