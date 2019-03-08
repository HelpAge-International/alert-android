package org.alertpreparedness.platform.v2.models

import org.joda.time.DateTime

abstract class Task(val id: String, val label: String, val dueDate: DateTime){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Task) return false

        if (id != other.id) return false
        if (label != other.label) return false
        if (dueDate != other.dueDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + dueDate.hashCode()
        return result
    }
}

class IndicatorTask(
        id: String,
        label: String,
        dueDate: DateTime,
        val indicator: Indicator
) : Task(id, label, dueDate) {

    constructor(indicator: Indicator) : this(
            indicator.id,
            indicator.name,
            indicator.dueDate,
            indicator
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IndicatorTask) return false
        if (!super.equals(other)) return false

        if (indicator != other.indicator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + indicator.hashCode()
        return result
    }
}

class ActionTask(
        id: String,
        label: String,
        dueDate: DateTime,
        val action: Action
) : Task(id, label, dueDate) {
    constructor(action: Action) : this(
            action.id,
            action.task,
            // If the action is assigned to the user, then there will be a due date.
            // ... OR at least it should be, for some reason some actions in live don't
            // have a due date so to stop it crashing here the default is set to 1 year from now
            // (So the task will never be visible in app)
            action.dueDate ?: DateTime().plusYears(1),
            action
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActionTask) return false
        if (!super.equals(other)) return false

        if (action != other.action) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + action.hashCode()
        return result
    }
}

class ApprovalTask(
        id: String,
        label: String,
        val responsePlan: ResponsePlan
) : Task(id, label, DateTime(0)) {

    constructor(responsePlan: ResponsePlan) : this(
            responsePlan.id,
            responsePlan.name,
            responsePlan
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApprovalTask) return false
        if (!super.equals(other)) return false

        if (responsePlan != other.responsePlan) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + responsePlan.hashCode()
        return result
    }
}