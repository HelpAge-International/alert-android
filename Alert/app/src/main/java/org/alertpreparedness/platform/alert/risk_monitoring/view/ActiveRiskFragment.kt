package org.alertpreparedness.platform.alert.risk_monitoring.view


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
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.HazardAdapter
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.OnIndicatorSelectedListener
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.BottomSheetDialog
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelCountry
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.ActiveRiskViewModel
import org.jetbrains.anko.find
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
class ActiveRiskFragment : Fragment(), OnIndicatorSelectedListener {

    private lateinit var mViewModel: ActiveRiskViewModel
    private var mCountryLocation = -1

    companion object {
        val HAZARD_ID = "hazard_id"
        val INDICATOR_ID = "indicator_id"
        val NETWORK_ID = "network_id"
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(ActiveRiskViewModel::class.java)
        mViewModel.getLiveCountryModel().observe(this, Observer<ModelCountry> { country ->
            country?.location?.let {
                mCountryLocation = it
            }
        })
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.fragment_active_risk, container, false)
        val rvRiskActive: RecyclerView? = view?.find(R.id.rvRiskActive)
        rvRiskActive?.layoutManager = LinearLayoutManager(AlertApplication.getContext())
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rvRiskActive?.addItemDecoration(decoration)
        mViewModel.getLiveGroups(true).observe(this, Observer<MutableList<ExpandableGroup<ModelIndicator>>> {
            val size = it?.size ?: 0
            if (size > 0) {
                pbLoading?.hide()
            }
            rvRiskActive?.adapter = HazardAdapter(it as List<ExpandableGroup<ModelIndicator>>, mCountryLocation, this)
        })
        return view
    }

    override fun selectedIndicator(hazardId: String, indicatorId: String) {
        Timber.d("ids: %s, %s", hazardId, indicatorId)
        val bsDialog = BottomSheetDialog()
        val bundle = Bundle()
        bundle.putString(HAZARD_ID, hazardId)
        bundle.putString(INDICATOR_ID, indicatorId)
        bsDialog.arguments = bundle
        bsDialog.show(fragmentManager, "bottom_sheet")
    }

}
