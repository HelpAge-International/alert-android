package org.alertpreparedness.platform.v2.utils.extensions

import org.alertpreparedness.platform.v2.models.Alert
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.WAITING_RESPONSE
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED

fun Alert.isRedAlertRequested(): Boolean {
    return state == WAITING_RESPONSE && level == RED
}
