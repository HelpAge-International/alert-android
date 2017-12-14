package org.alertpreparedness.platform.alert.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by faizmohideen on 13/12/2017.
 */

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface AlertRef {
}
