package org.alertpreparedness.platform.v2.models

class TimeTracking(
        val timeSpentInGreen: List<TimeTrackingItem>?,
        val timeSpentInAmber: List<TimeTrackingItem>?,
        val timeSpentInRed: List<TimeTrackingItem>?,
        val timeSpentInGrey: List<TimeTrackingItem>?
)