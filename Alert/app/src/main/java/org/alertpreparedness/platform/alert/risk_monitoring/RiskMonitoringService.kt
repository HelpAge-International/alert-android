package org.alertpreparedness.platform.alert.risk_monitoring

import com.google.gson.Gson
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.Observable
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.FirebaseHelper
import org.alertpreparedness.platform.alert.utils.PreferHelper
import org.json.JSONObject

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
//                            Timber.d(it)
                        val value = jsonObject.get(it.toString()).toString()
                        val countryData = gson.fromJson(value, CountryJsonData::class.java)
                        countryData.countryId = it
                        return@map countryData
//                            Timber.d(countryData.toString())
//                        mCountryDataMap.put(it, countryData)
                    } else {
                        return@map CountryJsonData(it, listOf())
                    }
                }
    }

    fun getHazards(countryId: String): Flowable<List<ModelHazard>> {

        val hazardsRef = FirebaseHelper.getHazardsRef(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), countryId)
        return RxFirebaseDatabase.observeValueEvent(hazardsRef, { snap ->
            snap.children.map {
                val fromJson = gson.fromJson(it.value.toString(), ModelHazard::class.java)
                return@map fromJson.copy(id = it.key)
            }
        })

    }

    fun getIndicators(hazardId: String): Flowable<List<ModelIndicator>> {
        val indicatorRef = FirebaseHelper.getIndicatorsRef(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), hazardId)
        return RxFirebaseDatabase.observeValueEvent(indicatorRef, { snap ->
            snap.children.map {
                val fromJson = gson.fromJson(it.value.toString(), ModelIndicator::class.java)
                return@map fromJson.copy(id = it.key)
            }
        })
    }


}