package org.alertpreparedness.platform.alert.risk_monitoring.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

/**
 * ==============================
 * Created by fei
 * Dated: 29/11/2017
 * Email: fei@rolleragency.co.uk
 * Copyright Roller Agency
 * ==============================
 */
class EditLogDialog:DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle("Edit note")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("SAVE", null)
                .create()
    }
}