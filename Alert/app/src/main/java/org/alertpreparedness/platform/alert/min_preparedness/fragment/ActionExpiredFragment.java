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
import org.alertpreparedness.platform.alert.dagger.annotation.ClockSettingsActionObservable;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.consumers.ItemConsumer;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.ActionFetcher;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.FetcherResultItem;
import org.alertpreparedness.platform.alert.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.ClockSettingsFetcher;
import org.alertpreparedness.platform.alert.model.User;
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
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionExpiredFragment extends Fragment implements UsersListDialogFragment.ItemSelectedListener, ActionAdapter.ActionAdapterListener {

    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @BindView(R.id.tvStatus)
    TextView tvActionExpired;

    @BindView(R.id.imgStatus)
    ImageView imgExpired;

    @BindView(R.id.tvNoAction)
    TextView txtNoAction;

    @Inject
    @ActionRef
    public DatabaseReference dbActionRef;

    @Inject
    PermissionsHelper permissions;

    @Inject
    @ClockSettingsActionObservable
    Flowable<Collection<ActionItemWrapper>> actionFlowable;

    @Inject
    User user;

    protected ActionAdapter mExpiredAdapter;
    private String actionID;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();

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
        assert imgExpired != null;
        imgExpired.setImageResource(R.drawable.ic_close_round);
        assert tvActionExpired != null;
        tvActionExpired.setText("Expired");
        tvActionExpired.setTextColor(getResources().getColor(R.color.alertRed));

        mExpiredAdapter = new ActionAdapter(getContext(), this);
        mActionRV.setAdapter(mExpiredAdapter);

        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

//        actionFlowable.filter(fetcherResultItem -> {
//            //filter by expired time
//            ActionModel actionModel = fetcherResultItem.getValue().makeModel();
//            return ;
//        }).subscribe(new ItemConsumer<>(fetcherResultItem -> {
//            ActionModel actionModel = fetcherResultItem.makeModel();
//            onActionRetrieved(actionModel);
//        }, wrapperToRemove -> onActionRemoved(wrapperToRemove.getPrimarySnapshot())));

        actionFlowable.subscribe(collectionFetcherResultItem -> {

            ArrayList<String> result = new ArrayList<>();

            for(ActionItemWrapper wrapper : collectionFetcherResultItem) {
                ActionModel actionModel = wrapper.makeModel();

                if(actionModel.getAsignee() != null && actionModel.getAsignee().equals(user.getUserID()) && !wrapper.checkActionInProgress() && actionModel.getLevel() == Constants.MPA
                        && !wrapper.checkActionInProgress()) {
                    onActionRetrieved(actionModel);
                    result.add(actionModel.getId());
                }

            }
            mExpiredAdapter.updateKeys(result);

        });


    }

    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        this.actionID = key;
        SheetMenu.with(getContext()).setMenu(R.menu.menu_expired_mpa).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.update_date:
                    showDatePicker(key);
                    break;
                case R.id.reassign_action:
//                    if(permissions.checkMPAActionAssign(mExpiredAdapter.getItem(pos), getActivity())) {
//                        dialog.show(getActivity().getFragmentManager(), "users_list");
//                    }
                    break;
                case R.id.action_notes:
                    Intent intent = new Intent(getActivity(), AddNotesActivity.class);
                    intent.putExtra(AddNotesActivity.PARENT_ACTION_ID, mExpiredAdapter.getItem(pos).getId());
                    intent.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent);
                    break;
                case R.id.attachments:
                    Intent intent2 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent2.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, mExpiredAdapter.getItem(pos).getId());
                    intent2.putExtra(ViewAttachmentsActivity.ACTION_ID, key);
                    startActivity(intent2);
                    break;
            }
            return false;
        }).show();
    }

    @Override
    public void itemRemoved(String key) {
        if (mExpiredAdapter.getItemCount() == 0) {
            txtNoAction.setVisibility(View.VISIBLE);
        }
    }

    //region UsersListDialogFragment.ActionAdapterListener
    @Override
    public void onItemSelected(UserModel model) {
        long millis = System.currentTimeMillis();
        dbActionRef.child(actionID).child("asignee").setValue(model.getUserID());
        dbActionRef.child(actionID).child("updatedAt").setValue(millis);
        mExpiredAdapter.notifyDataSetChanged();
    }
    //endregion

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
                    if(mExpiredAdapter.getItem(actionID).getFrequencyValue() != null) {
                        clocker = DateHelper.clockCalculation(
                                mExpiredAdapter.getItem(actionID).getFrequencyValue().longValue(),
                                mExpiredAdapter.getItem(actionID).getFrequencyBase().longValue()
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

    public void onActionRetrieved(ActionModel action) {
        if(permissions.checkCanViewMPA(action)) {
            txtNoAction.setVisibility(View.GONE);
            mExpiredAdapter.addItems(action.getId(), action);
        }
    }

    public void onActionRemoved(DataSnapshot snapshot) {
        mExpiredAdapter.removeItem(snapshot.getKey());
    }
}
