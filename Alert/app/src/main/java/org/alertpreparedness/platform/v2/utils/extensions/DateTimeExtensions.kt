package org.alertpreparedness.platform.v2.utils.extensions

import org.joda.time.DateTime
import org.joda.time.LocalDate

fun DateTime.isToday(): Boolean {
    return isBefore(LocalDate.now().plusDays(1).toDateTimeAtStartOfDay()) &&
            isAfter(LocalDate.now().toDateTimeAtStartOfDay())
}

fun DateTime.hasPassed(): Boolean {
    return isBeforeNow
}

fun DateTime.isThisWeek(): Boolean {
    return isAfter(LocalDate.now().toDateTimeAtStartOfDay()) &&
            isBefore(LocalDate.now().plusDays(7).toDateTimeAtStartOfDay())
}