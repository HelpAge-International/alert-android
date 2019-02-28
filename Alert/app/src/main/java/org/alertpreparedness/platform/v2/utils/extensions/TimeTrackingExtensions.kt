package org.alertpreparedness.platform.v2.utils.extensions

import org.alertpreparedness.platform.v2.models.TimeTracking
import org.alertpreparedness.platform.v2.models.TimeTrackingItem
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel.AMBER
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel.GREEN
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel.GREY
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel.RED
import org.joda.time.DateTime

fun List<TimeTrackingItem>.isTimerRunning(): Boolean {
    return isNotEmpty() && last().finish.millis == -1L
}

fun newTimeTracking(level: TimeTrackingLevel, uploadValue: Boolean = true): Map<String, Any> {
    return mapOf(
            level.firebasePath to listOf(TimeTrackingItem(DateTime(), DateTime(-1), level.value).toMap(uploadValue))
    )
}

fun TimeTracking?.updateTimeTrackingMap(level: TimeTrackingLevel, uploadValue: Boolean = true): Map<String, Any> {
    val timeTrackingMap = mapOf(
            RED to (this?.timeSpentInRed?.toMutableList() ?: mutableListOf()),
            AMBER to (this?.timeSpentInAmber?.toMutableList() ?: mutableListOf()),
            GREEN to (this?.timeSpentInGreen?.toMutableList() ?: mutableListOf()),
            GREY to (this?.timeSpentInGrey?.toMutableList() ?: mutableListOf())
    )

    val (currentLevel, currentLevelList) = timeTrackingMap
            .toList()
            .firstOrNull { (_, value) ->
                value.isTimerRunning()
            } ?: Pair<TimeTrackingLevel?, List<TimeTrackingItem>>(null, emptyList())



    if (currentLevel != level) {
        val currentTime = DateTime()
        if (currentLevel != null) {
            currentLevelList.last().finish = currentTime
        }

        timeTrackingMap.getValue(level).add(TimeTrackingItem(currentTime, DateTime(-1), level.value))
    }

    return timeTrackingMap.mapKeys { (level, _) ->
        level.firebasePath
    }
            .mapValues { (_, timeTrackingItems) ->
                timeTrackingItems.map { it.toMap(uploadValue) }
            }
}

fun TimeTrackingItem.toMap(uploadValue: Boolean = false): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>(
            "start" to start.millis,
            "finish" to finish.millis
    )

    if (uploadValue) {
        map["value"] = value as Any?
    }

    return map
}
