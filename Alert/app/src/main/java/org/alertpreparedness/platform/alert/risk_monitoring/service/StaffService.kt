package org.alertpreparedness.platform.alert.risk_monitoring.service

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toSingle
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelUserPublic
import org.alertpreparedness.platform.alert.utils.AppUtils
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.FirebaseHelper
import org.alertpreparedness.platform.alert.utils.PreferHelper
import java.io.StringReader

/**
 * Created by fei on 16/11/2017.
 */
object StaffService {

    val gson = Gson()

    fun getCountryStaff(countryId: String): Flowable<List<String>> {
        val staffCountry = FirebaseHelper.getStaffForCountry(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), countryId)
        return RxFirebaseDatabase.observeValueEvent(staffCountry)
                .map { snap ->
                    snap.children.map { it.key }
                }
    }

    fun getCountryAdmin(): Flowable<ModelUserPublic> {
        val userDetail = FirebaseHelper.getUserDetail(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), PreferHelper.getString(AlertApplication.getContext(), Constants.COUNTRY_ID))
        println("userDetail = ${userDetail.ref}")
        println("userDetail = ${userDetail}")

        return RxFirebaseDatabase.observeValueEvent(userDetail, { snap ->
            val toJson = RiskMonitoringService.gson.toJson(snap.value)
            val reader = JsonReader(StringReader(toJson.trim()))
            reader.isLenient = true
            val fromJson = RiskMonitoringService.gson.fromJson<ModelUserPublic>(reader, ModelUserPublic::class.java)
            println("fromJson = ${fromJson}")
            return@observeValueEvent fromJson
        })

    }

    fun getUserDetail(userId: String): Flowable<ModelUserPublic> {
        val userDetail = FirebaseHelper.getUserDetail(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), userId)
        return RxFirebaseDatabase.observeValueEvent(userDetail, ModelUserPublic::class.java)
                .map({ model: ModelUserPublic ->
                    println("model = ${model}")
                    model.id = userId
                    return@map model
                })
    }

    fun getCountryAdminDetail(userId: String): Flowable<ModelUserPublic> {
        val userDetail = FirebaseHelper.getUserDetail(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), userId)
        return RxFirebaseDatabase.observeValueEvent(userDetail, ModelUserPublic::class.java)
                .map({ model: ModelUserPublic ->
                    model.id = userId
                    return@map model
                })
    }
}