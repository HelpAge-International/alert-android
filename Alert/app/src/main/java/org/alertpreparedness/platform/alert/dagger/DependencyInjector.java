package org.alertpreparedness.platform.alert.dagger;

import org.alertpreparedness.platform.alert.AlertApplication;

/**
 * Created by Tj on 13/12/2017.
 */

public class DependencyInjector {

    private static ApplicationComponent applicationComponent;

    public static void initialize(AlertApplication diApplication) {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(diApplication))
                .firebaseModule(new FirebaseModule())
                .observableModule(new ObservableModule())
                .build();
    }

    public static void deinit(){
        applicationComponent = null;
    }

    public static ApplicationComponent applicationComponent() {
        return applicationComponent;
    }

    private DependencyInjector(){}
}