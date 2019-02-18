package org.alertpreparedness.platform.v2.dashboard.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_alert.view.ivHazardIcon
import kotlinx.android.synthetic.main.item_alert.view.tvAlertLevel
import kotlinx.android.synthetic.main.item_alert.view.tvAlertRequested
import kotlinx.android.synthetic.main.item_alert.view.tvHazardName
import kotlinx.android.synthetic.main.item_alert.view.tvNumOfPeople
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.dashboard.home.AlertActionType.NORMAL
import org.alertpreparedness.platform.v2.dashboard.home.AlertActionType.RED_ALERT_REQUESTED
import org.alertpreparedness.platform.v2.dashboard.home.AlertActionType.RED_ALERT_REQUIRES_ACTION
import org.alertpreparedness.platform.v2.dashboard.home.HomeAlertsAdapter.HomeAlertViewHolder
import org.alertpreparedness.platform.v2.models.Alert
import org.alertpreparedness.platform.v2.utils.DiffComparator
import org.alertpreparedness.platform.v2.utils.DiffListAdapter
import org.alertpreparedness.platform.v2.utils.ViewHolder
import org.alertpreparedness.platform.v2.utils.extensions.hide
import org.alertpreparedness.platform.v2.utils.extensions.show
import org.jetbrains.anko.imageResource

class HomeAlertsAdapter(val context: Context) :
        DiffListAdapter<Pair<AlertActionType, Alert>, HomeAlertViewHolder>(object :
                DiffComparator<Pair<AlertActionType, Alert>> {
            override fun areItemsTheSame(o1: Pair<AlertActionType, Alert>,
                    o2: Pair<AlertActionType, Alert>): Boolean {
                return o1.second.id == o2.second.id
            }

            override fun areContentsTheSame(o1: Pair<AlertActionType, Alert>,
                    o2: Pair<AlertActionType, Alert>): Boolean {
                return o1 == o2
            }
        }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAlertViewHolder {
        return HomeAlertViewHolder(LayoutInflater.from(context).inflate(R.layout.item_alert, parent, false))
    }

    inner class HomeAlertViewHolder(view: View) : ViewHolder<Pair<AlertActionType, Alert>>(view) {

        private val tvAlertLevel = view.tvAlertLevel
        private val ivHazardIcon = view.ivHazardIcon
        private val tvHazardName = view.tvHazardName
        private val tvNumOfPeople = view.tvNumOfPeople
        private val tvAlertRequested = view.tvAlertRequested

        override fun bind(model: Pair<AlertActionType, Alert>, position: Int) {
            val (alertActionType, alert) = model

            tvAlertLevel.setText(alert.level.string)
            tvAlertLevel.setBackgroundColor(ContextCompat.getColor(context, alert.level.color))

            ivHazardIcon.imageResource = alert.hazardScenario.icon
            tvHazardName.setText(alert.hazardScenario.text)

            tvNumOfPeople.text = context.resources.getQuantityString(R.plurals.alert_population,
                    alert.affectedAreas.size, alert.estimatedPopulation, alert.affectedAreas.size)

            when (alertActionType) {
                NORMAL -> {
                    tvAlertRequested.hide()
                }
                RED_ALERT_REQUESTED -> {
                    tvAlertLevel.setBackgroundColor(ContextCompat.getColor(context, R.color.alertGrey))
                    tvAlertRequested.show()
                    tvAlertRequested.setText(R.string.red_alert_requested)
                }
                RED_ALERT_REQUIRES_ACTION -> {
                    tvAlertLevel.setBackgroundColor(ContextCompat.getColor(context, R.color.alertGrey))
                    tvAlertRequested.show()
                    tvAlertRequested.setText(R.string.red_alert_requested_action_required)
                }
            }
        }
    }
}