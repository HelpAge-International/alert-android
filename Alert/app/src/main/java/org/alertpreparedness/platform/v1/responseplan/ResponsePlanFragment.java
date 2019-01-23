package org.alertpreparedness.platform.v1.responseplan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.alertpreparedness.platform.v1.MainDrawer;
import org.alertpreparedness.platform.v1.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 12/12/2017.
 */

public class ResponsePlanFragment extends Fragment {

    @BindView(R.id.tabLayout)
    TabLayout mTabs;

    @BindView(R.id.pager)
    ViewPager mPager;
    private PagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_response_plans, container, false);

        ButterKnife.bind(this, v);

        initViews();

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.response_plans);
        ((MainDrawer)getActivity()).removeActionbarElevation();
        return v;
    }

    private void initViews() {

        mTabs.addTab(mTabs.newTab());
        mTabs.addTab(mTabs.newTab());
        mTabs.setupWithViewPager(mPager);
        mAdapter = new ResponsePlanFragment.PagerAdapter(getFragmentManager());
        mPager.setAdapter(mAdapter);

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private Fragment one = new ArchivedFragment();
        private Fragment two = new ActiveFragment();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return one;
                default:
                    return two;
            }
        }

        public void stopAll() {
            one.onStop();
            two.onStop();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return "Archived";
                default: return "Active";
            }
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        mAdapter.stopAll();
    }
}
