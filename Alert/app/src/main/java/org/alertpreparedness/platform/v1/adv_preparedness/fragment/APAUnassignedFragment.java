package org.alertpreparedness.platform.v1.adv_preparedness.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
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
import java.util.Collection;
import java.util.HashMap;
import javax.inject.Inject;
import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.adv_preparedness.activity.EditAPAActivity;
import org.alertpreparedness.platform.v1.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.v1.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.ActiveActionObservable;
import org.alertpreparedness.platform.v1.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.firebase.TimeTrackingModel;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.FetcherResultItem;
import org.alertpreparedness.platform.v1.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.v1.interfaces.DisposableFragment;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PermissionsHelper;
import org.alertpreparedness.platform.v1.utils.SnackbarHelper;
import ru.whalemare.sheetmenu.SheetMenu;

public class APAUnassignedFragment extends BaseAPAFragment implements APActionAdapter.APAAdapterListener, UsersListDialogFragment.ItemSelectedListener, DisposableFragment {

    public APAUnassignedFragment() {
        // Required empty public constructor
    }

    @Inject
    @AlertRef
    DatabaseReference alertRef;

    @Inject
    @BaseAlertRef
    DatabaseReference baseAlertRef;


    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @BindView(R.id.imgStatus)
    ImageView imgActionUnassigned;

    @BindView(R.id.tvStatus)
    TextView tvActionUnassigned;

    @BindView(R.id.tvAPANoAction)
    TextView txtNoAction;

    @Inject
    @BaseActionRef
    DatabaseReference dbActionRef;

    @Inject
    User user;

    @Inject
    PermissionsHelper permissions;

    @Inject
    @ActiveActionObservable
    Flowable<FetcherResultItem<Collection<ActionItemWrapper>>> actionFlowable;

    CompositeDisposable disposable = new CompositeDisposable();

    private APActionAdapter mAPAdapter;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();
    private String actionID;
    private HashMap<String, ActionItemWrapper> actionWrappers = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_advanced, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.userScopeComponent().inject(this);

        initViews();

        dialog.setListener(this);

        return v;
    }

    private void initViews() {
        assert imgActionUnassigned != null;
        imgActionUnassigned.setImageResource(R.drawable.preparedness_red);
        assert tvActionUnassigned != null;
        tvActionUnassigned.setText(R.string.unassigned_title);
        tvActionUnassigned.setTextColor(getResources().getColor(R.color.alertRed));
        mAPAdapter = new APActionAdapter(getContext(), this);
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);

        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        handleAdvFab();

        initData();
    }

    private void initData() {
        disposable = new CompositeDisposable();
        disposable.add(actionFlowable.subscribe(collectionFetcherResultItem -> {

            ArrayList<String> result = new ArrayList<>();

            for(ActionItemWrapper wrapper : collectionFetcherResultItem.getValue()) {
                ActionModel actionModel = wrapper.makeModel();
                actionWrappers.put(actionModel.getId(), wrapper);
                if(actionModel.getAsignee() == null && actionModel.getLevel() == Constants.APA) {
                    onActionRetrieved(actionModel);
                    result.add(actionModel.getId());
                }
            }
            mAPAdapter.updateKeys(result);

        }));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        this.actionID = key;
        ActionModel item = mAPAdapter.getItem(pos);
        SheetMenu.with(getContext()).setMenu(R.menu.menu_unassigned_apa).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.assign_action:
                    if (!item.hasEnoughChsInfo() && item.isChs()) {
                        SnackbarHelper.show(getActivity(), getString(R.string.more_info_chs));
                    }
                    else {
                        dialog.show(getActivity().getSupportFragmentManager(), "users_list");
                    }
                    break;
                case R.id.edit_action:
                    if(permissions.checkEditAPA(mAPAdapter.getItem(pos), getActivity())) {
                        Intent i = new Intent(getContext(), EditAPAActivity.class);
                        i.putExtra(EditAPAActivity.MODEL, mAPAdapter.getItem(pos));
                        startActivity(i);
                    }
                    break;
            }
            return false;
        }).show();
    }

    @Override
    public void onAdapterItemRemoved(String key) {
        if(mAPAdapter.getItemCount() == 0) {
            txtNoAction.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(UserModel userModel) {
        ActionModel model = mAPAdapter.getItem(actionID);
        model.setAsignee(userModel.getUserID());
        model.setUpdatedAt(System.currentTimeMillis());
        mAPAdapter.getItem(actionID)
                .getTimeTracking()
                .updateActionTimeTracking(
                        TimeTrackingModel.LEVEL.RED,
                        model.getIsComplete(),
                        model.getIsArchived(),
                        true,
                        actionWrappers.get(model.getId()).checkActionInProgress()
                );
        dbActionRef.child(model.getParentId()).child(model.getId()).setValue(model);
        mAPAdapter.removeItem(actionID);
        mAPAdapter.notifyDataSetChanged();
    }

    public void onActionRetrieved(ActionModel action) {
        if(permissions.checkCanViewAPA(action)) {
            txtNoAction.setVisibility(View.GONE);
            mAPAdapter.addItems(action.getId(), action);
        }
    }


    @Override
    protected RecyclerView getListView() {
        return mAdvActionRV;
    }

    @Override
    public void dispose() {
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