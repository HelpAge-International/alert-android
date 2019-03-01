package org.alertpreparedness.platform.v1.risk_monitoring.view


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import kotlinx.android.synthetic.main.fragment_archived_risk.*
import org.alertpreparedness.platform.R
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
class ArchivedRiskFragment : Fragment(), OnIndicatorSelectedListener {

    private lateinit var mViewModel: ActiveRiskViewModel
    private var mCountryLocation = -1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(ActiveRiskViewModel::class.java)
        mViewModel.getLiveCountryModel().observe(this, Observer<ModelCountry> { country ->
            country?.location?.let {
                mCountryLocation = it
            }
        })
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.fragment_archived_risk, container, false)
        val rvRiskArchived: RecyclerView? = view?.find(R.id.rvRiskArchived)
        rvRiskArchived?.layoutManager = LinearLayoutManager(activity)
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rvRiskArchived?.addItemDecoration(decoration)
        mViewModel.getLiveGroups(false).observe(this, Observer<MutableList<ExpandableGroup<ModelIndicator>>> {
            val size = it?.size ?: 0
//            if (size > 0) {
                pbLoadingArchived?.hide()
//            }
            rvRiskArchived?.adapter = HazardAdapter(it as List<ExpandableGroup<ModelIndicator>>, mCountryLocation, this, mapOf(), context!!)
        })
        return view
    }

    override fun selectedIndicator(hazardId: String, indicatorId: String, networkId:String?, networkCountryId:String?) {
        Timber.d("received: %s, %s", hazardId, indicatorId)
        val bsDialog = BottomSheetDialog()
        val bundle = Bundle()
        bundle.putString(ActiveRiskFragment.HAZARD_ID, hazardId)
        bundle.putString(ActiveRiskFragment.INDICATOR_ID, indicatorId)
        networkId?.apply {bundle.putString(ActiveRiskFragment.NETWORK_ID, networkId)}
        networkCountryId?.apply { bundle.putString(ActiveRiskFragment.NETWORK_COUNTRY_ID, networkCountryId)  }
        bsDialog.arguments = bundle
        bsDialog.show(fragmentManager, "bottom_sheet")
    }

}
