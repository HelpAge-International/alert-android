package org.alertpreparedness.platform.v2.utils.dson

import com.google.firebase.database.DataSnapshot
import com.google.gson.JsonObject

interface DsonModelAdapter<T> {
    fun toLocal(dataSnapshots: List<DataSnapshot>, input: JsonObject, output: T) {
    }

    fun toFirebase(input: T, output: JsonObject) {
    }
}