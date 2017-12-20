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
import org.alertpreparedness.platform.alert.dagger.annotation.ResponsePlansRef;
import org.alertpreparedness.platform.alert.model.User;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 12/12/2017.
 */

public class ResponsePlanFragment extends Fragment implements ResponsePlansAdapter.ItemSelectedListner {

    @BindView(R.id.rvPlans)
    RecyclerView mPlansList;

    @Inject @ResponsePlansRef
    DatabaseReference responsePlans;

    @Inject
    User user;

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

        ArrayList<ResponsePlanObj> items = new ArrayList<>();

        System.out.println(responsePlans.getKey());

        System.out.println("responsePlans = " + responsePlans);

        mPlansList.setAdapter(new ResponsePlansAdapter(getContext(), items, this));
        mPlansList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPlansList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        responsePlans.addValueEventListener(new ValueEventListener() {

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {

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
                            new Date(createdAt))
                    );
                    mPlansList.getAdapter().notifyItemInserted(items.size()-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }

        });
//
//        items.add(new ResponsePlanObj(
//                "Cold Freeze",
//                "90%",
//                "Lorem inpum dlor sit amet",
//                0,
//                new Date()
//        ));


    }

    @Override
    public void onResponsePlanSelected(int pos) {
        ApprovalStatusDialog dialog = new ApprovalStatusDialog();
        Bundle data = new Bundle();
        ApprovalStatusObj[] items = new ApprovalStatusObj[] {
                new ApprovalStatusObj("Country Director", 0),
                new ApprovalStatusObj("Regional Director", 0)
        };
        data.putParcelableArray(ApprovalStatusDialog.APPROVAL_STATUSES, items);
        dialog.setArguments(data);
        dialog.show(getActivity().getSupportFragmentManager(), "alert_level");
    }
}
