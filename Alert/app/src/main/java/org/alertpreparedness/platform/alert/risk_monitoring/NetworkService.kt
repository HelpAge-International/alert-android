package org.alertpreparedness.platform.alert.risk_monitoring

import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.FirebaseHelper
import org.alertpreparedness.platform.alert.utils.PreferHelper
import org.json.JSONObject

/**
 * Created by fei on 15/11/2017.
 */
object NetworkService {

    fun mapNetworksForCountry(agencyId: String, countryId: String) : Flowable<MutableMap<String, String>> {
        val networkMapRef = FirebaseHelper.getNetworkMapRef(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), agencyId, countryId)
        return RxFirebaseDatabase.observeValueEvent(networkMapRef, { snap ->
            val networkMap = mutableMapOf<String, String>()
            snap.children.forEach {
                networkMap[it.key] = JSONObject(it.value.toString()).get("networkCountryId").toString()
            }
            return@observeValueEvent networkMap
        })
    }

}