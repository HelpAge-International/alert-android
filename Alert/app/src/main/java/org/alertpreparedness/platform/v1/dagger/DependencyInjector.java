package org.alertpreparedness.platform.v1.dagger;

import org.alertpreparedness.platform.v1.AlertApplication;

/**
 * Created by Tj on 13/12/2017.
 */

public class DependencyInjector {

    private static ApplicationComponent applicationComponent;
    private static ObservableComponent observableComponent;

    public static void initialize(AlertApplication diApplication) {
        if(applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(diApplication))
                    .firebaseModule(new FirebaseModule())
                    .build();
        }
    }

    public static void initializeUserScope() {
        if(observableComponent == null) {
            observableComponent = applicationComponent.observableComponentBuilder().build();
        }
    }

    public static void deinit(){
        applicationComponent = null;
        observableComponent = null;
    }

    public static ApplicationComponent applicationcomponent() {
        return applicationComponent;
    }

    public static ObservableComponent userScopeComponent() {
        return observableComponent;
    }

    private DependencyInjector(){}
}