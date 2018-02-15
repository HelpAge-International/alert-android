package org.alertpreparedness.platform.alert.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

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

import dagger.internal.DaggerCollections;
import timber.log.Timber;

public class SplashActivity extends BaseActivity {

    @Inject
    @AlertRef
    DatabaseReference alertRef;

    public static final int NOTIFICATION_ALERT = 0;
    public static final int NOTIFICATION_INDICATOR_ASSIGNED = 1;
    public static final int NOTIFICATION_INDICATOR_RESCHEDULE = 2;

    public static final String NOTIFICATION_FIELD_TYPE = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This method will be executed once the timer is over
        // Start your app main activity
        AppUtils.getDatabase();
        System.out.println("FirebaseAuth.getInstance().getCurrentUser() = " + FirebaseAuth.getInstance().getCurrentUser());
        System.out.println("new UserInfo().getUser() = " + new UserInfo().getUser());
        System.out.println("PreferHelper.getString(this, Constants.UID) = " + PreferHelper.getString(this, Constants.UID));


        if (FirebaseAuth.getInstance().getCurrentUser() != null && new UserInfo().getUser() != null && !PreferHelper.getString(this, Constants.UID).equals("")) {
            DependencyInjector.applicationComponent().inject(this);
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
                else{
                    startActivity(new Intent(this, HomeScreen.class));
                    finish();
                }
            }
            else {
                startActivity(new Intent(this, HomeScreen.class));
                finish();
            }
        } else {
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }

        OfflineSyncHandler.getInstance().sync();

        Timber.d("Token: " + FirebaseInstanceId.getInstance().getToken());
    }

    private class LaunchAlertDetailActivityListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            AlertModel model = dataSnapshot.getValue(AlertModel.class);

            assert model != null;
            model.setKey(dataSnapshot.getKey());
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
