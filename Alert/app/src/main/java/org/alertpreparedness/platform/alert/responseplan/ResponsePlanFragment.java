package org.alertpreparedness.platform.alert.responseplan;

import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.ExtensionHelperKt;
import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.ResponsePlansRef;
import org.alertpreparedness.platform.alert.dashboard.activity.UpdateAlertActivity;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 12/12/2017.
 */

public class ResponsePlanFragment extends Fragment implements ResponsePlansAdapter.ItemSelectedListner, ValueEventListener {

    @BindView(R.id.rvPlans)
    RecyclerView mPlansList;

    @Inject @ResponsePlansRef
    DatabaseReference responsePlans;

    @Inject
    User user;

    private ArrayList<ResponsePlanObj> items;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_response_plans, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.response_plans);

        return v;
    }

    private void initViews() {

        items = new ArrayList<>();

        mPlansList.setAdapter(new ResponsePlansAdapter(getContext(), items, this));
        mPlansList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPlansList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        responsePlans.addValueEventListener(this);

    }

    @Override
    public void onResponsePlanSelected(int pos) {
        ApprovalStatusDialog dialog = new ApprovalStatusDialog();
        Bundle data = new Bundle();
        ApprovalStatusObj[] items = new ApprovalStatusObj[] {
                new ApprovalStatusObj("Country Director", this.items.get(pos).countryApproval),
                new ApprovalStatusObj("Regional Director", this.items.get(pos).regionalApproval),
                new ApprovalStatusObj("Global Director", this.items.get(pos).globalApproval)
        };
        data.putParcelableArray(ApprovalStatusDialog.APPROVAL_STATUSES, items);
        dialog.setArguments(data);
        dialog.show(getActivity().getSupportFragmentManager(), "alert_level");
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot child : dataSnapshot.getChildren()) {

            System.out.println("child country = " + child.child("approval").child("countryDirector").getValue());
            System.out.println("child global = " + child.child("approval").child("globalDirector").getValue());
            System.out.println("child region = " + child.child("approval").child("regionDirector").getValue());

            int regionalApproval = getApprovalStatus(child.child("approval").child("regionDirector"));
            int countryApproval = getApprovalStatus(child.child("approval").child("countryDirector"));
            int globalApproval = getApprovalStatus(child.child("approval").child("globalDirector"));

            Long createdAt = (Long) child.child("timeCreated").getValue();
            String hazardType = ExtensionHelperKt.getHazardTypes().get(Integer.valueOf((String) child.child("hazardScenario").getValue()));
            String percentCompleted = String.valueOf(child.child("sectionsCompleted").getValue());
            int status = Integer.valueOf(String.valueOf(child.child("status").getValue()));
            String name = (String)child.child("name").getValue();

            items.add(new ResponsePlanObj(
                    hazardType,
                    percentCompleted,
                    name,
                    status,
                    new Date(createdAt),
                    regionalApproval,
                    countryApproval,
                    globalApproval)
            );
            mPlansList.getAdapter().notifyItemInserted(items.size()-1);
        }
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private int getApprovalStatus(DataSnapshot ref) {
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
