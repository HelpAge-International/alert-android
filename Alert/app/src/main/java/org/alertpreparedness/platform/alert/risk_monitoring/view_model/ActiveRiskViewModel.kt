package org.alertpreparedness.platform.alert.risk_monitoring.view_model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.helper.UserInfo
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelCountry
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelHazard
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelLog
import org.alertpreparedness.platform.alert.risk_monitoring.service.CountryService
import org.alertpreparedness.platform.alert.risk_monitoring.service.NetworkService
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService
import org.alertpreparedness.platform.alert.utils.Constants
import org.joda.time.DateTime
import timber.log.Timber

/**
 * Created by fei on 14/11/2017.
 */
class ActiveRiskViewModel : ViewModel() {

    private val mDisposables: CompositeDisposable = CompositeDisposable()
    private var mIndicatorMap = mutableMapOf<String, List<ModelIndicator>>()
    private var mIndicatorMapNetwork = mutableMapOf<String, List<ModelIndicator>>()
    private var mIndicatorMapNetworkLocal = mutableMapOf<String, List<ModelIndicator>>()
    private var mHazardNameMap = mutableMapOf<String, String>()
    private var mHazardNameMapNetwork = mutableMapOf<String, String>()
    private var mHazardNameMapNetworkLocal = mutableMapOf<String, String>()
    private var mGroups = mutableListOf<ExpandableGroup<ModelIndicator>>()
    private var mLiveData: MutableLiveData<MutableList<ExpandableGroup<ModelIndicator>>> = MutableLiveData()
    private val mAgencyId = UserInfo.getUser(AlertApplication.getContext()).agencyAdminID
    private val mCountryId = UserInfo.getUser(AlertApplication.getContext()).countryID
    private val mCountryModelLive: MutableLiveData<ModelCountry> = MutableLiveData()
    private val mIndicatorModelLive: MutableLiveData<ModelIndicator> = MutableLiveData()
    private val mLogsLive:MutableLiveData<List<ModelLog>> = MutableLiveData()

    fun getLiveGroups(isActive: Boolean): LiveData<MutableList<ExpandableGroup<ModelIndicator>>> {
        loadGroups(isActive)
        return mLiveData
    }

