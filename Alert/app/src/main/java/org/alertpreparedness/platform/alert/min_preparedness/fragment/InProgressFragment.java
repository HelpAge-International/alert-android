package org.alertpreparedness.platform.alert.min_preparedness.fragment;


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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.action.ActionFetcher;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PermissionsHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProgressFragment extends Fragment implements ActionAdapter.ActionAdapterListener, UsersListDialogFragment.ItemSelectedListener, ActionFetcher.ActionRetrievalListener {

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

    @Nullable
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
        mAdapter = new ActionAdapter(getContext(), this);
        assert mActionRV != null;
        mActionRV.setAdapter(mAdapter);

        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        new ActionFetcher(Constants.MPA, ActionFetcher.ACTION_STATE.IN_PROGRESS, this).fetch((ids)-> {});

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
                    intent3.putExtra(AddNotesActivity.PARENT_ACTION_ID, mAdapter.getItem(pos).getId());
                    intent3.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent3);
                    break;
                case R.id.attachments:
                    Intent intent2 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent2.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, mAdapter.getItem(pos).getId());
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

    @Override
    public void onActionRetrieved(DataSnapshot snapshot, Action action) {
        if(permissions.checkCanViewMPA(action)) {
            txtNoAction.setVisibility(View.GONE);
            mAdapter.addItems(snapshot.getKey(), action);
        }
    }

    @Override
    public void onActionRemoved(DataSnapshot snapshot) {
        mAdapter.removeItem(snapshot.getKey());
    }
}
