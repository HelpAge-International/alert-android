package org.alertpreparedness.platform.v2.models

import org.joda.time.DateTime

class TimeTrackingItem(
        var start: DateTime,
        var finish: DateTime,
        var value: Int?
) {

    fun copy(): TimeTrackingItem {
        return TimeTrackingItem(start, finish, value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimeTrackingItem) return false

        if (start != other.start) return false
        if (finish != other.finish) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + finish.hashCode()
        result = 31 * result + (value ?: 0)
        return result
    }
}
