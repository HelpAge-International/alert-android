package org.alertpreparedness.platform.alert.min_preparedness.fragment;

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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 13/12/2017.
 */

public class MinPreparednessFragment extends Fragment {

    @BindView(R.id.rvActionsAssigned)
    RecyclerView actionsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_min_preparedness, container, false);

        ButterKnife.bind(this, v);

        initViews();

        return v;
    }

    private void initViews() {
        actionsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager alertlayoutManager = new LinearLayoutManager(getContext());
        actionsRecyclerView.setLayoutManager(alertlayoutManager);
        actionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        actionsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
