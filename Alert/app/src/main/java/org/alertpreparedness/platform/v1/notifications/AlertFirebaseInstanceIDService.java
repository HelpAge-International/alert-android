package org.alertpreparedness.platform.v1.notifications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.v1.helper.UserInfo;
import org.alertpreparedness.platform.v1.model.User;

import javax.inject.Inject;

public class AlertFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Inject
    @UserPublicRef
    DatabaseReference userPublic;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            DependencyInjector.userScopeComponent().inject(this);
            User user = new UserInfo().getUser();
            String deviceNotificationId = FirebaseInstanceId.getInstance().getToken();

            if(deviceNotificationId != null) {
                userPublic.child(user.getUserID()).child("deviceNotificationId").setValue(deviceNotificationId);
            }
        }

    }
}
