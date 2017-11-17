package org.alertpreparedness.platform.alert.risk_monitoring

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.helper.UserInfo

/**
 * Created by fei on 16/11/2017.
 */
class AddIndicatorViewModel : ViewModel() {

    private val mDisposables: CompositeDisposable = CompositeDisposable()
    private val agencyId = UserInfo.getUser(AlertApplication.getContext()).agencyAdminID
    private val countryId = UserInfo.getUser(AlertApplication.getContext()).countryID
    private val mHazards: MutableLiveData<List<ModelHazard>> = MutableLiveData()
    private val mStaff: MutableLiveData<List<ModelUserPublic>> = MutableLiveData()

    fun getHazardsLive() : MutableLiveData<List<ModelHazard>> {
        getHazards(countryId)
        return mHazards
    }

    fun getStaffLive() : MutableLiveData<List<ModelUserPublic>> {
        getStaff()
        return mStaff
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
                .flatMap({ids ->
                    return@flatMap Flowable.fromIterable(ids)
                })
                .flatMap({id ->
                    return@flatMap StaffService.getUserDetail(id)
                })
                .subscribe({user ->
                    users.add(user)
                    mStaff.value = users
                })
        )
    }

    fun addIndicator(indicator: ModelIndicator): Task<Void>? = RiskMonitoringService.addIndicatorToHazard(indicator)

    override fun onCleared() {
        super.onCleared()
        mDisposables.clear()
    }

}