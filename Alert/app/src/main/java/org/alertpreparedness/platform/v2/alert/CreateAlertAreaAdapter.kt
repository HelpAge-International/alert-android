package org.alertpreparedness.platform.v2.alert

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_create_alert_area.view.btnRemove
import kotlinx.android.synthetic.main.item_create_alert_area.view.tvArea
import kotlinx.android.synthetic.main.item_create_alert_area_add.view.tvText
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.models.Area
import org.alertpreparedness.platform.v2.utils.getText

class CreateAlertAreaAdapter(private val context: Context, private val areaListener: AreaListener? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val items: MutableList<Area> = mutableListOf()

    private val viewTypeAdd = 0
    private val viewTypeItem = 1

    fun updateItems(areas: List<Area>?) {
        items.clear()

        if (areas != null) {
            items.addAll(items)
        }

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewTypeAdd -> AddViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.item_create_alert_area_add, parent, false))
            else -> ItemViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.item_create_alert_area, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> holder.bind(items[position])
            is AddViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == items.size - 1) {
            return viewTypeAdd
        } else {
            return viewTypeItem
        }
    }

    private fun areaRemoved(area: Area) {
        areaListener?.onAreaRemoved(area)
    }

    private fun addAreaClicked() {
        areaListener?.onAddAreaClicked()
    }

    inner class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvText = itemView.tvText

        init {
            tvText.setOnClickListener {
                addAreaClicked()
            }
        }

        fun bind() {
            if (items.isEmpty()) {
                tvText.setText(R.string.add_area)
            } else {
                tvText.setText(R.string.add_another_area)
            }
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvArea = itemView.tvArea
        private val btnRemove = itemView.btnRemove

        private lateinit var area: Area

        init {
            btnRemove.setOnClickListener {
                areaRemoved(area)
            }
        }

        fun bind(area: Area) {
            this.area = area

            tvArea.text = area.getText(context)
        }
    }
}

interface AreaListener {
    fun onAreaRemoved(area: Area)
    fun onAddAreaClicked()
}