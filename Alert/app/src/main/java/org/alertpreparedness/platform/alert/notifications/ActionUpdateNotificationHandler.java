package org.alertpreparedness.platform.alert.notifications;

import android.content.Context;
import android.os.Bundle;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class ActionUpdateNotificationHandler implements ActionFetcher.ActionFetcherListener {

    private final Context context;
    @Inject
    public User user;

    @Inject
    @BaseActionRef
    public DatabaseReference baseActionRef;

    public static final String BUNDLE_ACTION_ID = "ActionId";
    public static final String BUNDLE_GROUP_ID = "GroupId";
    public static final String BUNDLE_NOTIFICATION_TYPE = "NotificationType";
    public static final int NOTIFICATION_TYPE_PASSED = 0;
    public static final int NOTIFICATION_TYPE_7_DAYS = 1;
    public static final int NOTIFICATION_TYPE_1_DAY = 2;

    public static final int ACTION_TYPE_MPA = 1;
    public static final int ACTION_TYPE_APA = 2;

    private static final int DAY_SECS = 60 * 60 * 24;
    private static final int WEEK_SECS = DAY_SECS * 7;
    private static final int NOTIFICATION_ACCURACY = 1;


    public ActionUpdateNotificationHandler(Context context) {
        DependencyInjector.applicationComponent().inject(this);
        this.context = context;
    }

    public static void cancelAllNotifications(Context context){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        for(String notificationId : PreferHelper.getScheduledActionNotifications(context)){
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_PASSED, ACTION_TYPE_MPA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_1_DAY, ACTION_TYPE_MPA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_7_DAYS, ACTION_TYPE_MPA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_PASSED, ACTION_TYPE_APA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_1_DAY, ACTION_TYPE_APA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_7_DAYS, ACTION_TYPE_APA));
        }
        PreferHelper.setScheduledActionNotifications(context, new ArrayList<>());
    }

    public static void cancelNotification(Context context, String actionId){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_PASSED, ACTION_TYPE_MPA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_1_DAY, ACTION_TYPE_MPA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_7_DAYS, ACTION_TYPE_MPA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_PASSED, ACTION_TYPE_APA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_1_DAY, ACTION_TYPE_APA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_7_DAYS, ACTION_TYPE_APA));

        List<String> actionIds = PreferHelper.getScheduledActionNotifications(context);
        actionIds.remove(actionId);
        PreferHelper.setScheduledActionNotifications(context, actionIds);
    }

    public void scheduleAllNotifications(){
        new ActionFetcher(this).fetchAll();
    }

    @Override
    public void actionFetchSuccess(List<ActionFetcher.ActionFetcherResult> models) {
        Timber.d("Success");
        cancelAllNotifications(context);

        for(ActionFetcher.ActionFetcherResult model : models){
            Timber.d("Action Model: " + model.getAction());
            scheduleNotification(context, model.getAction(),model.getGroupId(), model.getActionId());
        }
        Timber.d("Scheduled Notifications: " + models.size());

    }

    public static void scheduleNotification(Context context, ActionModel actionModel, String groupId, String actionId) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        if(actionModel.getDueDate() == null){
            return;
        }

        int timeFromNow = (int) ((actionModel.getDueDate() - System.currentTimeMillis())/1000);
        Timber.d("TimeStamp: " + actionModel.getDueDate() + " Time from now: " + timeFromNow);

        if(timeFromNow > 0) {
            if(timeFromNow > WEEK_SECS){
                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_GROUP_ID, groupId);
                bundle.putString(BUNDLE_ACTION_ID, actionId);
                bundle.putInt(BUNDLE_NOTIFICATION_TYPE, NOTIFICATION_TYPE_7_DAYS);

                int startTime = timeFromNow - WEEK_SECS;

                dispatcher.schedule(
                        dispatcher
                                .newJobBuilder()
                                .setTrigger(Trigger.executionWindow(startTime, startTime + NOTIFICATION_ACCURACY))
                                .setLifetime(Lifetime.FOREVER)
                                .setTag(getTag(actionId, NOTIFICATION_TYPE_7_DAYS, actionModel.getType()))
                                .setReplaceCurrent(true)
                                .setExtras(bundle)
                                .setService(ActionNotificationService.class)
                                .build()
                );
            }

            if(timeFromNow > DAY_SECS){
                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_GROUP_ID, groupId);
                bundle.putString(BUNDLE_ACTION_ID, actionId);
                bundle.putInt(BUNDLE_NOTIFICATION_TYPE, NOTIFICATION_TYPE_1_DAY);

                int startTime = timeFromNow - DAY_SECS;

                dispatcher.schedule(
                        dispatcher
                                .newJobBuilder()
                                .setTrigger(Trigger.executionWindow(startTime, startTime + NOTIFICATION_ACCURACY))
                                .setLifetime(Lifetime.FOREVER)
                                .setTag(getTag(actionId, NOTIFICATION_TYPE_1_DAY, actionModel.getType()))
                                .setReplaceCurrent(true)
                                .setExtras(bundle)
                                .setService(ActionNotificationService.class)
                                .build()
                );
            }

//            Timber.d("Scheduling Notification for: " + timeFromNow + " seconds " + actionModel.getDueDate());

            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_GROUP_ID, groupId);
            bundle.putString(BUNDLE_ACTION_ID, actionId);
            bundle.putInt(BUNDLE_NOTIFICATION_TYPE, NOTIFICATION_TYPE_PASSED);

            dispatcher.schedule(
                    dispatcher
                            .newJobBuilder()
                            .setTrigger(Trigger.executionWindow(timeFromNow, timeFromNow + NOTIFICATION_ACCURACY))
                            .setLifetime(Lifetime.FOREVER)
                            .setTag(getTag(actionId, NOTIFICATION_TYPE_PASSED, actionModel.getType()))
                            .setReplaceCurrent(true)
                            .setExtras(bundle)
                            .setService(ActionNotificationService.class)
                            .build()
            );

        }


    }

    private static String getTag(String actionId, int notificationType, int actionType) {
        return actionId + "-" + notificationType + "-" + actionType;
    }

    @Override
    public void actionFetchFail() {
        Timber.d("FAIL");
    }
}
