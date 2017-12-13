package org.alertpreparedness.platform.alert.risk_monitoring.service

import com.google.firebase.database.DataSnapshot
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelNetwork
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.FirebaseHelper
import org.alertpreparedness.platform.alert.utils.PreferHelper
import org.json.JSONObject

/**
 * Created by fei on 15/11/2017.
 */
object NetworkService {

    private val mAppStatus = PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS)!!

    fun mapNetworksForCountry(agencyId: String, countryId: String) : Flowable<MutableMap<String, String>> {
        val networkMapRef = FirebaseHelper.getNetworkMapRef(mAppStatus, agencyId, countryId)
        return RxFirebaseDatabase.observeValueEvent(networkMapRef, { snap ->
            val networkMap = mutableMapOf<String, String>()
            snap.children.forEach {
                networkMap[it.key] = JSONObject(it.value.toString()).get("networkCountryId").toString()
            }
            return@observeValueEvent networkMap
        })
    }

    fun listLocalNetworksForCountry(agencyId: String, countryId: String): Flowable<List<String>> {
        val localNetworkRef = FirebaseHelper.getLocalNetworkRef(mAppStatus, agencyId, countryId)
        return RxFirebaseDatabase.observeValueEvent(localNetworkRef, {snap ->
            snap.children.map { it.key }
        })
    }

    fun getNetworkDetail(networkId:String) : Flowable<ModelNetwork> {
        val networkDetailRef = FirebaseHelper.getNetworkDetail(mAppStatus, networkId)
        return RxFirebaseDatabase.observeValueEvent(networkDetailRef, ModelNetwork::class.java)
                .map {network ->
                    return@map network.copy(id = networkId)
                }
    }

    fun checkConnectionState(): Flowable<Boolean> {
        val checkConnectedRef = FirebaseHelper.checkConnectedRef()
        return RxFirebaseDatabase.observeValueEvent(checkConnectedRef)
                .map { snap: DataSnapshot ->
                    snap.value as Boolean
                }
    }

}