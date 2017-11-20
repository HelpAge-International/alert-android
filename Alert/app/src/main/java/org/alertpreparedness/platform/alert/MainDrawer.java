package org.alertpreparedness.platform.alert;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;

import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.login.activity.LoginScreen;
import org.alertpreparedness.platform.alert.risk_monitoring.view.RiskActivity;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class MainDrawer extends BaseActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    protected FrameLayout content;

    protected void onCreateDrawer(final int layoutResID) {
        setContentView(R.layout.activity_main_drawer);

        content = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, content, true);

        firebaseAuth = FirebaseAuth.getInstance();

        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x->{
                            startActivity(new Intent(MainDrawer.this, HomeScreen.class));
                        });
                        break;
                    case R.id.nav_risk:
                        Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).take(1).subscribe(x->{
                            startActivity(RiskActivity.RiskIntent.getIntent(MainDrawer.this));
                        });
                        break;
                    case R.id.nav_logout:
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginScreen.class));
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

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
}
