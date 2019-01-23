package org.alertpreparedness.platform.v2.utils.extensions

import org.joda.time.LocalTime

fun String.toLocalTime(): LocalTime {
    return LocalTime.parse(this)
}




