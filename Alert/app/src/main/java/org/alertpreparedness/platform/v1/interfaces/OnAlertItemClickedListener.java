package org.alertpreparedness.platform.v1.interfaces;

import org.alertpreparedness.platform.v1.firebase.AlertModel;

/**
 * Created by faizmohideen on 23/11/2017.
 */

public interface OnAlertItemClickedListener {
    void onAlertItemClicked(AlertModel alert);
}
