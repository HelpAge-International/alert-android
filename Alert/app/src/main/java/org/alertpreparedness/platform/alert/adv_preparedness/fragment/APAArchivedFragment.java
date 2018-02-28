package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

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

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.action.ActionFetcher;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.BaseAPAFragment;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAArchivedFragment extends BaseAPAFragment implements APActionAdapter.APAAdapterListener, ActionFetcher.ActionRetrievalListener {

    public APAArchivedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @Nullable
    @BindView(R.id.imgStatus)
    ImageView imgActionArchived;

    @Nullable
    @BindView(R.id.tvStatus)
    TextView tvActionArchived;

    @BindView(R.id.tvAPANoAction)
    TextView txtNoAction;

    private APActionAdapter mAPAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_advanced, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {
        assert imgActionArchived != null;
        imgActionArchived.setImageResource(R.drawable.ic_close_round_gray);
        assert tvActionArchived != null;
        tvActionArchived.setText(R.string.archived_title);
        tvActionArchived.setTextColor(getResources().getColor(R.color.alertGray));
        mAPAdapter = new APActionAdapter(getContext(), this);
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);

        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        new ActionFetcher(Constants.APA, ActionFetcher.ACTION_STATE.ARCHIVED, this).fetch((ids) -> {
            mAPAdapter.bindChildListeners(ids);
        });

        handleAdvFab();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActionItemSelected(int pos, String key) {
        SheetMenu.with(getContext()).setMenu(R.menu.menu_archived).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
//                case R.id.reactive_action:
//                    //TODO
//                    Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Reactivate Clicked", Snackbar.LENGTH_LONG).show();
//                    break;
                case R.id.action_notes:
                    Intent intent2 = new Intent(getActivity(), AddNotesActivity.class);
                    intent2.putExtra(AddNotesActivity.PARENT_ACTION_ID, mAPAdapter.getItem(pos).getId());
                    intent2.putExtra(AddNotesActivity.ACTION_ID, key);
                    startActivity(intent2);
                    break;
                case R.id.attachments:
                    Intent intent3 = new Intent(getActivity(), ViewAttachmentsActivity.class);
                    intent3.putExtra(ViewAttachmentsActivity.PARENT_ACTION_ID, mAPAdapter.getItem(pos).getId());
                    intent3.putExtra(ViewAttachmentsActivity.ACTION_ID, key);
                    startActivity(intent3);
                    break;
            }
            return false;
        }).show();
    }

    @Override
    public void onAdapterItemRemoved(String key) {
        if(mAPAdapter.getItemCount()==0) {
            txtNoAction.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActionRetrieved(String key, Action action) {
        txtNoAction.setVisibility(View.GONE);
        mAPAdapter.addItems(key, action);
    }

    @Override
    public void onActionRemoved(String key) {
        mAPAdapter.removeItem(key);
    }

    @Override
    protected RecyclerView getListView() {
        return mAdvActionRV;
    }
}

