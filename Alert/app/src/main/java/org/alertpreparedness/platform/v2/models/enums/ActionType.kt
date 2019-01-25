package org.alertpreparedness.platform.v2.models.enums

import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class ActionType(val value: Int) {
    MPA(1),
    APA(2)
}

object ActionTypeSerializer : EnumSerializer<ActionType>(
        ActionType::class.java,
        { it?.value }
)
