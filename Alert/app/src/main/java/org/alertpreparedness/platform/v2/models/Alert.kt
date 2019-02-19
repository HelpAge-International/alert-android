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
        val timeCreated: DateTime,
        val reasonForRedAlert: String?,
        val affectedAreas: List<Area>,
        val timeUpdated: DateTime?,
        val updatedBy: String?,
        val otherName: String?,
        val name: String?,
        val timeTracking: TimeTracking?,
        val previousIsAmber: Boolean
) : BaseModel() {

    @Transient
    var state: AlertApprovalState? = null
}