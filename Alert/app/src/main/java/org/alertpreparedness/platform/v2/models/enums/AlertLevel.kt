package org.alertpreparedness.platform.v2.models.enums

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class AlertLevel(val value: Int, @ColorRes val color: Int, @StringRes val string: Int) {
    GREEN(0, R.color.alertGreen, R.string.green_alert_text),
    AMBER(1, R.color.alertAmber, R.string.amber_alert_text),
    RED(2, R.color.alertRed, R.string.red_alert_text)
}

object AlertLevelSerializer : EnumSerializer<AlertLevel>(
        AlertLevel::class.java,
        { it?.value }
)

