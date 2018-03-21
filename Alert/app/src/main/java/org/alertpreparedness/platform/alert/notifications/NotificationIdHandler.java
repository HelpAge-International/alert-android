package org.alertpreparedness.platform.alert.notifications;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class NotificationIdHandler {

    @Inject
    @UserPublicRef
    public DatabaseReference userPublicRef;

    public static final String DEVICE_IDS_KEY = "deviceNotificationIds";

    public NotificationIdHandler(){
        DependencyInjector.userScopeComponent().inject(this);
    }


    public void registerDeviceId(String userId, String deviceId){
        DatabaseReference deviceNotificationIdsRef = userPublicRef.child(userId).child(DEVICE_IDS_KEY);

        deviceNotificationIdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> ids = dataSnapshot.getValue(new GenericTypeIndicator<List<String>>() {});
                if(ids == null){
                    ids = new ArrayList<>();
                }
                if(!ids.contains(deviceId)) {
                    ids.add(deviceId);
                    deviceNotificationIdsRef.setValue(ids);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void deregisterDeviceId(String userId, String deviceId, DatabaseReference.CompletionListener completionListener){
        DatabaseReference deviceNotificationIdsRef = userPublicRef.child(userId).child(DEVICE_IDS_KEY);

        deviceNotificationIdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> ids = dataSnapshot.getValue(new GenericTypeIndicator<List<String>>() {});
                if(ids == null){
                    ids = new ArrayList<>();
                }
                if(ids.contains(deviceId)) {
                    ids.remove(deviceId);
                    deviceNotificationIdsRef.setValue(ids, completionListener);
                }
                else{
                    completionListener.onComplete(null, deviceNotificationIdsRef);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                completionListener.onComplete(databaseError, deviceNotificationIdsRef);
            }
        });
    }

    public interface DeviceNotificationDeRegisterListener{
        void onSuccess();
        void onFailure();
    }


}
