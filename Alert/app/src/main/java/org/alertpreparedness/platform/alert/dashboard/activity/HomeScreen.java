package org.alertpreparedness.platform.alert.dashboard.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.BaseActivity;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.risk_monitoring.RiskActivity;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;


public class HomeScreen extends BaseActivity {

    @BindView(R.id.txt_title)
    TextView mTxtTitle;
    @BindView(R.id.mainScreen)
    FrameLayout mMainScreen;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.my_drawer_layout)
    DrawerLayout mMyDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);

        initListeners();

        setUpNavigationView();

    }

    private void initListeners() {
        mNavView.setNavigationItemSelectedListener((menuItem) -> {
            mMyDrawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
                case R.id.nav_risk:
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x -> {
//                        RiskMonitoringActivity.startActivity(this);
                        startActivity(RiskActivity.RiskIntent.getIntent(AlertApplication.getContext()));
                    });
                    break;
                case R.id.nav_home:
                    break;
            }
            return true;
        });
    }

    private void setUpNavigationView() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mMyDrawerLayout, toolbar, R.string.nav_advanced, R.string.app_name) {
        };
        mMyDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


}
