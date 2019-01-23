package org.alertpreparedness.platform.v1.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface PreparednessClockSettingsFlowable {
}
