package org.alertpreparedness.platform.v2.models

import com.google.gson.annotations.SerializedName
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.joda.time.DateTime

class Hazard(
        @SerializedName("hazardScenario")
        val scenario: HazardScenario,
        val isActive: Boolean,
        val isSeasonal: Boolean,
        val risk: Int,
        val timeCreated: DateTime
) : BaseModel()