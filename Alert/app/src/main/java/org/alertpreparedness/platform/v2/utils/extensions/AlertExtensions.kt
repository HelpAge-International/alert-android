package org.alertpreparedness.platform.v2.utils.extensions

import org.alertpreparedness.platform.v2.models.Alert
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.APPROVED
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState.WAITING_RESPONSE
import org.alertpreparedness.platform.v2.models.enums.AlertLevel
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.AMBER
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.GREEN
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel

fun Alert.isRedAlertRequested(): Boolean {
    return state == WAITING_RESPONSE && level == RED
}

fun Alert.isRedAlertApproved(): Boolean {
    return state == APPROVED && level == RED
}

fun AlertLevel.toTimeTrackingLevel(): TimeTrackingLevel {
    return when (this) {
        GREEN -> TimeTrackingLevel.GREEN
        AMBER -> TimeTrackingLevel.AMBER
        RED -> TimeTrackingLevel.RED
    }
}
