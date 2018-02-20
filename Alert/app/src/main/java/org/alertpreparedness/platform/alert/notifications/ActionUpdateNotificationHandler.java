package org.alertpreparedness.platform.alert.notifications;

import android.content.Context;
import android.os.Bundle;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.LocalNetworkRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkCountryRef;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.ClockSetting;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    @Inject
    @CountryOfficeRef
    public DatabaseReference countryOfficeRef;

    @Inject
    @LocalNetworkRef
    public DatabaseReference localNetworkRef;

    @Inject
    @NetworkCountryRef
    public DatabaseReference networkCountryRef;

    public static final String BUNDLE_ACTION_ID = "ActionId";
    public static final String BUNDLE_GROUP_ID = "GroupId";
    public static final String BUNDLE_NOTIFICATION_TYPE = "NotificationType";
    public static final int NOTIFICATION_TYPE_PASSED = 0;
    public static final int NOTIFICATION_TYPE_7_DAYS = 1;
    public static final int NOTIFICATION_TYPE_1_DAY = 2;
    public static final int NOTIFICATION_TYPE_EXPIRED = 3;

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
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_EXPIRED, Constants.MPA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_PASSED, Constants.MPA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_1_DAY, Constants.MPA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_7_DAYS, Constants.MPA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_EXPIRED, Constants.APA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_PASSED, Constants.APA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_1_DAY, Constants.APA));
            dispatcher.cancel(getTag(notificationId, NOTIFICATION_TYPE_7_DAYS, Constants.APA));
        }
        PreferHelper.setScheduledActionNotifications(context, new ArrayList<>());
    }

    public static void cancelNotification(Context context, String actionId){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_EXPIRED, Constants.MPA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_PASSED, Constants.MPA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_1_DAY, Constants.MPA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_7_DAYS, Constants.MPA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_EXPIRED, Constants.APA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_PASSED, Constants.APA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_1_DAY, Constants.APA));
        dispatcher.cancel(getTag(actionId, NOTIFICATION_TYPE_7_DAYS, Constants.APA));

        List<String> actionIds = PreferHelper.getScheduledActionNotifications(context);
        actionIds.remove(actionId);
        PreferHelper.setScheduledActionNotifications(context, actionIds);
    }

    public void scheduleAllNotifications(){
        new ActionFetcher(this).fetchAll();
    }

    @Override
    public void actionFetchSuccess(ActionFetcher.ActionFetcherResult actionFetcherResult) {
        Timber.d("Success");
        cancelAllNotifications(context);

        for(ActionFetcher.ActionFetcherModel model : actionFetcherResult.getModels()){
            Timber.d("Action Model: " + model.getAction());
            ClockSetting clockSetting = null;
            switch (model.getActionType()) {
                case NETWORK_COUNTRY:
                    clockSetting = actionFetcherResult.getNetworkCountryClockSettings();
                    break;
                case LOCAL_NETWORK:
                    clockSetting = actionFetcherResult.getLocalNetworkClockSettings();
                    break;
                case COUNTRY:
                    clockSetting = actionFetcherResult.getCountryClockSettings();
                    break;
            }
            scheduleNotification(context, model.getAction(), model.getGroupId(), model.getActionId(), clockSetting);
        }
        Timber.d("Scheduled Notifications: " + actionFetcherResult.getModels().size());
    }
    public void scheduleNotification(Context context, ActionModel actionModel, String groupId, String actionId) {
        DatabaseReference dbRef = null;
        if(actionId.equals(user.getNetworkCountryID())){
            dbRef = networkCountryRef;
        }
        else if(actionId.equals(user.getLocalNetworkID())){
            dbRef = localNetworkRef;
        }
        else if(actionId.equals(user.getCountryID())){
            dbRef = countryOfficeRef;
        }

        if(dbRef != null){
            dbRef = dbRef.child("clockSettings").child("preparedness");
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    scheduleNotification(context, actionModel, groupId, actionId, dataSnapshot.getValue(ClockSetting.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e("Failed to fetch clock settings");
                }
            });
        }
        else {
            scheduleNotification(context, actionModel, groupId, actionId, null);
        }
    }
    public void scheduleNotification(Context context, ActionModel actionModel, String groupId, String actionId, ClockSetting clockSettings) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        //Due date based notifications
        if(actionModel.getDueDate() != null) {
            int timeFromNow = (int) ((actionModel.getDueDate() - System.currentTimeMillis()) / 1000);
            Timber.d("TimeStamp: " + actionModel.getDueDate() + " Time from now: " + timeFromNow);

            if (timeFromNow > 0) {
                if (timeFromNow > WEEK_SECS) {
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

                if (timeFromNow > DAY_SECS) {
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
        if(((actionModel.getFrequencyBase() != null && actionModel.getFrequencyValue() != null) || clockSettings != null) && actionModel.getCreatedAt() != null){
            Date startDate;

            if(actionModel.getIsCompleteAt() != null){
                startDate = new Date(actionModel.getIsCompleteAt());
            }
            else if(actionModel.getUpdatedAt() != null){
                startDate = new Date(actionModel.getUpdatedAt());
            }
            else{
                startDate = new Date(actionModel.getCreatedAt());
            }

            int frequencyBase;
            int frequencyValue;
            if(actionModel.getFrequencyBase() != null && actionModel.getFrequencyValue() != null) {
                frequencyBase = actionModel.getFrequencyBase();
                frequencyValue = actionModel.getFrequencyValue();
            }
            else{
                frequencyBase = clockSettings.getDurationType();
                frequencyValue = clockSettings.getValue();
            }

            Calendar expirationCalendar = Calendar.getInstance();
            expirationCalendar.setTime(startDate);

            if(frequencyBase == Constants.DUE_WEEK){
                expirationCalendar.add(Calendar.WEEK_OF_YEAR, frequencyValue);
            }
            else if(frequencyBase == Constants.DUE_MONTH){
                expirationCalendar.add(Calendar.MONTH, frequencyValue);
            }
            else if(frequencyBase == Constants.DUE_YEAR){
                expirationCalendar.add(Calendar.YEAR, frequencyValue);
            }

            Date expirationDate = expirationCalendar.getTime();

            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_GROUP_ID, groupId);
            bundle.putString(BUNDLE_ACTION_ID, actionId);
            bundle.putInt(BUNDLE_NOTIFICATION_TYPE, NOTIFICATION_TYPE_EXPIRED);

            int timeFromNow = (int) ((expirationDate.getTime() - System.currentTimeMillis())/1000);
            if(timeFromNow > 0) {
                dispatcher.schedule(
                        dispatcher
                                .newJobBuilder()
                                .setTrigger(Trigger.executionWindow(timeFromNow, timeFromNow + NOTIFICATION_ACCURACY))
                                .setLifetime(Lifetime.FOREVER)
                                .setTag(getTag(actionId, NOTIFICATION_TYPE_EXPIRED, actionModel.getType()))
                                .setReplaceCurrent(true)
                                .setExtras(bundle)
                                .setService(ActionNotificationService.class)
                                .build()
                );
                Timber.d("Scheduling Expiration" + actionId + " - " + expirationDate + " - " + timeFromNow);
            }
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
