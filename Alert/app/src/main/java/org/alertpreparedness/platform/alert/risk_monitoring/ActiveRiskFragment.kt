package org.alertpreparedness.platform.alert.risk_monitoring


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_active_risk.*
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.helper.UserInfo
import org.alertpreparedness.platform.alert.utils.Constants
import org.jetbrains.anko.find
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
class ActiveRiskFragment : Fragment() {

    private val mDisposables: CompositeDisposable = CompositeDisposable()
    private var mIndicatorMap = mutableMapOf<String, List<ModelIndicator>>()
    private var mHazardNameMap = mutableMapOf<String, String>()
    private var mGroups = mutableListOf<ExpandableGroup<ModelIndicator>>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initData()

        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.fragment_active_risk, container, false)
        val rvRiskActive: RecyclerView? = view?.find(R.id.rvRiskActive)
        rvRiskActive?.layoutManager = LinearLayoutManager(AlertApplication.getContext())
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rvRiskActive?.addItemDecoration(decoration)
        return view
    }

    private fun initData() {
        val countryId = UserInfo.getUser(activity).countryID
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
                                        rvRiskActive.adapter = HazardAdapter(mGroups)
                                    })
                            mDisposables.add(disposableIndicator)
                        }
                    }
                })
        mDisposables.add(disposableHazard)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDisposables.clear()
    }

    private fun getGroupIndex(id: String, list: List<ExpandableGroup<ModelIndicator>>): Int = list.map { it.title }.indexOf(id)


}
