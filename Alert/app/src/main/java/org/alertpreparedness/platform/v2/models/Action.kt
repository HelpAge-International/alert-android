package org.alertpreparedness.platform.v2.models

import com.google.gson.annotations.SerializedName
import org.alertpreparedness.platform.v2.models.enums.ActionType
import org.joda.time.DateTime
import java.util.Date

class Action(
        val assignee: String,
        val budget: Int,
        val createdAt: DateTime,
        @SerializedName("department")
        val departmentId: String,
        val dueDate: DateTime,
        val isComplete: Boolean,
        val isCompleteAt: Date,
        @SerializedName("level")
        val actionType: ActionType,
        val requireDoc: Boolean,
        val task: String,
        val type: Int,
        val updatedAt: DateTime
) : BaseModel() {

    override fun toString(): String {
        return "Action(assignee='$assignee', budget=$budget, createdAt=$createdAt, departmentId='$departmentId', dueDate=$dueDate, isComplete=$isComplete, isCompleteAt=$isCompleteAt, actionType=$actionType, requireDoc=$requireDoc, task='$task', type=$type, updatedAt=$updatedAt)"
    }
}