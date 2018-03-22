package org.alertpreparedness.platform.alert.risk_monitoring.view_model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import org.alertpreparedness.platform.alert.HAZARD_EMPTY
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelCountry
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelHazard
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelLog
import org.alertpreparedness.platform.alert.risk_monitoring.service.CountryService
import org.alertpreparedness.platform.alert.risk_monitoring.service.NetworkService
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService
import org.alertpreparedness.platform.alert.risk_monitoring.service.StaffService
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.PreferHelper
import org.joda.time.DateTime
import timber.log.Timber

/**
 * Created by fei on 14/11/2017.
 */
class ActiveRiskViewModel : AndroidViewModel, FirebaseAuth.AuthStateListener {

    constructor(application: Application) : super(application)

    private val mDisposables: CompositeDisposable = CompositeDisposable()
    private var mIndicatorMap = mutableMapOf<String, List<ModelIndicator>>()
    private var mIndicatorMapNetwork = mutableMapOf<String, List<ModelIndicator>>()
    private var mIndicatorMapNetworkLocal = mutableMapOf<String, List<ModelIndicator>>()
    private var mHazardNameMap = mutableMapOf<String, String>()
    private var mHazardNameMapNetwork = mutableMapOf<String, String>()
    private var mHazardNameMapNetworkLocal = mutableMapOf<String, String>()
    private var mGroups = mutableListOf<ExpandableGroup<ModelIndicator>>()
    private var mLiveData: MutableLiveData<MutableList<ExpandableGroup<ModelIndicator>>> = MutableLiveData()
    private val mCountryModelLive: MutableLiveData<ModelCountry> = MutableLiveData()
    private val mIndicatorModelLive: MutableLiveData<ModelIndicator> = MutableLiveData()
    private val mLogsLive: MutableLiveData<List<ModelLog>> = MutableLiveData()
    private val mNetworkMapLive: MutableLiveData<Map<String, String>> = MutableLiveData()
    private val mOtherHazardNameLive:MutableLiveData<String> = MutableLiveData()

    private val mAgencyId = PreferHelper.getString(getApplication(), Constants.AGENCY_ID)
    private val mCountryId = PreferHelper.getString(getApplication(), Constants.COUNTRY_ID)


