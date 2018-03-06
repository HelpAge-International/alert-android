package org.alertpreparedness.platform.alert.dagger.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * All hazards e.g. /sand/hazard
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseHazardRef {
}
