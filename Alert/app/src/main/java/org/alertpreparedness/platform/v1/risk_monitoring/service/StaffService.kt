package org.alertpreparedness.platform.v1.risk_monitoring.service

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import org.alertpreparedness.platform.BuildConfig
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelUserPublic
import org.alertpreparedness.platform.v1.utils.Constants
import org.alertpreparedness.platform.v1.utils.FirebaseHelper
import org.alertpreparedness.platform.v1.utils.PreferHelper
import java.io.StringReader

/**
 * Created by fei on 16/11/2017.
 */
class StaffService(private val context: Context) {

    val gson = Gson()

    fun getCountryStaff(countryId: String): Flowable<List<String>> {
        val staffCountry = FirebaseHelper.getStaffForCountry(BuildConfig.ROOT_NODE, countryId)
        return RxFirebaseDatabase.observeValueEvent(staffCountry)
                .map { snap ->
                    snap.children.map { it.key!! }
                }
    }

    fun getCountryAdmin(countryId : String): Flowable<ModelUserPublic> {
        val userDetail = FirebaseHelper.getUserDetail(BuildConfig.ROOT_NODE, PreferHelper.getString(context, Constants.COUNTRY_ID))

        return RxFirebaseDatabase.observeValueEvent(userDetail) { snap ->
            val toJson = RiskMonitoringService(context).gson.toJson(snap.value)
            val reader = JsonReader(StringReader(toJson.trim()))
            reader.isLenient = true
            val fromJson = RiskMonitoringService(context).gson.fromJson<ModelUserPublic>(reader, ModelUserPublic::class.java)
            fromJson.id = countryId
            return@observeValueEvent fromJson
        }
    }

    fun getUserDetail(userId: String): Flowable<ModelUserPublic> {
        val userDetail = FirebaseHelper.getUserDetail(BuildConfig.ROOT_NODE, userId)
        return RxFirebaseDatabase.observeValueEvent(userDetail)
                .map { snapshot: DataSnapshot ->

                    val toJson = RiskMonitoringService(context).gson.toJson(snapshot.value)
                    val reader = JsonReader(StringReader(toJson.trim()))
                    reader.isLenient = true
                    val model = RiskMonitoringService(context).gson.fromJson<ModelUserPublic>(reader, ModelUserPublic::class.java)

                    model.id = userId
                    return@map model
                }
    }

    fun getCountryAdminDetail(userId: String): Flowable<ModelUserPublic> {
        val userDetail = FirebaseHelper.getUserDetail(BuildConfig.ROOT_NODE, userId)
        return RxFirebaseDatabase.observeValueEvent(userDetail, ModelUserPublic::class.java)
                .map { model: ModelUserPublic ->
                    model.id = userId
                    return@map model
                }
    }
}