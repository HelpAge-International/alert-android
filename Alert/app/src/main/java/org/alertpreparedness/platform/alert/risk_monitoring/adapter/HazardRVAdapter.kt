package org.alertpreparedness.platform.alert.risk_monitoring.adapter

import android.os.Build
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
import org.alertpreparedness.platform.alert.getCountryImage
import org.alertpreparedness.platform.alert.getHazardImg
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.alert.utils.Constants
import org.jetbrains.anko.find
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.textColor
import org.joda.time.DateTime
import timber.log.Timber

/**
 * Created by fei on 08/11/2017.
 */

class HazardViewHolder(itemView: View, location:Int) : GroupViewHolder(itemView) {
    private val hazardTitle: TextView = itemView.findViewById(R.id.tvHazardName)
    private val hazardIcon: CircleImageView = itemView.findViewById(R.id.civHazard)
    private val hazardArrow: ImageView = itemView.findViewById(R.id.ivArrow)
    private val hazardLayout: LinearLayout = itemView.find(R.id.llHazard)
    private val mCountryLocation = location

    init {
        hazardLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setHazardTitle(group: ExpandableGroup<ModelIndicator>) {
        hazardTitle.text = group.title
        when (group.title) {
            "Country Context" -> {
                hazardIcon.imageResource = getCountryImage(mCountryLocation)
            }
            else -> {
                hazardIcon.imageResource = getHazardImg(hazardTitle.text.toString())
            }
        }

    }


}

class IndicatorViewHolder(itemView: View, listener: OnIndicatorSelectedListener, networkCountryMap:Map<String,String>?) : ChildViewHolder(itemView) {

    private val indicatorTitle: TextView = itemView.findViewById(R.id.tvIndicatorName)
    private val indicatorGeo: TextView = itemView.find(R.id.tvIndicatorGeo)
    private val indicatorLevel: TextView = itemView.find(R.id.tvIndicatorLevel)
    private val indicatorDue: TextView = itemView.find(R.id.tvIndicatorDate)
    private val indicatorNextUpdate: TextView = itemView.find(R.id.tvIndicatorNextUpdate)
    private val indicatorLayout: LinearLayout = itemView.find(R.id.llRiskIndicator)
    private val indicatorNetworkId: TextView = itemView.find(R.id.tvIndicatorNetworkName)
    private val mListener = listener
    private val mNetworkMap = networkCountryMap

    init {
        indicatorLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun onBind(indicator: ModelIndicator) {
        indicatorTitle.text = indicator.name
        indicatorGeo.text = Constants.INDICATOR_GEO_LOCATION[indicator.geoLocation]
        val dateTime = DateTime(indicator.dueDate)
        indicatorDue.text = String.format("%s %s %s", dateTime.dayOfMonth().asText, dateTime.monthOfYear().asShortText, dateTime.year().asText)
        if (indicator.networkName != null) {
            indicatorNetworkId.text = indicator.networkName
            indicatorNetworkId.visibility = View.VISIBLE
        } else {
            indicatorNetworkId.visibility = View.GONE
        }

        when (indicator.triggerSelected) {
            Constants.TRIGGER_GREEN -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    indicatorLevel.background = AlertApplication.getContext().resources.getDrawable(R.drawable.indicator_green, AlertApplication.getContext().theme)
                }
                else {
                    indicatorLevel.background = AlertApplication.getContext().resources.getDrawable(R.drawable.indicator_green)
                }
                indicatorLevel.text = Constants.TRIGGER_LEVEL[Constants.TRIGGER_GREEN]
                indicatorNextUpdate.textColor = AlertApplication.getContext().resources.getColor(R.color.alertGreen)
                indicatorDue.textColor = AlertApplication.getContext().resources.getColor(R.color.alertGreen)
            }
            Constants.TRIGGER_AMBER -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    indicatorLevel.background = AlertApplication.getContext().resources.getDrawable(R.drawable.indicator_amber, AlertApplication.getContext().theme)
                }
                else {
                    indicatorLevel.background = AlertApplication.getContext().resources.getDrawable(R.drawable.indicator_amber)
                }
                indicatorLevel.text = Constants.TRIGGER_LEVEL[Constants.TRIGGER_AMBER]
                indicatorNextUpdate.textColor = AlertApplication.getContext().resources.getColor(R.color.alertAmber)
                indicatorDue.textColor = AlertApplication.getContext().resources.getColor(R.color.alertAmber)
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    indicatorLevel.background = AlertApplication.getContext().resources.getDrawable(R.drawable.indicator_red, AlertApplication.getContext().theme)
                }
                else {
                    indicatorLevel.background = AlertApplication.getContext().resources.getDrawable(R.drawable.indicator_red)
                }
                indicatorLevel.text = Constants.TRIGGER_LEVEL[Constants.TRIGGER_RED]
                indicatorNextUpdate.textColor = AlertApplication.getContext().resources.getColor(R.color.alertRed)
                indicatorDue.textColor = AlertApplication.getContext().resources.getColor(R.color.alertRed)
            }
        }
        indicatorLevel.setOnClickListener {
//            Timber.d("hazardId: %s, indicatorId: %s", indicator.hazardScenario.key?:"", indicator.id)
            Timber.d("indicator model: %s", indicator)
//            Timber.d("map: %s", mNetworkMap.toString())
            indicator.hazardScenario.key?.let { it1 -> indicator.id?.let { it2 -> mListener.selectedIndicator(if (it1 == "countryContext" && indicator.networkId != null && mNetworkMap != null) mNetworkMap[indicator.networkId]!! else it1, it2, if (indicator.networkId != null) indicator.networkId else null, if (indicator.networkId != null && mNetworkMap != null && mNetworkMap.containsKey(indicator.networkId)) mNetworkMap[indicator.networkId] else null) } }
        }
    }
}

class HazardAdapter(groups: List<ExpandableGroup<ModelIndicator>>, countryLocation:Int, listener:OnIndicatorSelectedListener, networkCountryMap:Map<String,String>?) : ExpandableRecyclerViewAdapter<HazardViewHolder, IndicatorViewHolder>(groups) {

    private val mListener = listener
    private val mLocation = countryLocation
    private val mMap = networkCountryMap

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): IndicatorViewHolder {
        val view = View.inflate(AlertApplication.getContext(), R.layout.risk_indicator_item_view, null)
        return IndicatorViewHolder(view, mListener, mMap)
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
        return HazardViewHolder(view, mLocation)
    }

}

interface OnIndicatorSelectedListener {
    fun selectedIndicator(hazardId: String, indicatorId:String, networkId:String?, networkCountryId:String?)
}