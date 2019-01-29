package org.alertpreparedness.platform.v2.utils.extensions

import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.gson.stream.JsonReader
import org.alertpreparedness.platform.v1.utils.SnapshotExclusionStrat
import org.alertpreparedness.platform.v2.models.BaseModel
import org.alertpreparedness.platform.v2.models.ResponsePlanApprovalSerializer
import org.alertpreparedness.platform.v2.models.ResponsePlanApprovalState
import org.alertpreparedness.platform.v2.models.enums.ActionLevel
import org.alertpreparedness.platform.v2.models.enums.ActionLevelSerializer
import org.alertpreparedness.platform.v2.models.enums.ActionType
import org.alertpreparedness.platform.v2.models.enums.ActionTypeSerializer
import org.alertpreparedness.platform.v2.models.enums.ResponsePlanState
import org.alertpreparedness.platform.v2.models.enums.ResponsePlanStateSerializer
import org.alertpreparedness.platform.v2.models.enums.IndicatorTriggerLevel
import org.alertpreparedness.platform.v2.models.enums.IndicatorTriggerLevelSerializer
import org.joda.time.DateTime
import java.io.StringReader
import java.lang.IllegalArgumentException
import java.util.Date

fun DataSnapshot.firstChildKey(): String {
    return childKeys().first()
}

fun DataSnapshot.childKeys(): List<String> {
    return children.mapNotNull { it.key }
}


//DataSnapshot to model
val gson: Gson by lazy{
    val gsonBuilder = GsonBuilder()
    gsonBuilder.setExclusionStrategies(SnapshotExclusionStrat())
            .registerTypeAdapter(Date::class.java, JsonDeserializer<Date> {json, _, _ -> Date(json.asJsonPrimitive.asLong) })
            .registerTypeAdapter(Date::class.java, JsonSerializer<Date> {date, _, _ -> JsonPrimitive(date.time) })
            .registerTypeAdapter(DateTime::class.java, JsonDeserializer<DateTime> {json, _, _ -> DateTime(json.asJsonPrimitive.asLong) })
            .registerTypeAdapter(DateTime::class.java, JsonSerializer<DateTime> {date, _, _ -> JsonPrimitive(date.millis) })
            .registerTypeAdapter(IndicatorTriggerLevel::class.java, IndicatorTriggerLevelSerializer)
            .registerTypeAdapter(ActionLevel::class.java, ActionLevelSerializer)
            .registerTypeAdapter(ActionType::class.java, ActionTypeSerializer)
            .registerTypeAdapter(ResponsePlanState::class.java, ResponsePlanStateSerializer)
            .registerTypeAdapter(ResponsePlanApprovalState::class.java, ResponsePlanApprovalSerializer)
            .create()
}

inline fun <reified T: BaseModel> DataSnapshot.toModel(): T {
    return listOf(this).toMergedModel()
}

inline fun <reified T: BaseModel> Pair<DataSnapshot, DataSnapshot>.toMergedModel(): T {
    return toList().toMergedModel()
}

inline fun <reified T: BaseModel> List<DataSnapshot>.toMergedModel(): T {
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

    return jsonToModel(id, mergedObject)
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

