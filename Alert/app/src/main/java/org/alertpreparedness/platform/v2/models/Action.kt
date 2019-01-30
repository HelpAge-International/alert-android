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
        val dueDate: DateTime,
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
}