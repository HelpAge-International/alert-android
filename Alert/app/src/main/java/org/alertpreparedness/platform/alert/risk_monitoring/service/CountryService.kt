package org.alertpreparedness.platform.alert.risk_monitoring.service

import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.PreferHelper

/**
 * Created by fei on 21/11/2017.
 */
object CountryService {

    private val mAppStatus = PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS)!!

    fun getCountryModel() {

    }

}