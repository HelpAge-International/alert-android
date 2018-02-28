package org.alertpreparedness.platform.alert.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.IndicatorModel;
import org.alertpreparedness.platform.alert.firebase.ResponsePlanModel;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.login.activity.SplashActivity;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.Random;

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
            else if(type == SplashActivity.NOTIFICATION_RESPONSE_PLAN_RESCHEDULE) {
                rescheduleResponsePlanNotification(remoteMessage.getData().get("groupId"), remoteMessage.getData().get("responsePlanId"));
            }
            else if(type == SplashActivity.NOTIFICATION_RESPONSE_PLAN_COUNTRY_RESCHEDULE){
                rescheduleResponsePlanNotifications();
            }
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "alert")
                        .setContentTitle("Title")
                        .setContentText("Content")
                        .setSmallIcon(R.drawable.alert_logo)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("alert",
                        "Default Alert Notification Channel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(new Random().nextInt(), mBuilder.build());
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

    public void rescheduleResponsePlanNotification(String groupId, String responsePlanId){
        Timber.d("Rescheduling ResponsePlan Notification: " + groupId + " - " +  responsePlanId);

        User user = new UserInfo().getUser();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(PreferHelper.getString(getApplicationContext(), Constants.APP_STATUS)).child("responsePlan").child(groupId).child(responsePlanId);

        if(user != null){
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ResponsePlanModel responsePlanModel = AppUtils.getValueFromDataSnapshot(dataSnapshot, ResponsePlanModel.class);
                    if(responsePlanModel == null){
                        ResponsePlanUpdateNotificationHandler.cancelNotification(getApplicationContext(), responsePlanId);
                    }
                    else{
                        new ResponsePlanUpdateNotificationHandler(getApplicationContext()).scheduleNotification(getApplicationContext(), responsePlanModel, groupId, responsePlanId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    ResponsePlanUpdateNotificationHandler.cancelNotification(getApplicationContext(), responsePlanId);
                }
            });
        }
    }


    private void rescheduleActionNotifications() {
        new ActionUpdateNotificationHandler(this).scheduleAllNotifications();
    }

    private void rescheduleResponsePlanNotifications() {
        new ResponsePlanUpdateNotificationHandler(this).scheduleAllNotifications();
    }
}
