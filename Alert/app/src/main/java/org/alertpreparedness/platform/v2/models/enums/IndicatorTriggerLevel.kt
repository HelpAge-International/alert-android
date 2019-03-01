package org.alertpreparedness.platform.v2.models.enums

import androidx.annotation.StringRes
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v2.models.enums.IndicatorTriggerLevel.GREEN
import org.alertpreparedness.platform.v2.utils.EnumSerializer
import java.lang.reflect.Type

enum class IndicatorTriggerLevel(val value: Int, @StringRes val string: Int) {
    GREEN(0, R.string.green),
    AMBER(1, R.string.amber),
    RED(2, R.string.red)
}

object IndicatorTriggerLevelSerializer : EnumSerializer<IndicatorTriggerLevel>(
        IndicatorTriggerLevel::class.java,
        { it?.value }
)
