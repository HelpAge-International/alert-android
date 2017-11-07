package org.alertpreparedness.platform.alert.risk_monitoring


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.R
import org.jetbrains.anko.find
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
class ActiveRiskFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.fragment_active_risk, container, false)
        val rvRiskActive: RecyclerView? = view?.find(R.id.rvRiskActive)
        rvRiskActive?.layoutManager = LinearLayoutManager(AlertApplication.getContext())
        rvRiskActive?.hasFixedSize()
        val indicator1 = ModelIndicator("1", ModelHazard("1", 2, true, false, 10, 10000000, ""), 1, "test 1", "", 0, 1111111111, 2222222222, listOf("a", "b"), listOf(ModelTrigger("1", 1, "1"), ModelTrigger("1", 1, "1")))
        val indicator2 = ModelIndicator("2", ModelHazard("1", 2, true, false, 10, 10000000, ""), 1, "test 2", "", 0, 1111111111, 2222222222, listOf("a", "b"), listOf(ModelTrigger("1", 1, "1"), ModelTrigger("1", 1, "1")))
        val group1: ExpandableGroup<ModelIndicator> = ExpandableGroup("r1", listOf(indicator1, indicator2))
        val group2: ExpandableGroup<ModelIndicator> = ExpandableGroup("r2", listOf(indicator1, indicator2))
        rvRiskActive?.adapter = HazardAdapter(listOf(group1, group2))
        return view
    }

    class Hazard(hazardId: String, indicators: List<ModelIndicator>) : ExpandableGroup<ModelIndicator>(hazardId, indicators)

    class HazardViewHolder(itemView: View) : GroupViewHolder(itemView) {
        private val hazardTitle: TextView = itemView.findViewById(R.id.tvHazardName)
        private val hazardIcon: CircleImageView = itemView.findViewById(R.id.civHazard)
        private val hazardArrow: ImageView = itemView.findViewById(R.id.ivArrow)
        private val hazardLayout: LinearLayout = itemView.find(R.id.llHazard)

        init {
            hazardLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        fun setHazardTitle(group: ExpandableGroup<ModelIndicator>) {
            hazardTitle.text = group.title
        }
    }

    class IndicatorViewHolder(itemView: View) : ChildViewHolder(itemView) {
        private val indicatorTitle: TextView = itemView.findViewById(R.id.tvIndicatorName)
        private val indicatorGeo: TextView = itemView.find(R.id.tvIndicatorGeo)
        private val indicatorLevel: TextView = itemView.find(R.id.tvIndicatorLevel)
        private val indicatorDue: TextView = itemView.find(R.id.tvIndicatorDate)
        private val indicatorNextUpdate: TextView = itemView.find(R.id.tvIndicatorNextUpdate)
        private val indicatorLayout: LinearLayout = itemView.find(R.id.llRiskIndicator)

        init {
            indicatorLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        fun onBind(indicator: ModelIndicator) {
            indicatorTitle.text = indicator.name
            indicatorGeo.text = indicator.geoLocation.toString()
            indicatorLevel.text = indicator.triggerSelected.toString()
            indicatorDue.text = indicator.dueDate.toString()
            indicatorLevel.setOnClickListener { Timber.d("id: %s",indicator.id) }
        }
    }

    class HazardAdapter(groups: List<ExpandableGroup<ModelIndicator>>) : ExpandableRecyclerViewAdapter<HazardViewHolder, IndicatorViewHolder>(groups) {
        override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): IndicatorViewHolder {
            val view = View.inflate(AlertApplication.getContext(), R.layout.risk_indicator_item_view, null)
            return IndicatorViewHolder(view)
        }

        override fun onBindGroupViewHolder(holder: HazardViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
            @Suppress("UNCHECKED_CAST")
            holder?.setHazardTitle(group as ExpandableGroup<ModelIndicator>)
        }

        override fun onBindChildViewHolder(holder: IndicatorViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?, childIndex: Int) {
            val indicator: ModelIndicator = group?.items?.get(childIndex) as ModelIndicator
            holder?.onBind(indicator)
        }

        override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): HazardViewHolder {
            val view = View.inflate(AlertApplication.getContext(), R.layout.risk_hazard_item_view, null)
            return HazardViewHolder(view)
        }

    }

}
