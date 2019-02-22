package org.alertpreparedness.platform.v2.models

import com.google.gson.annotations.SerializedName
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState
import org.alertpreparedness.platform.v2.models.enums.AlertLevel
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.joda.time.DateTime

class Alert(
        @SerializedName("alertLevel")
        val level: AlertLevel,
        val createdBy: String,
        val estimatedPopulation: Long,
        val hazardScenario: HazardScenario,
        val infoNotes: String,
        val reasonForRedAlert: String?,
        val affectedAreas: List<Area>,
        @SerializedName("timeUpdated")
        val updatedAt: DateTime?,
        @SerializedName("timeCreated")
        val createdAt: DateTime,
        val updatedBy: String?,
        val otherName: String?,
        val name: String?,
        val timeTracking: TimeTracking?,
        val previousIsAmber: Boolean,
        val redAlertApproved: Boolean
) : BaseModel() {

    @Transient
    var state: AlertApprovalState? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Alert) return false

        if (level != other.level) return false
        if (createdBy != other.createdBy) return false
        if (estimatedPopulation != other.estimatedPopulation) return false
        if (hazardScenario != other.hazardScenario) return false
        if (infoNotes != other.infoNotes) return false
        if (reasonForRedAlert != other.reasonForRedAlert) return false
        if (affectedAreas != other.affectedAreas) return false
        if (updatedAt != other.updatedAt) return false
        if (createdAt != other.createdAt) return false
        if (updatedBy != other.updatedBy) return false
        if (otherName != other.otherName) return false
        if (name != other.name) return false
        if (timeTracking != other.timeTracking) return false
        if (previousIsAmber != other.previousIsAmber) return false
        if (redAlertApproved != other.redAlertApproved) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = level.hashCode()
        result = 31 * result + createdBy.hashCode()
        result = 31 * result + estimatedPopulation.hashCode()
        result = 31 * result + hazardScenario.hashCode()
        result = 31 * result + infoNotes.hashCode()
        result = 31 * result + (reasonForRedAlert?.hashCode() ?: 0)
        result = 31 * result + affectedAreas.hashCode()
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (updatedBy?.hashCode() ?: 0)
        result = 31 * result + (otherName?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (timeTracking?.hashCode() ?: 0)
        result = 31 * result + previousIsAmber.hashCode()
        result = 31 * result + redAlertApproved.hashCode()
        result = 31 * result + (state?.hashCode() ?: 0)
        return result
    }
}
