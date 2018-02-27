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

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.adv_preparedness.fragment.UsersListDialogFragment;
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.NetworkFetcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProgressFragment extends BaseInProgressFragment implements ActionAdapter.ItemSelectedListener, UsersListDialogFragment.ItemSelectedListener, NetworkFetcher.NetworkFetcherListener {

    private String actionID;

    public InProgressFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @BindView(R.id.tvNoAction)
    TextView txtNoAction;

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
        mAdapter = new ActionAdapter(getContext(), dbActionBaseRef, this);
        assert mActionRV != null;
        mActionRV.setAdapter(mAdapter);

        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        new NetworkFetcher(this).fetch();

        dbActionBaseRef.child(user.countryID).addChildEventListener(new InProgressListener(user.countryID));

    }


    @Override
    public void onActionItemSelected(int pos, String key, String userTypeID) {
        this.actionID = key;
        SheetMenu.with(getContext()).setMenu(R.menu.menu_in_progress).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.complete_action:
                    Intent intent = new Intent(getActivity(), CompleteActionActivity.class);
                    intent.putExtra(CompleteActionActivity.REQUIRE_DOC, getAdapter().getItem(pos).getRequireDoc());
                    intent.putExtra("ACTION_KEY", key);
                    intent.putExtra("USER_KEY", userTypeID);
                    startActivity(intent);
                    break;
                case R.id.reassign_action:
                    dialog.show(getActivity().getFragmentManager(), "users_list");
                    break;
                case R.id.action_notes:
                    Intent intent3 = new Intent(getActivity(), AddNotesActivity.class);
                    intent3.putExtra(AddNotesActivity.PARENT_ACTION_ID, getAdapter().getItem(pos).getId());
                    intent3.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent3);
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

    @Override
    protected int getType() {
        return Constants.MPA;
    }

    @Override
    protected PreparednessAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected RecyclerView getListView() {
        return mActionRV;
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
        ((ActionAdapter)getAdapter()).notifyDataSetChanged();
    }
    //endregion

    @Override
    public void onNetworkFetcherResult(NetworkFetcher.NetworkFetcherResult networkFetcherResult) {
        for (String id : networkFetcherResult.all()) {
            dbActionBaseRef.child(id).addChildEventListener(new InProgressListener(id));
        }
    }
}
