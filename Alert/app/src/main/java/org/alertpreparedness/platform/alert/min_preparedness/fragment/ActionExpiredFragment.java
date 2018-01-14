package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionExpiredFragment extends InProgressFragment {


    @Nullable
    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionExpired;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgExpired;

    protected ActionAdapter mExpiredAdapter;
    private Boolean isCHS = false;
    private Boolean isMandated = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_minimum, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {
        assert imgExpired != null;
        imgExpired.setImageResource(R.drawable.ic_close_round);
        assert tvActionExpired != null;
        tvActionExpired.setText("Expired");
        tvActionExpired.setTextColor(getResources().getColor(R.color.alertRed));

        mExpiredAdapter = getmAdapter();
        mActionRV.setAdapter(mExpiredAdapter);

        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        dbActionRef.addValueEventListener(this);
    }


    public ActionAdapter getmAdapter() {
        return new ActionAdapter(getContext(), dbActionRef, this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot getChild : dataSnapshot.getChildren()) {
            String actionIDs = getChild.getKey();
            String taskName = (String) getChild.child("task").getValue();
            String department = (String) getChild.child("department").getValue();
            String assignee = (String) getChild.child("asignee").getValue();
            Boolean isArchived = (Boolean) getChild.child("isArchived").getValue();
            Boolean isComplete = (Boolean) getChild.child("isComplete").getValue();
            Long actionType = (Long) getChild.child("type").getValue();
            Long dueDate = (Long) getChild.child("dueDate").getValue();
            Long budget = (Long) getChild.child("budget").getValue();
            Long level =  (Long) getChild.child("level").getValue();

            System.out.println("dataSnapshot = " + dataSnapshot);
            mExpiredAdapter.addExpiredItem(getChild.getKey(), new Action(
                    taskName,
                    department,
                    assignee,
                    isArchived,
                    isComplete,
                    isCHS,
                    isMandated,
                    actionType,
                    dueDate,
                    budget,
                    level,
                    dbAgencyRef.getRef(),
                    dbUserPublicRef.getRef())
            );

            //CHS
            dbCHSRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                        if (actionIDs.contains(getChild.getKey())) {
                            String taskNameMandated = (String) getChild.child("task").getValue();
                            isCHS = true;
                            mExpiredAdapter.addUnassignedItem(getChild.getKey(), new Action(
                                    taskNameMandated,
                                    department,
                                    assignee,
                                    isArchived,
                                    isComplete,
                                    isCHS,
                                    isMandated,
                                    actionType,
                                    dueDate,
                                    budget,
                                    level,
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef())
                            );
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            //Mandated
            dbMandatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                        if (actionIDs.contains(getChild.getKey())) {
                            String taskNameMandated = (String) getChild.child("task").getValue();
                            String departmentMandated = (String) getChild.child("department").getValue();
                            isCHS = false;
                            isMandated = true;
                            mExpiredAdapter.addUnassignedItem(getChild.getKey(), new Action(
                                    taskNameMandated,
                                    departmentMandated,
                                    assignee,
                                    isArchived,
                                    isComplete,
                                    isCHS,
                                    isMandated,
                                    actionType,
                                    dueDate,
                                    budget,
                                    level,
                                    dbAgencyRef.getRef(),
                                    dbUserPublicRef.getRef())
                            );
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onActionItemSelected(int pos, String key) {
        SheetMenu.with(getContext()).setMenu(R.menu.menu_expired).setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.update_date:
                        showDatePicker(key);
                        break;
                    case R.id.reassign_action:
                        Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Reassigned Clicked", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.action_notes:

                        break;
                    case R.id.attachments:
                        Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Attached Clicked", Snackbar.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        }).show();
    }

    private void showDatePicker(String key) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String givenDateString = i2 + " " + i1 + " " + i + " 23:59:00";//due the end of the day.
                SimpleDateFormat sdf = new SimpleDateFormat("dd mm yyyy HH:mm:ss", Locale.getDefault());
                try {
                    Date mDate = sdf.parse(givenDateString);
                    long timeInMilliseconds = mDate.getTime();

                    dbActionRef.child(key).child("dueDate").setValue(timeInMilliseconds);//save due date in milliSec.

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day);
        pickerDialog.show();
    }
}
