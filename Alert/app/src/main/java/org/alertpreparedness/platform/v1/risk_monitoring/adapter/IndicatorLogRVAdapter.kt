package org.alertpreparedness.platform.v1.risk_monitoring.adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.EditLogDialog
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelLog
import org.alertpreparedness.platform.v1.risk_monitoring.service.RiskMonitoringService
import org.alertpreparedness.platform.v1.utils.Constants
import org.alertpreparedness.platform.v1.utils.PreferHelper
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import timber.log.Timber


/**
 * Created by fei on 29/11/2017.
 */
class IndicatorLogRVAdapter(private val logs: List<ModelLog>, private val context: Context, private val indicatorId: String, private val fm: FragmentManager) : RecyclerView.Adapter<LogViewHolder>() {

    private val mUid = PreferHelper.getString(context, Constants.UID)

    override fun getItemCount(): Int {
        return logs.size
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val logModel = logs[position]
        Timber.d(logModel.toString())
        logModel.apply {
            holder?.tvLogWriterName?.text = logModel.addedByName ?: ""
            holder?.tvLogContent?.text = logModel.content
            val dateTime = DateTime(logModel.timeStamp)
            val fmt = DateTimeFormat.forPattern("d MMMM yyyy")
            holder?.tvLogDate?.text = dateTime.toString(fmt)
            when (logModel.triggerAtCreation) {
                Constants.TRIGGER_GREEN -> {
                    holder?.tvLogStatus?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_dot, 0)
                }
                Constants.TRIGGER_AMBER -> {
                    holder?.tvLogStatus?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.amber_dot, 0)
                }
                else -> {
                    holder?.tvLogStatus?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_dot, 0)
                }
            }
            Timber.d("addBy: %s, uid: %s", logModel.addedBy, mUid)
            holder?.ivLogMenu?.visibility = if (mUid == logModel.addedBy || PreferHelper.getInt(context, Constants.USER_TYPE) == Constants.CountryAdmin) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = View.inflate(context, R.layout.item_view_indicator_log, null)
        return LogViewHolder(view, logs, context, indicatorId, fm)
    }

}


class LogViewHolder(itemView: View, private val logs: List<ModelLog>, private val context: Context, private val indicatorId: String, private val fm: FragmentManager) : RecyclerView.ViewHolder(itemView) {
    val tvLogWriterName: TextView = itemView.findViewById(R.id.tvLogWriterName)
    val tvLogContent: TextView = itemView.findViewById(R.id.tvLogContent)
    val tvLogDate: TextView = itemView.findViewById(R.id.tvLogDate)
    val tvLogStatus: TextView = itemView.findViewById(R.id.tvLogStatus)
    val ivLogMenu: ImageView = itemView.findViewById(R.id.ivLogMenu)
    val flMenu: FrameLayout = itemView.findViewById(R.id.flMenu)
    val llLogItem: LinearLayout = itemView.findViewById(R.id.llLogItem)

    init {
        llLogItem.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        flMenu.setOnClickListener {
            val popMenu = PopupMenu(context, ivLogMenu)
            popMenu.menu.add(context.getString(R.string.edit))
            popMenu.menu.add(context.getString(R.string.delete))
            popMenu.menuInflater.inflate(R.menu.popup_template_menu, popMenu.menu)
            popMenu.show()

            popMenu.setOnMenuItemClickListener { menuItem ->
                Timber.d(logs[adapterPosition].toString())
                when (menuItem.title) {
                    "Edit" -> {
                        Timber.d("edit")
                        val editDialog = EditLogDialog()
                        val bundle = Bundle()
                        bundle.putString(EditLogDialog.LOG_CONTENT, logs[adapterPosition].content)
                        bundle.putString(EditLogDialog.LOG_ID, logs[adapterPosition].id)
                        bundle.putString(EditLogDialog.INDICATOR_ID, indicatorId)
                        editDialog.arguments = bundle
                        editDialog.show(fm, "edit_log_dialog")
                    }
                    else -> {
                        Timber.d("delete")
                        context.alert("Deleting this entry will remove it from this indicator log, this action cannot be undone. Are you sure you want to continue?", "Delete entry") {
                            yesButton { logs[adapterPosition].id?.let { RiskMonitoringService(context).deleteLog(indicatorId, it) } }
                            noButton {}
                        }.show()
                    }
                }
                true
            }
        }
    }
}