package org.alertpreparedness.platform.alert.risk_monitoring.view_model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.helper.UserInfo
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelHazard
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelUserPublic
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService
import org.alertpreparedness.platform.alert.risk_monitoring.service.StaffService
import org.json.JSONObject

/**
 * Created by fei on 16/11/2017.
 */
class AddIndicatorViewModel : ViewModel() {

    private val mDisposables: CompositeDisposable = CompositeDisposable()
    private val agencyId = UserInfo.getUser(AlertApplication.getContext()).agencyAdminID
    private val countryId = UserInfo.getUser(AlertApplication.getContext()).countryID
    private val mHazards: MutableLiveData<List<ModelHazard>> = MutableLiveData()
    private val mStaff: MutableLiveData<List<ModelUserPublic>> = MutableLiveData()
    private val mOtherNamesLive:MutableLiveData<Pair<String, String>> = MutableLiveData()
    private val mCountryJsonDtaLive:MutableLiveData<List<CountryJsonData>> = MutableLiveData()
    private val mCountryDataList: ArrayList<CountryJsonData> = arrayListOf()

    fun getHazardsLive(): MutableLiveData<List<ModelHazard>> {
        getHazards(countryId)
        return mHazards
    }

    fun getStaffLive(): MutableLiveData<List<ModelUserPublic>> {
        getStaff()
        return mStaff
    }

    fun getHazardOtherNameMapLive(hazard: ModelHazard): MutableLiveData<Pair<String, String>> {
        getHazardOtherName(hazard)
        return mOtherNamesLive
    }

    fun getCountryJsonDataLive(): MutableLiveData<List<CountryJsonData>> {
        getCountryJson()
        return mCountryJsonDtaLive
    }

    private fun getCountryJson() {
        mDisposables.add(
                RiskMonitoringService.readJsonFile()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ fileText ->
                            val jsonObject = JSONObject(fileText)
                            mDisposables.add(
                                    RiskMonitoringService.mapJasonToCountryData(jsonObject, Gson())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({ countryData: CountryJsonData ->
                                                mCountryDataList.add(countryData)
                                                mCountryJsonDtaLive.value = mCountryDataList
                                            })
                            )

                        })
        )
    }

    private fun getHazards(countryId: String) {
        mDisposables.add(RiskMonitoringService.getHazards(countryId)
                .map { it.filter { it.isActive } }
                .subscribe({ hazards ->
                    mHazards.value = hazards
                })
        )
    }

    private fun getStaff() {
        val users = mutableListOf<ModelUserPublic>()
        mDisposables.add(StaffService.getCountryStaff(countryId)
                .doOnSubscribe { users.clear() }
                .flatMap({ ids ->
                    return@flatMap Flowable.fromIterable(ids)
                })
                .flatMap({ id ->
                    return@flatMap StaffService.getUserDetail(id)
                })
                .subscribe({ user ->
                    users.add(user)
                    mStaff.value = users
                })
        )
    }

    private fun getHazardOtherName(hazard: ModelHazard) {
        mDisposables.add(RiskMonitoringService.getHazardOtherName(hazard)
                .subscribe({ pair ->
                    mOtherNamesLive.value = pair
                })
        )
    }

    fun addIndicator(indicator: ModelIndicator): Task<Void>? = RiskMonitoringService.addIndicatorToHazard(indicator)

    override fun onCleared() {
        super.onCleared()
        mDisposables.clear()
    }

}