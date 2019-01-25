package org.alertpreparedness.platform.v2.models.enums

import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class ResponsePlanState(val value: Int) {
    IN_PROGRESS(0),
    NOT_APPROVED(1),
    APPROVED(2),
    REJECTED(3)
}

object ResponsePlanStateSerializer: EnumSerializer<ResponsePlanState>(ResponsePlanState::class.java, { it?.value } )
