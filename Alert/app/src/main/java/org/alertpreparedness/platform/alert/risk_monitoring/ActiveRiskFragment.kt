package org.alertpreparedness.platform.alert.risk_monitoring


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_active_risk.*
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.R


/**
 * A simple [Fragment] subclass.
 */
class ActiveRiskFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_active_risk, container, false)
        rvRiskActive.layoutManager = LinearLayoutManager(AlertApplication.getContext())
        rvRiskActive.hasFixedSize()
        return view
    }

//    class Hazard(title: String, items: List<ModelIndicator>) : ExpandableGroup<ModelIndicator>(title, items)
//
//    class HazardViewHolder(itemView: View) : GroupViewHolder(itemView) {
//        private var hazardTitle: TextView = itemView.findViewById(R.id.tvHazardName)
//
//        fun setHazardTitle(group: ExpandableGroup<ModelIndicator>) {
//            hazardTitle.text = group.title
//        }
//    }
//
//    class IndicatorViewHolder(itemView: View) : ChildViewHolder(itemView) {
//        private var indicatorTitle: TextView = itemView.findViewById(R.id.tvIndicatorName)
//
//        fun onBind(indicator: ModelIndicator) {
//            indicatorTitle.text = indicator.name
//        }
//    }
//
//    class HazardAdapter(groups: List<ExpandableGroup<ModelIndicator>>) : ExpandableRecyclerViewAdapter<HazardViewHolder, IndicatorViewHolder>(groups) {
//        override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): IndicatorViewHolder {
//            val view = View.inflate(AlertApplication.getContext(), R.layout.risk_indicator_item_view, null)
//            return IndicatorViewHolder(view)
//        }
//
//        override fun onBindGroupViewHolder(holder: HazardViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
//            @Suppress("UNCHECKED_CAST")
//            holder?.setHazardTitle(group as ExpandableGroup<ModelIndicator>)
//        }
//
//        override fun onBindChildViewHolder(holder: IndicatorViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?, childIndex: Int) {
//            val indicator: ModelIndicator = group?.items?.get(childIndex) as ModelIndicator
//            holder?.onBind(indicator)
//        }
//
//        override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): HazardViewHolder {
//            val view = View.inflate(AlertApplication.getContext(), R.layout.risk_hazard_item_view, null)
//            return HazardViewHolder(view)
//        }
//
//    }

}
