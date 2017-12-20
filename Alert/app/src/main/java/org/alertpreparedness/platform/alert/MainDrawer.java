package org.alertpreparedness.platform.alert;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dashboard.activity.CreateAlertActivity;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.dashboard.fragment.HomeFragment;
import org.alertpreparedness.platform.alert.login.activity.LoginScreen;
import org.alertpreparedness.platform.alert.mycountry.MyCountryFragment;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.MinPreparednessFragment;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.responseplan.ResponsePlanFragment;
import org.alertpreparedness.platform.alert.risk_monitoring.view.RiskFragment;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import io.reactivex.Observable;

public class MainDrawer extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private int mCurrentItem = R.id.nav_home;

    public enum ActionBarState {
        ALERT,
        NORMAL
    }

    private ActionBarDrawerToggle drawerToggle;
    private FirebaseAuth firebaseAuth;
    private UserInfo mUserInfo;
    public static final String TAG = "MAIN_DRAWER";

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

//    @BindView(R.id.tvUserName)
//    TextView tvUserName;
//
//    @BindView(R.id.tvDepartment)
//    TextView tvDepartment;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);

        setContentView(R.layout.activity_main_drawer);

        ButterKnife.bind(this);

        setUserName();

//        toggleActionBar(ActionBarState.ALERT);

        appBarTitle.setOnClickListener(this);

        mUserInfo = new UserInfo();

        firebaseAuth = FirebaseAuth.getInstance();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference db = ref.
                child(PreferHelper.getString(getApplicationContext(), Constants.APP_STATUS)).
                child("userPublic").child(PreferHelper.getString(getApplicationContext(), Constants.UID));

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    String firstname = dataSnapshot.child("firstName").getValue().toString();
                    String lastname = dataSnapshot.child("lastName").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    User user = new User(firstname, lastname, email);
                } catch (Exception e) {
                    Log.e(TAG, String.valueOf(e));
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
                case R.id.nav_logout:
                    PreferHelper.getInstance(getApplicationContext()).edit().remove(UserInfo.PREFS_USER).apply();
                    mUserInfo.clearAll();
                    firebaseAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginScreen.class));
                    finish();
                    break;
                case R.id.nav_minimum:
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x -> setFragment(new MinPreparednessFragment()));
                    break;
                case R.id.nav_response:
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x-> setFragment(new ResponsePlanFragment()));
                    break;
                case R.id.nav_my_country:
                    System.out.println("HERE CLOSED");
                    Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x-> setFragment(new MyCountryFragment()));
                    break;

            }
            mCurrentItem = item.getItemId();
        }
        return false;
    }

}
