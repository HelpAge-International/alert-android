package org.alertpreparedness.platform.alert.dagger.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by Tj on 06/02/2018.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalNetworkRef {
}
