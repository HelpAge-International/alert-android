package org.alertpreparedness.platform.alert.risk_monitoring.view_model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.location.Geocoder
import android.location.Location
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.toObservable
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
import timber.log.Timber
import java.util.*

/**
 * Created by fei on 16/11/2017.
 */
class AddIndicatorViewModel : ViewModel() {

    private val mDisposables: CompositeDisposable = CompositeDisposable()
    private val agencyId = UserInfo.getUser(AlertApplication.getContext()).agencyAdminID
    private val countryId = UserInfo.getUser(AlertApplication.getContext()).countryID
    private val mHazards: MutableLiveData<List<ModelHazard>> = MutableLiveData()
    private val mStaff: MutableLiveData<List<ModelUserPublic>> = MutableLiveData()
    private val mOtherNamesLive: MutableLiveData<Pair<String, String>> = MutableLiveData()
    private val mCountryJsonDtaLive: MutableLiveData<List<CountryJsonData>> = MutableLiveData()
    private val mCountryDataList: ArrayList<CountryJsonData> = arrayListOf()
    private val mAddressLive: MutableLiveData<String> = MutableLiveData()

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

    fun getAddressLive(location: Location): MutableLiveData<String> {
        getAddressByLocation(location)
        return mAddressLive
    }

    private fun getAddressByLocation(location: Location) {
        val geoCoder = Geocoder(AlertApplication.getContext(), Locale.getDefault())
        mDisposables.add(geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ addresses ->
                    Timber.d(addresses.toString())
                    Timber.d("address: %s", addresses.getAddressLine(0))
                    if (addresses == null || addresses.maxAddressLineIndex < 0) {
                        mAddressLive.value = ""
                    } else {
                        mAddressLive.value = addresses.getAddressLine(0)
                    }
                }, { error ->
                    Toasty.error(AlertApplication.getContext(), error.message.toString()).show()
                    mAddressLive.value = ""
                }))
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ user ->
                    users.add(user)
                    mStaff.value = users
                })
        )
    }

    private fun getHazardOtherName(hazard: ModelHazard) {
        mDisposables.add(RiskMonitoringService.getHazardOtherName(hazard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pair ->
                    mOtherNamesLive.value = pair
                })
        )
    }

    fun addIndicator(indicator: ModelIndicator, countryContext:Boolean) = RiskMonitoringService.addIndicatorToHazard(indicator, countryContext)

    override fun onCleared() {
        super.onCleared()
        mDisposables.clear()
    }

}