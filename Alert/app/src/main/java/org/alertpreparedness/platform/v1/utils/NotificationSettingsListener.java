package org.alertpreparedness.platform.v1.utils;

import android.app.Notification;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationSettingsListener implements ValueEventListener {

    private final Context context;
    private Notification notification;
    private String notificationTag;
    private int notificationSetting;

    public NotificationSettingsListener(Context context, Notification notification, String notificationTag, int notificationSetting) {
        this.notification = notification;
        this.notificationTag = notificationTag;
        this.notificationSetting = notificationSetting;
        this.context = context;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<Integer> notificationSettings = dataSnapshot.getValue(new GenericTypeIndicator<List<Integer>>() {});

        if(notificationSettings != null && notificationSettings.contains(notificationSetting)){
            //Enabled
            AppUtils.sendNotification(context,notificationTag, notification);
        }
        else{
            //Not enabled
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
