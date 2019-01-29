package org.alertpreparedness.platform.v2.models.enums

import androidx.annotation.StringRes
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class ActionLevel(val value: Int, @StringRes val string: Int) {
    MPA(1, R.string.minimum),
    APA(2, R.string.advanced)
}

object ActionLevelSerializer : EnumSerializer<ActionLevel>(
        ActionLevel::class.java,
        { it?.value }
)
