package org.alertpreparedness.platform.alert.login.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.BaseActivity;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.offline.OfflineSyncHandler;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import javax.inject.Inject;

import timber.log.Timber;

public class SplashActivity extends BaseActivity {

    @Inject
    @AlertRef
    DatabaseReference alertRef;

    public static final int NOTIFICATION_ALERT = 0;
    public static final int NOTIFICATION_INDICATOR_ASSIGNED = 1;
    public static final int NOTIFICATION_INDICATOR_RESCHEDULE = 2;
    public static final int NOTIFICATION_ACTION_ASSIGNED = 3;
    public static final int NOTIFICATION_ACTION_RESCHEDULE = 4;
    public static final int NOTIFICATION_ACTION_COUNTRY_RESCHEDULE = 5;
    public static final int NOTIFICATION_ACTION_LOCAL_NETWORK_RESCHEDULE = 6;
    public static final int NOTIFICATION_ACTION_NETWORK_COUNTRY_RESCHEDULE = 7;
    public static final int NOTIFICATION_RESPONSE_PLAN_RESCHEDULE = 8;
    public static final int NOTIFICATION_RESPONSE_PLAN_COUNTRY_RESCHEDULE = 9;

    public static final String NOTIFICATION_FIELD_TYPE = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This method will be executed once the timer is over
        // Start your app main activity
        AppUtils.getDatabase();

        if (FirebaseAuth.getInstance().getCurrentUser() != null && new UserInfo().getUser() != null && !PreferHelper.getString(this, Constants.UID).equals("")) {
            DependencyInjector.userScopeComponent().inject(this);
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("type")) {
                int notificationType = Integer.parseInt(getIntent().getExtras().getString("type"));
                if(notificationType == NOTIFICATION_ALERT){
                    String alertId = getIntent().getExtras().getString("alertId");

                    assert alertId != null;
                    alertRef.child(alertId).addListenerForSingleValueEvent(new LaunchAlertDetailActivityListener());

                }
                else if(notificationType == NOTIFICATION_INDICATOR_ASSIGNED)
                {
//                    String indicatorId = getIntent().getExtras().getString("indicatorId");
                    Intent intent = new Intent(this, HomeScreen.class);
                    intent.putExtra(HomeScreen.START_SCREEN, HomeScreen.SCREEN_INDICATOR);
                    startActivity(intent);
                    finish();
                }
                else if(notificationType == NOTIFICATION_ACTION_ASSIGNED)
                {
                    Intent intent = new Intent(this, HomeScreen.class);
                    int actionLevel = Integer.parseInt(getIntent().getExtras().getString("actionLevel"));

                    intent.putExtra(HomeScreen.START_SCREEN, actionLevel == 1 ? HomeScreen.SCREEN_MPA : HomeScreen.SCREEN_APA);
                    startActivity(intent);
                    finish();
                }
                else{
                    startActivity(new Intent(this, HomeScreen.class));
                    finish();
                }
            }
            else {
                startActivity(new Intent(this, HomeScreen.class));
                finish();
            }
            OfflineSyncHandler.getInstance().sync((AlertApplication) getApplication(), () -> {});
        } else {
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }


        Timber.d("Token: " + FirebaseInstanceId.getInstance().getToken());
    }

    private class LaunchAlertDetailActivityListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            AlertModel model = dataSnapshot.getValue(AlertModel.class);

            assert model != null;
            model.setId(dataSnapshot.getKey());
            model.setParentKey(dataSnapshot.getRef().getParent().getKey());
            Intent intent = new Intent(SplashActivity.this, AlertDetailActivity.class);
            intent.putExtra(AlertDetailActivity.EXTRA_ALERT, model);
            startActivity(intent);
            finish();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            finish();
        }
    }
}
