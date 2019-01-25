package org.alertpreparedness.platform.v2.models.enums

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.alertpreparedness.platform.v2.models.enums.IndicatorTriggerLevel.GREEN
import org.alertpreparedness.platform.v2.utils.EnumSerializer
import java.lang.reflect.Type

enum class IndicatorTriggerLevel(val value: Int) {
    GREEN(0),
    AMBER(1),
    RED(2)
}

object IndicatorTriggerLevelSerializer : EnumSerializer<IndicatorTriggerLevel>(
        IndicatorTriggerLevel::class.java,
        { it?.value }
)
