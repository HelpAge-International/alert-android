package org.alertpreparedness.platform.v2.models

import com.google.gson.annotations.SerializedName
import org.alertpreparedness.platform.v2.models.enums.ActionLevel
import org.alertpreparedness.platform.v2.models.enums.ActionType
import org.joda.time.DateTime
import java.util.Date

class Action(
        @SerializedName("asignee")
        val assignee: String?,
        val budget: Int,
        val createdAt: DateTime,
        @SerializedName("department")
        val departmentId: String,
        val dueDate: DateTime?,
        val isComplete: Boolean,
        val isCompleteAt: Date,
        @SerializedName("level")
        val actionLevel: ActionLevel,
        @SerializedName("type")
        val actionType: ActionType,
        val requireDoc: Boolean,
        val task: String,
        val updatedAt: DateTime
) : BaseModel() {

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

        return true
    }

    override fun hashCode(): Int {
        var result = assignee?.hashCode() ?: 0
        result = 31 * result + budget
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + departmentId.hashCode()
        result = 31 * result + dueDate.hashCode()
        result = 31 * result + isComplete.hashCode()
        result = 31 * result + isCompleteAt.hashCode()
        result = 31 * result + actionLevel.hashCode()
        result = 31 * result + actionType.hashCode()
        result = 31 * result + requireDoc.hashCode()
        result = 31 * result + task.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}