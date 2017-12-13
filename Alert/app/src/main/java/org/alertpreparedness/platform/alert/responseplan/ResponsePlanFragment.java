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

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 12/12/2017.
 */

public class ResponsePlanFragment extends Fragment implements ResponsePlansAdapter.ItemSelectedListner {

    @BindView(R.id.rvPlans)
    RecyclerView mPlansList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_response_plans, container, false);

        ButterKnife.bind(this, v);

        initViews();

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.response_plans);

        return v;
    }

    private void initViews() {

        ArrayList<ResponsePlanObj> items = new ArrayList<>();
        items.add(new ResponsePlanObj(
                "Cold Freeze",
                "90%",
                "Lorem inpum dlor sit amet",
                0,
                new Date()
        ));

        mPlansList.setAdapter(new ResponsePlansAdapter(getContext(), items, this));
        mPlansList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPlansList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

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
