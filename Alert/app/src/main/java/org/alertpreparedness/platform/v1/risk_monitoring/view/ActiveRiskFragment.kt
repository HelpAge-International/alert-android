package org.alertpreparedness.platform.v1.risk_monitoring.view


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import kotlinx.android.synthetic.main.fragment_active_risk.*
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v1.risk_monitoring.adapter.HazardAdapter
import org.alertpreparedness.platform.v1.risk_monitoring.adapter.OnIndicatorSelectedListener
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.BottomSheetDialog
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelCountry
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.v1.risk_monitoring.view_model.ActiveRiskViewModel
import org.jetbrains.anko.find
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
class ActiveRiskFragment : Fragment(), OnIndicatorSelectedListener {

    private lateinit var mViewModel: ActiveRiskViewModel
    private var mCountryLocation = -1
    private var mNetworkCountryMap: Map<String, String>? = null

    companion object {
        val HAZARD_ID = "hazard_id"
        val INDICATOR_ID = "indicator_id"
        val NETWORK_ID = "network_id"
        val NETWORK_COUNTRY_ID = "network_country_id"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(ActiveRiskViewModel::class.java)
        val view = inflater?.inflate(R.layout.fragment_active_risk, container, false)
        val rvRiskActive: RecyclerView? = view?.find(R.id.rvRiskActive)

        mViewModel.getLiveCountryModel().observe(this, Observer<ModelCountry> { country ->
            country?.location?.let {
                mCountryLocation = it
            }

            mViewModel.getLiveNetworkMap().observe(this, Observer { map ->
                mNetworkCountryMap = map
            })
            // Inflate the layout for this fragment

            rvRiskActive?.layoutManager = LinearLayoutManager(context)
            val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            rvRiskActive?.addItemDecoration(decoration)
            mViewModel.getLiveGroups(true).observe(this, Observer<MutableList<ExpandableGroup<ModelIndicator>>> {
                val size = it?.size ?: 0
                if (size > 0) {
                    pbLoading?.hide()
                }

                rvRiskActive?.adapter = HazardAdapter(it as List<ExpandableGroup<ModelIndicator>>, mCountryLocation, this, mNetworkCountryMap, context!!)
            })
        })

        return view
    }

    override fun selectedIndicator(hazardId: String, indicatorId: String, networkId:String?, networkCountryId:String?) {
        Timber.d("ids: %s, %s", hazardId, indicatorId)
        val bsDialog = BottomSheetDialog()
        val bundle = Bundle()
        bundle.putString(HAZARD_ID, hazardId)
        bundle.putString(INDICATOR_ID, indicatorId)
        networkId?.apply {bundle.putString(NETWORK_ID, networkId)}
        networkCountryId?.apply { bundle.putString(NETWORK_COUNTRY_ID, networkCountryId)  }
        bsDialog.arguments = bundle
        bsDialog.show(fragmentManager, "bottom_sheet")
    }

}
