package org.alertpreparedness.platform.v2.models.enums

import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class AlertApprovalState(val value: Int) {
    WAITING_RESPONSE(0),
    APPROVED(1),
    REJECTED(2),
}

object AlertApprovalStateSerializer :
        EnumSerializer<AlertApprovalState>(AlertApprovalState::class.java, { it?.value })
