package org.alertpreparedness.platform.v1.min_preparedness.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.ClockSettingsActionObservable;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.v1.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.v1.firebase.ClockSetting;
import org.alertpreparedness.platform.v1.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.v1.helper.DateHelper;
import org.alertpreparedness.platform.v1.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.v1.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.ClockSettingsFetcher;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PermissionsHelper;
import org.alertpreparedness.platform.v1.utils.SnackbarHelper;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
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
    @BaseActionRef
    DatabaseReference baseActionRef;


    @Inject
    User user;

    CompositeDisposable disposable = new CompositeDisposable();

    protected ActionAdapter mExpiredAdapter;
    private String actionID;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_minimum, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.userScopeComponent().inject(this);

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

        initData();

    }

    private void initData() {
        disposable = new CompositeDisposable();
        disposable.add(actionFlowable.subscribe(collectionFetcherResultItem -> {

            ArrayList<String> result = new ArrayList<>();

            for(ActionItemWrapper wrapper : collectionFetcherResultItem) {
                ActionModel actionModel = wrapper.makeModel();

                if(actionModel.getAsignee() != null &&
                        actionModel.getAsignee().equals(user.getUserID()) &&
                        !wrapper.checkActionInProgress() &&
                        !actionModel.getIsArchived() &&
                        actionModel.getLevel() == Constants.MPA) {

                    onActionRetrieved(actionModel);
                    result.add(actionModel.getId());
                }

            }
            mExpiredAdapter.updateKeys(result);

        }));
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
                    intent.putExtra(AddNotesActivity.PARENT_ACTION_ID, parentId);
                    intent.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent);
                    break;
                case R.id.attachments:
                    Intent intent2 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent2.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, parentId);
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
                baseActionRef.child(mExpiredAdapter.getItem(actionID).getParentId()).child(key).child("dueDate").setValue(newDate.getMillis());//save due date in milliSec.

                new ClockSettingsFetcher().rxFetch(ClockSettingsFetcher.TYPE_PREPAREDNESS).subscribe(clockSettingsResult -> {

                    Long clocker;
                    if(mExpiredAdapter.getItem(actionID).getFrequencyValue() != null) {
                        clocker = DateHelper.clockCalculation(
                                mExpiredAdapter.getItem(actionID).getFrequencyValue().longValue(),
                                mExpiredAdapter.getItem(actionID).getFrequencyBase().longValue()
                        );

                    }
                    else {
                        ClockSetting res = clockSettingsResult.all().get(mExpiredAdapter.getItem(actionID).getParentId());
                        clocker = DateHelper.clockCalculation(((Integer)res.getValue()).longValue(), ((Integer)res.getDurationType()).longValue());
                    }
                    baseActionRef.child(mExpiredAdapter.getItem(actionID).getParentId()).child(key).child("updatedAt").setValue(new DateTime().plusMillis(clocker.intValue()).getMillis());

                });
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

    @Override
    public void onStop() {
        super.onStop();
        disposable.dispose();
    }


    @Override
    public void onPause() {
        super.onPause();
        disposable.dispose();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(disposable.isDisposed()) {
            initData();
        }
    }

}
