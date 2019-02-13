package org.alertpreparedness.platform.v2.models

import org.alertpreparedness.platform.v2.models.enums.DurationType

enum class ClockSettingsSource {
    ACTION,
    COUNTRY
}

data class ClockSettings(val clockSettingsSource: ClockSettingsSource, val type: DurationType, val value: Int) {
    fun calculateOffset(): Long {
        return type.millis * value
    }
}