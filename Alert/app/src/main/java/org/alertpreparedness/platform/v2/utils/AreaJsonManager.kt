package org.alertpreparedness.platform.v2.utils

import android.content.Context
import org.alertpreparedness.platform.v2.models.Area
import org.alertpreparedness.platform.v2.models.enums.Country
import org.jetbrains.anko.doAsync
import org.json.JSONObject

data class CountryArea(
        val country: Country,
        val level1Areas: Map<Int, Level1Area>
)

data class Level1Area(
        val id: Int,
        val name: String,
        val level2Areas: Map<Int, Level2Area>
)

data class Level2Area(
        val id: Int,
        val name: String
)

object AreaJsonManager {

    private var cache: Map<Int, CountryArea>? = null

    @Synchronized
    fun getAreas(context: Context): Map<Int, CountryArea> {
        return if (cache != null) {
            cache!!
        } else {
            loadFromJson(context)
        }
    }

    private fun loadFromJson(context: Context): Map<Int, CountryArea> {
        val fileText: String = context.assets.open("country_levels_values.json").bufferedReader().use {
            it.readText()
        }
        val jsonObject = JSONObject(fileText)

        cache = Country.values().map { country ->
            val countryJson = jsonObject.optJSONObject(country.value.toString())
            country.value to CountryArea(
                    country,
                    getLevelOneAreas(countryJson)
            )
        }
                .toMap()

        return cache!!
    }

    private fun getLevelOneAreas(countryJson: JSONObject?): Map<Int, Level1Area> {
        if (countryJson == null) {
            return emptyMap()
        }
        val levelOneAreas = mutableMapOf<Int, Level1Area>()
        val levelOneJsonArray = countryJson.getJSONArray("levelOneValues")
        for (index in 0 until levelOneJsonArray.length()) {
            val levelOneJson = levelOneJsonArray.optJSONObject(index)
            val id = levelOneJson.getInt("id")
            levelOneAreas.put(
                    id,
                    Level1Area(
                            id,
                            levelOneJson.getString("value"),
                            getLevelTwoAreas(levelOneJson)
                    )
            )
        }

        return levelOneAreas
    }

    private fun getLevelTwoAreas(levelOneJson: JSONObject?): Map<Int, Level2Area> {
        if (levelOneJson == null) {
            return emptyMap()
        }
        val levelTwoAreas = mutableMapOf<Int, Level2Area>()
        val levelTwoJsonArray = levelOneJson.getJSONArray("levelTwoValues")
        for (index in 0 until levelTwoJsonArray.length()) {
            val levelTwoJson = levelTwoJsonArray.optJSONObject(index)
            val id = levelTwoJson.getInt("id")
            levelTwoAreas[id] = Level2Area(
                    id,
                    levelTwoJson.getString("value")
            )
        }

        return levelTwoAreas
    }

    data class AreaData(
            val country: Country,
            val level1Name: Level1Area?,
            val level2Name: Level2Area?
    )

    fun getAreaData(context: Context, area: Area): AreaData {
        val areas = getAreas(context)

        val country = area.country
        val level1: Level1Area? = areas[country.value]?.level1Areas?.get(area.level1)
        val level2: Level2Area? = level1?.level2Areas?.get(area.level2)

        return AreaData(
                country,
                level1,
                level2
        )
    }

    fun preCache(context: Context) {
        doAsync {
            getAreas(context)
        }
    }
}

fun Area.getText(context: Context): String {
    val areaData = AreaJsonManager.getAreaData(context, this)

    return listOfNotNull(
            context.getString(areaData.country.string),
            areaData.level1Name,
            areaData.level2Name
    )
            .joinToString(", ")
}
