package org.alertpreparedness.platform.alert.dagger.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by Tj on 14/03/2018.
 */

@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface InActiveActionObservable {
}
