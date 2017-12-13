package org.alertpreparedness.platform.alert.risk_monitoring.service

import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelCountry
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.FirebaseHelper
import org.alertpreparedness.platform.alert.utils.PreferHelper

/**
 * Created by fei on 21/11/2017.
 */
object CountryService {

    private val mAppStatus = PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS)!!

    fun getCountryModel(agencyId:String, countryId:String): Flowable<ModelCountry> {
        val countryDetailRef = FirebaseHelper.getCountryDetail(mAppStatus, agencyId, countryId)
        return RxFirebaseDatabase.observeValueEvent(countryDetailRef, ModelCountry::class.java)
    }

}