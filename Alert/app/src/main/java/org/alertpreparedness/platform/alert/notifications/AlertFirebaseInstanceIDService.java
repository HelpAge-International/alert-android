package org.alertpreparedness.platform.alert.notifications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;

import javax.inject.Inject;

public class AlertFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Inject
    @UserPublicRef
    DatabaseReference userPublic;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            DependencyInjector.applicationComponent().inject(this);
            User user = new UserInfo().getUser();
            String deviceNotificationId = FirebaseInstanceId.getInstance().getToken();

            if(deviceNotificationId != null) {
                userPublic.child(user.getUserID()).child("deviceNotificationId").setValue(deviceNotificationId);
            }
        }

    }
}
