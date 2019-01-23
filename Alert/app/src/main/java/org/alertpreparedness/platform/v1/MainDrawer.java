package org.alertpreparedness.platform.v1;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.v1.adv_preparedness.fragment.AdvPreparednessFragment;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.v1.dagger.annotation.PermissionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.UserRef;
import org.alertpreparedness.platform.v1.dashboard.activity.CreateAlertActivity;
import org.alertpreparedness.platform.v1.dashboard.fragment.HomeFragment;
import org.alertpreparedness.platform.v1.mycountry.MyCountryFragment;
import org.alertpreparedness.platform.v1.min_preparedness.fragment.MinPreparednessFragment;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.responseplan.ResponsePlanFragment;
import org.alertpreparedness.platform.v1.risk_monitoring.view.RiskFragment;
import org.alertpreparedness.platform.v1.settings.SettingsFragment;
import org.alertpreparedness.platform.v1.utils.AppUtils;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.v2.GlideApp;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;

public class MainDrawer extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private int mCurrentItem = R.id.nav_home;

    public enum ActionBarState {
        ALERT,
        NORMAL
    }

    private ActionBarDrawerToggle drawerToggle;
    private FirebaseAuth firebaseAuth;
    public static final String TAG = "MAIN_DRAWER";

    static class HeaderViews {
        @BindView(R.id.tvUserName) TextView mUsername;
        @BindView(R.id.tvDepartment) TextView mDepartment;
        @BindView(R.id.img_profile)
        CircleImageView logo;
    }

    final HeaderViews header = new HeaderViews();

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.main_drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.alert_appbar)
    public Toolbar alertToolbar;

    @BindView(R.id.toolbar)
    public Toolbar normalToolbar;

    @BindView(R.id.custom_bar_title)
    TextView appBarTitle;

    @BindView(R.id.alert_appbar_layout)
    CardView alertActionbarContainer;

    @BindView(R.id.normal_action_bar)
    CardView normalActionbarContainer;

    @Inject @UserRef
    DatabaseReference userRef;

    @Inject @AgencyRef
    DatabaseReference agencyRef;

    @Inject
    User user;

    @Inject
    @PermissionRef
    DatabaseReference permissionsRef;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);

        setContentView(R.layout.activity_main_drawer);

        ButterKnife.bind(this);
        ButterKnife.bind(header, navigationView.getHeaderView(0));

        DependencyInjector.applicationcomponent().inject(this);

        setUserName();

        appBarTitle.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        ((AlertApplication)getApplicationContext()).startPermissionListeners(permissionsRef, user);
    }

    public void removeActionbarElevation() {
        normalActionbarContainer.setCardElevation(0);
        alertActionbarContainer.setCardElevation(0);
    }


    public void showActionbarElevation() {
        normalActionbarContainer.setCardElevation(8);
        alertActionbarContainer.setCardElevation(8);
    }

    private void setUserName() {

        agencyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String res = String.format(
                        getString(R.string.navbar_subtitle),
                        AppUtils.getUserTypeString(user.getUserType()),
                        dataSnapshot.child("name").getValue(String.class),
                        user.getCountryName()
                );
                header.mDepartment.setText(res);
                String urlPath = (String)dataSnapshot.child("logoPath").getValue();
                GlideApp.with(MainDrawer.this)
                        .load(urlPath)
                        .dontAnimate()
                        .placeholder(R.drawable.agency_icon_placeholder)
                        .into(header.logo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    String firstname = dataSnapshot.child("firstName").getValue(String.class);
                    String lastname = dataSnapshot.child("lastName").getValue(String.class);

                    header.mUsername.setText(String.format("%s %s", firstname, lastname));

                }
                catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void toggleActionBar(ActionBarState type) {
        switch (type) {
            case ALERT:
                alertActionbarContainer.setVisibility(View.VISIBLE);
                normalActionbarContainer.setVisibility(View.GONE);
                alertActionbarContainer.setCardElevation(8);
                setSupportActionBar(alertToolbar);
                break;
            case NORMAL:
                normalActionbarContainer.setCardElevation(8);
                alertActionbarContainer.setVisibility(View.GONE);
                normalActionbarContainer.setVisibility(View.VISIBLE);
                setSupportActionBar(normalToolbar);
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    public void toggleActionBarWithTitle(ActionBarState type, @StringRes int title, @Nullable int bg) {
        toggleActionBar(type);
        switch (type) {
            case ALERT:
                appBarTitle.setText(title);
                appBarTitle.setBackgroundResource(bg);
                break;
            case NORMAL:
                assert getSupportActionBar() != null;
                getSupportActionBar().setTitle(title);
                break;
        }
    }

    public void toggleActionBarWithTitle(ActionBarState type, @StringRes int title) {
        toggleActionBar(type);
        switch (type) {
            case ALERT:
                appBarTitle.setText(title);
                break;
            case NORMAL:
                assert getSupportActionBar() != null;
                getSupportActionBar().setTitle(title);
                break;
        }
    }

    protected void setFragment(Fragment fragment) {
        AppUtils.setFragment(MainDrawer.this, R.id.content_frame, fragment, "main");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view) {
        if (view == appBarTitle) {
            startActivity(new Intent(this, CreateAlertActivity.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawers();
        if (mCurrentItem != item.getItemId()) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x-> setFragment(new HomeFragment()));
    //                setFragment(new HomeFragment());
                    break;
                case R.id.nav_risk:
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x -> setFragment(new RiskFragment()));
                    break;
                case R.id.nav_minimum:
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x -> setFragment(new MinPreparednessFragment()));
                    break;
                case R.id.nav_advanced:
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x -> setFragment(new AdvPreparednessFragment()));
                    break;
                case R.id.nav_response:
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x-> setFragment(new ResponsePlanFragment()));
                    break;
                case R.id.nav_my_country:
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x-> setFragment(new MyCountryFragment()));
                    break;
                case R.id.nav_settings:
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x-> setFragment(new SettingsFragment()));
                    break;

            }
            mCurrentItem = item.getItemId();
        }
        return false;
    }

}
