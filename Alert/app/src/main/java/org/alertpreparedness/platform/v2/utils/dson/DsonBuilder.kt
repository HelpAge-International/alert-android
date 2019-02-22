package org.alertpreparedness.platform.v2.utils.dson

import com.google.gson.ExclusionStrategy
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

class DsonBuilder {
    val gsonBuilder = GsonBuilder()
    val modelAdapters = mapOf<Class<*>, DsonModelAdapter<*>>()

    fun setExclusionStrategies(vararg strategies: ExclusionStrategy): DsonBuilder {
        gsonBuilder.setExclusionStrategies(*strategies)
        return this
    }

    fun registerTypeAdapter(type: Type, typeAdapter: Any): DsonBuilder {
        gsonBuilder.registerTypeAdapter(type, typeAdapter)
        return this
    }

    fun <T> registerModelAdapter(type: Class<T>, modelAdapter: DsonModelAdapter<T>) {
    }
}