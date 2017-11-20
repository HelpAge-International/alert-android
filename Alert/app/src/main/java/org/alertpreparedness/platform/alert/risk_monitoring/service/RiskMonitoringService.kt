package org.alertpreparedness.platform.alert.risk_monitoring.service

import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.Observable
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.helper.UserInfo
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelHazard
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.FirebaseHelper
import org.alertpreparedness.platform.alert.utils.PreferHelper
import org.json.JSONObject
import java.io.StringReader

/**
 * Created by Fei on 11/11/2017.
 */

object RiskMonitoringService {

    private val gson: Gson = Gson()

    fun readJsonFile(): Observable<String> {
        return Observable.create { subscriber ->
            val fileText: String = AlertApplication.getContext().assets.open("country_levels_values.json").bufferedReader().use {
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

        val hazardsRef = FirebaseHelper.getHazardsRef(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), countryId)
        return RxFirebaseDatabase.observeValueEvent(hazardsRef, { snap ->
            snap.children.map {
                val toJson = gson.toJson(it.value)
                val reader = JsonReader(StringReader(toJson.trim()))
                reader.isLenient = true
                val fromJson = gson.fromJson<ModelHazard>(reader, ModelHazard::class.java)
                return@map fromJson.copy(id = it.key)
            }
        })
    }

    fun getHazardOtherName(hazard: ModelHazard): Flowable<Pair<String, String>> {
        val hazardsOtherNameRef = FirebaseHelper.getHazardsOtherNameRef(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), hazard.otherName)
        return RxFirebaseDatabase.observeValueEvent(hazardsOtherNameRef)
                .map { snap ->
                    hazard.id?.let { Pair<String, String>(it, JSONObject(gson.toJson(snap.value)).getString("name")) }
                }
    }

    fun getIndicators(hazardId: String): Flowable<List<ModelIndicator>> {
        val indicatorRef = FirebaseHelper.getIndicatorsRef(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), hazardId)
        return RxFirebaseDatabase.observeValueEvent(indicatorRef, { snap ->
            snap.children.map {
                val toJson = gson.toJson(it.value)
                val reader = JsonReader(StringReader(toJson.trim()))
                reader.isLenient = true
                val fromJson = gson.fromJson<ModelIndicator>(reader, ModelIndicator::class.java)
                return@map fromJson.copy(id = it.key)
            }
        })
    }

    fun addIndicatorToHazard(indicator: ModelIndicator): Task<Void>? {
        val indicatorRef = FirebaseHelper.getIndicatorsRef(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), indicator.hazardScenario.id)
        val key = indicatorRef.push().key
        return indicatorRef.child(key).setValue(indicator)
    }

    fun getIndicatorsForAssignee(hazardId: String, networkId: String?): Flowable<List<ModelIndicator>> {
        val indicatorRef = FirebaseHelper.getIndicatorsRef(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), hazardId).orderByChild("assignee").equalTo(UserInfo.getUser(AlertApplication.getContext()).userID)
        return RxFirebaseDatabase.observeValueEvent(indicatorRef, { snap ->
            snap.children.map {
                val toJson = gson.toJson(it.value)
                val reader = JsonReader(StringReader(toJson.trim()))
                reader.isLenient = true
                val fromJson = gson.fromJson<ModelIndicator>(reader, ModelIndicator::class.java)
                if (networkId != null) {
                    return@map fromJson.copy(id = it.key, networkId = networkId)
                } else {
                    return@map fromJson.copy(id = it.key)
                }
            }
        })
    }


}