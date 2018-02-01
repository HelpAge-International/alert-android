package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 21/12/2017.
 */

public class ActionUnassignedFragment extends BaseUnassignedFragment {

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

    private ActionAdapter mUnassignedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_minimum, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        return v;
    }

    private void initViews() {
        assert imgUnassigned != null;
        imgUnassigned.setImageResource(R.drawable.ic_close_round);
        assert tvActionUnassigned != null;
        tvActionUnassigned.setText("Unassigned");
        tvActionUnassigned.setTextColor(getResources().getColor(R.color.alertRed));

        mUnassignedAdapter = new ActionAdapter(getContext(), dbActionBaseRef, this);
        mActionRV.setAdapter(mUnassignedAdapter);
        mActionRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActionRV.setItemAnimator(new DefaultItemAnimator());
        mActionRV.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        ids = new String[]{user.getCountryID(), user.getNetworkID(), user.getLocalNetworkID(), user.getNetworkCountryID()};

        for (String id : ids) {
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

    @Override
    protected int getType() {
        return Constants.MPA;
    }

    @Override
    protected PreparednessAdapter getAdapter() {
        return mUnassignedAdapter;
    }


    @Override
    public void onActionItemSelected(int pos, String key, String userTypeID) {
        Snackbar.make(getActivity().findViewById(R.id.cl_in_progress), "Currently under development!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected TextView getNoActionView() {
        return txtNoAction;
    }


}
