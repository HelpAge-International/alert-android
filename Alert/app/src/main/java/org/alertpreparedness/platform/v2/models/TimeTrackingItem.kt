package org.alertpreparedness.platform.v2.models

import org.joda.time.DateTime

class TimeTrackingItem(
        var start: DateTime,
        var finish: DateTime
) {

    fun copy(): TimeTrackingItem {
        return TimeTrackingItem(start, finish)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimeTrackingItem) return false

        if (start != other.start) return false
        if (finish != other.finish) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + finish.hashCode()
        return result
    }
}
