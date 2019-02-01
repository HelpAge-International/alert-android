package org.alertpreparedness.platform.v2.models

import org.alertpreparedness.platform.v2.models.enums.DurationType

data class ClockSettings(val type: DurationType, val value: Int) {
    fun calculateOffset(): Long {
        return type.millis * value
    }
}