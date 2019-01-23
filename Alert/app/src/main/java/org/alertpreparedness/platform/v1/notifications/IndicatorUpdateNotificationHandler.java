package org.alertpreparedness.platform.v1.notifications;

import android.content.Context;
import android.os.Bundle;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.v1.firebase.IndicatorModel;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.PreferHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class IndicatorUpdateNotificationHandler implements IndicatorFetcher.IndicatorFetcherListener {

    private final Context context;
    @Inject
    public User user;

    @Inject
    @BaseIndicatorRef
    public DatabaseReference baseIndicatorRef;

    public static final String BUNDLE_HAZARD_ID = "HazardId";
    public static final String BUNDLE_INDICATOR_ID = "IndicatorId";
    public static final String BUNDLE_NOTIFICATION_TYPE = "NotificationType";
    public static final int NOTIFICATION_TYPE_PASSED = 0;
    public static final int NOTIFICATION_TYPE_7_DAYS = 1;
    public static final int NOTIFICATION_TYPE_1_DAY = 2;

    private static final int DAY_SECS = 60 * 60 * 24;
    private static final int WEEK_SECS = DAY_SECS * 7;
    private static final int NOTIFICATION_ACCURACY = 1;


    public IndicatorUpdateNotificationHandler(Context context) {
        DependencyInjector.userScopeComponent().inject(this);
        this.context = context;
    }

    public static void cancelAllNotifications(Context context){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        for(String notificationId : PreferHelper.getScheduledIndicatorNotifications(context)){
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_PASSED));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_1_DAY));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_7_DAYS));
        }
        PreferHelper.setScheduledIndicatorNotifications(context, new ArrayList<>());
    }

    public static void cancelNotification(Context context, String indicatorId){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        dispatcher.cancel(getTag(indicatorId, NOTIFICATION_TYPE_PASSED));
        dispatcher.cancel(getTag(indicatorId, NOTIFICATION_TYPE_1_DAY));
        dispatcher.cancel(getTag(indicatorId, NOTIFICATION_TYPE_7_DAYS));

        List<String> indicatorIds = PreferHelper.getScheduledIndicatorNotifications(context);
        indicatorIds.remove(indicatorId);
        PreferHelper.setScheduledIndicatorNotifications(context, indicatorIds);
    }

    public void scheduleAllNotifications(){
        new IndicatorFetcher(this).fetchAll();
    }

    @Override
    public void indicatorFetchSuccess(List<IndicatorFetcher.IndicatorFetcherResult> models) {
        cancelAllNotifications(context);

        for(IndicatorFetcher.IndicatorFetcherResult model : models){
            scheduleNotification(context, model.getIndicator(), model.getHazardId(), model.getIndicatorId());
        }
        Timber.d("Scheduled Notifications: " + models.size());

    }

    public static void scheduleNotification(Context context, IndicatorModel indicatorModel, String hazardId, String indicatorId) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        int timeFromNow = (int) ((indicatorModel.getDueDate() - System.currentTimeMillis())/1000);
        Timber.d("TimeStamp: " + indicatorModel.getDueDate() + " Time from now: " + timeFromNow);

        if(timeFromNow > 0) {
            if(timeFromNow > WEEK_SECS){
                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_HAZARD_ID, hazardId);
                bundle.putString(BUNDLE_INDICATOR_ID, indicatorId);
                bundle.putInt(BUNDLE_NOTIFICATION_TYPE, NOTIFICATION_TYPE_7_DAYS);

                int startTime = timeFromNow - WEEK_SECS;

                dispatcher.schedule(
                        dispatcher
                                .newJobBuilder()
                                .setTrigger(Trigger.executionWindow(startTime, startTime + NOTIFICATION_ACCURACY))
                                .setLifetime(Lifetime.FOREVER)
                                .setTag(getTag(indicatorId, NOTIFICATION_TYPE_7_DAYS))
                                .setReplaceCurrent(true)
                                .setExtras(bundle)
                                .setService(IndicatorNotificationService.class)
                                .build()
                );
            }

            if(timeFromNow > DAY_SECS){
                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_HAZARD_ID, hazardId);
                bundle.putString(BUNDLE_INDICATOR_ID, indicatorId);
                bundle.putInt(BUNDLE_NOTIFICATION_TYPE, NOTIFICATION_TYPE_1_DAY);

                int startTime = timeFromNow - DAY_SECS;

                dispatcher.schedule(
                        dispatcher
                                .newJobBuilder()
                                .setTrigger(Trigger.executionWindow(startTime, startTime + NOTIFICATION_ACCURACY))
                                .setLifetime(Lifetime.FOREVER)
                                .setTag(getTag(indicatorId, NOTIFICATION_TYPE_1_DAY))
                                .setReplaceCurrent(true)
                                .setExtras(bundle)
                                .setService(IndicatorNotificationService.class)
                                .build()
                );
            }

//            Timber.d("Scheduling Notification for: " + timeFromNow + " seconds " + indicatorModel.getDueDate());

            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_HAZARD_ID, hazardId);
            bundle.putString(BUNDLE_INDICATOR_ID, indicatorId);
            bundle.putInt(BUNDLE_NOTIFICATION_TYPE, NOTIFICATION_TYPE_PASSED);

            dispatcher.schedule(
                    dispatcher
                            .newJobBuilder()
                            .setTrigger(Trigger.executionWindow(timeFromNow, timeFromNow + NOTIFICATION_ACCURACY))
                            .setLifetime(Lifetime.FOREVER)
                            .setTag(getTag(indicatorId, NOTIFICATION_TYPE_PASSED))
                            .setReplaceCurrent(true)
                            .setExtras(bundle)
                            .setService(IndicatorNotificationService.class)
                            .build()
            );

        }


    }

    private static String getTag(String indicatorId, int notificationType) {
        return indicatorId + "-" + notificationType;
    }

    @Override
    public void indicatorFetchFail() {
        Timber.d("FAIL");
    }
}
