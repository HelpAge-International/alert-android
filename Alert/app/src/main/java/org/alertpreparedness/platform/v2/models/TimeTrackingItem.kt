package org.alertpreparedness.platform.v2.models

import org.joda.time.DateTime

class TimeTrackingItem(
        var start: DateTime,
        var finish: DateTime
) {

    fun copy(): TimeTrackingItem {
        return TimeTrackingItem(start, finish)
    }
}
