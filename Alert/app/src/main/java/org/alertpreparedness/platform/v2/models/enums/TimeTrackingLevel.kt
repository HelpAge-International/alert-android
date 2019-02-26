package org.alertpreparedness.platform.v2.models.enums

enum class TimeTrackingLevel(val firebasePath: String, val value: Int) {
    RED("timeSpentInRed", 2),
    AMBER("timeSpentInAmber", 1),
    GREEN("timeSpentInGreen", 0),
    GREY("timeSpentInGrey", 3)
}