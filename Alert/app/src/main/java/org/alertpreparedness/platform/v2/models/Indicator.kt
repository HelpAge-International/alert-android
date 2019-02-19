package org.alertpreparedness.platform.v2.models

import com.google.gson.annotations.SerializedName
import org.alertpreparedness.platform.v2.models.enums.IndicatorTriggerLevel
import org.joda.time.DateTime

class Indicator(
        val assignee: String,
        val category: Int,
        val dueDate: DateTime,
        val geoLoation: Int,
        val updatedAt: DateTime,
        @SerializedName("triggerSelected")
        val triggerLevel: IndicatorTriggerLevel,
        val name: String
) : BaseModel() {

    @Transient
    lateinit var parentId: String
}