package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.adv_preparedness.activity.EditAPAActivity;
import org.alertpreparedness.platform.alert.adv_preparedness.adapter.APActionAdapter;
import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionMandatedRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.min_preparedness.activity.AddNotesActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.alert.min_preparedness.activity.ViewAttachmentsActivity;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.BaseUnassignedFragment;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.NetworkFetcher;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class APAUnassignedFragment extends BaseUnassignedFragment implements APActionAdapter.ItemSelectedListener, UsersListDialogFragment.ItemSelectedListener, NetworkFetcher.NetworkFetcherListener {

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
        mAPAdapter = new APActionAdapter(getContext(), dbActionRef, this);
        assert mAdvActionRV != null;
        mAdvActionRV.setAdapter(mAPAdapter);

        mAdvActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdvActionRV.setItemAnimator(new DefaultItemAnimator());
        mAdvActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        new NetworkFetcher(this).fetch();

        dbActionBaseRef.child(user.countryID).addChildEventListener(new UnassignedChildListener(user.countryID));
        dbActionBaseRef.child(user.countryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    getCHSForNewUser(user.countryID);
                    getMandatedForNewUser(user.countryID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
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
    public void onItemSelected(UserModel userModel) {
        long millis = System.currentTimeMillis();
        dbActionRef.child(actionID).child("asignee").setValue(userModel.getUserID());
        dbActionRef.child(actionID).child("updatedAt").setValue(millis);
        mAPAdapter.removeItem(actionID);
        mAPAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getType() {
        return Constants.APA;
    }

    @Override
    protected PreparednessAdapter getAdapter() {
        return mAPAdapter;
    }

    @Override
    protected TextView getNoActionView() {
        return txtNoAction;
    }

    @Override
    protected RecyclerView getListView() {
        return mAdvActionRV;
    }

    @Override
    public void onNetworkFetcherResult(NetworkFetcher.NetworkFetcherResult networkFetcherResult) {

        for (String id : networkFetcherResult.all()) {
            if(id != null) {
                dbActionBaseRef.child(id).addChildEventListener(new UnassignedChildListener(id));
                dbActionBaseRef.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            getCHSForNewUser(id);
                            getMandatedForNewUser(id);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}