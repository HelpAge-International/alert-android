package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.action.ActionFetcher;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;

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

public class ActionUnassignedFragment extends Fragment implements UsersListDialogFragment.ItemSelectedListener, ActionAdapter.ActionAdapterListener, ActionFetcher.ActionRetrievalListener {

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

    @Inject
    @ActionRef
    public DatabaseReference dbActionRef;

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
        tvActionUnassigned.setText(R.string.unassigned_title);
        tvActionUnassigned.setTextColor(getResources().getColor(R.color.alertRed));

        mUnassignedAdapter = new ActionAdapter(getContext(), this);
        mActionRV.setAdapter(mUnassignedAdapter);
        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        new ActionFetcher(Constants.MPA, ActionFetcher.ACTION_STATE.UNASSIGNED, this).fetch((ids)-> {
        });

    }

    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        this.actionID = key;

        SheetMenu.with(getContext()).setMenu(R.menu.menu_unassigned).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.update_date:
                    showDatePicker(key);
                    break;
                case R.id.assign_action:
                    if(!mUnassignedAdapter.getItem(pos).hasCHSInfo() && mUnassignedAdapter.getItem(pos).isCHS()) {
                        SnackbarHelper.show(getActivity(), "The action needs more information from the web portal");
                    }
                    else {
                        dialog.show(getActivity().getFragmentManager(), "users_list");
                    }                    break;
                case R.id.action_notes:
                    Intent intent = new Intent(getActivity(), AddNotesActivity.class);
                    intent.putExtra(AddNotesActivity.PARENT_ACTION_ID, mUnassignedAdapter.getItem(pos).getId());
                    intent.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent);
                    break;
                case R.id.attachments:
                    Intent intent2 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent2.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, mUnassignedAdapter.getItem(pos).getId());
                    intent2.putExtra(ViewAttachmentsActivity.ACTION_ID, key);
                    startActivity(intent2);
                    break;
            }
            return false;
        }).show();
    }

    @Override
    public void itemRemoved(String key) {
        if(mUnassignedAdapter.getItemCount() == 0) {
            txtNoAction.setVisibility(View.VISIBLE);
        }
    }

    private void showDatePicker(String key) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), (datePicker, i, i1, i2) -> {
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
        }, year, month, day);
        pickerDialog.show();
    }


    //region UsersListDialogFragment.ActionAdapterListener
    @Override
    public void onItemSelected(UserModel model) {
        long millis = System.currentTimeMillis();
        dbActionRef.child(actionID).child("asignee").setValue(model.getUserID());
        dbActionRef.child(actionID).child("updatedAt").setValue(millis);
        mUnassignedAdapter.removeItem(actionID);
        mUnassignedAdapter.notifyDataSetChanged();
    }
    //endregion


    @Override
    public void onActionRetrieved(DataSnapshot snapshot, Action action) {
        txtNoAction.setVisibility(View.GONE);
        mUnassignedAdapter.addItems(snapshot.getKey(), action);
    }

    @Override
    public void onActionRemoved(DataSnapshot snapshot) {
        mUnassignedAdapter.removeItem(snapshot.getKey());
    }
}
