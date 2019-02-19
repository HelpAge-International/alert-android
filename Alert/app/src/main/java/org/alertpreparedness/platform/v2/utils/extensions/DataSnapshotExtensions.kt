package org.alertpreparedness.platform.v2.utils.extensions

import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.stream.JsonReader
import org.alertpreparedness.platform.v1.utils.SnapshotExclusionStrat
import org.alertpreparedness.platform.v2.models.BaseModel
import org.alertpreparedness.platform.v2.models.ResponsePlanApproval
import org.alertpreparedness.platform.v2.models.ResponsePlanApprovalSerializer
import org.alertpreparedness.platform.v2.models.enums.ActionLevel
import org.alertpreparedness.platform.v2.models.enums.ActionLevelSerializer
import org.alertpreparedness.platform.v2.models.enums.ActionType
import org.alertpreparedness.platform.v2.models.enums.ActionTypeSerializer
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalState
import org.alertpreparedness.platform.v2.models.enums.AlertApprovalStateSerializer
import org.alertpreparedness.platform.v2.models.enums.AlertLevel
import org.alertpreparedness.platform.v2.models.enums.AlertLevelSerializer
import org.alertpreparedness.platform.v2.models.enums.Country
import org.alertpreparedness.platform.v2.models.enums.CountrySerializer
import org.alertpreparedness.platform.v2.models.enums.DurationType
import org.alertpreparedness.platform.v2.models.enums.DurationTypeSerializer
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.models.enums.HazardScenarioSerializer
import org.alertpreparedness.platform.v2.models.enums.IndicatorTriggerLevel
import org.alertpreparedness.platform.v2.models.enums.IndicatorTriggerLevelSerializer
import org.alertpreparedness.platform.v2.models.enums.Privacy
import org.alertpreparedness.platform.v2.models.enums.PrivacySerializer
import org.alertpreparedness.platform.v2.models.enums.ResponsePlanState
import org.alertpreparedness.platform.v2.models.enums.ResponsePlanStateSerializer
import org.alertpreparedness.platform.v2.models.enums.Sector
import org.alertpreparedness.platform.v2.models.enums.SectorSerializer
import org.alertpreparedness.platform.v2.models.enums.Title
import org.alertpreparedness.platform.v2.models.enums.TitleSerializer
import org.joda.time.DateTime
import java.io.StringReader
import java.lang.reflect.Type
import java.util.Date

fun DataSnapshot.childKeys(): List<String> {
    return children.mapNotNull { it.key }
}

fun DataSnapshot.firstChildKey(): String {
    return childKeys().first()
}
//DataSnapshot to model
val gson: Gson by lazy{
    val gsonBuilder = GsonBuilder()
    gsonBuilder.setExclusionStrategies(SnapshotExclusionStrat())
            .registerTypeAdapter(Date::class.java, JsonDeserializer<Date> {json, _, _ -> Date(json.asJsonPrimitive.asLong) })
            .registerTypeAdapter(Date::class.java, JsonSerializer<Date> {date, _, _ -> JsonPrimitive(date.time) })
            .registerTypeAdapter(DateTime::class.java, object : JsonDeserializer<DateTime>, JsonSerializer<DateTime> {
                override fun deserialize(json: JsonElement, typeOfT: Type?,
                        context: JsonDeserializationContext?): DateTime {
                    return DateTime(json.asJsonPrimitive.asLong)
                }

                override fun serialize(src: DateTime, typeOfSrc: Type?,
                        context: JsonSerializationContext?): JsonElement {
                    return JsonPrimitive(src.millis)
                }
            })
            .registerTypeAdapter(IndicatorTriggerLevel::class.java, IndicatorTriggerLevelSerializer)
            .registerTypeAdapter(ActionLevel::class.java, ActionLevelSerializer)
            .registerTypeAdapter(ActionType::class.java, ActionTypeSerializer)
            .registerTypeAdapter(ResponsePlanState::class.java, ResponsePlanStateSerializer)
            .registerTypeAdapter(ResponsePlanApproval::class.java, ResponsePlanApprovalSerializer)
            .registerTypeAdapter(DurationType::class.java, DurationTypeSerializer)
            .registerTypeAdapter(Title::class.java, TitleSerializer)
            .registerTypeAdapter(AlertLevel::class.java, AlertLevelSerializer)
            .registerTypeAdapter(HazardScenario::class.java, HazardScenarioSerializer)
            .registerTypeAdapter(AlertApprovalState::class.java, AlertApprovalStateSerializer)
            .registerTypeAdapter(Country::class.java, CountrySerializer)
            .registerTypeAdapter(Privacy::class.java, PrivacySerializer)
            .registerTypeAdapter(Sector::class.java, SectorSerializer)
            .create()
}

inline fun <reified T : BaseModel> DataSnapshot.toModel(objectModifier: (T, JsonObject) -> Unit = { _, _ -> }): T {
    return listOf(this).toMergedModel(objectModifier)
}

inline fun <reified T : BaseModel> Pair<DataSnapshot, DataSnapshot>.toMergedModel(
        objectModifier: (T, JsonObject) -> Unit = { _, _ -> }): T {
    return toList().toMergedModel(objectModifier)
}

inline fun <reified T : BaseModel> List<DataSnapshot>.toMergedModel(
        objectModifier: (T, JsonObject) -> Unit = { _, _ -> }): T {
    if(isEmpty()) throw IllegalArgumentException("")

    val id = first().key!!

    val jsonList = map {
        it.toJson()
    }

    var mergedObject = jsonList.first()
    val toMergeList = jsonList.subList(1, size)

    for (toMerge in toMergeList) {
        mergedObject = mergedObject.mergeWith(toMerge)
    }

    val toReturn: T = jsonToModel(id, mergedObject)
    objectModifier(toReturn, mergedObject)
    return toReturn
}

inline fun <reified T: BaseModel> jsonToModel(id: String, jsonObject: JsonObject): T {
    val reader = JsonReader(StringReader(gson.toJson(jsonObject).trim { it <= ' ' }))
    reader.isLenient = true
    val obj: T = gson.fromJson(reader, T::class.java)
    obj.id = id
    return obj
}

fun DataSnapshot.toJson(): JsonObject {
    return gson.toJsonTree(value).asJsonObject
}

fun DataSnapshot.toJsonArray(): JsonArray {
    return gson.toJsonTree(value).asJsonArray
}


fun JsonObject.mergeWith(other: JsonObject): JsonObject{
    val output = JsonObject()

    val keys = keySet().union(other.keySet())

    for (key in keys) {
        val o1: JsonElement? = get(key)
        val o2: JsonElement? = other.get(key)
        if(o1 != null && o2 != null){
            if(o1.isJsonObject && o2.isJsonObject){
                output.add(key, o1.asJsonObject.mergeWith(o2.asJsonObject))
            }
            else {
                output.add(key, o1)
            }
        }
        else if(o1 != null){
            output.add(key, o1)
        }
        else {
            output.add(key, o2)
        }
    }
    return output
}

