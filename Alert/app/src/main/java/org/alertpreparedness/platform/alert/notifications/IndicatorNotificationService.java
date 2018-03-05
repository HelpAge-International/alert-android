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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.alert.firebase.IndicatorModel;
import org.alertpreparedness.platform.alert.risk_monitoring.view.UpdateIndicatorActivity;

import java.util.Random;

import javax.inject.Inject;

import timber.log.Timber;

public class IndicatorNotificationService extends JobService {

    @Inject
    @BaseIndicatorRef
    DatabaseReference baseIndicatorRef;

    @Override
    public boolean onStartJob(JobParameters job) {
        if(FirebaseAuth.getInstance().getCurrentUser() != null && job.getExtras() != null) {
            DependencyInjector.applicationComponent().inject(this);

            String hazardId = job.getExtras().getString(IndicatorUpdateNotificationHandler.BUNDLE_HAZARD_ID, null);
            String indicatorId = job.getExtras().getString(IndicatorUpdateNotificationHandler.BUNDLE_INDICATOR_ID, null);
            int notificationType = job.getExtras().getInt(IndicatorUpdateNotificationHandler.BUNDLE_NOTIFICATION_TYPE, IndicatorUpdateNotificationHandler.NOTIFICATION_TYPE_PASSED);

            if(hazardId != null && indicatorId != null){
                baseIndicatorRef.child(hazardId).child(indicatorId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        IndicatorModel indicatorModel = dataSnapshot.getValue(IndicatorModel.class);
                        String title;
                        String content;
                        switch(notificationType) {
                            case IndicatorUpdateNotificationHandler.NOTIFICATION_TYPE_7_DAYS:
                                title = getString(R.string.notification_indicator_7_days_title);
                                content = getString(R.string.notification_indicator_7_days_content, indicatorModel.getName());
                                break;
                            case IndicatorUpdateNotificationHandler.NOTIFICATION_TYPE_1_DAY:
                                title = getString(R.string.notification_indicator_1_day_title);
                                content = getString(R.string.notification_indicator_1_day_content, indicatorModel.getName());

                                break;
                            case IndicatorUpdateNotificationHandler.NOTIFICATION_TYPE_PASSED:
                            default:
                                title = getString(R.string.notification_indicator_passed_title);
                                content = getString(R.string.notification_indicator_passed_content, indicatorModel.getName());
                        }


                        Intent intent = new Intent(getApplicationContext(), UpdateIndicatorActivity.class);
                        intent.putExtra("hazard_id", hazardId);
                        intent.putExtra("indicator_id", indicatorId);

                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext(), "alert")
                                        .setContentTitle(title)
                                        .setContentText(content)
                                        .setSmallIcon(R.drawable.alert_logo)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);



                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        if (notificationManager != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel channel = new NotificationChannel("alert",
                                        "Default Alert Notification Channel",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                notificationManager.createNotificationChannel(channel);
                            }
                            Timber.d("Sending Notification for:" + hazardId + " - " + indicatorId);
                            notificationManager.notify(indicatorId, new Random().nextInt(), mBuilder.build());
                        }

                        jobFinished(job, false);
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

    @Override
    public boolean onStopJob(JobParameters job) {

        return false;
    }
}
