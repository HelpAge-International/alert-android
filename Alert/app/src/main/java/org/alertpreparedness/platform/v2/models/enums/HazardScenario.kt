package org.alertpreparedness.platform.v2.models.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class HazardScenario(
        val value: Int,
        @StringRes
        val text: Int,
        @DrawableRes
        val icon: Int
) {

    OTHER(-1, R.string.hazard_other, R.drawable.other),
    COLD_WAVE(0, R.string.hazard_cold_wave, R.drawable.cold_wave),
    CONFLICT(1, R.string.hazard_conflict, R.drawable.conflict),
    CYCLONE(2, R.string.hazard_cyclone, R.drawable.cyclone),
    DROUGHT(3, R.string.hazard_drought, R.drawable.drought),
    EARTHQUAKE(4, R.string.hazard_earthquake, R.drawable.earthquake),
    EPIDEMIC(5, R.string.hazard_epidemic, R.drawable.epidemic),
    FIRE(6, R.string.hazard_fire, R.drawable.fire),
    FLASH_FLOOD(7, R.string.hazard_flash_flood, R.drawable.flash_flood),
    FLOOD(8, R.string.hazard_flood, R.drawable.flood),
    HEAT_WAVE(9, R.string.hazard_heat_wave, R.drawable.heat_wave),
    HEAVY_RAIN(10, R.string.hazard_heavy_rain, R.drawable.heavy_rain),
    HUMANITARIAN_ACCESS(11, R.string.hazard_humanitarian_access, R.drawable.humanitarian_access),
    INSECT_INFESTATION(12, R.string.hazard_insect_infestation, R.drawable.insect_infestation),
    LANDSLIDE(13, R.string.hazard_landslide, R.drawable.landslide_mudslide),
    LOCUST_INFESTATION(14, R.string.hazard_locust_infestation, R.drawable.locust_infestation),
    MUDSLIDE(15, R.string.hazard_mudslide, R.drawable.landslide_mudslide),
    POPULATION_DISPLACEMENT(16, R.string.hazard_population_displacement, R.drawable.population_displacement),
    POPULATION_RETURN(17, R.string.hazard_population_return, R.drawable.population_return),
    SNOW_AVALANCHE(18, R.string.hazard_snow_avalanche, R.drawable.snow_avalanche),
    SNOWFALL(19, R.string.hazard_snowfall, R.drawable.snowfall),
    STORM(20, R.string.hazard_storm, R.drawable.storm),
    STORM_SURGE(21, R.string.hazard_storm_surge, R.drawable.storm_surge),
    TECHNOLOGICAL_DISASTER(22, R.string.hazard_technological_disaster, R.drawable.technological_disaster),
    TORNADO(23, R.string.hazard_tornado, R.drawable.tornado),
    TSUNAMI(24, R.string.hazard_tsunami, R.drawable.tsunami),
    VIOLENT_WIND(25, R.string.hazard_violent_wind, R.drawable.violent_wind),
    VOLCANO(26, R.string.hazard_volcano, R.drawable.volcano);
}

object HazardScenarioSerializer : EnumSerializer<HazardScenario>(
        HazardScenario::class.java,
        { it?.value }
)

