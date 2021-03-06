package org.alertpreparedness.platform.v1.adv_preparedness.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.alertpreparedness.platform.v1.MainDrawer;
import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.adv_preparedness.activity.CreateAPAActivity;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.interfaces.DisposableFragment;
import org.alertpreparedness.platform.v1.utils.PermissionsHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 05/01/2018.
 */

public class AdvPreparednessFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.action_pager)
    ViewPager mPager;

    @BindView(R.id.fabAddAPA)
    public FloatingActionButton fabCreateAPA;

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

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopAll();
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private DisposableFragment one = new APAExpiredFragment();
        private DisposableFragment two = new APAUnassignedFragment();
        private DisposableFragment three = new APACompletedFragment();
        private DisposableFragment four = new APAInactiveFragment();
        private DisposableFragment five = new APAArchivedFragment();
        private DisposableFragment six = new APAInProgressFragment();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 1:
                    return (Fragment) one;
                case 2:
                    return (Fragment) two;
                case 3:
                    return (Fragment) three;
                case 4:
                    return (Fragment) four;
                case 5:
                    return (Fragment) five;
                default:
                    return (Fragment) six;
            }
        }

        public void stopAll() {
            one.dispose();
            three.dispose();
            two.dispose();
            five.dispose();
            four.dispose();
            six.dispose();
        }

        @Override
        public int getCount() {
            return 6;
        }

    }


}
