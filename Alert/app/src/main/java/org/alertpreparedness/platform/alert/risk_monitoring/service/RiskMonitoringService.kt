package org.alertpreparedness.platform.alert.risk_monitoring.service

import android.content.Context
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import org.alertpreparedness.platform.alert.risk_monitoring.model.*
import org.alertpreparedness.platform.alert.utils.AppUtils
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.FirebaseHelper
import org.alertpreparedness.platform.alert.utils.PreferHelper
import org.json.JSONObject
import timber.log.Timber
import java.io.StringReader

/**
 * Created by Fei on 11/11/2017.
 */

class RiskMonitoringService(private val context : Context) {

    public val gson: Gson = Gson()
    private val mAppStatus = PreferHelper.getString(context, Constants.APP_STATUS)

    fun readJsonFile(): Observable<String> {
        return Observable.create { subscriber ->
            val fileText: String = context.assets.open("country_levels_values.json").bufferedReader().use {
                it.readText()
            }
            subscriber.onNext(fileText)
            subscriber.onComplete()
        }
    }

    fun mapJasonToCountryData(jsonObject: JSONObject, gson: Gson): Observable<CountryJsonData> {
        return Observable.range(0, 249)
                .map {
                    if (!jsonObject.isNull(it.toString())) {
                        val value = jsonObject.get(it.toString()).toString()
                        val countryData = gson.fromJson(value, CountryJsonData::class.java)
                        countryData.countryId = it
                        return@map countryData
                    } else {
                        return@map CountryJsonData(it, listOf())
                    }
                }
    }

    fun getHazards(countryId: String): Flowable<List<ModelHazard>> {

        val hazardsRef = FirebaseHelper.getHazardsRef(mAppStatus, countryId)
        return RxFirebaseDatabase.observeValueEvent(hazardsRef, { snap ->
            println("hazardsRefsnap = ${snap}")
            if (snap.value != null && snap.children.count() > 0) {
                snap.children.map {
                    val toJson = gson.toJson(it.value)
                    val reader = JsonReader(StringReader(toJson.trim()))
                    reader.isLenient = true
                    val fromJson = gson.fromJson<ModelHazard>(reader, ModelHazard::class.java)
                    return@map fromJson.copy(id = it.key)
                }
            } else {
                return@observeValueEvent listOf<ModelHazard>()
            }
        })
    }

    fun getHazardOtherName(hazard: ModelHazard): Flowable<Pair<String, String>> {
        val hazardsOtherNameRef = FirebaseHelper.getHazardsOtherNameRef(mAppStatus, hazard.otherName)
        return RxFirebaseDatabase.observeValueEvent(hazardsOtherNameRef)
                .map { snap ->
                    hazard.id?.let { Pair<String, String>(it, JSONObject(gson.toJson(snap.value)).getString("name")) }
                }
    }

    fun getIndicators(hazardId: String): Flowable<List<ModelIndicator>> {
        val indicatorRef = FirebaseHelper.getIndicatorsRef(mAppStatus, hazardId)
        return RxFirebaseDatabase.observeValueEvent(indicatorRef, { snap ->
            snap.children.map {
                val toJson = gson.toJson(it.value)
                val reader = JsonReader(StringReader(toJson.trim()))
                reader.isLenient = true
                val fromJson = gson.fromJson<ModelIndicator>(reader, ModelIndicator::class.java)
                fromJson.hazardId = hazardId
                return@map fromJson.copy(id = it.key)
            }
        })
    }

