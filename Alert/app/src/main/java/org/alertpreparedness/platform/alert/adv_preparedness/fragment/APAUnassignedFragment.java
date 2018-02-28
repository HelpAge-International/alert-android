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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.action.ActionFetcher;
import org.alertpreparedness.platform.alert.adv_preparedness.activity.EditAPAActivity;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.BaseAPAFragment;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.BaseUnassignedFragment;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.NetworkFetcher;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAUnassignedFragment extends BaseAPAFragment implements APActionAdapter.APAAdapterListener, UsersListDialogFragment.ItemSelectedListener, ActionFetcher.ActionRetrievalListener {

    public APAUnassignedFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.rvAdvAction)
    RecyclerView mAdvActionRV;

    @BindView(R.id.imgStatus)
    ImageView imgActionUnassigned;

    @BindView(R.id.tvStatus)
    TextView tvActionUnassigned;

    @BindView(R.id.tvAPANoAction)
    TextView txtNoAction;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    private APActionAdapter mAPAdapter;
    private UsersListDialogFragment dialog = new UsersListDialogFragment();
    private String actionID;
    private int freqValue = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_advanced, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        dialog.setListener(this);

        return v;
    }

    private void initViews() {
        assert imgActionUnassigned != null;
        imgActionUnassigned.setImageResource(R.drawable.ic_close_round);
        assert tvActionUnassigned != null;
        tvActionUnassigned.setText("Unassigned");
        tvActionUnassigned.setTextColor(getResources().getColor(R.color.alertRed));
        mAPAdapter = new APActionAdapter(getContext(), this);
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);

        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        new ActionFetcher(Constants.APA, ActionFetcher.ACTION_STATE.UNASSIGNED, this).fetch((ids) -> {
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
        this.actionID = key;
        SheetMenu.with(getContext()).setMenu(R.menu.menu_unassigned_apa).setClick(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.assign_action:
                    dialog.show(getActivity().getFragmentManager(), "users_list");
                    break;
                case R.id.edit_action:
                    Intent i = new Intent(getContext(), EditAPAActivity.class);
                    i.putExtra(EditAPAActivity.APA_ID, key);
                    startActivity(i);
                    break;
            }
            return false;
        }).show();
    }

    @Override
    public void onAdapterItemRemoved(String key) {

    }

    @Override
    public void onItemSelected(UserModel userModel) {
        long millis = System.currentTimeMillis();
        dbActionRef.child(actionID).child("asignee").setValue(userModel.getUserID());
        dbActionRef.child(actionID).child("updatedAt").setValue(millis);
        mAPAdapter.removeItem(actionID);
        mAPAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActionRetrieved(String key, Action action) {
        txtNoAction.setVisibility(View.VISIBLE);
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