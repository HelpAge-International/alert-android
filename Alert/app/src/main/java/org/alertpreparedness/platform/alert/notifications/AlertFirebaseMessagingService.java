package org.alertpreparedness.platform.alert.notifications;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.IndicatorModel;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.login.activity.SplashActivity;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import timber.log.Timber;

public class AlertFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Timber.d("Recieved notification: " + remoteMessage.getData());

        if(remoteMessage.getData().containsKey(SplashActivity.NOTIFICATION_FIELD_TYPE)){
            int type = Integer.parseInt(remoteMessage.getData().get(SplashActivity.NOTIFICATION_FIELD_TYPE));
            if(type == SplashActivity.NOTIFICATION_INDICATOR_RESCHEDULE) {
                rescheduleIndicatorNotification(remoteMessage.getData().get("hazardId"), remoteMessage.getData().get("indicatorId"));
            }
            else if(type == SplashActivity.NOTIFICATION_ACTION_RESCHEDULE) {
                rescheduleActionNotification(remoteMessage.getData().get("groupId"), remoteMessage.getData().get("actionId"));
            }
            //Ideally these would only reschedule the ones that are needed, but alas
            else if(type == SplashActivity.NOTIFICATION_ACTION_COUNTRY_RESCHEDULE){
                rescheduleActionNotifications();
            }
            else if(type == SplashActivity.NOTIFICATION_ACTION_LOCAL_NETWORK_RESCHEDULE){
                rescheduleActionNotifications();
            }
            else if(type == SplashActivity.NOTIFICATION_ACTION_NETWORK_COUNTRY_RESCHEDULE){
                rescheduleActionNotifications();
            }
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }


    public void rescheduleIndicatorNotification(String hazardId, String indicatorId){
        Timber.d("Rescheduling Indicator Notification: " + hazardId + " - " + indicatorId);

        User user = new UserInfo().getUser();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(PreferHelper.getString(getApplicationContext(), Constants.APP_STATUS)).child("indicator").child(hazardId).child(indicatorId);

        if(user != null){
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    IndicatorModel indicatorModel = dataSnapshot.getValue(IndicatorModel.class);
                    if(indicatorModel == null || !indicatorModel.getAssignee().equals(user.getUserID())){
                        IndicatorUpdateNotificationHandler.cancelNotification(getApplicationContext(), indicatorId);
                    }
                    else{
                        IndicatorUpdateNotificationHandler.scheduleNotification(getApplicationContext(), indicatorModel, hazardId, indicatorId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    IndicatorUpdateNotificationHandler.cancelNotification(getApplicationContext(), indicatorId);
                }
            });
        }
    }

    public void rescheduleActionNotification(String groupId, String actionId){
        Timber.d("Rescheduling Action Notification: " + groupId + " - " +  actionId);

        User user = new UserInfo().getUser();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(PreferHelper.getString(getApplicationContext(), Constants.APP_STATUS)).child("action").child(groupId).child(actionId);

        if(user != null){
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ActionModel actionModel = AppUtils.getValueFromDataSnapshot(dataSnapshot, ActionModel.class);
                    if(actionModel == null || !actionModel.getAsignee().equals(user.getUserID())){
                        ActionUpdateNotificationHandler.cancelNotification(getApplicationContext(), actionId);
                    }
                    else{
                        new ActionUpdateNotificationHandler(getApplicationContext()).scheduleNotification(getApplicationContext(), actionModel, groupId, actionId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    ActionUpdateNotificationHandler.cancelNotification(getApplicationContext(), actionId);
                }
            });
        }
    }

    private void rescheduleActionNotifications() {
        System.out.println("");
        new ActionUpdateNotificationHandler(this).scheduleAllNotifications();
    }
}
