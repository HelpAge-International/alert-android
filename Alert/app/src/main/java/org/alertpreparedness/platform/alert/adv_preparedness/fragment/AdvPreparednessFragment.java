package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 05/01/2018.
 */

public class AdvPreparednessFragment extends Fragment {
    @BindView(R.id.action_pager)
    ViewPager mPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_min_preparedness, container, false);

        ButterKnife.bind(this, v);

        initViews();

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.title_adv_preparedness);
        ((MainDrawer)getActivity()).removeActionbarElevation();

        return v;
    }

    private void initViews() {
        mPager.setAdapter(new AdvPreparednessFragment.PagerAdapter(getFragmentManager()));
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 1:
                    return new APAExpiredFragment();
                case 2:
                    return new APAUnassignedFragment();
                case 3:
                    return new APACompletedFragment();
                case 4:
                    return new APAInactiveFragment();
                case 5:
                    return new APAArchivedFragment();
                default:
                    return new APAInProgressFragment();
            }
        }

        @Override
        public int getCount() {
            return 6;
        }

    }
}
