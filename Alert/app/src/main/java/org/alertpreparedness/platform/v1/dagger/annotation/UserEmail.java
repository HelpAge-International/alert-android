package org.alertpreparedness.platform.v1.dagger.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by Tj on 10/01/2018.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface UserEmail {
}
