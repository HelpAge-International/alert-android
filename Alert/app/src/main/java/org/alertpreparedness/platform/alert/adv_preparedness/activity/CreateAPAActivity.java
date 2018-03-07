package org.alertpreparedness.platform.alert.adv_preparedness.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.alert.dashboard.activity.HazardSelectionActivity;
import org.alertpreparedness.platform.alert.firebase.APAAction;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.AssignToDialogFragment;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.AssignToListener;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.DepartmentDialogFragment;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.DepartmentListener;
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelUserPublic;
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.AddIndicatorViewModel;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import shortbread.Shortcut;

@Shortcut(id = "createAPA", icon = R.drawable.fab_add, shortLabel = "Create APA")
public class CreateAPAActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, AssignToListener, DepartmentListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.etNotes)
    EditText task;

    @BindView(R.id.etHazardAssociation)
    EditText hazard;

    @BindView(R.id.etDepartment)
    EditText department;

    @BindView(R.id.etAssignTo)
    EditText assignTo;

    @BindView(R.id.etBudget)
    EditText budget;

    @BindView(R.id.myRadioGroup)
    RadioGroup needsDocumentView;

    @Inject
    @ActionRef
    DatabaseReference actionRef;

    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    @Inject
    @CountryOfficeRef
    DatabaseReference countryRef;

    protected int mSelectedAssignPosition = 0;
    protected int mDepartmentPosition = 0;
    protected boolean needsDocument;
    protected AddIndicatorViewModel mViewModel;
    protected ArrayList<ModelUserPublic> mStaff;
    protected AssignToDialogFragment mDialogAssign = new AssignToDialogFragment();
    protected DepartmentDialogFragment mDepartmentFragment = new DepartmentDialogFragment();
    protected String assignee;
    public static final int HAZARD_RESULT = 9003;
    protected int mCurrentHazardType;
    protected ArrayList<DepartmentModel> departments = new ArrayList<>();
    protected String selectedDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_apa);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ButterKnife.bind(this);
        DependencyInjector.applicationComponent().inject(this);

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
                    String hazardType = data.getStringExtra(HazardSelectionActivity.HAZARD_TITLE);
                    mCurrentHazardType = data.getIntExtra(HazardSelectionActivity.HAZARD_TYPE, 0);
                    hazard.setText(hazardType);
                }
                break;
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
    }

    @OnClick(R.id.etHazardAssociation)
    void onHazardClick(View v) {
        startActivityForResult(new Intent(this, HazardSelectionActivity.class), HAZARD_RESULT);
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

        APAAction apaAction = new APAAction();
        apaAction.setAsignee(assignee);
        apaAction.setDueDate(new DateTime().plusWeeks(1).getMillis());
        apaAction.setTask(task.getText().toString());
        apaAction.setBudget(Integer.valueOf(budget.getText().toString()));
        apaAction.setAssignHazard(new ArrayList<Integer>() {{
            add(mCurrentHazardType);
        }});
        apaAction.setCreatedAt(new Date().getTime());
        apaAction.setRequireDoc(needsDocument);
        apaAction.setDepartment(selectedDepartment);
        apaAction.setLevel(Constants.APA);
        apaAction.setType(Constants.CUSTOM);
        DatabaseReference ref = actionRef.push();
        ref.setValue(apaAction);
        finish();
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
}
