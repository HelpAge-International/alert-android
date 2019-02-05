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
        val timeCreated: DateTime
) : BaseModel() {

    var state: AlertApprovalState? = null
}