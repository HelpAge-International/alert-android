package org.alertpreparedness.platform.v2.utils.extensions

import com.google.gson.JsonElement
import com.google.gson.JsonObject

operator fun JsonElement.get(key: String): JsonElement? {
    return asJsonObject[key]
}

fun JsonElement.firstChild(): JsonElement? {
    return when {
        isJsonArray -> asJsonArray.firstOrNull()
        isJsonObject -> {
            val obj = asJsonObject
            obj.get(obj.firstChildKey())
        }
        else -> null
    }
}

fun JsonObject.childKeys(): List<String> {
    return keySet().mapNotNull { it }
}

fun JsonObject.firstChildKey(): String {
    return childKeys().first()
}

