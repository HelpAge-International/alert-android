package org.alertpreparedness.platform.v1.risk_monitoring.service

import com.google.firebase.database.DataSnapshot
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import org.alertpreparedness.platform.BuildConfig
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelNetwork
import org.alertpreparedness.platform.v1.utils.FirebaseHelper
import org.json.JSONObject

/**
 * Created by fei on 15/11/2017.
 */
class NetworkService {

    fun mapNetworksForCountry(agencyId: String, countryId: String) : Flowable<MutableMap<String, String>> {
        val networkMapRef = FirebaseHelper.getNetworkMapRef(BuildConfig.ROOT_NODE, agencyId, countryId)
        return RxFirebaseDatabase.observeValueEvent(networkMapRef) { snap ->
            val networkMap = mutableMapOf<String, String>()
            snap.children.forEach {
                networkMap[it.key!!] = JSONObject(it.value.toString()).get("networkCountryId").toString()
            }
            return@observeValueEvent networkMap
        }
    }

    fun listLocalNetworksForCountry(agencyId: String, countryId: String): Flowable<List<String>> {
        val localNetworkRef = FirebaseHelper.getLocalNetworkRef(BuildConfig.ROOT_NODE, agencyId, countryId)
        return RxFirebaseDatabase.observeValueEvent(localNetworkRef) {snap ->
            snap.children.map { it.key!! }
        }
    }

    fun getNetworkDetail(networkId:String) : Flowable<ModelNetwork> {
        val networkDetailRef = FirebaseHelper.getNetworkDetail(BuildConfig.ROOT_NODE, networkId)
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