package org.alertpreparedness.platform.alert.risk_monitoring

import com.google.gson.Gson
import io.reactivex.Observable
import org.alertpreparedness.platform.alert.AlertApplication
import org.json.JSONObject

/**
 * Created by Fei on 11/11/2017.
 */

object RiskMonitoringService {

    fun readJsonFile(): Observable<String> {
        return Observable.create { subscriber ->
            val fileText: String = AlertApplication.getContext().assets.open("country_levels_values.json").bufferedReader().use {
                it.readText()
            }
            subscriber.onNext(fileText)
            subscriber.onComplete()
        }
    }

    fun mapJasonToCountryData(jsonObject:JSONObject, gson: Gson) :Observable<CountryJsonData> {
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
}