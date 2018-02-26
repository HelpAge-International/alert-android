package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.utils.Constants;

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

public class ActionUnassignedFragment extends BaseUnassignedFragment implements UsersListDialogFragment.ItemSelectedListener, ActionAdapter.ItemSelectedListener {

    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @BindView(R.id.tvStatus)
    TextView tvActionUnassigned;

    @BindView(R.id.imgStatus)
    ImageView imgUnassigned;

    @Inject
    @BaseActionRef
    DatabaseReference baseActionRef;

    @BindView(R.id.tvNoAction)
    TextView txtNoAction;

    private ActionAdapter mUnassignedAdapter;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();

    private String actionID;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_minimum, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        dialog.setListener(this);

        return v;
    }

    private void initViews() {
        assert imgUnassigned != null;
        imgUnassigned.setImageResource(R.drawable.ic_close_round);
        assert tvActionUnassigned != null;
        tvActionUnassigned.setText("Unassigned");
        tvActionUnassigned.setTextColor(getResources().getColor(R.color.alertRed));

        mUnassignedAdapter = new ActionAdapter(getContext(), dbActionBaseRef, this);
        mActionRV.setAdapter(mUnassignedAdapter);
        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        ids = new String[]{user.getCountryID(), user.getNetworkID(), user.getLocalNetworkID(), user.getNetworkCountryID()};

        for (String id : ids) {
            if(id != null) {
                dbActionBaseRef.child(id).addChildEventListener(new UnassignedChildListener(id));
                dbActionBaseRef.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            getCHSForNewUser(id);
                            getMandatedForNewUser(id);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }

    }

    @Override
    protected int getType() {
        return Constants.MPA;
    }

    @Override
    protected PreparednessAdapter getAdapter() {
        return mUnassignedAdapter;
    }

    @Override
    protected RecyclerView getListView() {
        return mActionRV;
    }


    @Override
    public void onActionItemSelected(int pos, String key, String userTypeID) {
        this.actionID = key;

        SheetMenu.with(getContext()).setMenu(R.menu.menu_unassigned).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.update_date:
                    showDatePicker(key);
                    break;
                case R.id.assign_action:
                    dialog.show(getActivity().getFragmentManager(), "users_list");
                    break;
                case R.id.action_notes:
                    Intent intent = new Intent(getActivity(), AddNotesActivity.class);
                    intent.putExtra(AddNotesActivity.PARENT_ACTION_ID, getAdapter().getItem(pos).getId());
                    intent.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent);
                    break;
                case R.id.attachments:
                    Intent intent2 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent2.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, getAdapter().getItem(pos).getId());
                    intent2.putExtra(ViewAttachmentsActivity.ACTION_ID, key);
                    startActivity(intent2);
                    break;
            }
            return false;
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
                    long millis = System.currentTimeMillis();

                    dbActionRef.child(key).child("dueDate").setValue(timeInMilliseconds);//save due date in milliSec.
                    dbActionRef.child(key).child("updatedAt").setValue(millis);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day);
        pickerDialog.show();
    }

    @Override
    protected TextView getNoActionView() {
        return txtNoAction;
    }

    //region UsersListDialogFragment.ItemSelectedListener
    @Override
    public void onItemSelected(UserModel model) {
        long millis = System.currentTimeMillis();
        dbActionRef.child(actionID).child("asignee").setValue(model.getUserID());
        dbActionRef.child(actionID).child("updatedAt").setValue(millis);
        mUnassignedAdapter.removeItem(actionID);
        mUnassignedAdapter.notifyDataSetChanged();
    }
    //endregion
}
