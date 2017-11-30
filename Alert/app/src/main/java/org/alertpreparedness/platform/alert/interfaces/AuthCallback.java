package org.alertpreparedness.platform.alert.interfaces;

import android.content.Context;

import org.alertpreparedness.platform.alert.model.User;

/**
 * Created by faizmohideen on 17/11/2017.
 */

public interface AuthCallback {
    void onUserAuthorized(User user);
    Context getContext();
}
