package org.alertpreparedness.platform.v1.notifications;

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

import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.v1.dagger.annotation.ResponsePlansRef;
import org.alertpreparedness.platform.v1.firebase.ResponsePlanModel;
import org.alertpreparedness.platform.v1.firebase.ClockSetting;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PreferHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class ResponsePlanUpdateNotificationHandler implements ResponsePlanFetcher.ResponsePlanFetcherListener {

    private final Context context;
    @Inject
    public User user;

    @Inject
    @ResponsePlansRef
    public DatabaseReference responsePlanRef;

    @Inject
    @CountryOfficeRef
    public DatabaseReference countryOfficeRef;


    public static final String BUNDLE_RESPONSE_PLAN_ID = "ResponsePlanId";
    public static final String BUNDLE_GROUP_ID = "GroupId";
    public static final String BUNDLE_NOTIFICATION_TYPE = "NotificationType";
    public static final int NOTIFICATION_TYPE_EXPIRED = 0;

    private static final int NOTIFICATION_ACCURACY = 1;


    public ResponsePlanUpdateNotificationHandler(Context context) {
        DependencyInjector.userScopeComponent().inject(this);
        this.context = context;
    }

    public static void cancelAllNotifications(Context context){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        for(String responsePlanId : PreferHelper.getScheduledResponsePlanNotifications(context)){
            dispatcher.cancel(getTag(responsePlanId, NOTIFICATION_TYPE_EXPIRED));
        }
        PreferHelper.setScheduledResponsePlanNotifications(context, new ArrayList<>());
    }

    public static void cancelNotification(Context context, String responsePlanId){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        dispatcher.cancel(getTag(responsePlanId, NOTIFICATION_TYPE_EXPIRED));

        List<String> responsePlanIds = PreferHelper.getScheduledResponsePlanNotifications(context);
        responsePlanIds.remove(responsePlanId);
        PreferHelper.setScheduledResponsePlanNotifications(context, responsePlanIds);
    }

    public void scheduleAllNotifications(){
        Timber.d("Scheduling Response Plan Notifications");
        new ResponsePlanFetcher(this).fetch();
    }

    @Override
    public void responsePlanFetchSuccess(ResponsePlanFetcher.ResponsePlanFetcherResult responsePlanFetcherResult) {
        Timber.d("Success");
        cancelAllNotifications(context);

        for(ResponsePlanFetcher.ResponsePlanFetcherModel model : responsePlanFetcherResult.getModels()){
            Timber.d("ResponsePlan Model: " + model.getResponsePlan());
            ClockSetting clockSetting = null;
            switch (model.getResponsePlanType()) {
                case COUNTRY:
                    clockSetting = responsePlanFetcherResult.getCountryClockSettings();
                    break;
            }

            scheduleNotification(context, model.getResponsePlan(), model.getGroupId(), model.getResponsePlanId(), clockSetting);
        }
        Timber.d("Scheduled Response Plan Notifications: " + responsePlanFetcherResult.getModels().size());
    }

    public void scheduleNotification(Context context, ResponsePlanModel responsePlanModel, String groupId, String responsePlanId) {
        if(groupId.equals(user.getCountryID())){
            DatabaseReference dbRef = countryOfficeRef.child("clockSettings").child("responsePlans");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dbRef.removeEventListener(this);
                    scheduleNotification(context, responsePlanModel, groupId, responsePlanId, dataSnapshot.getValue(ClockSetting.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e("Failed to fetch clock settings");
                }
            });
        }
    }
    public void scheduleNotification(Context context, ResponsePlanModel responsePlanModel, String groupId, String responsePlanId, ClockSetting clockSettings) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        if(clockSettings != null && responsePlanModel.getTimeCreated() != null){
            Date startDate;

            if(responsePlanModel.getTimeUpdated() != null){
                 startDate = new Date(responsePlanModel.getTimeUpdated());
            }
            else{
                startDate = new Date(responsePlanModel.getTimeCreated());
            }

            int frequencyBase = clockSettings.getDurationType();
            int frequencyValue = clockSettings.getValue();

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
            bundle.putString(BUNDLE_RESPONSE_PLAN_ID, responsePlanId);
            bundle.putInt(BUNDLE_NOTIFICATION_TYPE, NOTIFICATION_TYPE_EXPIRED);

            int timeFromNow = (int) ((expirationDate.getTime() - System.currentTimeMillis())/1000);

            Timber.d("Schedule Response Plan: " + responsePlanId + " - " + startDate.getTime() + " - "  + expirationDate + " - " + timeFromNow);

            if(timeFromNow > 0) {
                dispatcher.schedule(
                        dispatcher
                                .newJobBuilder()
                                .setTrigger(Trigger.executionWindow(timeFromNow, timeFromNow + NOTIFICATION_ACCURACY))
                                .setLifetime(Lifetime.FOREVER)
                                .setTag(getTag(responsePlanId, NOTIFICATION_TYPE_EXPIRED))
                                .setReplaceCurrent(true)
                                .setExtras(bundle)
                                .setService(ResponsePlanNotificationService.class)
                                .build()
                );
            }
        }
    }

    private static String getTag(String responsePlanId, int notificationType) {
        return responsePlanId + "-" + notificationType;
    }

    @Override
    public void responsePlanFetchFail() {
        Timber.d("FAIL");
    }
}
