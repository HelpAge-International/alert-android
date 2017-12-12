package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertFieldsAdapter;
import org.alertpreparedness.platform.alert.dashboard.model.AlertFieldModel;
import org.alertpreparedness.platform.alert.helper.AlertLevelDialog;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.interfaces.iRedAlertRequest;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.risk_monitoring.view.SelectAreaActivity;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.DBListener;
import org.alertpreparedness.platform.alert.utils.FirebaseHelper;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

public class UpdateAlertActivity extends CreateAlertActivity {

    private Alert alert;
    private DBListener dbListener = new DBListener();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToolbar.setTitle(R.string.update_alert);

        if (alert == null){
            Intent intent = getIntent();
            alert = (Alert) intent.getSerializableExtra(EXTRA_ALERT);
        }

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

    private void fetchDetails() {
        for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
            if(i == alert.getHazardScenario()) {
                mFieldsAdapter.setTextFieldValue(0, Constants.HAZARD_SCENARIO_NAME[i]);
            }
        }

        if(alert.getAlertLevel() == 1){
            mFieldsAdapter.setTextFieldValue(1, R.drawable.amber_dot_26dp, "Amber Alert" );
        }else if (alert.getAlertLevel() == 2){
            mFieldsAdapter.setTextFieldValue(1, R.drawable.red_dot_26dp, "Red Alert" );
        }

        mFieldsAdapter.setTextFieldValue(2, alert.getPopulation()+"");

        for(int i = 0; i < Constants.COUNTRIES.length; i++){
            if(alert.getCountry() == i){
                mFieldsAdapter.addSubListValue(3,Constants.COUNTRIES[i] );
            }
        }

        mFieldsAdapter.setTextFieldValue(4, alert.getInfo());
    }

    private void setUpActionBarColour() {
        Window window = getWindow();
        if(alert.getAlertLevel() == 1){
            mToolbar.setBackgroundResource(R.color.alertAmber);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Amber));
            }

        }else if(alert.getAlertLevel() == 2){
            mToolbar.setBackgroundResource(R.color.alertRed);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Red));
            }
        }
    }

    @Override
    public void saveData(boolean isRedAlert) {

        String hName = mFieldsAdapter.getModel(0).resultTitle;
        String hType = mFieldsAdapter.getModel(1).resultTitle;
        String population = mFieldsAdapter.getModel(2).resultTitle;
        List<String> areas = mFieldsAdapter.getModel(3).strings;
        String info = mFieldsAdapter.getModel(4).resultTitle;

        update(hName, hType, population, areas, info);
    }

    private void update(String hName, String hType, String population, List<String> areas, String info) {
        String countryID = UserInfo.getUser(UpdateAlertActivity.this).countryID;
        String[] usersID = new String[]{countryID};
        String mAppStatus = PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS);
        ValueEventListener valueEventListener;

//        for (String ids : usersID) {
//            String key = database.child(mAppStatus).child("alert").child(ids).push().getKey();
//
//            System.out.println("KEY: " + key);
//        }
        for (String ids : usersID) {
            DatabaseReference db = database.child(mAppStatus).child("alert").child(ids);
            db.addListenerForSingleValueEvent(valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        //dataSnapshot.child()
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            db.addValueEventListener(valueEventListener);
            dbListener.add(db, valueEventListener);
        }

        backToDetailView();
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

        }else{
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
        if (alert == null){
            Intent intent = getIntent();
            alert = (Alert) intent.getSerializableExtra(EXTRA_ALERT);
        }
    }

}
