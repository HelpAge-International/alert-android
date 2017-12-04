package org.alertpreparedness.platform.alert.risk_monitoring.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import kotlinx.android.synthetic.main.dialog_edit_log.view.*
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService
import timber.log.Timber

/**
 * ==============================
 * Created by fei
 * Dated: 29/11/2017
 * Email: fei@rolleragency.co.uk
 * Copyright Roller Agency
 * ==============================
 */
class EditLogDialog : DialogFragment() {

    companion object {
        val INDICATOR_ID = "indicator_id"
        val LOG_CONTENT = "log_content"
        val LOG_ID = "log_id"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val content = arguments.getString(LOG_CONTENT) ?: ""
        val logId = arguments.getString(LOG_ID) ?: ""
        val indicatorId = arguments.getString(INDICATOR_ID) ?: ""
        Timber.d("indicator id: %s, log id: %s, log content: %s", indicatorId, logId, content)
        val view = View.inflate(activity, R.layout.dialog_edit_log, null)
        view.etEditLog.setText(content)
        return AlertDialog.Builder(activity, R.style.EditDialogTheme)
                .setTitle(getString(R.string.edit_message))
                .setView(view)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.save), { _, _ ->
                    if (indicatorId.isNotEmpty() && logId.isNotEmpty() && content.isNotEmpty()) {
                        Timber.d("start updating log content*****************")
                        RiskMonitoringService.updateLogContent(indicatorId, logId, view.etEditLog.text.toString())
                    }
                })
                .create()
    }
}