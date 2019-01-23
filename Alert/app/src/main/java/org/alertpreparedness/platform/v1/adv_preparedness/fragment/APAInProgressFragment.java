package org.alertpreparedness.platform.v1.adv_preparedness.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.annotation.ActiveActionObservable;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.adv_preparedness.activity.EditAPAActivity;
import org.alertpreparedness.platform.v1.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.v1.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.v1.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.v1.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.FetcherResultItem;
import org.alertpreparedness.platform.v1.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.v1.interfaces.DisposableFragment;
import org.alertpreparedness.platform.v1.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PermissionsHelper;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 05/01/2018.
 */

public class APAInProgressFragment extends BaseAPAFragment implements APActionAdapter.APAAdapterListener, UsersListDialogFragment.ItemSelectedListener, DisposableFragment {

    private String actionID;

    public APAInProgressFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

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

    CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    PermissionsHelper permissions;

    @Inject
    @ActiveActionObservable
    Flowable<FetcherResultItem<Collection<ActionItemWrapper>>> actionFlowable;

    private APActionAdapter mAPAdapter;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();

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

            ArrayList<String> keysToUpdate = new ArrayList<>();

            for (ActionItemWrapper wrapper : collectionFetcherResultItem.getValue()) {
                ActionModel actionModel = wrapper.makeModel();
                if (actionModel.getAsignee() != null
                        && user.getUserID().equals(actionModel.getAsignee())
                        && wrapper.checkActionInProgress()
                        && !actionModel.getIsArchived()
                        && !actionModel.getIsComplete()
                        && actionModel.getLevel() == Constants.APA
                        ) {
                    keysToUpdate.add(actionModel.getId());
                    onActionRetrieved(actionModel);
                }
            }
            mAPAdapter.updateKeys(keysToUpdate);

        }));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        this.actionID = key;
        SheetMenu.with(getContext()).setMenu(R.menu.menu_in_progress).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.edit:
                    if(permissions.checkEditAPA(mAPAdapter.getItem(pos), getActivity())) {
                        Intent i = new Intent(getContext(), EditAPAActivity.class);
                        i.putExtra(EditAPAActivity.MODEL, mAPAdapter.getItem(pos));
                        startActivity(i);
                    }
                    break;
                case R.id.complete_action:
                    if(permissions.checkCompleteAPAAction(mAPAdapter.getItem(pos), getActivity())) {
                        Intent intent = new Intent(getActivity(), CompleteActionActivity.class);
                        intent.putExtra(CompleteActionActivity.REQUIRE_DOC, mAPAdapter.getItem(pos).getRequireDoc());
                        intent.putExtra(CompleteActionActivity.ACTION_KEY, key);
                        intent.putExtra(CompleteActionActivity.PARENT_KEY, parentId);
                        startActivity(intent);
                    }
                    break;
                case R.id.reassign_action:
                    if(permissions.checkAssignAPA(mAPAdapter.getItem(pos), getActivity())) {
                        dialog.show(getActivity().getFragmentManager(), "users_list");
                    }
                    break;
                case R.id.action_notes:
                    Intent intent3 = new Intent(getActivity(), AddNotesActivity.class);
                    intent3.putExtra(AddNotesActivity.PARENT_ACTION_ID, parentId);
                    intent3.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent3);
                    break;
                case R.id.attachments:
                    Intent intent2 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent2.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, parentId);
                    intent2.putExtra(ViewAttachmentsActivity.ACTION_ID, key);
                    startActivity(intent2);
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

    @Override
    public void dispose() {
        disposable.dispose();
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

}
