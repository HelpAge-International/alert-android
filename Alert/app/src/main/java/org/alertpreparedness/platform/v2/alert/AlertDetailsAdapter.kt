package org.alertpreparedness.platform.v2.alert

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.android.synthetic.main.fragment_my_country.view.tvDescription
import kotlinx.android.synthetic.main.item_alert_detail.view.ivIcon
import kotlinx.android.synthetic.main.item_alert_detail.view.tvTitle
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.models.Alert
import org.alertpreparedness.platform.v2.utils.AreaJsonManager
import org.alertpreparedness.platform.v2.utils.DiffComparator
import org.alertpreparedness.platform.v2.utils.DiffListAdapter
import org.alertpreparedness.platform.v2.utils.ViewHolder
import org.alertpreparedness.platform.v2.utils.extensions.isRedAlertRequested

data class ItemAlertDetail(
        val id: String,
        @DrawableRes val icon: Int,
        @StringRes val title: Int,
        val description: String
)

class AlertDetailsAdapter(val context: Context) :
        DiffListAdapter<ItemAlertDetail, AlertDetailsAdapter.AlertDetailsViewHolder>(object :
                DiffComparator<ItemAlertDetail> {
            override fun areItemsTheSame(o1: ItemAlertDetail, o2: ItemAlertDetail): Boolean {
                return o1.id == o2.id
            }

            override fun areContentsTheSame(o1: ItemAlertDetail, o2: ItemAlertDetail): Boolean {
                return o1 == o2
            }
        }) {

    fun update(alert: Alert) {
        val items = mutableListOf<ItemAlertDetail>()

        items += ItemAlertDetail(
                "risk",
                alert.hazardScenario.icon,
                R.string.alert_details_risk,
                context.getString(alert.hazardScenario.text)
        )

        if (alert.isRedAlertRequested()) {
            items += ItemAlertDetail(
                    "redReason",
                    R.drawable.alert_red_reason,
                    R.string.alert_details_red_reason,
                    alert.reasonForRedAlert ?: ""
            )
        }

        items += ItemAlertDetail(
                "populationAffected",
                R.drawable.alert_population,
                R.string.alert_details_population,
                context.resources.getQuantityString(R.plurals.alert_people_affected,
                        alert.estimatedPopulation.toInt(), alert.estimatedPopulation)
        )

        items += ItemAlertDetail(
                "affectedAreas",
                R.drawable.alert_areas,
                R.string.alert_details_areas,
                alert.affectedAreas
                        .map { area ->
                            AreaJsonManager.getAreaData(context, area)
                        }
                        .joinToString("\n") {
                            listOfNotNull(
                                    context.getString(it.country.string),
                                    it.level1Name?.name,
                                    it.level2Name?.name
                            )
                                    .joinToString(", ")
                        }
        )

        items += ItemAlertDetail(
                "notes",
                R.drawable.alert_information,
                R.string.alert_details_notes,
                alert.infoNotes
        )

        updateItems(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertDetailsViewHolder {
        return AlertDetailsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_alert_detail, parent, false))
    }

    inner class AlertDetailsViewHolder(itemView: View) : ViewHolder<ItemAlertDetail>(itemView) {

        val ivIcon = itemView.ivIcon
        val tvTitle = itemView.tvTitle
        val tvDescription = itemView.tvDescription

        override fun bind(model: ItemAlertDetail, position: Int) {
            ivIcon.setImageResource(model.icon)
            tvTitle.setText(model.title)
            tvDescription.text = model.description
        }
    }
}

