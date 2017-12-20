package org.alertpreparedness.platform.alert.interfaces;

import org.alertpreparedness.platform.alert.dashboard.model.Alert;
import org.alertpreparedness.platform.alert.firebase.AlertModel;

/**
 * Created by faizmohideen on 23/11/2017.
 */

public interface OnAlertItemClickedListener {
    void onAlertItemClicked(AlertModel alert);
}
