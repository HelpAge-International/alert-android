package org.alertpreparedness.platform.v2.models.enums

import androidx.annotation.StringRes
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class ActionType(val value: Int, @StringRes val text: Int) {
    CHS(0, R.string.action_type_chs),
    MANDATED(1, R.string.action_type_mandated),
    CUSTOM(2, R.string.action_type_custom)
}

object ActionTypeSerializer : EnumSerializer<ActionType>(
        ActionType::class.java,
        { it?.value }
)
