package org.alertpreparedness.platform.alert.risk_monitoring

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import org.alertpreparedness.platform.alert.R

/**
 * Created by fei on 08/11/2017.
 */
class SourceDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        return AlertDialog.Builder(activity)
                .setView(View.inflate(activity, R.layout.dialog_indicator_source, null))
                .setTitle("Add source")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("SAVE", null)
                .create()
    }
}