    fun addIndicatorToHazard(indicator: ModelIndicator, countryContext: Boolean) {
        val indicatorRef = FirebaseHelper.getIndicatorsRef(mAppStatus, if (countryContext) PreferHelper.getString(context, Constants.COUNTRY_ID) else indicator.hazardScenario.id)
        val ref = indicatorRef.push()
        println("mIndicatorModeltriggerSelected = ${ref}")
        if (countryContext) {
            ref.setValue(indicator).continueWith {
                ref.child("hazardScenario").setValue(ModelHazardCountryContext())
            }
        } else {
            println("indicatorRef.child(key) = ${ref}")
            println("indicator = ${indicator}")
            try {
                indicator.hazardId = indicator.hazardScenario.id!!
                ref.setValue(indicator).continueWith {
                    val update = indicator.hazardScenario
                    val updateMap = mutableMapOf("active" to null, "isActive" to update.isActive, "id" to null, "key" to update.id, "seasonal" to null, "isSeasonal" to update.isSeasonal)
                    ref.child("hazardScenario").updateChildren(updateMap)
                }
            }
            catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getIndicatorsForAssignee(hazardId: String, network: ModelNetwork?): Flowable<List<ModelIndicator>> {
        val indicatorRef = FirebaseHelper.getIndicatorsRef(mAppStatus, hazardId).orderByChild("assignee").equalTo(PreferHelper.getString(context, Constants.UID))
        return RxFirebaseDatabase.observeValueEvent(indicatorRef, { snap ->
            snap.children.map {
                val toJson = gson.toJson(it.value)
                val reader = JsonReader(StringReader(toJson.trim()))
                reader.isLenient = true
                val fromJson = gson.fromJson<ModelIndicator>(reader, ModelIndicator::class.java)
                fromJson.hazardId = hazardId
                if (network != null) {
                    Timber.d("network id: %s, name: %s", network.id, network.name)
                    return@map fromJson.copy(id = it.key, networkId = network.id, networkName = network.name)
                } else {
                    return@map fromJson.copy(id = it.key)
                }
            }
        })
    }

    fun getIndicatorsForLocalNetwork(hazardId: String, network: ModelNetwork?, s: String?): Flowable<List<ModelIndicator>> {
//        println("getIndicatorsForLocalNetworks = ${s}")
        val indicatorRef = FirebaseHelper.getIndicatorsRef(mAppStatus, hazardId).orderByChild("assignee")
        return RxFirebaseDatabase.observeValueEvent(indicatorRef, { snap ->
            snap.children
            .filter {
                item ->
                return@filter item.child("assignee").value.toString() == PreferHelper.getString(context, Constants.UID)
                    || item.child("assignee").value.toString() == PreferHelper.getString(context, Constants.COUNTRY_ID)
            }
            .map {
//                println("mHazardNameMapNetworkLocal = ${s + hazardId}")
                val toJson = gson.toJson(it.value)
                val reader = JsonReader(StringReader(toJson.trim()))
                reader.isLenient = true
                val fromJson = gson.fromJson<ModelIndicator>(reader, ModelIndicator::class.java)
                fromJson.hazardId = hazardId
                if (network != null) {
                    return@map fromJson.copy(id = it.key, networkId = network.id, networkName = network.name)
                } else {
                    return@map fromJson.copy(id = it.key)
                }
            }
        })
    }

    fun getIndicatorModel(hazardId: String, indicatorId: String): Flowable<ModelIndicator> {
        Timber.d("actual ids: %s, %s", hazardId, indicatorId)
        val indicatorRef = FirebaseHelper.getIndicatorRef(mAppStatus, hazardId, indicatorId)
        return RxFirebaseDatabase.observeValueEvent(indicatorRef, { snap ->
            val toJson = gson.toJson(snap.value)
            val jsonReader = JsonReader(StringReader(toJson))
            jsonReader.isLenient = true
            val model = gson.fromJson<ModelIndicator>(jsonReader, ModelIndicator::class.java)
            model.hazardId = hazardId
            return@observeValueEvent model.copy(id = indicatorId)
        })
    }

    fun updateIndicator(hazardId: String, indicatorId: String, updateData: Map<String, Any>): Completable {
        val indicatorRef = FirebaseHelper.getIndicatorRef(mAppStatus, hazardId, indicatorId)
        return RxFirebaseDatabase.updateChildren(indicatorRef, updateData)
    }

    fun getLogs(id: String): Flowable<List<ModelLog>> {
        val logRef = FirebaseHelper.getIndicatorLogRef(mAppStatus, id)
        return RxFirebaseDatabase.observeValueEvent(logRef, { snap ->
            snap.children.map {
                val toJson = gson.toJson(it.value)
                val jsonReader = JsonReader(StringReader(toJson))
                jsonReader.isLenient = true
                return@map gson.fromJson<ModelLog>(jsonReader, ModelLog::class.java).copy(id = it.key)
            }
        })
    }

    fun deleteLog(id: String, logId: String) {
        val logRef = FirebaseHelper.getIndicatorLogRef(mAppStatus, id)
        logRef.child(logId).removeValue()
    }

    fun addLogToIndicator(log: ModelLog, indicatorId: String) {
        val logRef = FirebaseHelper.getIndicatorLogRef(mAppStatus, indicatorId)
        val key = logRef.push().key
        logRef.child(key).setValue(log)
    }

    fun updateLogContent(indicatorId: String, logId: String, content: String) {
        val logRef = FirebaseHelper.getIndicatorLogRef(mAppStatus, indicatorId)
        logRef.child(logId).child("content").setValue(content)
    }

    fun getHazardOtherNameString(id: String): Flowable<String> {
        val otherNameRef = AppUtils.getDatabase().getReference(mAppStatus).child("hazardOther").child(id).child("name")
        return RxFirebaseDatabase.observeValueEvent(otherNameRef, String::class.java)
    }

}