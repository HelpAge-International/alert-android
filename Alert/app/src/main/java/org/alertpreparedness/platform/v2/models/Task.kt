package org.alertpreparedness.platform.v2.models

import org.alertpreparedness.platform.v2.models.enums.ActionLevel
import org.alertpreparedness.platform.v2.models.enums.IndicatorTriggerLevel
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
        val indicatorTriggerLevel: IndicatorTriggerLevel
) : Task(id, label, dueDate) {

    constructor(indicator: Indicator) : this(
            indicator.id,
            indicator.name,
            indicator.dueDate,
            indicator.triggerLevel
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IndicatorTask) return false
        if (!super.equals(other)) return false

        if (indicatorTriggerLevel != other.indicatorTriggerLevel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + indicatorTriggerLevel.hashCode()
        return result
    }
}

class ActionTask(
        id: String,
        label: String,
        dueDate: DateTime,
        val actionLevel: ActionLevel
) : Task(id, label, dueDate) {

    constructor(action: Action) : this(
            action.id,
            action.task,
            action.dueDate,
            action.actionLevel
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActionTask) return false
        if (!super.equals(other)) return false

        if (actionLevel != other.actionLevel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + actionLevel.hashCode()
        return result
    }
}

class ApprovalTask(
        id: String,
        label: String
) : Task(id, label, DateTime(0)) {

    constructor(responsePlan: ResponsePlan) : this(
            responsePlan.id,
            responsePlan.name
    )
}