package org.alertpreparedness.platform.v2.utils.extensions

import io.reactivex.Observable
import io.reactivex.rxkotlin.combineLatest
import org.alertpreparedness.platform.v2.db
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.TimeTrackingItem
import org.alertpreparedness.platform.v2.models.enums.ActionLevel.MPA
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.setValueRx
import org.alertpreparedness.platform.v2.utils.extensions.TimeTrackingLevel.AMBER
import org.alertpreparedness.platform.v2.utils.extensions.TimeTrackingLevel.GREEN
import org.alertpreparedness.platform.v2.utils.extensions.TimeTrackingLevel.GREY
import org.alertpreparedness.platform.v2.utils.extensions.TimeTrackingLevel.RED
import org.joda.time.DateTime

enum class TimeTrackingLevel(val firebasePath: String) {
    RED("timeSpentInRed"),
    AMBER("timeSpentInAmber"),
    GREEN("timeSpentInGreen"),
    GREY("timeSpentInGrey")
}

fun Action.updateTimeTracking(countryId: String, level: TimeTrackingLevel): Observable<Unit> {
    val timeTrackingMap = mapOf<TimeTrackingLevel, List<TimeTrackingItem>>(
            RED to (timeTracking?.timeSpentInRed?.map { it.copy() } ?: listOf()),
            AMBER to (timeTracking?.timeSpentInAmber?.map { it.copy() } ?: listOf()),
            GREEN to (timeTracking?.timeSpentInGreen?.map { it.copy() } ?: listOf()),
            GREY to (timeTracking?.timeSpentInGrey?.map { it.copy() } ?: listOf())
    )

    val (currentLevel, currentLevelList) = timeTrackingMap
            .toList()
            .firstOrNull { (_, value) ->
                value.isTimerRunning()
            } ?: Pair<TimeTrackingLevel?, List<TimeTrackingItem>>(null, emptyList())


    if (currentLevel == level) {
        return Observable.just(Unit)
    } else {
        val currentTime = DateTime()
        val observables = mutableListOf<Observable<Unit>>()
        if (currentLevel != null) {
            currentLevelList.last().finish = currentTime

            observables += db.child("action")
                    .child(countryId)
                    .child(id)
                    .child("timeTracking")
                    .child(currentLevel.firebasePath)
                    .setValueRx(currentLevelList)
        }

        observables += db.child("action")
                .child(countryId)
                .child(id)
                .child("timeTracking")
                .child(level.firebasePath)
                .setValueRx(timeTrackingMap.getValue(level) + TimeTrackingItem(currentTime, DateTime(-1)))

        return observables.combineLatest { Unit }
    }
}

fun Action.getNewTimeTrackingLevel(hazards: List<HazardScenario>, updatedAt: DateTime? = this.updatedAt,
        assignee: String? = this.assignee, isArchived: Boolean = this.isArchived): TimeTrackingLevel {
    return if (isArchived) {
        GREY
    } else if (!isActive(hazards)) {
        GREY
    } else if (assignee == null) {
        RED
    } else if (getExpirationTime(updatedAt = updatedAt).isBeforeNow) {
        RED
    } else if (isComplete) {
        GREEN
    } else if (!isComplete) {
        AMBER
    }
    //This case should never happen
    else {
        GREY
    }
}

private fun List<TimeTrackingItem>.isTimerRunning(): Boolean {
    return isNotEmpty() && last().finish.millis == -1L
}

fun Action.isActive(hazards: List<HazardScenario>): Boolean {
    return actionLevel == MPA || ((assignedHazards ?: listOf())
            .intersect(hazards)
            .isNotEmpty()
            || (assignedHazards == null && hazards.isNotEmpty()))
}

fun Action.canBeAssigned(): Boolean {
    return dueDate != null && budget != null && requireDoc != null
}