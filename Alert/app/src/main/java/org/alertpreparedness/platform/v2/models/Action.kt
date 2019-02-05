package org.alertpreparedness.platform.v2.models

import com.google.gson.annotations.SerializedName
import org.alertpreparedness.platform.v2.models.enums.ActionLevel
import org.alertpreparedness.platform.v2.models.enums.ActionType
import org.joda.time.DateTime

class Action(
        @SerializedName("asignee")
        val assignee: String?,
        val budget: Double?,
        val createdAt: DateTime,
        @SerializedName("department")
        val departmentId: String,
        val dueDate: DateTime?,
        val isComplete: Boolean,
        val isCompleteAt: DateTime?,
        @SerializedName("level")
        val actionLevel: ActionLevel,
        @SerializedName("type")
        val actionType: ActionType,
        val requireDoc: Boolean,
        val task: String,
        val updatedAt: DateTime,
        val isArchived: Boolean
) : BaseModel() {

    @Transient
    lateinit var clockSettings: ClockSettings

    fun getExpirationTime(): DateTime {
        return if (isCompleteAt != null) {
            isCompleteAt.plus(clockSettings.calculateOffset())
        } else {
            updatedAt.plus(clockSettings.calculateOffset())
        }
    }

    override fun toString(): String {
        return "Action(assignee='$assignee', budget=$budget, createdAt=$createdAt, departmentId='$departmentId', dueDate=$dueDate, isComplete=$isComplete, isCompleteAt=$isCompleteAt, actionLevel=$actionLevel, requireDoc=$requireDoc, task='$task', type=$actionType, updatedAt=$updatedAt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Action) return false

        if (assignee != other.assignee) return false
        if (budget != other.budget) return false
        if (createdAt != other.createdAt) return false
        if (departmentId != other.departmentId) return false
        if (dueDate != other.dueDate) return false
        if (isComplete != other.isComplete) return false
        if (isCompleteAt != other.isCompleteAt) return false
        if (actionLevel != other.actionLevel) return false
        if (actionType != other.actionType) return false
        if (requireDoc != other.requireDoc) return false
        if (task != other.task) return false
        if (updatedAt != other.updatedAt) return false
        if (isArchived != other.isArchived) return false
        if (clockSettings != other.clockSettings) return false

        return true
    }

    override fun hashCode(): Int {
        var result = assignee?.hashCode() ?: 0
        result = 31 * result + (budget?.hashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + departmentId.hashCode()
        result = 31 * result + (dueDate?.hashCode() ?: 0)
        result = 31 * result + isComplete.hashCode()
        result = 31 * result + (isCompleteAt?.hashCode() ?: 0)
        result = 31 * result + actionLevel.hashCode()
        result = 31 * result + actionType.hashCode()
        result = 31 * result + requireDoc.hashCode()
        result = 31 * result + task.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + isArchived.hashCode()
        result = 31 * result + clockSettings.hashCode()
        return result
    }
}