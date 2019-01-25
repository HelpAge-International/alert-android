package org.alertpreparedness.platform.v2.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

open class EnumSerializer<T : Enum<T>>(val clazz: Class<T>, val converter: (T?) -> Int?) : JsonSerializer<T>,
        JsonDeserializer<T> {

    override fun serialize(src: T?, typeOfSrc: Type?,
            context: JsonSerializationContext?): JsonElement? {
        return JsonPrimitive(converter(src) ?: 0)
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?,
            context: JsonDeserializationContext?): T {
        val value = clazz.enumConstants.firstOrNull { converter(it) == json?.asInt }

        return value ?: clazz.enumConstants.first()
    }

    fun jsonToEnum(jsonVal: Int?): T? {
        return clazz.enumConstants.firstOrNull { converter(it) == jsonVal }
    }

    fun enumToJson(t: T?): Int? {
        return converter(t)
    }
}