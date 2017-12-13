package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertFieldsAdapter;
import org.alertpreparedness.platform.alert.dashboard.model.AlertFieldModel;
import org.alertpreparedness.platform.alert.helper.AlertLevelDialog;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicatorLocation;
import org.alertpreparedness.platform.alert.risk_monitoring.view.SelectAreaActivity;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.alertpreparedness.platform.alert.utils.SimpleAdapter;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

public class CreateAlertActivity extends AppCompatActivity implements AlertFieldsAdapter.ClickListener, AlertLevelDialog.TypeSelectedListener {

    public static final int EFFECTED_AREA_REQUEST = 9002;
    public static final int HAZARD_RESULT = 9003;

    protected DatabaseReference database = FirebaseDatabase.getInstance().getReference();

//    private ArrayList<>

    @BindView(R.id.btnSaveChanges)
    Button saveButton;

    @BindView(R.id.rvFields)
    RecyclerView fields;

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    public AlertLevelDialog mAlertLevelFragment;
    public AlertFieldsAdapter mFieldsAdapter;

    protected Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ALERT)){
            alert = (Alert) intent.getSerializableExtra(EXTRA_ALERT);
        }

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<AlertFieldModel> list = new ArrayList<>();
        list.add(new AlertFieldModel(AlertFieldsAdapter.TEXT_FIELD, R.drawable.alert_hazard, R.string.select_hazard));
        list.add(new AlertFieldModel(AlertFieldsAdapter.TEXT_FIELD, R.drawable.alert_base, R.string.alert_level));
        list.add(new AlertFieldModel(AlertFieldsAdapter.EDIT_TEXT, R.drawable.alert_population, R.string.estimated_peeps, InputType.TYPE_CLASS_NUMBER));
        list.add(new AlertFieldModel(AlertFieldsAdapter.RECYCLER, R.drawable.alert_areas, R.string.effected_area, null));
        list.add(new AlertFieldModel(AlertFieldsAdapter.EDIT_TEXT, R.drawable.alert_information, R.string.info_sources));

        mFieldsAdapter = new AlertFieldsAdapter(this, list, this);
        fields.setAdapter(mFieldsAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        fields.setLayoutManager(mLayoutManager);
        fields.setNestedScrollingEnabled(false);

        mAlertLevelFragment = new AlertLevelDialog();
        mAlertLevelFragment.setListener(this);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onItemClicked(int position) {
//        System.out.println("position = [" + position + "]");
        switch (position) {
            case 0:
                startActivityForResult(new Intent(this, HazardSelectionActivity.class), HAZARD_RESULT);
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
    public void onSubItemRemoved(int positionInParent, int position) {
        if(positionInParent == 3) {//affected areas

        }
    }

    @CallSuper
    @OnClick(R.id.btnSaveChanges)
    public void onSaveClicked(View v) {

        if(!hasErrors(mFieldsAdapter.isRedAlert())) {
            saveData(mFieldsAdapter.isRedAlert());
        }

    }

    protected void saveData(boolean isRedAlert) {
//        mFieldsAdapter.getModel(2).resultTitle);
//        mFieldsAdapter.getModel((isRedAlert ? 4 : 3)).strings;
    }

    protected boolean hasErrors(boolean isRedAlert) {
        boolean hasError = false;

        if(mFieldsAdapter.getModel(0).resultTitle == null) {
            SnackbarHelper.show(this, getString(R.string.specify_hazar));
            hasError = true;
        }
        else if(mFieldsAdapter.getModel(1).resultTitle == null) {
            SnackbarHelper.show(this, getString(R.string.specify_alert_level));
            hasError = true;
        }
        else if(mFieldsAdapter.getModel(2).resultTitle == null) {
            SnackbarHelper.show(this, getString(R.string.specify_peeps));
            hasError = true;
        }
        else if(isRedAlert && mFieldsAdapter.getModel(3).resultTitle == null) {
            SnackbarHelper.show(this, getString(R.string.red_alert_specify));
            hasError = true;
        }
        else {
            if(mFieldsAdapter.getModel(getIndex(isRedAlert, 3)).strings == null || mFieldsAdapter.getModel(getIndex(isRedAlert, 3)).strings.size() == 0) {
                SnackbarHelper.show(this, getString(R.string.specify_areas));
                hasError = true;
            }
            else if(mFieldsAdapter.getModel(getIndex(isRedAlert, 3)).resultTitle == null) {
                SnackbarHelper.show(this, getString(R.string.specify_info));
                hasError = true;
            }
        }
        return hasError;
    }

    protected static int getIndex(boolean isRedAlert, int base) {
        return (isRedAlert ? base + 1 : base);
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
                    String hazardType = data.getStringExtra(HazardSelectionActivity.HAZARD_TYPE);
                    mFieldsAdapter.setTextFieldValue(0, hazardType);
                }
                break;
        }
    }

    protected void addArea(ModelIndicatorLocation location){
        if (alert == null || location == null) return;;

        String countryID = UserInfo.getUser(CreateAlertActivity.this).countryID;

        String mAppStatus = PreferHelper.getString(getApplicationContext(), Constants.APP_STATUS);

        DatabaseReference db = database.child(mAppStatus).child("alert").child(countryID)
                .child(alert.getId());

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int level1 = location.getLevel1() == null ? -1 : location.getLevel1();
                    int level2 =location.getLevel2() == null ? -1 : location.getLevel2();
                    AffectedArea affectedArea = new AffectedArea(location.getCountry(),
                            level1, level2);
                    db
                            .child("affectedAreas")
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

    public static class AffectedArea{
        private int country, level1, level2;

        public AffectedArea(int country, int level1, int level2) {
            this.country = country;
            this.level1 = level1;
            this.level2 = level2;
        }

        public AffectedArea() {
        }

        public int getCountry() {
            return country;
        }

        public void setCountry(int country) {
            this.country = country;
        }

        public int getLevel1() {
            return level1;
        }

        public void setLevel1(int level1) {
            this.level1 = level1;
        }

        public int getLevel2() {
            return level2;
        }

        public void setLevel2(int level2) {
            this.level2 = level2;
        }
    }

    protected int levelNew = -1;
    @Override
    public void onTypeSelected(int type) {
        levelNew =-1;
        @DrawableRes int icon;
        String title;
        switch (type) {
            case 1:
                mFieldsAdapter.removeRedReason();
                icon = R.drawable.alert_amber_icon;
                title = getString(R.string.amber_alert_text);
                break;
            case 2:
                icon = R.drawable.alert_red_icon;
                title = getString(R.string.red_alert_text);
                break;
            default:
                mFieldsAdapter.removeRedReason();
                icon = R.drawable.alert_green_icon;
                title = getString(R.string.green_alert_text);
                break;
        }
        mFieldsAdapter.setTextFieldValue(1, icon, title);
        if(type == 2) {
            mFieldsAdapter.addRedAlertReason();
            saveButton.setText(R.string.request_red_alert);
        }
        else {
            saveButton.setText(R.string.confirm_alert_level);
        }
    }
}
