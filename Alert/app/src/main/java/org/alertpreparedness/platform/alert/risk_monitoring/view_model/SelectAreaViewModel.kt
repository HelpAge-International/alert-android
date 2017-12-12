package org.alertpreparedness.platform.alert.risk_monitoring.view_model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelCountry
import org.alertpreparedness.platform.alert.risk_monitoring.service.CountryService
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.PreferHelper
import org.json.JSONObject
import timber.log.Timber

/**
 * Created by fei on 22/11/2017.
 */
class SelectAreaViewModel : ViewModel(), FirebaseAuth.AuthStateListener {

    private val mDisposables = CompositeDisposable()
    private val mSelectedCountryLive: MutableLiveData<ModelCountry> = MutableLiveData()
    private val mCountryJsonDataLive: MutableLiveData<List<CountryJsonData>> = MutableLiveData()
    private val mCountryDataList: ArrayList<CountryJsonData> = arrayListOf()
    private val mCountryId = PreferHelper.getString(AlertApplication.getContext(), Constants.COUNTRY_ID)
    private val mAgencyId = PreferHelper.getString(AlertApplication.getContext(), Constants.AGENCY_ID)

    init {
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    fun getSelectedCountryLive(): MutableLiveData<ModelCountry> {
        getSelectedCountry()
        return mSelectedCountryLive
    }

    fun getCountryJsonDataLive(): MutableLiveData<List<CountryJsonData>> {
        getCountryJsonData()
        return mCountryJsonDataLive
    }


    private fun getCountryJsonData() {
        mDisposables.add(
                RiskMonitoringService.readJsonFile()
                        .map { fileText ->
                            return@map JSONObject(fileText)
                        }
                        .flatMap { jsonObject: JSONObject ->
                            RiskMonitoringService.mapJasonToCountryData(jsonObject, Gson())
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ countryData: CountryJsonData ->
                            Timber.d("Country id is: %s, level 1: %s", countryData.countryId, countryData.levelOneValues?.size)
                            mCountryDataList.add(countryData)
                            mCountryJsonDataLive.value = mCountryDataList
                        })
        )
    }

    private fun getSelectedCountry() {
        mDisposables.add(CountryService.getCountryModel(mAgencyId, mCountryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ country: ModelCountry ->
                    mSelectedCountryLive.value = country
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        mDisposables.clear()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        if (auth.currentUser == null) {
            mDisposables.clear()
        }
    }

}