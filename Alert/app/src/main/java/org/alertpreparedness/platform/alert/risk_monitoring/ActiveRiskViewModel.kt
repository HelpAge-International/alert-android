package org.alertpreparedness.platform.alert.risk_monitoring

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.helper.UserInfo
import org.alertpreparedness.platform.alert.utils.Constants
import timber.log.Timber

/**
 * Created by fei on 14/11/2017.
 */
class ActiveRiskViewModel : ViewModel() {

    private val mDisposables: CompositeDisposable = CompositeDisposable()
    private var mIndicatorMap = mutableMapOf<String, List<ModelIndicator>>()
    private var mIndicatorMapNetwork = mutableMapOf<String, List<ModelIndicator>>()
    private var mHazardNameMap = mutableMapOf<String, String>()
    private var mHazardNameMapNetwork = mutableMapOf<String, String>()
    private var mGroups = mutableListOf<ExpandableGroup<ModelIndicator>>()
    private var mLiveData: MutableLiveData<MutableList<ExpandableGroup<ModelIndicator>>> = MutableLiveData()

    fun getLiveGroups(): LiveData<MutableList<ExpandableGroup<ModelIndicator>>> {
        loadGroups()
        return mLiveData
    }

    private fun loadGroups() {
        val agencyId = UserInfo.getUser(AlertApplication.getContext()).agencyAdminID
        val countryId = UserInfo.getUser(AlertApplication.getContext()).countryID
        Timber.d("agency id: %s", agencyId)
        Timber.d("country id: %s", countryId)
        val disposableCountryContext = RiskMonitoringService.getIndicators(countryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ indicators ->
                    val group = ExpandableGroup("Country Context", indicators)
                    val groupIndex = getGroupIndex(group.title, mGroups)
                    Timber.d("group index: %s", groupIndex)
                    if (groupIndex != -1) {
                        val existItems = mGroups[groupIndex].items
                        val totalItems = existItems.plus(indicators)
                        mGroups.removeAt(groupIndex)
                        mGroups.add(0, ExpandableGroup("Country Context", indicators))
                    } else {
                        mGroups.add(0, group)
                    }
                    mLiveData.value = mGroups

                })
        mDisposables.add(disposableCountryContext)

        val disposableHazard = RiskMonitoringService.getHazards(countryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ hazards: List<ModelHazard>? ->
                    hazards?.forEach {
                        if (it.id != countryId) {
                            mHazardNameMap.put(it.id, Constants.HAZARD_SCENARIO[it.hazardScenario])
                            val disposableIndicator = RiskMonitoringService.getIndicators(it.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ indicators ->
                                        mIndicatorMap.put(it.id, indicators)
                                        val group = ExpandableGroup(mHazardNameMap[it.id], indicators)
                                        val groupIndex = getGroupIndex(group.title, mGroups)
                                        Timber.d("group index: %s", groupIndex)
                                        if (groupIndex != -1) {
                                            val existItems = mGroups[groupIndex].items
                                            val totalItems = existItems.plus(indicators)
                                            mGroups.removeAt(groupIndex)
                                            mGroups.add(ExpandableGroup(mHazardNameMap[it.id], indicators))
                                        } else {
                                            mGroups.add(group)
                                        }
                                        mLiveData.value = mGroups
                                    })
                            mDisposables.add(disposableIndicator)
                        }
                    }
                })
        mDisposables.add(disposableHazard)

//        val disposableNetwork = NetworkService.mapNetworksForCountry(agencyId, countryId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ networkMap ->
//                    networkMap.forEach { (networkId, networkCountryId) ->
//                        Timber.d("networkId: %s, networkCountryId: %s", networkId, networkCountryId)
//                        mDisposables.add(RiskMonitoringService.getHazards(networkCountryId)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe({ hazards: List<ModelHazard>? ->
//                                    hazards?.forEach {
//                                        if (it.id != networkCountryId) {
//                                            mHazardNameMapNetwork.put(it.id, Constants.HAZARD_SCENARIO[it.hazardScenario])
//                                            mDisposables.add(RiskMonitoringService.getIndicators(it.id)
//                                                    .subscribeOn(Schedulers.io())
//                                                    .observeOn(AndroidSchedulers.mainThread())
//                                                    .subscribe({ indicators ->
//                                                        mIndicatorMapNetwork.put(it.id, indicators)
//                                                        val group = ExpandableGroup(mHazardNameMapNetwork[it.id], indicators)
//                                                        val groupIndex = getGroupIndex(group.title, mGroups)
//                                                        Timber.d("group index: %s", groupIndex)
//                                                        if (groupIndex != -1) {
//                                                            val existItems = mGroups[groupIndex].items
//                                                            val totalItems = mIndicatorMapNetwork[it.id]?.let { it1 -> existItems.plus(it1) }
//                                                            mGroups.removeAt(groupIndex)
//                                                            mGroups.add(ExpandableGroup(mHazardNameMap[it.id], totalItems))
//                                                        } else {
//                                                            mGroups.add(group)
//                                                        }
//                                                        mLiveData.value = mGroups
//                                                    }))
//                                        }
//                                    }
//                                }))
//                    }
//                })
//        mDisposables.add(disposableNetwork)

    }

    override fun onCleared() {
        super.onCleared()
        mDisposables.clear()
    }

    private fun getGroupIndex(id: String, list: List<ExpandableGroup<ModelIndicator>>): Int = list.map { it.title }.indexOf(id)

}