package org.alertpreparedness.platform.alert.adv_preparedness.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.ExtensionHelperKt;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.firebase.APAAction;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.AssignToListener;
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.DepartmentListener;
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelUserPublic;
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.AddIndicatorViewModel;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;


public class EditAPAActivity extends CreateAPAActivity implements RadioGroup.OnCheckedChangeListener, AssignToListener, DepartmentListener {

    public static final String APA_ID = "apa_id";
    private String apaId;
    private APAAction model;

    @Override
    protected void initData() {
        apaId = getIntent().getStringExtra(APA_ID);
        actionRef.child(apaId).addValueEventListener(new ActionRetrievalListener());
    }

    @Override
    protected void initViews() {
        mDialogAssign.setOnAssignToListener(this);
        mDepartmentFragment.setOnAssignToListener(this);
        needsDocumentView.setOnCheckedChangeListener(this);
        assignTo.setFocusable(false);
        department.setFocusable(false);
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
        model.setBudget(Integer.valueOf(budget.getText().toString()));
        model.setAssignHazard(new ArrayList<Integer>() {{
            add(mCurrentHazardType);
        }});
        model.setUpdatedAt(new Date().getTime());
        model.setRequireDoc(needsDocument);
        model.setDepartment(selectedDepartment);
        model.setLevel(Constants.APA);
        model.setType(Constants.CUSTOM);
        DatabaseReference ref = actionRef.child(apaId);
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

    private class ActionRetrievalListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            model = dataSnapshot.getValue(APAAction.class);

            assert model != null;
            mCurrentHazardType = model.getAssignHazard().get(0);
            needsDocument = model.getRequireDoc();
            budget.setText(model.getBudget().toString());
            task.setText(model.getTask());
            hazard.setText(ExtensionHelperKt.getHazardTypes().get(model.getAssignHazard().get(0)));

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
            countryRef.addListenerForSingleValueEvent(new CountryListener());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class AgencyListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.child("departments").exists()) {
                for(DataSnapshot department : dataSnapshot.child("departments").getChildren()) {
                    DepartmentModel model = department.getValue(DepartmentModel.class);
                    if(model != null) {
                        model.setKey(department.getKey());
                        EditAPAActivity.this.departments.add(model);
                    }
                }
            }
            department.setFocusable(true);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            department.setFocusable(true);
        }
    }

    private class CountryListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.child("departments").exists()) {
                int i = 0;
                for(DataSnapshot department : dataSnapshot.child("departments").getChildren()) {
                    DepartmentModel model = department.getValue(DepartmentModel.class);
                    if(model != null) {
                        if(department.getKey().equals(EditAPAActivity.this.model.getDepartment())) {
                            selectedDepartment = department.getKey();
                            EditAPAActivity.this.department.setText(model.getName());
                            mDepartmentPosition = i;
                        }
                        model.setKey(department.getKey());
                        EditAPAActivity.this.departments.add(model);
                    }
                    i++;
                }
            }

            agencyRef.addListenerForSingleValueEvent(new AgencyListener());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    }
}
