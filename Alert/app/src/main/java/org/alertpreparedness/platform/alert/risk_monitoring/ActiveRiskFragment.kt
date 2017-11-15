package org.alertpreparedness.platform.alert.risk_monitoring


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
import org.jetbrains.anko.find


/**
 * A simple [Fragment] subclass.
 */
class ActiveRiskFragment : Fragment() {

    private lateinit var mViewModel:ActiveRiskViewModel


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(ActiveRiskViewModel::class.java)
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.fragment_active_risk, container, false)
        val rvRiskActive: RecyclerView? = view?.find(R.id.rvRiskActive)
        rvRiskActive?.layoutManager = LinearLayoutManager(AlertApplication.getContext())
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rvRiskActive?.addItemDecoration(decoration)
        mViewModel.getLiveGroups().observe(this, Observer<MutableList<ExpandableGroup<ModelIndicator>>> {
            val size = it?.size ?: 0
            if (size > 0) {pbLoading?.hide()}
            rvRiskActive?.adapter = HazardAdapter(it as List<ExpandableGroup<ModelIndicator>>)
        })
        return view
    }

}
