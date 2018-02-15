package org.alertpreparedness.platform.alert.notifications;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.alertpreparedness.platform.alert.firebase.IndicatorModel;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.login.activity.SplashActivity;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import timber.log.Timber;

public class AlertFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        RemoteMessage.Notification notification = remoteMessage.getNotification();

        if(remoteMessage.getData().containsKey(SplashActivity.NOTIFICATION_FIELD_TYPE)){
            rescheduleIndicatorNotification(remoteMessage.getData().get("hazardId"), remoteMessage.getData().get("indicatorId"));
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }


    public void rescheduleIndicatorNotification(String hazardId, String indicatorId){
        Timber.d("Rescheduling Notification: " + hazardId, indicatorId);

        User user = new UserInfo().getUser();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(PreferHelper.getString(getApplicationContext(), Constants.APP_STATUS)).child("indicator").child(hazardId).child(indicatorId);

        if(user != null){
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    IndicatorModel indicatorModel = dataSnapshot.getValue(IndicatorModel.class);
                    if(indicatorModel == null || !indicatorModel.getAssignee().equals(user.getUserID())){
                        IndicatorUpdateNotificationHandler.cancelNotification(getApplicationContext(), hazardId);
                    }
                    else{
                        IndicatorUpdateNotificationHandler.scheduleNotification(getApplicationContext(), indicatorModel, hazardId, indicatorId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    IndicatorUpdateNotificationHandler.cancelNotification(getApplicationContext(), hazardId);
                }
            });
        }
    }
}
