package org.alertpreparedness.platform.v2.models.enums

import androidx.annotation.StringRes
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class Sector(val value: Int, @StringRes string: Int) {
    WASH(0, R.string.sector_wash),
    HEALTH(1, R.string.sector_health),
    SHELTER(2, R.string.sector_shelter),
    NUTRITION(3, R.string.sector_nutrition),
    FOOD_SECURITY_AND_LIVELIHOODS(4, R.string.sector_food_security_and_livelihoods),
    PROTECTION(5, R.string.sector_protection),
    CAMP_MANAGEMENT(6, R.string.sector_camp_management),
    OTHER(7, R.string.sector_other)
}

object SectorSerializer : EnumSerializer<Sector>(
        Sector::class.java,
        { it?.value }
)

