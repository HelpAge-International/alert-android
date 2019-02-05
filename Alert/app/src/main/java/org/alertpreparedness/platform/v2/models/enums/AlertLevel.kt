package org.alertpreparedness.platform.v2.models.enums

import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class AlertLevel(val value: Int) {
    GREEN(0),
    AMBER(1),
    RED(2)
}

object AlertLevelSerializer : EnumSerializer<AlertLevel>(
        AlertLevel::class.java,
        { it?.value }
)

