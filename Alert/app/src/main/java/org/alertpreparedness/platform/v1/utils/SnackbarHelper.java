package org.alertpreparedness.platform.v1.utils;

import android.app.Activity;
import com.google.android.material.snackbar.Snackbar;

/**
 * Created by Tj on 29/11/2017.
 */

public class SnackbarHelper {

    public static void show(Activity activity, String message) {
        Snackbar snackbar = Snackbar
                .make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