    fun getLiveCountryModel(): MutableLiveData<ModelCountry> {
        mDisposables.add(CountryService.getCountryModel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ country ->
                    mCountryModelLive.value = country
                })
        )
        return mCountryModelLive
    }

    fun getLiveIndicatorModel(hazardId: String, indicatorId: String): MutableLiveData<ModelIndicator> {
        mDisposables.add(
                RiskMonitoringService.getIndicatorModel(hazardId, indicatorId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ indicator ->
                            mIndicatorModelLive.value = indicator
                        })
        )
        return mIndicatorModelLive
    }

    fun getLiveIndicatorLogs(indicatorId: String): MutableLiveData<List<ModelLog>> {
        mDisposables.add(
                RiskMonitoringService.getLogs(indicatorId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({logs ->
                            mLogsLive.value = logs
                        })
        )
        return mLogsLive
    }

    fun updateIndicatorLevel(hazardId: String, indicatorId: String, indicator: ModelIndicator, selection: Int) {
        var dueTime = indicator.dueDate
        val modelTrigger = indicator.trigger[selection]
        when (modelTrigger.durationType.toInt()) {
            Constants.HOUR -> {
                dueTime = DateTime().plusHours(modelTrigger.frequencyValue.toInt()).millis
            }
            Constants.DAY -> {
                dueTime = DateTime().plusDays(modelTrigger.frequencyValue.toInt()).millis
            }
            Constants.WEEK -> {
                dueTime = DateTime().plusWeeks(modelTrigger.frequencyValue.toInt()).millis
            }
            Constants.MONTH -> {
                dueTime = DateTime().plusMonths(modelTrigger.frequencyValue.toInt()).millis
            }
            Constants.YEAR -> {
                dueTime = DateTime().plusYears(modelTrigger.frequencyValue.toInt()).millis
            }
            else -> {
                throw IllegalArgumentException("Duration type is not valid!")
            }
        }
        val updateMap = mutableMapOf<String, Any>("dueDate" to dueTime, "triggerSelected" to selection, "updatedAt" to DateTime().millis)
        mDisposables.add(RiskMonitoringService.updateIndicatorLevel(hazardId, indicatorId, updateMap).subscribe())
    }

    private fun loadGroups(isActive: Boolean) {

        Timber.d("agency id: %s", mAgencyId)
        Timber.d("country id: %s", mCountryId)

        //get other names
        mDisposables.add(RiskMonitoringService.getHazards(mCountryId)
                .map { it.filter { it.hazardScenario == -1 } }
                .flatMap { Flowable.fromIterable(it) }
                .flatMap { RiskMonitoringService.getHazardOtherName(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pair ->
                    mHazardNameMap.put(pair.first, pair.second)
                })
        )

        //country context
        if (isActive) {
            val disposableCountryContext = RiskMonitoringService.getIndicators(mCountryId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ indicators ->
                        //                        Timber.d("country context indicators: %s", indicators.size)
                        val group = ExpandableGroup("Country Context", indicators)
                        val groupIndex = getGroupIndex(group.title, mGroups)
                        if (groupIndex != -1) {
                            val existItems = mGroups[groupIndex].items
                            var totalItems = existItems
                            indicators.forEach { i ->
                                if (existItems.map { it.id }.contains(i.id)) {
                                    val index = existItems.map { it.id }.indexOf(i.id)
                                    totalItems[index] = i
                                } else {
                                    totalItems.add(i)
                                }
                            }
                            val toDeleteList = existItems.filter { it.networkId == null }.filter { !indicators.map { it.id }.contains(it.id) }
                            totalItems = totalItems.filter { !toDeleteList.map { it.id }.contains(it.id) }

                            mGroups.removeAt(groupIndex)
                            mGroups.add(0, ExpandableGroup("Country Context", totalItems))
                        } else {
                            mGroups.add(0, group)
                        }
                        mLiveData.value = mGroups

                    })
            mDisposables.add(disposableCountryContext)
        }


        //normal country hazard and indicators
        val disposableHazard = RiskMonitoringService.getHazards(mCountryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ hazards: List<ModelHazard>? ->
                    hazards?.forEach {
                        if (it.id != null) {
                            if (it.id != mCountryId) {

                                when (it.hazardScenario) {
                                    -1 -> {
                                        if (!mHazardNameMap.containsKey(it.id!!)) {
                                            mHazardNameMap.put(it.id!!, "")
                                        }
                                    }
                                    else -> {
                                        mHazardNameMap.put(it.id!!, Constants.HAZARD_SCENARIO_NAME[it.hazardScenario])
                                    }
                                }

                                val disposableIndicator = RiskMonitoringService.getIndicators(it.id!!)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ indicators ->
                                            mIndicatorMap.put(it.id!!, indicators)
                                            val group = ExpandableGroup(mHazardNameMap[it.id!!], indicators)
                                            val groupIndex = getGroupIndex(group.title, mGroups)
                                            if (groupIndex != -1) {
                                                val existItems = mGroups[groupIndex].items
                                                var totalItems = existItems
                                                if (it.isActive == isActive) {

                                                    indicators.forEach { i ->
                                                        if (existItems.map { it.id }.contains(i.id)) {
                                                            val index = existItems.map { it.id }.indexOf(i.id)
                                                            totalItems[index] = i
                                                        } else {
                                                            totalItems.add(i)
                                                        }
                                                    }
                                                    val toDeleteList = existItems.filter { it.networkId == null }.filter { !indicators.map { it.id }.contains(it.id) }
                                                    totalItems = totalItems.filter { !toDeleteList.map { it.id }.contains(it.id) }
                                                } else {
                                                    totalItems = removeListFromList(existItems, indicators)
                                                }
                                                if (totalItems.isNotEmpty()) {
                                                    mGroups.removeAt(groupIndex)
                                                    mGroups.add(ExpandableGroup(mHazardNameMap[it.id!!], totalItems))
                                                } else {
                                                    mGroups.removeAt(groupIndex)
                                                }
                                            } else {
                                                if (it.isActive == isActive && group.items.isNotEmpty()) {
                                                    mGroups.add(group)
                                                }
                                            }
                                            mLiveData.value = mGroups
                                        })
                                mDisposables.add(disposableIndicator)
                            }
                        }
                    }
                })
        mDisposables.add(disposableHazard)

        //network hazard and indicators
        val disposableNetwork = NetworkService.mapNetworksForCountry(mAgencyId, mCountryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ networkMap ->
                    networkMap.forEach { (networkId, networkCountryId) ->
                        //                        Timber.d("networkId: %s, networkCountryId: %s", networkId, networkCountryId)

                        //get other names for network hazards
                        mDisposables.add(RiskMonitoringService.getHazards(networkCountryId)
                                .map { it.filter { it.hazardScenario == -1 } }
                                .flatMap { Flowable.fromIterable(it) }
                                .flatMap { RiskMonitoringService.getHazardOtherName(it) }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ pair ->
                                    mHazardNameMapNetwork.put(pair.first, pair.second)
                                })
                        )

                        mDisposables.add(NetworkService.getNetworkDetail(networkId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ network ->
                                    //                                    Timber.d(network.toString())

                                    mDisposables.add(RiskMonitoringService.getHazards(networkCountryId)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({ hazards: List<ModelHazard>? ->
                                                hazards?.forEach {
                                                    //get network indicators for hazard
                                                    if (it.id != null) {
                                                        if (it.id != networkCountryId) {
                                                            when (it.hazardScenario) {
                                                                -1 -> {
                                                                    if (!mHazardNameMapNetwork.containsKey(it.id!!)) {
                                                                        mHazardNameMapNetwork.put(it.id!!, "")
                                                                    }
                                                                }
                                                                else -> {
                                                                    mHazardNameMapNetwork.put(it.id!!, Constants.HAZARD_SCENARIO_NAME[it.hazardScenario])
                                                                }
                                                            }

                                                            mDisposables.add(RiskMonitoringService.getIndicatorsForAssignee(it.id!!, network)
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe({ indicators ->
                                                                        mIndicatorMapNetwork.put(it.id!!, indicators)
                                                                        val group = ExpandableGroup(mHazardNameMapNetwork[it.id!!], indicators)
                                                                        val groupIndex = getGroupIndex(group.title, mGroups)
                                                                        if (groupIndex != -1) {
                                                                            val existItems = mGroups[groupIndex].items
                                                                            var totalItems = existItems
                                                                            if (it.isActive == isActive) {
                                                                                indicators.forEach { i ->
                                                                                    if (existItems.map { it.id }.contains(i.id)) {
                                                                                        val index = existItems.map { it.id }.indexOf(i.id)
                                                                                        totalItems[index] = i
                                                                                    } else {
                                                                                        totalItems.add(i)
                                                                                    }
                                                                                }
                                                                                val toDeleteList = existItems.filter { it.networkId == networkId }.filter { !indicators.map { it.id }.contains(it.id) }
                                                                                totalItems = totalItems.filter { !toDeleteList.map { it.id }.contains(it.id) }
                                                                            } else {
                                                                                totalItems = removeListFromList(existItems, indicators)
                                                                            }
                                                                            if (totalItems?.isNotEmpty() == true) {
                                                                                mGroups.removeAt(groupIndex)
                                                                                mGroups.add(ExpandableGroup(mHazardNameMapNetwork[it.id!!], totalItems))
                                                                            } else {
                                                                                mGroups.removeAt(groupIndex)
                                                                            }
                                                                        } else {
                                                                            if (it.isActive == isActive && group.items.isNotEmpty()) {
                                                                                mGroups.add(group)
                                                                            }
                                                                        }
                                                                        mLiveData.value = mGroups
                                                                    }))
                                                        }
                                                    }

                                                    //get network country context indicators
                                                    if (isActive) {
                                                        mDisposables.add(RiskMonitoringService.getIndicatorsForAssignee(networkCountryId, network)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe({ indicators ->
                                                                    val group = ExpandableGroup("Country Context", indicators)
                                                                    val groupIndex = getGroupIndex(group.title, mGroups)
                                                                    if (groupIndex != -1) {
                                                                        val existItems = mGroups[groupIndex].items
                                                                        var totalItems = existItems
                                                                        indicators.forEach { i ->
                                                                            if (existItems.map { it.id }.contains(i.id)) {
                                                                                val index = existItems.map { it.id }.indexOf(i.id)
                                                                                totalItems[index] = i
                                                                            } else {
                                                                                totalItems.add(i)
                                                                            }
                                                                        }
                                                                        val toDeleteList = existItems.filter { it.networkId == networkId }.filter { !indicators.map { it.id }.contains(it.id) }
                                                                        totalItems = totalItems.filter { !toDeleteList.map { it.id }.contains(it.id) }
                                                                        if (totalItems.isNotEmpty()) {
                                                                            mGroups.removeAt(groupIndex)
                                                                            mGroups.add(0, ExpandableGroup("Country Context", totalItems))
                                                                        }
                                                                    } else {
                                                                        if (group.items.isNotEmpty()) {
                                                                            mGroups.add(0, group)
                                                                        }
                                                                    }
                                                                    mLiveData.value = mGroups
                                                                }))
                                                    }
                                                }
                                            }))

                                })
                        )
                    }
                })
        mDisposables.add(disposableNetwork)

        //local network hazard and indicators
        mDisposables.add(
                NetworkService.listLocalNetworksForCountry(mAgencyId, mCountryId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ localNetworkList ->
                            Timber.d("local networks: %s", localNetworkList.size)
                            localNetworkList.forEach { localNetworkId ->
                                Timber.d("local network id: %s", localNetworkId)

                                //get other names for local network hazards
                                mDisposables.add(RiskMonitoringService.getHazards(localNetworkId)
                                        .map { it.filter { it.hazardScenario == -1 } }
                                        .flatMap { Flowable.fromIterable(it) }
                                        .flatMap { RiskMonitoringService.getHazardOtherName(it) }
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ pair ->
                                            mHazardNameMapNetworkLocal.put(pair.first, pair.second)
                                        })
                                )

                                mDisposables.add(NetworkService.getNetworkDetail(localNetworkId)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ network ->
                                            Timber.d(network.toString())

                                            mDisposables.add(RiskMonitoringService.getHazards(localNetworkId)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({ hazards: List<ModelHazard>? ->
                                                        Timber.d("local network hazards: %s", hazards?.size ?: 0)
                                                        hazards?.forEach {
                                                            //get network indicators for hazard
                                                            if (it.id != null) {
                                                                if (it.id != localNetworkId) {
                                                                    when (it.hazardScenario) {
                                                                        -1 -> {
                                                                            if (!mHazardNameMapNetworkLocal.containsKey(it.id!!)) {
                                                                                mHazardNameMapNetworkLocal.put(it.id!!, "")
                                                                            }
                                                                        }
                                                                        else -> {
                                                                            mHazardNameMapNetworkLocal.put(it.id!!, Constants.HAZARD_SCENARIO_NAME[it.hazardScenario])
                                                                        }
                                                                    }

                                                                    mDisposables.add(RiskMonitoringService.getIndicatorsForAssignee(it.id!!, network)
                                                                            .subscribeOn(Schedulers.io())
                                                                            .observeOn(AndroidSchedulers.mainThread())
                                                                            .subscribe({ indicators ->
                                                                                Timber.d("local network indicator size: %s", indicators.size)
                                                                                mIndicatorMapNetworkLocal.put(it.id!!, indicators)
                                                                                val group = ExpandableGroup(mHazardNameMapNetworkLocal[it.id!!], indicators)
                                                                                val groupIndex = getGroupIndex(group.title, mGroups)
                                                                                if (groupIndex != -1) {
                                                                                    val existItems = mGroups[groupIndex].items
                                                                                    var totalItems = existItems
                                                                                    if (it.isActive == isActive) {
                                                                                        indicators.forEach { i ->
                                                                                            if (existItems.map { it.id }.contains(i.id)) {
                                                                                                val index = existItems.map { it.id }.indexOf(i.id)
                                                                                                totalItems[index] = i
                                                                                            } else {
                                                                                                totalItems.add(i)
                                                                                            }
                                                                                        }
                                                                                        val toDeleteList = existItems.filter { it.networkId == localNetworkId }.filter { !indicators.map { it.id }.contains(it.id) }
                                                                                        totalItems = totalItems.filter { !toDeleteList.map { it.id }.contains(it.id) }
                                                                                    } else {
                                                                                        totalItems = removeListFromList(existItems, indicators)
                                                                                    }
                                                                                    if (totalItems?.isNotEmpty() == true) {
                                                                                        mGroups.removeAt(groupIndex)
                                                                                        mGroups.add(ExpandableGroup(mHazardNameMapNetworkLocal[it.id!!], totalItems))
                                                                                    } else {
                                                                                        mGroups.removeAt(groupIndex)
                                                                                    }
                                                                                } else {
                                                                                    if (it.isActive == isActive && group.items.isNotEmpty()) {
                                                                                        mGroups.add(group)
                                                                                    }
                                                                                }
                                                                                mLiveData.value = mGroups
                                                                            }))
                                                                }
                                                            }

                                                            //get network country context indicators
                                                            if (isActive) {
                                                                mDisposables.add(RiskMonitoringService.getIndicatorsForAssignee(localNetworkId, network)
                                                                        .subscribeOn(Schedulers.io())
                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                        .subscribe({ indicators ->
                                                                            Timber.d("local netowrk country context: %s", indicators.size)
                                                                            val group = ExpandableGroup("Country Context", indicators)
                                                                            val groupIndex = getGroupIndex(group.title, mGroups)
                                                                            if (groupIndex != -1) {
                                                                                val existItems = mGroups[groupIndex].items
                                                                                var totalItems = existItems
                                                                                indicators.forEach { i ->
                                                                                    if (existItems.map { it.id }.contains(i.id)) {
                                                                                        val index = existItems.map { it.id }.indexOf(i.id)
                                                                                        totalItems[index] = i
                                                                                    } else {
                                                                                        totalItems.add(i)
                                                                                    }
                                                                                }
                                                                                val toDeleteList = existItems.filter { it.networkId == localNetworkId }.filter { !indicators.map { it.id }.contains(it.id) }
                                                                                totalItems = totalItems.filter { !toDeleteList.map { it.id }.contains(it.id) }
                                                                                if (totalItems.isNotEmpty()) {
                                                                                    mGroups.removeAt(groupIndex)
                                                                                    mGroups.add(0, ExpandableGroup("Country Context", totalItems))
                                                                                }
                                                                            } else {
                                                                                if (group.items.isNotEmpty()) {
                                                                                    mGroups.add(0, group)
                                                                                }
                                                                            }
                                                                            mLiveData.value = mGroups
                                                                        }))
                                                            }
                                                        }
                                                    })
                                            )

                                        })
                                )
                            }
                        })
        )
    }

    private fun removeListFromList(existItems: List<ModelIndicator>, indicators: List<ModelIndicator>): MutableList<ModelIndicator> {
        val existIds = existItems.map { it.id }
        Timber.d("exist: %s", existIds)
        val filteredList = existItems.map { it.id }.filter { !indicators.map { it.id }.contains(it) }.map { existIds.indexOf(it) }.map { existItems[it] }
        return filteredList.toMutableList()
    }

    override fun onCleared() {
        super.onCleared()
        mDisposables.clear()
    }

    private fun getGroupIndex(id: String, list: List<ExpandableGroup<ModelIndicator>>): Int = list.map { it.title }.indexOf(id)

}