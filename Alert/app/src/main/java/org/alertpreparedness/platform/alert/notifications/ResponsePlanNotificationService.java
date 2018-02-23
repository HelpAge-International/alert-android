package org.alertpreparedness.platform.alert.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseResponsePlansRef;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.firebase.ResponsePlanModel;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.util.Random;

import javax.inject.Inject;

import timber.log.Timber;

public class ResponsePlanNotificationService extends JobService {

    @Inject
    @BaseResponsePlansRef
    DatabaseReference baseResponsePlanRef;

    @Override
    public boolean onStartJob(JobParameters job) {

        User user = new UserInfo().getUser();

        Timber.d("ACTION JOB START");

        DependencyInjector.applicationComponent().inject(this);

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
                            showNotificaiton(responsePlanModel, groupId, actionId);
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

    private void showNotificaiton(ResponsePlanModel responsePlanModel, String groupId, String actionId) {
//        if (responsePlanModel != null) {
//            String title = getString();
//            String content;
//
//            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
////            intent.putExtra("group_id", groupId);
////            intent.putExtra("respone_plan_id", actionId);
////            intent.putExtra(HomeScreen.START_SCREEN, responsePlanModel.getType() == 1 ? HomeScreen.SCREEN_MPA : HomeScreen.SCREEN_APA);
////
////            Timber.d("StartScreen Plan: " + (responsePlanModel.getType() == 1 ? HomeScreen.SCREEN_MPA : HomeScreen.SCREEN_APA));
//
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(getApplicationContext(), "alert")
//                            .setContentTitle(title)
//                            .setContentText(content)
//                            .setSmallIcon(R.drawable.alert_logo)
//                            .setPriority(NotificationCompat.PRIORITY_HIGH)
//                            .setContentIntent(pendingIntent)
//                            .setAutoCancel(true);
//
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if (notificationManager != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    NotificationChannel channel = new NotificationChannel("alert",
//                            "Default Alert Notification Channel",
//                            NotificationManager.IMPORTANCE_DEFAULT);
//                    notificationManager.createNotificationChannel(channel);
//                }
//                notificationManager.notify(actionId, new Random().nextInt(), mBuilder.build());
//            }
//        }
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        return false;
    }
}
