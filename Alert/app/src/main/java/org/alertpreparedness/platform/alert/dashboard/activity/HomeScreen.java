package org.alertpreparedness.platform.alert.dashboard.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class HomeScreen extends AppCompatActivity {

    @BindView(R.id.txt_title)
    TextView mTxtTitle;
    @BindView(R.id.mainScreen)
    FrameLayout mMainScreen;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.my_drawer_layout)
    DrawerLayout mMyDrawerLayout;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);

        initListners();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        setUpNavigationView();

    }

    private void initListners() {
        mNavView.setNavigationItemSelectedListener((menuItem) -> {
            Timber.d(menuItem.toString());
            switch (menuItem.getItemId()) {
                case R.id.nav_risk:

                    break;
                case R.id.nav_home:
                    break;
            }
            return true;
        });
    }

    private void setUpNavigationView() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.nav_advanced, R.string.app_name) {
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


}
