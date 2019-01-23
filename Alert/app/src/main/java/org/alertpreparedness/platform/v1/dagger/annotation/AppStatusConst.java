package org.alertpreparedness.platform.v1.dagger.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by Tj on 13/12/2017.
 */

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface AppStatusConst {
}
