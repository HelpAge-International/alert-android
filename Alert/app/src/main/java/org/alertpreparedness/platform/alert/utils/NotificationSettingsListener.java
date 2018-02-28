package org.alertpreparedness.platform.alert.utils;

import android.app.Notification;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

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
        HashMap<String, Integer> notificationSettings = (HashMap<String, Integer>) dataSnapshot.getValue();

        if(notificationSettings.containsKey(String.valueOf(notificationSetting)) && notificationSettings.get(String.valueOf(notificationSetting)) == 1){
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
