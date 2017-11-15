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
class ActiveRiskViewModel: ViewModel() {

    var test:Int = 0
    private val mDisposables: CompositeDisposable = CompositeDisposable()
    private var mIndicatorMap = mutableMapOf<String, List<ModelIndicator>>()
    private var mHazardNameMap = mutableMapOf<String, String>()
    private var mGroups = mutableListOf<ExpandableGroup<ModelIndicator>>()
    private var mLiveData:MutableLiveData<MutableList<ExpandableGroup<ModelIndicator>>>? = null

    fun getLiveGroups() :  LiveData<MutableList<ExpandableGroup<ModelIndicator>>>  {
        if (mLiveData == null) {
            mLiveData = MutableLiveData()
        }
        loadGroups()
        return mLiveData as MutableLiveData<MutableList<ExpandableGroup<ModelIndicator>>>
    }

    fun loadGroups() {
        val countryId = UserInfo.getUser(AlertApplication.getContext()).countryID
        Timber.d("country id: %s", countryId)
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
                                            mGroups.removeAt(groupIndex)
                                        }
                                        mGroups.add(group)
                                        mLiveData?.value = mGroups
//                                        rvRiskActive.adapter = HazardAdapter(mGroups)
                                    })
                            mDisposables.add(disposableIndicator)
                        }
                    }
                })
        mDisposables.add(disposableHazard)
    }

    fun getGroups():List<ExpandableGroup<ModelIndicator>> {
        return mGroups
    }

    override fun onCleared() {
        super.onCleared()
        mDisposables.clear()
    }

    private fun getGroupIndex(id: String, list: List<ExpandableGroup<ModelIndicator>>): Int = list.map { it.title }.indexOf(id)

}