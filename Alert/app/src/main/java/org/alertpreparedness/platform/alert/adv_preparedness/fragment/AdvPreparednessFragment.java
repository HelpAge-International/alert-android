package org.alertpreparedness.platform.alert.adv_preparedness.fragment;

import android.content.Intent;
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
import org.alertpreparedness.platform.alert.adv_preparedness.activity.CreateAPAActivity;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.utils.PermissionsHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 05/01/2018.
 */

public class AdvPreparednessFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.action_pager)
    ViewPager mPager;

    @BindView(R.id.fabAddAPA)
    public android.support.design.widget.FloatingActionButton fabCreateAPA;

    @Inject
    PermissionsHelper permissions;
    private PagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_adv_preparedness, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.userScopeComponent().inject(this);

        initViews();

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.title_adv_preparedness);
        ((MainDrawer)getActivity()).removeActionbarElevation();

        return v;
    }

    private void initViews() {
        if(!permissions.checkCreateAPA(getActivity())) {
            fabCreateAPA.setVisibility(View.GONE);
        }
        else {
            fabCreateAPA.setOnClickListener(this);
        }

        mAdapter = new AdvPreparednessFragment.PagerAdapter(getFragmentManager());

        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(6);

    }

    @Override
    public void onClick(View view) {
        if(view == fabCreateAPA){
            Intent intent = new Intent(getActivity(), CreateAPAActivity.class);
            startActivity(intent);
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private Fragment one = new APAExpiredFragment();
        private Fragment two = new APAUnassignedFragment();
        private Fragment three = new APACompletedFragment();
        private Fragment four = new APAInactiveFragment();
        private Fragment five = new APAArchivedFragment();
        private Fragment six = new APAInProgressFragment();

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
                case 5:
                    return five;
                default:
                    return six;
            }
        }

        public void stopAll() {
            one.onStop();
            three.onStop();
            two.onStop();
            five.onStop();
            four.onStop();
            six.onStop();
        }

        @Override
        public int getCount() {
            return 6;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopAll();
        System.out.println("STOPPED stop");
    }

}
