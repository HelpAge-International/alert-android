package org.alertpreparedness.platform.alert.risk_monitoring.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicatorLocation
import org.jetbrains.anko.find
import timber.log.Timber

/**
 * Created by fei on 09/11/2017.
 */
class AreaRVAdapter(private val areas: MutableList<ModelIndicatorLocation>, private val countryDataList:List<CountryJsonData>) : RecyclerView.Adapter<AreaViewHolder>() {

    private var listener: OnAreaDeleteListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AreaViewHolder =
            AreaViewHolder(View.inflate(AlertApplication.getContext(), R.layout.area_item_view, null), listener)

    override fun onBindViewHolder(holder: AreaViewHolder?, position: Int) {
        with(areas[position]) {
            holder?.tvAreaName?.text = String.format("%s %s %s", this.country,
                    if (this.level1 != -1) this.level1?.let { ", "+countryDataList[this.country as Int].levelOneValues?.get(it)?.value } else "",
                    if (this.level2 != -1) this.level2?.let { this.level1?.let { it1 -> ", "+countryDataList[this.country as Int].levelOneValues?.get(it1)?.levelTwoValues?.get(it)?.value } } else "")
        }
    }

    override fun getItemCount(): Int = areas.size


    fun setOnAreaDeleteListener(areaDeleteListener: OnAreaDeleteListener) {
        listener = areaDeleteListener
    }

}

class AreaViewHolder(itemView: View, listener: OnAreaDeleteListener?) : RecyclerView.ViewHolder(itemView) {

    val tvAreaName: TextView = itemView.find(R.id.tvAreaItemName)
    val btnRemove: Button = itemView.find(R.id.btnAreaItemRemove)
    val llArea: LinearLayout = itemView.find(R.id.llAreaItem)

    init {
        llArea.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        btnRemove.setOnClickListener {
            Timber.d("delete position: %s", adapterPosition)
            listener?.areaRemovePosition(adapterPosition)
        }
    }

}

interface OnAreaDeleteListener {
    fun areaRemovePosition(position: Int)
}