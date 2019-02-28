package org.alertpreparedness.platform.v2.utils.extensions

import io.reactivex.Observable
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.enums.ActionLevel.MPA
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel.AMBER
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel.GREEN
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel.GREY
import org.alertpreparedness.platform.v2.models.enums.TimeTrackingLevel.RED
import org.alertpreparedness.platform.v2.repository.Repository.db
import org.alertpreparedness.platform.v2.updateChildrenRx
import org.joda.time.DateTime

fun Action.updateTimeTracking(countryId: String, level: TimeTrackingLevel): Observable<Unit> {
    return db.child("alert")
            .child(countryId)
            .child(id)
            .child("timeTracking")
            .updateChildrenRx(this.timeTracking.updateTimeTrackingMap(level, false))
            .map { Unit }
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

fun Action.isActive(hazards: List<HazardScenario>): Boolean {
    return actionLevel == MPA || ((assignedHazards ?: listOf())
            .intersect(hazards)
            .isNotEmpty()
            || (assignedHazards == null && hazards.isNotEmpty()))
}

fun Action.canBeAssigned(): Boolean {
    return dueDate != null && budget != null && requireDoc != null
}
