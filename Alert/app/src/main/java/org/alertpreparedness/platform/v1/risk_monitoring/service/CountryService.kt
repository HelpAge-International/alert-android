package org.alertpreparedness.platform.v1.risk_monitoring.service

import android.content.Context
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelCountry
import org.alertpreparedness.platform.v1.utils.Constants
import org.alertpreparedness.platform.v1.utils.FirebaseHelper
import org.alertpreparedness.platform.v1.utils.PreferHelper

/**
 * Created by fei on 21/11/2017.
 */
class CountryService(private val context : Context) {

    private val mAppStatus = PreferHelper.getString(context, Constants.APP_STATUS)!!

    fun getCountryModel(agencyId:String, countryId:String): Flowable<ModelCountry> {
        val countryDetailRef = FirebaseHelper.getCountryDetail(mAppStatus, agencyId, countryId)
        return RxFirebaseDatabase.observeValueEvent(countryDetailRef, ModelCountry::class.java)
    }

}