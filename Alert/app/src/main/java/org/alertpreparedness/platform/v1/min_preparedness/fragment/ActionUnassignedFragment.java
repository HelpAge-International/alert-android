package org.alertpreparedness.platform.v1.min_preparedness.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.database.DatabaseReference;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import javax.inject.Inject;
import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.v1.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionGroupObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.ClockSettingsFetcher;
import org.alertpreparedness.platform.v1.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.v1.helper.DateHelper;
import org.alertpreparedness.platform.v1.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.v1.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PermissionsHelper;
import org.alertpreparedness.platform.v1.utils.SnackbarHelper;
import org.joda.time.DateTime;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionUnassignedFragment extends Fragment implements UsersListDialogFragment.ItemSelectedListener, ActionAdapter.ActionAdapterListener {

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

    @Inject
    PermissionsHelper permissions;

    private ActionAdapter mUnassignedAdapter;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();

    private String actionID;

    @Inject
    @ActionGroupObservable
    Flowable<Collection<ActionItemWrapper>> actionFlowable;

    @Inject
    User user;

    CompositeDisposable disposable = new CompositeDisposable();

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
        assert imgUnassigned != null;
        imgUnassigned.setImageResource(R.drawable.preparedness_red);
        assert tvActionUnassigned != null;
        tvActionUnassigned.setText(R.string.unassigned_title);
        tvActionUnassigned.setTextColor(getResources().getColor(R.color.alertRed));

        mUnassignedAdapter = new ActionAdapter(getContext(), this);
        mActionRV.setAdapter(mUnassignedAdapter);
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

                if(actionModel.getAsignee() == null && actionModel.getLevel() == Constants.MPA) {
                    System.out.println("actionModel = " + actionModel);
                    onActionRetrieved(actionModel);
                    result.add(actionModel.getId());
                }

            }
            mUnassignedAdapter.updateKeys(result);

        }));
    }

    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        this.actionID = key;

        SheetMenu.with(getContext()).setMenu(R.menu.menu_unassigned_mpa).setClick(menuItem -> {
            ActionModel item = mUnassignedAdapter.getItem(pos);
            switch (menuItem.getItemId()) {
                case R.id.update_date:
                    showDatePicker(key);
                    break;
                case R.id.assign_action:
                    if(permissions.checkMPAActionAssign(item, getActivity())) {
                        if (!item.hasEnoughChsInfo() && item.isChs()) {
                            SnackbarHelper.show(getActivity(), getString(R.string.more_info_chs));
                        }
                        else {
                            dialog.show(getActivity().getSupportFragmentManager(), "users_list");
                        }
                        break;
                    }
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
            DateTime newDate = new DateTime().withYear(i).withMonthOfYear(i1+1).withDayOfMonth(i2);
            long millis = System.currentTimeMillis();

            if(newDate.getMillis() <= millis) {
                SnackbarHelper.show(getActivity(), getString(R.string.past_date_error));
            }
            else {
                dbActionRef.child(key).child("dueDate").setValue(newDate.getMillis());//save due date in milliSec.

                new ClockSettingsFetcher(((value, durationType) -> {
                    Long clocker;
                    if(mUnassignedAdapter.getItem(actionID).getFrequencyValue() != null) {
                        clocker = DateHelper.clockCalculation(
                                mUnassignedAdapter.getItem(actionID).getFrequencyValue().longValue(),
                                mUnassignedAdapter.getItem(actionID).getFrequencyBase().longValue()
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
        mUnassignedAdapter.removeItem(actionID);
        mUnassignedAdapter.notifyDataSetChanged();
    }
    //endregion

    public void onActionRetrieved(ActionModel action) {
        if(permissions.checkCanViewMPA(action)) {
            txtNoAction.setVisibility(View.GONE);
            mUnassignedAdapter.addItems(action.getId(), action);
        }
    }

    @Override
    public void onStop() {
        System.out.println("STOPPED");
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
