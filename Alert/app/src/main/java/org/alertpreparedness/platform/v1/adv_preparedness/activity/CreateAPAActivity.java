package org.alertpreparedness.platform.v1.adv_preparedness.activity;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Pair;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.AgencyObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.v1.dagger.annotation.AlertGroupObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.v1.dashboard.activity.MultiHazardSelectionActivity;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.firebase.AlertModel;
import org.alertpreparedness.platform.v1.firebase.TimeTrackingModel;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.ClockSettingsFetcher;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.FetcherResultItem;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.AssignToDialogFragment;
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.AssignToListener;
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.DepartmentDialogFragment;
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.DepartmentListener;
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelUserPublic;
import org.alertpreparedness.platform.v1.risk_monitoring.view_model.AddIndicatorViewModel;
import org.alertpreparedness.platform.v1.utils.AppUtils;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.SnackbarHelper;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import shortbread.Shortcut;

@Shortcut(id = "createAPA", icon = R.drawable.fab_add, shortLabel = "Create APA")
public class CreateAPAActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, AssignToListener, DepartmentListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.etNotes)
    EditText task;

    @BindView(R.id.tvHazardAssociation)
    TextView hazard;

    @BindView(R.id.etDepartment)
    EditText department;

    @BindView(R.id.etAssignTo)
    EditText assignTo;

    @BindView(R.id.etBudget)
    EditText budget;

    @BindView(R.id.myRadioGroup)
    RadioGroup needsDocumentView;

    @Inject
    @BaseActionRef
    DatabaseReference baseActionRef;

    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    @Inject
    @CountryOfficeRef
    DatabaseReference countryRef;

    @Inject
    @AlertGroupObservable
    Flowable<Collection<DataSnapshot>> alertFlowable;

    protected int mSelectedAssignPosition = 0;
    protected int mDepartmentPosition = 0;
    protected boolean needsDocument;
    protected AddIndicatorViewModel mViewModel;
    protected ArrayList<ModelUserPublic> mStaff;
    protected AssignToDialogFragment mDialogAssign = new AssignToDialogFragment();
    protected DepartmentDialogFragment mDepartmentFragment = new DepartmentDialogFragment();
    protected String assignee;
    public static final int HAZARD_RESULT = 9003;
    protected List<Constants.Hazard> mCurrentHazards = new ArrayList<>();
    protected ArrayList<DepartmentModel> departments = new ArrayList<>();
    protected String selectedDepartment;

    @Inject
    User user;

    @Inject
    @AgencyObservable
    public Flowable<FetcherResultItem<DataSnapshot>> agencyObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_apa);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ButterKnife.bind(this);
        DependencyInjector.userScopeComponent().inject(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener((t) -> {
            finish();
        });

        initData();
        initViews();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HAZARD_RESULT:
                if (resultCode == RESULT_OK) {
                    mCurrentHazards.clear();
                    mCurrentHazards.addAll(Collections2.transform(
                            data.getIntegerArrayListExtra(MultiHazardSelectionActivity.HAZARDS),
                            input -> Constants.Hazard.values()[input]
                    ));

                    updateHazardsTextView();
                }
                break;
        }
    }

    protected void updateHazardsTextView() {
        if(mCurrentHazards.size() == 0){
            hazard.setText(null);
        }
        else if(mCurrentHazards.size() == Constants.Hazard.values().length){
            hazard.setText(R.string.all_hazards);
        }
        else{
            Collection<String> hazardNames = Collections2.transform(mCurrentHazards, input -> getString(input.getStringRes()));
            hazard.setText(TextUtils.join(", ", hazardNames));
        }
    }

    protected void initData() {
        countryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("departments").exists()) {
                    for(DataSnapshot department : dataSnapshot.child("departments").getChildren()) {
                        DepartmentModel model = department.getValue(DepartmentModel.class);
                        if(model != null) {
                            model.setKey(department.getKey());
                            CreateAPAActivity.this.departments.add(model);
                        }
                    }
                }

                agencyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("departments").exists()) {
                            for(DataSnapshot department : dataSnapshot.child("departments").getChildren()) {
                                DepartmentModel model = department.getValue(DepartmentModel.class);
                                if(model != null) {
                                    model.setKey(department.getKey());
                                    CreateAPAActivity.this.departments.add(model);
                                }
                            }
                        }
                        department.setFocusable(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        department.setFocusable(true);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mViewModel = ViewModelProviders.of(this).get(AddIndicatorViewModel.class);
        mViewModel.getStaffLive().observe(this, modelUserPublics -> {
            mStaff = (ArrayList<ModelUserPublic>) modelUserPublics;
            assignTo.setFocusable(true);
        });

    }

    protected void initViews() {
        mDialogAssign.setOnAssignToListener(this);
        mDepartmentFragment.setOnAssignToListener(this);
        needsDocumentView.setOnCheckedChangeListener(this);
        assignTo.setFocusable(false);
        department.setFocusable(false);

        updateHazardsTextView();
    }

    @OnClick(R.id.tvHazardAssociation)
    void onHazardClick(View v) {
        Intent intent = new Intent(this, MultiHazardSelectionActivity.class);
        intent.putIntegerArrayListExtra(MultiHazardSelectionActivity.HAZARDS, Lists.newArrayList(Collections2.transform(mCurrentHazards, Constants.Hazard::getId)));
        startActivityForResult(intent, HAZARD_RESULT);
    }

    @OnClick(R.id.etDepartment)
    void onDepartmentClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("assign_position", mDepartmentPosition);

        if(departments != null) {
            bundle.putSerializable("staff_selection", departments);
        }

        mDepartmentFragment.setArguments(bundle);
        mDepartmentFragment.show(getSupportFragmentManager(), "department_dialog");
    }

    @OnClick(R.id.etAssignTo)
    void onAssignClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("assign_position", mSelectedAssignPosition);

        if(mStaff != null) {
            bundle.putSerializable("staff_selection", mStaff);
        }

        mDialogAssign.setArguments(bundle);
        mDialogAssign.show(getSupportFragmentManager(), "dialog_assign");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.btn_confirm:
                save();
                break;
        }

        return true;
    }

    protected void save() {
        if(budget.getText().length() == 0) {
            SnackbarHelper.show(this, "Please enter a budget");
            return;
        }

        if(department.getText().length() == 0) {
            SnackbarHelper.show(this, "Please enter a department");
            return;
        }

        if(task.getText().length() == 0) {
            SnackbarHelper.show(this, "Please enter a task");
            return;
        }

        if(assignTo.getText().length() == 0) {
            SnackbarHelper.show(this, "Please assign to a staff member");
            return;
        }

        if(hazard.getText().length() == 0) {
            SnackbarHelper.show(this, "Please select a hazard");
            return;
        }

        ActionModel apaAction = new ActionModel();
        apaAction.setLevel(Constants.APA);
        apaAction.setAsignee(assignee);
        apaAction.setDueDate(new DateTime().plusWeeks(1).getMillis());
        apaAction.setTask(task.getText().toString());
        apaAction.setBudget(Long.valueOf(budget.getText().toString()));
        ArrayList<Integer> hazardIds = Lists.newArrayList(
                Collections2.transform(mCurrentHazards, Constants.Hazard::getId)
        );

        apaAction.setAssignHazard(mCurrentHazards.size() == Constants.Hazard.values().length ? null : hazardIds);
        apaAction.setCreatedAt(new Date().getTime());
        apaAction.setRequireDoc(needsDocument);
        apaAction.setDepartment(selectedDepartment);
        apaAction.setLevel(Constants.APA);
        apaAction.setType(Constants.CUSTOM);

        Flowable<ClockSettingsFetcher.ClockSettingsResult> clockSettingsFlowable = new ClockSettingsFetcher()
                .rxFetch(ClockSettingsFetcher.TYPE_PREPAREDNESS);


        Flowable<Pair<Set<Integer>, ClockSettingsFetcher.ClockSettingsResult>> pairs = Flowable.combineLatest(clockSettingsFlowable, alertFlowable, (clockSettingsResult, snapshots) -> {
            Set<Integer> hazards = new HashSet<>();
            for(DataSnapshot snapshot : snapshots) {
                AlertModel model = AppUtils.getFirebaseModelFromDataSnapshot(snapshot, AlertModel.class);
                if(model.getParentId().equals(user.countryID) && model.getAlertLevel() == Constants.TRIGGER_RED && model.getRedAlertApproved()) {
                    hazards.add(model.getHazardScenario());
                }
            }
            return new Pair<>(hazards, clockSettingsResult);
        });

        pairs.firstElement().subscribe(pair -> {
            ClockSettingsFetcher.ClockSettingsResult clockSettingsResult = pair.second;
            Set<Integer> hazards = pair.first;
            boolean isInProgress = AppUtils.isActionInProgress(apaAction, clockSettingsResult.all().get(user.countryID));
            boolean isActive = false;

            if(apaAction.getAssignHazard() == null && hazards.size() > 0) {
                isActive = true;
            }
            else if(apaAction.getAssignHazard() != null){
                for (Integer h : apaAction.getAssignHazard()) {
                    if (hazards.contains(h)) {
                        isActive = true;
                        break;
                    }
                }
            }

            isInProgress = isInProgress && isActive;

            TimeTrackingModel timeTrackingModel = new TimeTrackingModel();
            timeTrackingModel.updateActionTimeTracking(
                    TimeTrackingModel.LEVEL.NEW,
                    apaAction.getIsComplete(),
                    apaAction.getIsArchived(),
                    apaAction.getAsignee() != null,
                    isInProgress
            );
            apaAction.setTimeTracking(timeTrackingModel);
            DatabaseReference ref = baseActionRef.child(user.countryID).push();
            ref.setValue(apaAction);
            finish();
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rbtNoDoc:
                needsDocument = false;
                break;
            case R.id.rbtYesDoc:
                needsDocument = true;
                break;
        }
    }

    @Override
    public void userAssignedTo(@org.jetbrains.annotations.Nullable ModelUserPublic user, int position) {
        if(user != null) {
            assignTo.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            mSelectedAssignPosition = position;
            assignee = user.getId();
        }
    }

    @Override
    public void departmentSelected(@Nullable DepartmentModel model, int position) {
        if(model != null) {
            mSelectedAssignPosition = position;
            selectedDepartment = model.getKey();
            department.setText(model.getName());
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

}
