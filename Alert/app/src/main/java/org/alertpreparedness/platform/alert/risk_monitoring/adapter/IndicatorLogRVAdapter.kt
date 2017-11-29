package org.alertpreparedness.platform.alert.risk_monitoring.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.EditLogDialog
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelLog
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.PreferHelper
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import timber.log.Timber



/**
 * Created by fei on 29/11/2017.
 */
class IndicatorLogRVAdapter(private val logs:List<ModelLog>, private val context:Context, private val indicatorId:String, private val fm: FragmentManager) : RecyclerView.Adapter<LogViewHolder>() {

    private val mUid = PreferHelper.getString(AlertApplication.getContext(), Constants.UID)

    override fun getItemCount(): Int {
        return logs.size
    }

    override fun onBindViewHolder(holder: LogViewHolder?, position: Int) {
        val logModel = logs[position]
        Timber.d(logModel.toString())
        logModel.apply {
            holder?.tvLogWriterName?.text = logModel.addedByName?:""
            holder?.tvLogContent?.text = logModel.content
            val dateTime = DateTime(logModel.timeStamp)
            val fmt = DateTimeFormat.forPattern("d MMMM yyyy")
            holder?.tvLogDate?.text = dateTime.toString(fmt)
            when (logModel.triggerAtCreation) {
                Constants.TRIGGER_GREEN -> {
                    holder?.tvLogStatus?.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.green_dot,0)
                }
                Constants.TRIGGER_AMBER -> {
                    holder?.tvLogStatus?.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.amber_dot,0)
                }
                else -> {
                    holder?.tvLogStatus?.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.red_dot,0)
                }
            }
            Timber.d("addBy: %s, uid: %s", logModel.addedBy, mUid)
            holder?.ivLogMenu?.visibility = if (mUid == logModel.addedBy || PreferHelper.getInt(context, Constants.USER_TYPE) == Constants.CountryAdmin) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LogViewHolder {
        val view = View.inflate(AlertApplication.getContext(), R.layout.item_view_indicator_log, null)
        return LogViewHolder(view, logs, context, indicatorId, fm)
    }

}


class LogViewHolder(itemView: View, private val logs: List<ModelLog>, private val context:Context, private val indicatorId:String, private val fm:FragmentManager) : RecyclerView.ViewHolder(itemView) {
    val tvLogWriterName: TextView = itemView.findViewById(R.id.tvLogWriterName)
    val tvLogContent: TextView = itemView.findViewById(R.id.tvLogContent)
    val tvLogDate: TextView = itemView.findViewById(R.id.tvLogDate)
    val tvLogStatus: TextView = itemView.findViewById(R.id.tvLogStatus)
    val ivLogMenu:ImageView = itemView.findViewById(R.id.ivLogMenu)
    val flMenu:FrameLayout = itemView.findViewById(R.id.flMenu)
    val llLogItem:LinearLayout = itemView.findViewById(R.id.llLogItem)

    init {
        llLogItem.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        flMenu.setOnClickListener {
            val popMenu = PopupMenu(context, ivLogMenu)
            popMenu.menu.add("Edit")
            popMenu.menu.add("Delete")
            popMenu.menuInflater.inflate(R.menu.popup_template_menu, popMenu.menu)
            popMenu.show()

            popMenu.setOnMenuItemClickListener { menuItem ->
                Timber.d(logs[adapterPosition].toString())
                when (menuItem.title) {
                    "Edit" -> {
                        Timber.d("edit")
                        val editDialog = EditLogDialog()
                        editDialog.show(fm, "edit_log_dialog")
                    }
                    else -> {
                        Timber.d("delete")
                        logs[adapterPosition].id?.let { RiskMonitoringService.deleteLog(indicatorId, it) }
                    }
                }
                true
            }
        }
    }
}