package org.alertpreparedness.platform.v1.min_preparedness.fragment;


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
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.v1.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.ClockSettingsActionObservable;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.v1.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.v1.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.v1.min_preparedness.adapter.ActionAdapter;
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
 * A simple {@link Fragment} subclass.
 */
public class InProgressFragment extends Fragment implements ActionAdapter.ActionAdapterListener, UsersListDialogFragment.ItemSelectedListener {

    private String actionID;

    @Inject
    @ActionRef
    public DatabaseReference dbActionRef;

    public InProgressFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @BindView(R.id.tvNoAction)
    TextView txtNoAction;

    @Inject
    PermissionsHelper permissions;

    private ActionAdapter mAdapter;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();

    @Inject
    User user;

    @Inject
    @ClockSettingsActionObservable
    Flowable<Collection<ActionItemWrapper>> actionFlowable;

    CompositeDisposable disposable = new CompositeDisposable();

    @Nullable
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
        mAdapter = new ActionAdapter(getContext(), this);
        assert mActionRV != null;
        mActionRV.setAdapter(mAdapter);

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

                if(actionModel.getAsignee() != null && actionModel.getAsignee().equals(user.getUserID()) && !actionModel.getIsComplete() && !actionModel.getIsArchived() && actionModel.getLevel() == Constants.MPA
                        && wrapper.checkActionInProgress()) {
                    onActionRetrieved(actionModel);
                    result.add(actionModel.getId());
                }

            }
            mAdapter.updateKeys(result);

        }));
    }


    @Override
    public void onActionItemSelected(int pos, String key, String parentId) {
        this.actionID = key;
        SheetMenu.with(getContext()).setMenu(R.menu.menu_in_progress_mpa).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.complete_action:
                    if(permissions.checkCompleteMPAAction(mAdapter.getItem(pos), getActivity())) {
                        Intent intent = new Intent(getActivity(), CompleteActionActivity.class);
                        intent.putExtra(CompleteActionActivity.REQUIRE_DOC, mAdapter.getItem(pos).getRequireDoc());
                        intent.putExtra(CompleteActionActivity.ACTION_KEY, key);
                        intent.putExtra(CompleteActionActivity.PARENT_KEY, parentId);
                        startActivity(intent);
                    }
                    break;
                case R.id.reassign_action:
                    if (permissions.checkMPAActionAssign(mAdapter.getItem(pos), getActivity())) {
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
                    break;
            }
            return false;
        }).show();
    }

    @Override
    public void itemRemoved(String key) {
        if(mAdapter.getItemCount() == 0) {
            txtNoAction.setVisibility(View.VISIBLE);
        }
    }


    //region UsersListDialogFragment.ActionAdapterListener
    @Override
    public void onItemSelected(UserModel model) {
        long millis = System.currentTimeMillis();
        dbActionRef.child(actionID).child("asignee").setValue(model.getUserID());
        dbActionRef.child(actionID).child("updatedAt").setValue(millis);
        mAdapter.notifyDataSetChanged();
    }
    //endregion

    public void onActionRetrieved(ActionModel action) {
        if(permissions.checkCanViewMPA(action)) {
            txtNoAction.setVisibility(View.GONE);
            mAdapter.addItems(action.getId(), action);
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
