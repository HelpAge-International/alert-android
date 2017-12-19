package org.alertpreparedness.platform.alert.dagger;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.dashboard.activity.CreateAlertActivity;
import org.alertpreparedness.platform.alert.responseplan.ResponsePlanFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Tj on 13/12/2017.
 */

@Singleton
@Component(modules = {ApplicationModule.class, FirebaseModule.class})
public interface ApplicationComponent {
    void inject(ResponsePlanFragment fragment);
    void inject(CreateAlertActivity activity);
}
