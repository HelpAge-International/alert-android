package org.alertpreparedness.platform.v2.models.enums

import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class DurationType(val value: Int, val millis: Long) {
    WEEK(0, 1000L * 60 * 60 * 7),
    MONTH(1, 1000L * 60 * 60 * 30),
    YEAR(2, 1000L * 60 * 60 * 365)
}

object DurationTypeSerializer : EnumSerializer<DurationType>(
        DurationType::class.java,
        { it?.value }
)