    init {
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    fun getLiveGroups(isActive: Boolean): LiveData<MutableList<ExpandableGroup<ModelIndicator>>> {
        loadGroups(isActive)
        return mLiveData
    }

    fun getLiveCountryModel(): MutableLiveData<ModelCountry> {
        mDisposables.add(CountryService(getApplication()).getCountryModel(mAgencyId, mCountryId)
                .subscribe({ country ->
                    mCountryModelLive.value = country
                }, { error ->
                    Timber.d(error.message)
                })
        )
        return mCountryModelLive
    }

    fun getLiveIndicatorModel(hazardId: String, indicatorId: String): MutableLiveData<ModelIndicator> {
        mDisposables.add(
                RiskMonitoringService(getApplication()).getIndicatorModel(hazardId, indicatorId)
                        .subscribe({ indicator ->
                            mIndicatorModelLive.value = indicator
                        }, { error ->
                            Timber.d(error.message)
                        })
        )
        return mIndicatorModelLive
    }

    fun getLiveOtherHazardName(id:String): MutableLiveData<String> {
        mDisposables.add(
                RiskMonitoringService(getApplication()).getHazardOtherNameString(id)
                        .subscribe({name ->
                            mOtherHazardNameLive.value = name
                        })
        )
        return mOtherHazardNameLive
    }

    fun getLiveIndicatorLogs(indicatorId: String): MutableLiveData<List<ModelLog>> {
        mDisposables.add(
                RiskMonitoringService(getApplication()).getLogs(indicatorId)
                        .subscribe({ logs ->

                            logs.forEach { model ->
                                mDisposables.add(
                                        StaffService(getApplication()).getUserDetail(model.addedBy)
                                                .subscribe({ user ->
                                                    model.addedByName = String.format("%s %s", user.firstName, user.lastName)
                                                    mLogsLive.value = logs
                                                })
                                )
                            }

                            mLogsLive.value = logs
                        }, { error ->
                            Timber.d(error.message)
                        })
        )
        return mLogsLive
    }

    fun updateIndicatorLevel(hazardId: String, indicatorId: String, indicator: ModelIndicator, selection: Int) {
        var dueTime: Long
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
        val updateMap = mutableMapOf("dueDate" to dueTime, "triggerSelected" to selection, "updatedAt" to DateTime().millis)
        mDisposables.add(RiskMonitoringService(getApplication()).updateIndicator(hazardId, indicatorId, updateMap).subscribe())
    }

    fun getLiveNetworkMap(): MutableLiveData<Map<String, String>> {
        mDisposables.add(
                NetworkService(getApplication()).mapNetworksForCountry(mAgencyId, mCountryId)
                        .subscribe({ map ->
                            mNetworkMapLive.value = map
                        }, { error ->
                            Timber.d(error.message)
                        })
        )
        return mNetworkMapLive
    }

    private fun loadGroups(isActive: Boolean) {

        //get other names
        mDisposables.add(RiskMonitoringService(getApplication()).getHazards(mCountryId)
                .map { it.filter { it.hazardScenario == -1 } }
                .flatMap { Flowable.fromIterable(it) }
                .flatMap { RiskMonitoringService(getApplication()).getHazardOtherName(it) }
                .subscribe({ pair ->
                    mHazardNameMap.put(pair.first, pair.second)
                }, { error ->
                    Timber.d(error.message)
                })
        )

        //country context
        if (isActive) {
            val disposableCountryContext = RiskMonitoringService(getApplication()).getIndicators(mCountryId)
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
                            val toDeleteList = existItems.filter { it.networkId == null }.filter { !indicators.map { it.id }.contains(it.id) }
                            totalItems = totalItems.filter { !toDeleteList.map { it.id }.contains(it.id) }

                            mGroups.removeAt(groupIndex)
                            mGroups.add(0, ExpandableGroup("Country Context", totalItems))
                        } else {
                            mGroups.add(0, group)
                        }
                        mLiveData.value = mGroups

                    }, { error ->
                        Timber.d(error.message)
                    })
            mDisposables.add(disposableCountryContext)
        }


        //normal country hazard and indicators
        val disposableHazard = RiskMonitoringService(getApplication()).getHazards(mCountryId)
                .subscribe({ hazards: List<ModelHazard>? ->
                    Timber.d("***********************")
                    Timber.d(hazards.toString())
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
                                val disposableIndicator = RiskMonitoringService(getApplication()).getIndicators(it.id!!)
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
                                                else if(it.isActive == isActive && group.items.isEmpty()) {
                                                    val v = ModelIndicator(modelType = HAZARD_EMPTY)
                                                    v.modelType = HAZARD_EMPTY
                                                    group.items.add(v)
                                                    mGroups.add(group)
                                                }
                                            }
                                            mLiveData.value = mGroups
                                        }, { error ->
                                            Timber.d(error.message)
                                        })
                                mDisposables.add(disposableIndicator)
                            }
                        }
                    }
                }, { error ->
                    Timber.d(error.message)
                })
        mDisposables.add(disposableHazard)

        //network hazard and indicators
        val disposableNetwork = NetworkService(getApplication()).mapNetworksForCountry(mAgencyId, mCountryId)
                .subscribe({ networkMap ->
                    networkMap.forEach { (networkId, networkCountryId) ->

                        //get other names for network hazards
                        mDisposables.add(RiskMonitoringService(getApplication()).getHazards(networkCountryId)
                                .map { it.filter { it.hazardScenario == -1 } }
                                .flatMap { Flowable.fromIterable(it) }
                                .flatMap { RiskMonitoringService(getApplication()).getHazardOtherName(it) }
                                .subscribe({ pair ->
                                    mHazardNameMapNetwork.put(pair.first, pair.second)
                                })
                        )

                        mDisposables.add(NetworkService(getApplication()).getNetworkDetail(networkId)
                                .subscribe({ network ->

                                    mDisposables.add(RiskMonitoringService(getApplication()).getHazards(networkCountryId)
                                            .subscribe({ hazards: List<ModelHazard>? ->
                                                hazards?.forEach {
                                                    //get network indicators for hazard
                                                    if (it.id != null) {
                                                        if (it.id != networkCountryId && it.hazardScenario > -2) {
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

                                                            mDisposables.add(RiskMonitoringService(getApplication()).getIndicatorsForAssignee(it.id!!, network)
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
                                                        mDisposables.add(RiskMonitoringService(getApplication()).getIndicatorsForAssignee(networkCountryId, network)
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
                }, { error ->
                    Timber.d(error.message)
                })
        mDisposables.add(disposableNetwork)

        //local network hazard and indicators
        mDisposables.add(
                NetworkService(getApplication()).listLocalNetworksForCountry(mAgencyId, mCountryId)
                        .subscribe({ localNetworkList ->
                            Timber.d("local networks: %s", localNetworkList.size)
                            localNetworkList.forEach { localNetworkId ->
                                Timber.d("local network id: %s", localNetworkId)

                                //get other names for local network hazards
                                mDisposables.add(RiskMonitoringService(getApplication()).getHazards(localNetworkId)
                                        .map { it.filter { it.hazardScenario == -1 } }
                                        .flatMap { Flowable.fromIterable(it) }
                                        .flatMap { RiskMonitoringService(getApplication()).getHazardOtherName(it) }
                                        .subscribe({ pair ->
                                            mHazardNameMapNetworkLocal.put(pair.first, pair.second)
                                        }, { error ->
                                            Timber.d(error.message)
                                        })
                                )

                                mDisposables.add(NetworkService(getApplication()).getNetworkDetail(localNetworkId)
                                        .subscribe({ network ->
                                            Timber.d(network.toString())

                                            mDisposables.add(RiskMonitoringService(getApplication()).getHazards(localNetworkId)
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

                                                                    println("mHazardNameMapNetworkLocal1 = ${it.id!!}")

                                                                    mDisposables.add(RiskMonitoringService(getApplication()).getIndicatorsForLocalNetwork(it.id!!, network, mHazardNameMapNetworkLocal[it.id!!])
                                                                            .subscribe({ indicators ->
                                                                                mIndicatorMapNetworkLocal.put(it.id!!, indicators)
                                                                                val group = ExpandableGroup(mHazardNameMapNetworkLocal[it.id!!], indicators)
                                                                                val groupIndex = getGroupIndex(group.title, mGroups)
                                                                                println("mHazardNameMapNetworkLocal2 = ${it.id!!}")

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
                                                                                }
                                                                                else if (it.isActive == isActive && group.items.isNotEmpty()) {
                                                                                    mGroups.add(group)
                                                                                }
                                                                                mLiveData.value = mGroups
                                                                            }, { error ->
                                                                                Timber.d(error.message)
                                                                            }))

                                                                }
                                                            }

                                                            //get network country context indicators
                                                            if (isActive) {
                                                                mDisposables.add(RiskMonitoringService(getApplication()).getIndicatorsForAssignee(localNetworkId, network)
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
                                                                        }, { error ->
                                                                            Timber.d(error.message)
                                                                        }))
                                                            }
                                                        }
                                                    })
                                            )

                                        }, { error ->
                                            Timber.d(error.message)
                                        })
                                )
                            }
                        }, { error ->
                            Timber.d(error.message)
                        })
        )
    }

    fun addLogToIndicator(log: ModelLog, indicatorId: String) {
        RiskMonitoringService(getApplication()).addLogToIndicator(log, indicatorId)
    }

    private fun removeListFromList(existItems: List<ModelIndicator>, indicators: List<ModelIndicator>): MutableList<ModelIndicator> {
        val existIds = existItems.map { it.id }
        Timber.d("exist: %s", existIds)
        val filteredList = existItems.map { it.id }.filter { !indicators.map { it.id }.contains(it) }.map { existIds.indexOf(it) }.map { existItems[it] }
        return filteredList.toMutableList()
    }

    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
        mDisposables.clear()
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        if (auth.currentUser == null) {
            mDisposables.clear()
        }
    }

    private fun getGroupIndex(id: String, list: List<ExpandableGroup<ModelIndicator>>): Int = list.map { it.title }.indexOf(id)

}