package org.alertpreparedness.platform.alert.risk_monitoring.service

import com.google.gson.Gson
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelUserPublic
import org.alertpreparedness.platform.alert.utils.AppUtils
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.FirebaseHelper
import org.alertpreparedness.platform.alert.utils.PreferHelper

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

    fun getCountryAdmin(): Flowable<List<String>> {
        val admins = AppUtils.getDatabase().getReference(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS)).child("directorCountry")

        return RxFirebaseDatabase.observeValueEvent(admins)
                .map { snap ->
                    snap.children.map { it.value as String }
                }
    }

    fun getUserDetail(userId: String): Flowable<ModelUserPublic> {
        val userDetail = FirebaseHelper.getUserDetail(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), userId)
        return RxFirebaseDatabase.observeValueEvent(userDetail, ModelUserPublic::class.java)
                .map({ model: ModelUserPublic ->
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