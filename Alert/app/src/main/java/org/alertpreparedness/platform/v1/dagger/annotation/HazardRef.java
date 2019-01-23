package org.alertpreparedness.platform.v1.dagger.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Hazards for the current user's country ID, e.g. /sand/hazard/<countryId>
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface HazardRef {
}
