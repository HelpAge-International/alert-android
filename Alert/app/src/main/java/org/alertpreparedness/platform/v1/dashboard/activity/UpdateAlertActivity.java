package org.alertpreparedness.platform.v1.dashboard.activity;

import static org.alertpreparedness.platform.v1.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.v1.firebase.AffectedAreaModel;
import org.alertpreparedness.platform.v1.firebase.AlertModel;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelIndicatorLocation;
import org.alertpreparedness.platform.v1.risk_monitoring.view.SelectAreaActivity;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.DBListener;
import org.alertpreparedness.platform.v1.utils.SnackbarHelper;

public class UpdateAlertActivity extends CreateAlertActivity  {

    private DBListener dbListener = new DBListener();
    private List<AffectedAreaModel> affectedAreas = new ArrayList<>();
    private String countryID;
    protected int levelNew = -1;
    int hazardNew = -1;

    protected AlertModel alert;

    @Inject @AlertRef
    DatabaseReference alertRef;

    @Inject @BaseAlertRef
    DatabaseReference baseAlertRef;

    @Inject
    User user;
    private int mOrigionalLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ALERT)){
            alert = (AlertModel) intent.getSerializableExtra(EXTRA_ALERT);
            mAlertLevel = alert.getAlertLevel();
            mOrigionalLevel = mAlertLevel;
            affectedAreas = alert.getAffectedAreas();
        }

        DependencyInjector.userScopeComponent().inject(this);

        mToolbar.setTitle(R.string.update_alert);
        countryID = user.countryID;


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
                if(alert.getAlertLevel() != Constants.TRIGGER_RED) {
                    startActivityForResult(new Intent(this, SelectAreaActivity.class), EFFECTED_AREA_REQUEST);
                }
                break;
            case 4:
                if(alert.getAlertLevel() == Constants.TRIGGER_RED) {
                    startActivityForResult(new Intent(this, SelectAreaActivity.class), EFFECTED_AREA_REQUEST);
                }
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

        int level1 = location.getLevel1() == null ? -1 : location.getLevel1();
        int level2 =location.getLevel2() == null ? -1 : location.getLevel2();
        AffectedAreaModel affectedArea = new AffectedAreaModel(location.getCountry(),
                level1, level2);
        affectedAreas.add(affectedArea);

    }

    private void fetchDetails() {
        if(alert.getOtherName() != null){
            mFieldsAdapter.setTextFieldValue(0, alert.getOtherName());
        }
        else {
            for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
                if (i == alert.getHazardScenario()) {
                    mFieldsAdapter.setTextFieldValue(0, Constants.HAZARD_SCENARIO_NAME[i]);
                }
            }
        }

        int alertLevel = levelNew == -1 ? alert.getAlertLevel() : levelNew;

        if (alertLevel == 1) {
            mFieldsAdapter.setTextFieldValue(1, R.drawable.amber_dot_26dp, "Amber Alert");
        } else if (alertLevel == 2) {
            mFieldsAdapter.setTextFieldValue(1, R.drawable.red_dot_26dp, "Red Alert");
        }

        mFieldsAdapter.setTextFieldValue(2, alert.getEstimatedPopulation() + "");

        for (AffectedAreaModel m : alert.getAffectedAreas()) {
            if(m != null && m.getCountry() != null) {
                String res = Constants.COUNTRIES[m.getCountry()];
                if (m.getLevel1Name() != null) {
                    res += ", " + m.getLevel1Name();
                    if(m.getLevel2Name() != null) {
                        res += ", " + m.getLevel2Name();
                    }
                }
                mFieldsAdapter.addSubListValue(3, res);
            }
        }
        mFieldsAdapter.setTextFieldValue(4, alert.getInfoNotes());
        if(!alert.getRedAlertApproved() && alertLevel == Constants.TRIGGER_RED) {
            mFieldsAdapter.addRedAlertReason(alert.getReasonForRedAlert());
        }
    }

    private void setUpActionBarColour() {
        Window window = getWindow();
        if (alert.getAlertLevel() == 1) {
            mToolbar.setBackgroundResource(R.color.alertAmber);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Amber));
            }

        }
        else if (alert.getAlertLevel() == 2) {
            mToolbar.setBackgroundResource(R.color.alertRed);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Red));
            }
        }
    }

    @Override
    public void saveData(boolean isRedAlert) {
        long population = Long.parseLong(mFieldsAdapter.getModel(mFieldsAdapter.isRedAlert() ? 3 : 2).resultTitle);
        String info = mFieldsAdapter.getModel(mFieldsAdapter.isRedAlert() ?  5 : 4).resultTitle;

        if(isRedAlert) {
            String reason = mFieldsAdapter.getModel(2).resultTitle;
            update(mAlertLevel, reason, population, info);
        }
        else {
            update(mAlertLevel, null, population, info);
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
        }
        super.onTypeSelected(type);
    }

    @Override
    public void onSubItemRemoved(int positionInParent, int position) {
        if(positionInParent == getIndex(mFieldsAdapter.isRedAlert(), 3)) {//affected areas
            DatabaseReference db = alertRef.child(alert.getId()).child("affectedAreas");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<AffectedAreaModel> areas = (ArrayList<AffectedAreaModel>) dataSnapshot.getValue();
                    if (areas != null) {
                        areas.remove(position);
                        db.setValue(areas);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void update(int alertLevel, String reason, long population, String info) {

        DatabaseReference alertRef = baseAlertRef.child(alert.getParentKey()).child(alert.getId());

        alert.setAffectedAreas(affectedAreas);
        if(reason != null) {
            alert.setReasonForRedAlert(reason);
        }
        alert.setTimeUpdated(new Date().getTime());
        alert.setInfoNotes(info);
        alert.setEstimatedPopulation(population);
        //TODO: get timeTracking null
        alert.getTimeTracking().updateAlertTimeTracking(mOrigionalLevel, alertLevel);
        alert.setAlertLevel(alertLevel);
        alertRef.setValue(alert);
        finishAffinity();
        startActivity(new Intent(UpdateAlertActivity.this, HomeScreen.class));
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
            alert = (AlertModel) intent.getSerializableExtra(EXTRA_ALERT);
        }
    }
}
