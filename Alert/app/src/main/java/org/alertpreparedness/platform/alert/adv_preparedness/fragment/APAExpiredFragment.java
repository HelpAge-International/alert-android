package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.annotation.ActiveActionObservable;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.ClockSetting;
import org.alertpreparedness.platform.alert.adv_preparedness.activity.EditAPAActivity;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.FetcherResultItem;
import org.alertpreparedness.platform.alert.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.ClockSettingsFetcher;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PermissionsHelper;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAExpiredFragment extends BaseAPAFragment implements APActionAdapter.APAAdapterListener, UsersListDialogFragment.ItemSelectedListener{

    private String actionID;

    public APAExpiredFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgActionExpired;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionExpired;

    @BindView(R.id.tvAPANoAction)
    TextView txtNoAction;

    @Inject
    @NetworkRef
    DatabaseReference dbNetworkRef;

    @Inject
    @NetworkRef
    DatabaseReference networkRef;

    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    @Inject
    @AlertRef
    DatabaseReference alertRef;

    @Inject
    @BaseAlertRef
    DatabaseReference baseAlertRef;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    @Inject
    User user;

    @Inject
    PermissionsHelper permissions;

    @Inject
    @ActiveActionObservable
    Flowable<FetcherResultItem<Collection<ActionItemWrapper>>> actionFlowable;

    private APActionAdapter mAPAdapter;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_advanced, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();
        dialog.setListener(this);

        return v;
    }

    private void initViews() {
        assert imgActionExpired != null;
        imgActionExpired.setImageResource(R.drawable.ic_close_round);
        assert tvActionExpired != null;
        tvActionExpired.setText("Expired");
        tvActionExpired.setTextColor(getResources().getColor(R.color.alertRed));
        mAPAdapter = new APActionAdapter(getContext(), this);
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);

        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        handleAdvFab();

        actionFlowable
        .subscribe(collectionFetcherResultItem -> {

            ArrayList<String> result = new ArrayList<>();

            for(ActionItemWrapper wrapper : collectionFetcherResultItem.getValue()) {
                if(wrapper.getActionSnapshot() != null) {
                    ActionModel actionModel = wrapper.makeModel();
                    if(!user.getUserID().equals(actionModel.getAsignee())) {
                        break;
                    }
                }
                if(!wrapper.checkActionInProgress()) {
                    ActionModel actionModel = wrapper.makeModel();
                    onActionRetrieved(actionModel);
                    result.add(actionModel.getId());
                }
            }
            mAPAdapter.updateKeys(result);

        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        this.actionID = key;
        SheetMenu.with(getContext()).setMenu(R.menu.menu_expired).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.edit:
                    if(permissions.checkEditAPA(mAPAdapter.getItem(pos), getActivity())) {
                        Intent i = new Intent(getContext(), EditAPAActivity.class);
                        i.putExtra(EditAPAActivity.APA_ID, key);
                        startActivity(i);
                    }
                    break;
                case R.id.update_date:
                    showDatePicker(key);
                    break;
                case R.id.reassign_action:
                    if(permissions.checkAssignAPA(mAPAdapter.getItem(pos), getActivity())) {
                        dialog.show(getActivity().getFragmentManager(), "users_list");
                    }
                    break;
                case R.id.action_notes:
                    Intent intent = new Intent(getActivity(), AddNotesActivity.class);
                    intent.putExtra(AddNotesActivity.PARENT_ACTION_ID, mAPAdapter.getItem(pos).getId());
                    intent.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent);
                    break;
                case R.id.attachments:
                    Intent intent2 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent2.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, mAPAdapter.getItem(pos).getId());
                    intent2.putExtra(ViewAttachmentsActivity.ACTION_ID, key);
                    startActivity(intent2);
                    break;
            }
            return false;
        }).show();
    }

    @Override
    public void onAdapterItemRemoved(String key) {
        if (mAPAdapter.getItemCount() == 0) {
            txtNoAction.setVisibility(View.VISIBLE);
        }
    }

    private void showDatePicker(String key) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), (datePicker, i, i1, i2) -> {
            DateTime newDate = new DateTime().withYear(i).withMonthOfYear(i1+1).withDayOfMonth(i2);
            long millis = System.currentTimeMillis();

            if(newDate.getMillis() <= millis) {
                SnackbarHelper.show(getActivity(), getString(R.string.past_date_error));
            }
            else {
                dbActionRef.child(key).child("dueDate").setValue(newDate.getMillis());//save due date in milliSec.

              new ClockSettingsFetcher(((value, durationType) -> {
                    Long clocker;
                    if(mAPAdapter.getItem(actionID).getFrequencyValue() != null) {
                        clocker = DateHelper.clockCalculation(
                                mAPAdapter.getItem(actionID).getFrequencyValue().longValue(),
                                mAPAdapter.getItem(actionID).getFrequencyBase().longValue()
                        );
                    }
                    else {
                        clocker = DateHelper.clockCalculation(value, durationType);
                    }

                    dbActionRef.child(key).child("createdAt").setValue(newDate.plusMillis(clocker.intValue()).getMillis());
                    dbActionRef.child(key).child("updatedAt").setValue(newDate.plusMillis(clocker.intValue()).getMillis());
                })).fetch();
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
        mAPAdapter.notifyDataSetChanged();
    }
    //endregion

    @Override
    protected RecyclerView getListView() {
        return mAdvActionRV;
    }

    public void onActionRetrieved(ActionModel action) {
        if(permissions.checkCanViewAPA(action)) {
            txtNoAction.setVisibility(View.GONE);
            mAPAdapter.addItems(action.getId(), action);
        }
    }

    public void onActionRemoved(DataSnapshot snapshot) {
        mAPAdapter.removeItem(snapshot.getKey());
    }


}

