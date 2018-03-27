package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertFieldsAdapter;
import org.alertpreparedness.platform.alert.dashboard.model.AlertFieldModel;
import org.alertpreparedness.platform.alert.firebase.AffectedAreaModel;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.firebase.ApprovalModel;
import org.alertpreparedness.platform.alert.firebase.TimeTrackingItemModel;
import org.alertpreparedness.platform.alert.firebase.TimeTrackingModel;
import org.alertpreparedness.platform.alert.helper.AlertLevelDialog;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicatorLocation;
import org.alertpreparedness.platform.alert.risk_monitoring.view.SelectAreaActivity;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import shortbread.Shortcut;

@Shortcut(id = "createAlert", icon = R.drawable.fab_add, shortLabel = "Create Alert")
public class CreateAlertActivity extends AppCompatActivity implements AlertFieldsAdapter.ClickListener, AlertLevelDialog.TypeSelectedListener {

    public static final int EFFECTED_AREA_REQUEST = 9002;
    public static final int HAZARD_RESULT = 9003;
    public AlertLevelDialog mAlertLevelFragment;
    public AlertFieldsAdapter mFieldsAdapter;

    protected int mCurrentHazardType;
    protected ArrayList<AffectedAreaModel> mCurrentAffectedAreas = new ArrayList<>();
    protected int mAlertLevel;

    @BindView(R.id.btnSaveChanges)
    Button saveButton;

    @BindView(R.id.rvFields)
    RecyclerView fields;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject @AlertRef
    DatabaseReference alertRef;

    @Inject
    User user;

    @Inject @BaseDatabaseRef
    DatabaseReference database;

    private Bundle mBundleRecyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);

        ButterKnife.bind(this);
        DependencyInjector.userScopeComponent().inject(this);

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
        fields.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
        if(positionInParent == getIndex(mFieldsAdapter.isRedAlert(), 3)) {//affected areas
            if(mCurrentAffectedAreas.get(position) != null) {
                mCurrentAffectedAreas.remove(position);
            }
        }
    }

    @OnClick(R.id.btnSaveChanges)
    public void onSaveClicked(View v) {

        if(!hasErrors(mFieldsAdapter.isRedAlert())) {
            saveData(mFieldsAdapter.isRedAlert());
        }

    }

    protected void saveData(boolean isRedAlert) {
        AlertModel m = new AlertModel(
                mAlertLevel,
                mCurrentHazardType,
                Long.valueOf(mFieldsAdapter.getModel(getIndex(isRedAlert, 2)).resultTitle),
                mFieldsAdapter.getModel(getIndex(isRedAlert, 4)).resultTitle,
                user.getUserID(),
                mCurrentAffectedAreas
        );
        m.setUpdatedBy(user.getUserID());
        ApprovalModel approval = new ApprovalModel();
        approval.setCountryDirector(new HashMap<String, Integer>() {{
            if (isRedAlert) {
                put(user.getCountryID(), 0);
            }
        }});

        m.setApproval(approval);
        if(isRedAlert) {
            m.setReasonForRedAlert(mFieldsAdapter.getRedAlertReason());
        }

        TimeTrackingModel timeTracking = new TimeTrackingModel();

        TimeTrackingItemModel timeTrackingItemModel = new TimeTrackingItemModel();
        timeTrackingItemModel.setFinish(-1L);
        timeTrackingItemModel.setStart(new Date().getTime());

        switch (mAlertLevel) {
            case Constants.TRIGGER_RED://grey, cant be approved yet
                timeTracking.setTimeSpentInGrey(new ArrayList<TimeTrackingItemModel>() {{
                    add(timeTrackingItemModel);
                }});
                break;
            case Constants.TRIGGER_GREEN:
                timeTracking.setTimeSpentInGreen(new ArrayList<TimeTrackingItemModel>() {{
                    add(timeTrackingItemModel);
                }});
                break;
            case Constants.TRIGGER_AMBER:
                timeTracking.setTimeSpentInAmber(new ArrayList<TimeTrackingItemModel>() {{
                    add(timeTrackingItemModel);
                }});
                break;
        }

        DatabaseReference ref = alertRef.push();

        ref.setValue(m);
        finish();
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
            else if(mFieldsAdapter.getModel(getIndex(isRedAlert, 4)).resultTitle == null) {
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

                    mCurrentAffectedAreas.add(new AffectedAreaModel(area));
                    mFieldsAdapter.addSubListValue(3, displayable);
                }

                break;
            case HAZARD_RESULT:
                if (resultCode == RESULT_OK) {
                    String hazardType = data.getStringExtra(HazardSelectionActivity.HAZARD_TITLE);

                    mCurrentHazardType = data.getIntExtra(HazardSelectionActivity.HAZARD_TYPE, 0);
                    mFieldsAdapter.setTextFieldValue(0, hazardType);
                }
                break;
        }
    }

    @Override
    public void onTypeSelected(int type) {
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
        mAlertLevel = type;
        mFieldsAdapter.setTextFieldValue(1, icon, title);
        if(type == 2) {
            mFieldsAdapter.addRedAlertReason();
            saveButton.setText(R.string.request_red_alert);
        }
        else {
            saveButton.setText(R.string.confirm_alert_level);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = fields.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable("state", listState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable("state");
            fields.getLayoutManager().onRestoreInstanceState(listState);
        }
    }
}
