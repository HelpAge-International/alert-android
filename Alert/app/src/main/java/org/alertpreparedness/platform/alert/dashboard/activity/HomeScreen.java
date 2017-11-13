package org.alertpreparedness.platform.alert.dashboard.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.PreferHelper;


public class HomeScreen extends  MainDrawer {
    private User user;
    private String[] users = {"administratorCountry", "countryDirector", "ert", "ertLeader", "partner"};

    public static final String mypreference = "mypref";
    public static final String userKey = "UserType";

    public static final PreferHelper sharedPreferences = new PreferHelper();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateDrawer(R.layout.activity_home_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.alert_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        for(int i = 0; i < users.length; i++) {
            UserInfo.getUserType(this, users[i]);
        }
    }
}
