package org.alertpreparedness.platform.v2.models

class TimeTracking(
        val timeSpentInGreen: List<TimeTrackingItem>?,
        val timeSpentInAmber: List<TimeTrackingItem>?,
        val timeSpentInRed: List<TimeTrackingItem>?,
        val timeSpentInGrey: List<TimeTrackingItem>?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimeTracking) return false

        if (timeSpentInGreen != other.timeSpentInGreen) return false
        if (timeSpentInAmber != other.timeSpentInAmber) return false
        if (timeSpentInRed != other.timeSpentInRed) return false
        if (timeSpentInGrey != other.timeSpentInGrey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeSpentInGreen?.hashCode() ?: 0
        result = 31 * result + (timeSpentInAmber?.hashCode() ?: 0)
        result = 31 * result + (timeSpentInRed?.hashCode() ?: 0)
        result = 31 * result + (timeSpentInGrey?.hashCode() ?: 0)
        return result
    }
}