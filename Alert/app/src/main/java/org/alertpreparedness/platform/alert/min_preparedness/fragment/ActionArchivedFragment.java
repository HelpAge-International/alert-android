package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dashboard.fragment.HomeFragment;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.NetworkFetcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionArchivedFragment extends BaseArchivedFragment implements ActionAdapter.ItemSelectedListener, NetworkFetcher.NetworkFetcherListener {

    @BindView(R.id.rvMinAction)
    RecyclerView mActionRV;

    @BindView(R.id.tvStatus)
    TextView tvActionArchived;

    @BindView(R.id.imgStatus)
    ImageView imgArchived;

    @Nullable
    @BindView(R.id.tvNoAction)
    TextView txtNoAction;

    private ActionAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_minimum, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {
        assert imgArchived != null;
        imgArchived.setImageResource(R.drawable.ic_close_round_gray);
        assert tvActionArchived != null;
        tvActionArchived.setText(R.string.archived_title);
        tvActionArchived.setTextColor(getResources().getColor(R.color.alertGray));

        mAdapter = new ActionAdapter(getContext(), dbActionRef, this);
        mActionRV.setAdapter(mAdapter);

        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));


        new NetworkFetcher(this).fetch();

        dbActionBaseRef.child(user.countryID).addChildEventListener(new ArchivedChildListener(user.countryID));

    }

    @Override
    public void onActionItemSelected(int pos, String key, String userTypeID) {
        SheetMenu.with(getContext()).setMenu(R.menu.menu_archived).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.reactive_action:
                    //TODO
                    Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Reactivate Clicked", Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.action_notes:
                    Intent intent2 = new Intent(getActivity(), AddNotesActivity.class);
                    intent2.putExtra(AddNotesActivity.PARENT_ACTION_ID, getAdapter().getItem(pos).getId());
                    intent2.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent2);
                    break;
                case R.id.attachments:
                    Intent intent3 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent3.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, getAdapter().getItem(pos).getId());
                    intent3.putExtra(ViewAttachmentsActivity.ACTION_ID, key);
                    startActivity(intent3);
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

    @Override
    public void onNetworkFetcherResult(NetworkFetcher.NetworkFetcherResult networkFetcherResult) {
        for (String id : networkFetcherResult.all()) {
            if(id != null) {
                dbActionBaseRef.child(id).addChildEventListener(new ArchivedChildListener(id));
            }
        }
    }
}
