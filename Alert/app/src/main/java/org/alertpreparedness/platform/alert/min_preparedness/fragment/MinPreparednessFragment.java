package org.alertpreparedness.platform.alert.min_preparedness.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.ActionAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.responseplan.ActiveFragment;
import org.alertpreparedness.platform.alert.responseplan.ArchivedFragment;
import org.alertpreparedness.platform.alert.responseplan.ResponsePlanObj;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 13/12/2017.
 */

public class MinPreparednessFragment extends Fragment {

    @BindView(R.id.action_pager)
    ViewPager mPager;
    private PagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_min_preparedness, container, false);

        ButterKnife.bind(this, v);

        initViews();

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.title_min_preparedness);
        ((MainDrawer)getActivity()).removeActionbarElevation();

        return v;
    }

    private void initViews() {
//        fabCreateAPA.setVisibility(View.GONE);
        mAdapter = new MinPreparednessFragment.PagerAdapter(getFragmentManager());
        mPager.setOffscreenPageLimit(5);
        mPager.setAdapter(mAdapter);
    }


    private class PagerAdapter extends FragmentStatePagerAdapter {

        private Fragment one = new ActionExpiredFragment();
        private Fragment two = new ActionUnassignedFragment();
        private Fragment three = new ActionCompletedFragment();
        private Fragment four = new ActionArchivedFragment();
        private Fragment five = new InProgressFragment();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 1:
                    return one;
                case 2:
                    return two;
                case 3:
                    return three;
                case 4:
                    return four;
                default:
                    return five;
            }
        }

        public void stopAll() {
            one.onStop();
            three.onStop();
            two.onStop();
            five.onStop();
            four.onStop();
        }

        @Override
        public int getCount() {
            return 5;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopAll();
    }

}
