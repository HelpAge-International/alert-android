package org.alertpreparedness.platform.v2.models.enums

import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class ActionType(val value: Int) {
    CHS(0),
    MANDATED(1),
    CUSTOM(2)
}

object ActionTypeSerializer : EnumSerializer<ActionType>(
        ActionType::class.java,
        { it?.value }
)
