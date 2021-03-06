package org.alertpreparedness.platform.v1.adv_preparedness.activity;

import androidx.lifecycle.ViewModelProviders;
import android.widget.RadioGroup;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.firebase.consumers.ItemConsumer;
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.AssignToListener;
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.DepartmentListener;
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelUserPublic;
import org.alertpreparedness.platform.v1.risk_monitoring.view_model.AddIndicatorViewModel;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.SnackbarHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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
            mCurrentHazards.addAll(Collections2.transform(model.getAssignHazard(), Constants.Hazard::getById));
        }
        else{
            mCurrentHazards.addAll(Arrays.asList(Constants.Hazard.values()));
        }
        needsDocument = model.getRequireDoc();
        budget.setText(model.getBudget().toString());
        task.setText(model.getTask());

        disposable.add(agencyObservable.subscribe(new ItemConsumer<>(this::processAgency, item -> {
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
        updateHazardsTextView();
        

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

        ArrayList<Integer> hazardIds = Lists.newArrayList(
                Collections2.transform(mCurrentHazards, Constants.Hazard::getId)
        );

        model.setAssignHazard(mCurrentHazards.size() == Constants.Hazard.values().length ? null : hazardIds);
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
