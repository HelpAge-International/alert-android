package org.alertpreparedness.platform.alert.helper;

import org.alertpreparedness.platform.alert.dashboard.model.Alert;

/**
 * Created by am2230 on 10/12/2017.
 */

interface AlertCallback {
    void onLoaded(Alert alert);
    void onUpdated(Alert alert);
}
