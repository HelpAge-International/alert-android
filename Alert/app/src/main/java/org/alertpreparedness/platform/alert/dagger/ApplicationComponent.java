package org.alertpreparedness.platform.alert.dagger;


import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.notifications.IndicatorUpdateNotificationHandler;
import org.alertpreparedness.platform.alert.notifications.NotificationIdHandler;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Tj on 13/12/2017.
 */

@Singleton
@Component(modules = {ApplicationModule.class, FirebaseModule.class})
public interface ApplicationComponent {
    ObservableComponent.Builder observableComponentBuilder();

    void inject(UserInfo info);

    void inject(MainDrawer drawer);

    void inject(NotificationIdHandler notificationIdHandler);

    void inject(IndicatorUpdateNotificationHandler indicatorUpdateNotificationHandler);
}

