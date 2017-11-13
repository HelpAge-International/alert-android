package org.alertpreparedness.platform.alert.risk_monitoring

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import org.alertpreparedness.platform.alert.R
import timber.log.Timber

/**
 * Created by fei on 08/11/2017.
 */

class LocationSelectionDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle("Location selection")
                .setSingleChoiceItems(arrayOf("National", "Subnational", "Use my location"), 0) { _, position -> Timber.d("position: %s", position) }
                .setPositiveButton(getString(R.string.save), null)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
    }
}