package org.alertpreparedness.platform.alert.notifications;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.model.User;

import java.util.List;

import javax.inject.Inject;

public class NotificationIdHandler {

    @Inject
    @UserPublicRef
    public DatabaseReference userPublicRef;

    @Inject
    public User user;

    public static final String DEVICE_IDS_KEY = "deviceNotificationIds";

    public NotificationIdHandler(){
        DependencyInjector.applicationComponent().inject(this);
    }


    public void registerDeviceId(String deviceId){
        DatabaseReference deviceNotificationIdsRef = userPublicRef.child(user.getUserID()).child(DEVICE_IDS_KEY);

        deviceNotificationIdsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                List<String> ids = mutableData.getValue(new GenericTypeIndicator<List<String>>());
                
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }


}
