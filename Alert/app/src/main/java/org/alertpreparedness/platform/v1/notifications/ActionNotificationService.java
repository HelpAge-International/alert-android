package org.alertpreparedness.platform.v1.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionCHSRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionMandatedRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.NotificationSettingsRef;
import org.alertpreparedness.platform.v1.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.helper.UserInfo;
import org.alertpreparedness.platform.v1.min_preparedness.activity.CompleteActionActivity;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.AppUtils;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.NotificationSettingsListener;

import javax.inject.Inject;

import timber.log.Timber;

public class ActionNotificationService extends JobService {

    @Inject
    @BaseActionRef
    DatabaseReference baseActionRef;

    @Inject
    @BaseActionMandatedRef
    DatabaseReference baseActionMandatedRef;

    @Inject
    @BaseActionCHSRef
    DatabaseReference baseActionCHSRef;

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
            String groupId = job.getExtras().getString(ActionUpdateNotificationHandler.BUNDLE_GROUP_ID, null);
            String actionId = job.getExtras().getString(ActionUpdateNotificationHandler.BUNDLE_ACTION_ID, null);
            int notificationType = job.getExtras().getInt(ActionUpdateNotificationHandler.BUNDLE_NOTIFICATION_TYPE, ActionUpdateNotificationHandler.NOTIFICATION_TYPE_PASSED);

            if(groupId != null && actionId != null){
                Timber.d("Fetching " + baseActionRef.child(groupId).child(actionId).getRef());
                baseActionRef.child(groupId).child(actionId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ActionModel actionModel = AppUtils.getValueFromDataSnapshot(dataSnapshot, ActionModel.class);
                        if(actionModel != null) {
                            if (actionModel.getType() == Constants.CUSTOM) {
                                showNotification(actionModel, notificationType, groupId, actionId);
                                jobFinished(job, false);
                            } else {
                                DatabaseReference dbRef = null;
                                if (actionModel.getType() == Constants.CHS) {
                                    dbRef = baseActionCHSRef.child(user.getSystemAdminID()).child(actionId);
                                } else if(actionModel.getType() == Constants.MANDATED){
                                    dbRef = baseActionMandatedRef.child(user.getAgencyAdminID()).child(actionId);
                                }
                                if(dbRef != null){
                                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            ActionModel actionModelTask = AppUtils.getValueFromDataSnapshot(dataSnapshot, ActionModel.class);

                                            actionModel.setTask(actionModelTask == null ? "" : actionModelTask.getTask());

                                            showNotification(actionModel, notificationType, groupId, actionId);
                                            jobFinished(job, false);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            jobFinished(job, true);
                                        }
                                    });
                                }
                            }
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

    private void showNotification(ActionModel actionModel, int notificationType, String groupId, String actionId) {
        if (actionModel != null) {
            String title;
            String content;

            if(actionModel.getLevel() == null) return;

            if(actionModel.getLevel() == Constants.MPA) {
                switch (notificationType) {
                    case ActionUpdateNotificationHandler.NOTIFICATION_TYPE_EXPIRED:
                        title = getString(R.string.notification_mpa_expired_title);
                        content = getString(R.string.notification_mpa_expired_content, actionModel.getTask());
                        break;
                    case ActionUpdateNotificationHandler.NOTIFICATION_TYPE_7_DAYS:
                        title = getString(R.string.notification_mpa_7_days_title);
                        content = getString(R.string.notification_mpa_7_days_content, actionModel.getTask());
                        break;
                    case ActionUpdateNotificationHandler.NOTIFICATION_TYPE_1_DAY:
                        title = getString(R.string.notification_mpa_1_day_title);
                        content = getString(R.string.notification_mpa_1_day_content, actionModel.getTask());
                        break;
                    case ActionUpdateNotificationHandler.NOTIFICATION_TYPE_PASSED:
                    default:
                        title = getString(R.string.notification_mpa_passed_title);
                        content = getString(R.string.notification_mpa_passed_content, actionModel.getTask());
                }
            }
            else if(actionModel.getLevel() == Constants.APA){
                switch (notificationType) {
                    case ActionUpdateNotificationHandler.NOTIFICATION_TYPE_EXPIRED:
                        title = getString(R.string.notification_mpa_expired_title);
                        content = getString(R.string.notification_mpa_expired_content, actionModel.getTask());
                        break;
                    case ActionUpdateNotificationHandler.NOTIFICATION_TYPE_7_DAYS:
                        title = getString(R.string.notification_apa_7_days_title);
                        content = getString(R.string.notification_apa_7_days_content, actionModel.getTask());
                        break;
                    case ActionUpdateNotificationHandler.NOTIFICATION_TYPE_1_DAY:
                        title = getString(R.string.notification_apa_1_day_title);
                        content = getString(R.string.notification_apa_1_day_content, actionModel.getTask());

                        break;
                    case ActionUpdateNotificationHandler.NOTIFICATION_TYPE_PASSED:
                    default:
                        title = getString(R.string.notification_apa_passed_title);
                        content = getString(R.string.notification_apa_passed_content, actionModel.getTask());
                }
            }
            else{
                return;
            }

            Intent intent;
            if(notificationType == ActionUpdateNotificationHandler.NOTIFICATION_TYPE_EXPIRED) {
                intent = new Intent(getApplicationContext(), HomeScreen.class);
                intent.putExtra("group_id", groupId);
                intent.putExtra("action_id", actionId);
                intent.putExtra(HomeScreen.START_SCREEN, actionModel.getType() == 1 ? HomeScreen.SCREEN_MPA : HomeScreen.SCREEN_APA);
            }
            else{
                intent = new Intent(getApplicationContext(), CompleteActionActivity.class);
                intent.putExtra(CompleteActionActivity.ACTION_KEY, actionId);
                intent.putExtra(CompleteActionActivity.PARENT_KEY, groupId);
                intent.putExtra(CompleteActionActivity.REQUIRE_DOC, actionModel.getRequireDoc());
            }

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

            if(notificationType == ActionUpdateNotificationHandler.NOTIFICATION_TYPE_EXPIRED) {
                notificationSettingsRef.addListenerForSingleValueEvent(new NotificationSettingsListener(this, notification, actionId, Constants.NOTIFICATION_SETTING_MPA_APA_EXPIRED));
            }
            else {
                AppUtils.sendNotification(this, actionId, notification);
            }
        }
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        return false;
    }
}
