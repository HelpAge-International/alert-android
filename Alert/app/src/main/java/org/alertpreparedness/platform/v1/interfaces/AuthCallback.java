package org.alertpreparedness.platform.v1.interfaces;

import android.content.Context;

import org.alertpreparedness.platform.v1.model.User;

/**
 * Created by faizmohideen on 17/11/2017.
 */

public interface AuthCallback {
    void onUserAuthorized(User user);
    Context getContext();
}
