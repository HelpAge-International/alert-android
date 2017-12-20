package org.alertpreparedness.platform.alert.responseplan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.ExtensionHelperKt;
import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.ResponsePlansRef;
import org.alertpreparedness.platform.alert.model.User;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 12/12/2017.
 */

public class ActiveFragment extends Fragment implements ResponsePlansAdapter.ResponseAdapterListener, ValueEventListener {

    @BindView(R.id.rvPlans)
    RecyclerView mPlansList;

    @Inject @ResponsePlansRef
    DatabaseReference responsePlans;

    @Inject
    User user;

    protected ResponsePlansAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_response_plans_subfrag, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.response_plans);
        ((MainDrawer)getActivity()).removeActionbarElevation();
        return v;
    }

    private void initViews() {

        mAdapter = getmAdapter();
        mPlansList.setAdapter(mAdapter);

        mPlansList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPlansList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        responsePlans.addValueEventListener(this);

    }

    protected ResponsePlansAdapter getmAdapter() {
        return new ResponsePlansAdapter(getContext(), responsePlans, true, this);
    }

    @Override
    public void onResponsePlanSelected(int pos) {
        ApprovalStatusDialog dialog = new ApprovalStatusDialog();
        Bundle data = new Bundle();
        ApprovalStatusObj[] items = new ApprovalStatusObj[] {
                new ApprovalStatusObj("Country Director", mAdapter.getItem(pos).countryApproval),
                new ApprovalStatusObj("Regional Director", mAdapter.getItem(pos).regionalApproval),
                new ApprovalStatusObj("Global Director", mAdapter.getItem(pos).globalApproval)
        };
        data.putParcelableArray(ApprovalStatusDialog.APPROVAL_STATUSES, items);
        dialog.setArguments(data);
        dialog.show(getActivity().getSupportFragmentManager(), "alert_level");
    }



    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot child : dataSnapshot.getChildren()) {

            boolean isActive = (boolean)child.child("isActive").getValue();

            if(isActive) {
                int regionalApproval = getApprovalStatus(child.child("approval").child("regionDirector"));
                int countryApproval = getApprovalStatus(child.child("approval").child("countryDirector"));
                int globalApproval = getApprovalStatus(child.child("approval").child("globalDirector"));

                Long createdAt = (Long) child.child("timeCreated").getValue();
                String hazardType = ExtensionHelperKt.getHazardTypes().get(Integer.valueOf((String) child.child("hazardScenario").getValue()));
                String percentCompleted = String.valueOf(child.child("sectionsCompleted").getValue());
                int status = Integer.valueOf(String.valueOf(child.child("status").getValue()));
                String name = (String) child.child("name").getValue();

                System.out.println("child.getKey() = " + child.getKey());
                System.out.println("user.countryID = " + user.countryID);

                mAdapter.addItem(child.getKey(), new ResponsePlanObj(
                        hazardType,
                        percentCompleted,
                        name,
                        status,
                        new Date(createdAt),
                        regionalApproval,
                        countryApproval,
                        globalApproval)
                );
            }
        }
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    protected int getApprovalStatus(DataSnapshot ref) {
        if(ref.getValue() != null) {
            for (DataSnapshot child : ref.getChildren()) {
                if(child.getValue() != null) {
                   return  (int)((long)child.getValue());
                }
                else {
                    break;
                }
            }
        }
        return 0;
    }

    @Override
    public void onCancelled(DatabaseError firebaseError) {

    }


}
