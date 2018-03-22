package org.alertpreparedness.platform.alert.adv_preparedness.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.ExtensionHelperKt;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyObservable;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.consumers.ItemConsumer;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.FetcherResultItem;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.AssignToListener;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.DepartmentListener;
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelUserPublic;
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.AddIndicatorViewModel;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;


public class EditAPAActivity extends CreateAPAActivity implements RadioGroup.OnCheckedChangeListener, AssignToListener, DepartmentListener {

    public static final String MODEL = "model";
    private ActionModel model;

    CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void initData() {
        model = (ActionModel) getIntent().getSerializableExtra(MODEL);
    }

    @Override
    protected void initViews() {
        mDialogAssign.setOnAssignToListener(this);
        mDepartmentFragment.setOnAssignToListener(this);
        needsDocumentView.setOnCheckedChangeListener(this);
        assignTo.setFocusable(false);
        department.setFocusable(false);
        if(model.getAssignHazard() != null && model.getAssignHazard().size() > 0) {
            mCurrentHazardType = model.getAssignHazard().get(0);
        }
        needsDocument = model.getRequireDoc();
        budget.setText(model.getBudget().toString());
        task.setText(model.getTask());
        hazard.setText(ExtensionHelperKt.getHazardTypes().get(model.getAssignHazard().get(0)));

        disposable.add(agencyObservable.subscribe(new ItemConsumer<DataSnapshot>(this::processAgency, item -> {
            //not used
        })));

        mViewModel = ViewModelProviders.of(EditAPAActivity.this).get(AddIndicatorViewModel.class);
        mViewModel.getStaffLive().observe(EditAPAActivity.this, modelUserPublics -> {
            mStaff = (ArrayList<ModelUserPublic>) modelUserPublics;
            assignTo.setFocusable(true);

            if (modelUserPublics != null && modelUserPublics.get(modelUserPublics.size() - 1).getId().equals(model.getAsignee())) {
                ModelUserPublic user = modelUserPublics.get(modelUserPublics.size() - 1);
                assignee = user.getId();
                mSelectedAssignPosition = modelUserPublics.size() - 1;
                assignTo.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            }

        });
    }

    private void processAgency(DataSnapshot item) {
        int i = 0;
        for(DataSnapshot department : item.child("departments").getChildren()) {
            DepartmentModel model = department.getValue(DepartmentModel.class);
            if(model != null) {
                if(department.getKey().equals(this.model.getDepartment())) {
                    selectedDepartment = department.getKey();
                    this.department.setText(model.getName());
                    mDepartmentPosition = i;
                }
                model.setKey(department.getKey());
                EditAPAActivity.this.departments.add(model);
            }
            i++;
        }
    }

    @Override
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

        model.setAsignee(assignee);
        model.setTask(task.getText().toString());
        model.setBudget(Long.valueOf(budget.getText().toString()));
        model.setAssignHazard(new ArrayList<Integer>() {{
            add(mCurrentHazardType);
        }});
        model.setUpdatedAt(new Date().getTime());
        model.setRequireDoc(needsDocument);
        model.setDepartment(selectedDepartment);
        model.setLevel(Constants.APA);
        model.setType(Constants.CUSTOM);
        DatabaseReference ref = baseActionRef.child(model.getParentId()).child(model.getId());
        ref.setValue(model);
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
    public void userAssignedTo(@Nullable ModelUserPublic user, int position) {
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
        disposable.dispose();
    }

}
