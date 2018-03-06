package org.alertpreparedness.platform.alert.dagger.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * All indicators e.g. /sand/indicator
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseIndicatorRef {
}
