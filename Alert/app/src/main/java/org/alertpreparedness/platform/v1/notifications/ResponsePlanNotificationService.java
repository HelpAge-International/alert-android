package org.alertpreparedness.platform.v1.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseResponsePlansRef;
import org.alertpreparedness.platform.v1.dagger.annotation.NotificationSettingsRef;
import org.alertpreparedness.platform.v1.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.v1.firebase.ResponsePlanModel;
import org.alertpreparedness.platform.v1.helper.UserInfo;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.AppUtils;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.NotificationSettingsListener;

import javax.inject.Inject;

import timber.log.Timber;

public class ResponsePlanNotificationService extends JobService {

    @Inject
    @BaseResponsePlansRef
    DatabaseReference baseResponsePlanRef;

    @Inject
    @NotificationSettingsRef
    DatabaseReference notificationSettingsRef;

    @Override
    public boolean onStartJob(JobParameters job) {

        User user = new UserInfo().getUser();

        Timber.d("ACTION JOB START");

        DependencyInjector.userScopeComponent().inject(this);

        if(job.getExtras() != null) {
            Timber.d("JOB EXTRAS NOT NULL");
            String groupId = job.getExtras().getString(ResponsePlanUpdateNotificationHandler.BUNDLE_GROUP_ID, null);
            String actionId = job.getExtras().getString(ResponsePlanUpdateNotificationHandler.BUNDLE_RESPONSE_PLAN_ID, null);

            if(groupId != null && actionId != null){
                Timber.d("Fetching " + baseResponsePlanRef.child(groupId).child(actionId).getRef());
                baseResponsePlanRef.child(groupId).child(actionId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ResponsePlanModel responsePlanModel = AppUtils.getValueFromDataSnapshot(dataSnapshot, ResponsePlanModel.class);
                        if(responsePlanModel != null) {
                            showNotification(responsePlanModel, groupId, actionId);
                            jobFinished(job, false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        jobFinished(job, true);
                    }
                });
            }
        }
        return false;
    }

    //TODO:
    private void showNotification(ResponsePlanModel responsePlanModel, String groupId, String responsePlanId) {
        if (responsePlanModel != null) {
            String title = getString(R.string.notification_response_plan_exipred_title);
            String content = getString(R.string.notification_response_plan_exipred_content, responsePlanModel.getName());

            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
            intent.putExtra("group_id", groupId);
            intent.putExtra("response_plan_id", responsePlanId);
//            intent.putExtra(HomeScreen.START_SCREEN, responsePlanModel.getType() == 1 ? HomeScreen.SCREEN_MPA : HomeScreen.SCREEN_APA);
//
//            Timber.d("StartScreen Plan: " + (responsePlanModel.getType() == 1 ? HomeScreen.SCREEN_MPA : HomeScreen.SCREEN_APA));

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), "alert")
                            .setContentTitle(title)
                            .setContentText(content)
                            .setSmallIcon(R.drawable.alert_logo)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

            Notification notification = mBuilder.build();

            notificationSettingsRef.addListenerForSingleValueEvent(new NotificationSettingsListener(this, notification, responsePlanId, Constants.NOTIFICATION_SETTING_MPA_APA_EXPIRED));
        }
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        return false;
    }
}
